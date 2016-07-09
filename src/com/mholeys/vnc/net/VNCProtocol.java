package com.mholeys.vnc.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.Inflater;

import com.mholeys.vnc.VNCConnectionException;
import com.mholeys.vnc.auth.Authentication;
import com.mholeys.vnc.auth.NoAuthentication;
import com.mholeys.vnc.auth.TightVNCAuthentication;
import com.mholeys.vnc.auth.VNCAuthentication;
import com.mholeys.vnc.data.Encoding;
import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.data.PointerPoint;
import com.mholeys.vnc.display.IPasswordRequester;
import com.mholeys.vnc.display.IScreen;
import com.mholeys.vnc.display.IUserInterface;
import com.mholeys.vnc.encoding.CursorPseudoEncoding;
import com.mholeys.vnc.encoding.ZLibStream;
import com.mholeys.vnc.message.ClientInitMessage;
import com.mholeys.vnc.message.ServerInitMessage;
import com.mholeys.vnc.message.client.FramebufferUpdateRequest;
import com.mholeys.vnc.message.client.PointerEvent;
import com.mholeys.vnc.message.client.SetEncodings;
import com.mholeys.vnc.message.client.SetPixelFormatMessage;
import com.mholeys.vnc.message.server.FrameBufferUpdate;

public class VNCProtocol implements Runnable {

	public static final int FRAME_BUFFER_UDPATE = 0;
	public static final int RETRY_LIMIT = 5;
	
	public String address;
	public int port;
	
	public Socket socket;
	public OutputStream out;
	public InputStream in;
	public DataOutputStream dataOut;
	public DataInputStream dataIn;
	
	public IPasswordRequester password;
	
	public PixelFormat format;
	public int width, height;
	public IUserInterface ui;
	public String name;
	public ZLibStream[] streams = new ZLibStream[5];
	
	private boolean running = false;
	
	public VNCProtocol(String address, int port, IPasswordRequester password, IUserInterface ui) throws UnknownHostException, IOException {
		this.address = address;
		this.port = port;
		this.password = password;
		this.ui = ui;
		running = true;
	}
	
	public void initSocket(String address, int port) throws IOException {
		if (socket != null) {
			socket.close();
		}
		this.socket = new Socket(address, port);
		in = socket.getInputStream();
		out = socket.getOutputStream();
		dataIn = new DataInputStream(in);
		dataOut = new DataOutputStream(out);
		for (int i = 0; i < streams.length; i++) {
			if (streams[i] == null) {
				streams[i] = new ZLibStream(in, new Inflater());
			} else {
				streams[i].inflater.reset();
			}
		}
	}
	
	public void run() {
		try {
			boolean connected = false;
			int retry = 0;
			while (!connected && retry < RETRY_LIMIT) {
				initSocket(address, port);
				connected = handshake();
				retry++;
			}
			if (!connected && retry == RETRY_LIMIT) {
				System.exit(0);
			}
		} catch (IOException e) {
			throw new VNCConnectionException("Failed to connect to server: \"" + address + ":" + port + "\"");
		}
		try {
			ui.setSize(width, height);
			ui.show();
			sendFormat();
			sendSetEncoding();
			sendFrameBufferUpdateRequest(false);
			long timer = System.currentTimeMillis();
			boolean shouldRequest = false;
			int updateRequests = 0;
			while (running) {
				if (System.currentTimeMillis() - timer > 1000) {
					timer += 1000;
					if (updateRequests < 2) {
						shouldRequest = true;
					}
					updateRequests = 0;
				}
				if (shouldRequest) {
					sendFrameBufferUpdateRequest(true);
				}
				if (ui.getDisplay().sendPointer()) {
					sendPointerUpdate();
					//shouldRequest = true; //TODO implement local mouse to reduce bandwidth
				}
				shouldRequest = true;
				if (dataIn.available() == 0) {
					continue;
				}
				int id = dataIn.readByte();
				
				switch (id) {
				case FRAME_BUFFER_UDPATE:
					readFrameBufferUpdate();
					shouldRequest = true;
					break;
				default:
					System.out.println("Unknown message id: " + id);
				}
			}
		} catch (IOException e) {
			throw new VNCConnectionException("Connection ended");
		}
	}

	public boolean handshake() throws IOException {
		byte[] serverVersionBytes = new byte[12];
		dataIn.read(serverVersionBytes);
		String serverVersion = new String(serverVersionBytes);
		System.out.println("Server supports version: " + serverVersion);
		String version = "RFB 003.008\n";
		dataOut.writeBytes(version);

		//Get number of security types
		int number = dataIn.readByte();
		System.out.println(number + " types of security");
		
		if (number == 0) {
			System.out.println("Connection error");
			int length = dataIn.readInt();
			byte[] message = new byte[length];
			dataIn.readFully(message);
			System.out.println(new String(message));
		}
		
		boolean invalid = false, none = false, vnc_auth = false, tight_auth = false, realvnc = false;
		
		//Get the different types
		byte[] types = new byte[number];
		dataIn.read(types);
		
		for (int i = 0; i < types.length; i++) {
			switch (types[i] & 0xFF) {
			case 0:
				invalid = true;
				break;
			case 1:
				none = true;
				break;
			case 2:
				vnc_auth = true;
				break;
			case 3:
			case 4:
				realvnc = true;
			case 16:
				tight_auth = true;
				break;
			}
		}
		System.out.println("Invalid " + invalid);
		System.out.println("None " + none);
		System.out.println("VNC " + vnc_auth);
		System.out.println("TightVNC " + tight_auth);
		System.out.println("RealVNC " + realvnc);
		
		String pass = password.getPassword();
		
		boolean tight = false;
		Authentication auth = null;
		if (tight_auth) {
			auth = new TightVNCAuthentication(socket, pass);
			tight = true;
		} else if (vnc_auth) {
			auth = new VNCAuthentication(socket, pass);
		} else if (none) {
			auth = new NoAuthentication(socket, null);
		}
		if (auth == null) {
			System.err.println("No common authentication");
			dataOut.writeByte(-1);
			socket.close();
			return false;
		}
		dataOut.writeByte(auth.getSecurityId());
		boolean authenticated = auth.authenticate();
		
		if (!authenticated) {
			System.out.println("Failed to authenticate");
			return false;
		}
		
		ClientInitMessage clientInit = new ClientInitMessage(socket);
		clientInit.sendMessage();
		
		if (tight) {
			System.out.println("Connected via tight");
		}
		
		ServerInitMessage serverInit = new ServerInitMessage(socket, tight);
		serverInit.receiveMessage();
		name = serverInit.name;
		width = serverInit.framebufferWidth;
		height = serverInit.framebufferHeight;
		format = serverInit.format;
		
		System.out.println("Connected successfully to:");
		System.out.println(serverInit.name);
		System.out.println("Width: " + serverInit.framebufferWidth);
		System.out.println("Height: " + serverInit.framebufferHeight);
		System.out.println("Bits per pixel: " + format.bitsPerPixel);
		System.out.println("Depth: " + format.depth);
		return true;
	}
	
	public void sendFormat() throws IOException {
		SetPixelFormatMessage pixelFormat = new SetPixelFormatMessage(socket);
		PixelFormat format = new PixelFormat();
		format.bitsPerPixel = 32;
		format.depth = 24;
		format.bigEndianFlag = true;
		format.trueColorFlag = true;
		format.redMax = 255;
		format.greenMax = 255;
		format.blueMax = 255;
		format.redShift = 16;
		format.greenShift = 8;
		format.blueShift = 0;
		pixelFormat.format = format;
		pixelFormat.sendMessage();
	}

	public void sendSetEncoding() throws IOException {
		SetEncodings encodings = new SetEncodings(socket);
		encodings.encodings.add(Encoding.TIGHT_ENCODING.getStartID());
		encodings.encodings.add(Encoding.ZLIB_ENCODING.getStartID());
		encodings.encodings.add(Encoding.RAW_ENCODING.getStartID());
		//encodings.encodings.add(Encoding.COPY_RECT_ENCODING.getStartID());
		encodings.encodings.add(Encoding.JPEG_QUALITY_LEVEL_PSEUDO_ENCODING.getEndID());
		encodings.encodings.add(Encoding.COMPRESSION_LEVEL_PSEUDO_ENCODING.getEndID());
		encodings.encodings.add(Encoding.CURSOR_PSEUDO_ENCODING.getStartID());
		
		encodings.sendMessage();
	}
	
	public void sendFrameBufferUpdateRequest(boolean incremental) throws IOException {
		FramebufferUpdateRequest fbRequest = new FramebufferUpdateRequest(socket);
		fbRequest.incremental = (byte) (incremental ? 1 : 0);
		fbRequest.x = 0;
		fbRequest.y = 0;
		fbRequest.width = (short) width;
		fbRequest.height = (short) height;
		fbRequest.sendMessage();	
	}

	public void sendPointerUpdate() throws IOException {
		PointerPoint p = ui.getDisplay().getLocalPointer();
		if (p != null) {
			PointerEvent pEvent = new PointerEvent(socket);
			pEvent.x = p.x;
			pEvent.y = p.y;
			pEvent.setClick(p.left, p.right);
			pEvent.sendMessage();
		}
	}

	public void readFrameBufferUpdate() throws IOException {
		FrameBufferUpdate update = new FrameBufferUpdate(socket, ui.getScreen(), format, streams);
		update.receiveMessage();
	}
	
	public void disconnect() {
		ui.exit();
		try {
			out.close();
			in.close();
			dataOut.close();
			dataIn.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
