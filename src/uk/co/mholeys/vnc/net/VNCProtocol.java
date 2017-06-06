package uk.co.mholeys.vnc.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.Inflater;

import uk.co.mholeys.vnc.VNCConnectionException;
import uk.co.mholeys.vnc.auth.Authentication;
import uk.co.mholeys.vnc.auth.NoAuthentication;
import uk.co.mholeys.vnc.auth.TightVNCAuthentication;
import uk.co.mholeys.vnc.auth.VNCAuthentication;
import uk.co.mholeys.vnc.data.EncodingSettings;
import uk.co.mholeys.vnc.data.KeyboardUpdate;
import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PointerPoint;
import uk.co.mholeys.vnc.display.IUserInterface;
import uk.co.mholeys.vnc.display.UpdateManager;
import uk.co.mholeys.vnc.display.input.IConnectionInformation;
import uk.co.mholeys.vnc.display.input.IPasswordRequester;
import uk.co.mholeys.vnc.encoding.ZLibStream;
import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.message.ClientInitMessage;
import uk.co.mholeys.vnc.message.ServerInitMessage;
import uk.co.mholeys.vnc.message.client.FramebufferUpdateRequest;
import uk.co.mholeys.vnc.message.client.KeyEvent;
import uk.co.mholeys.vnc.message.client.PointerEvent;
import uk.co.mholeys.vnc.message.client.SetEncodings;
import uk.co.mholeys.vnc.message.client.SetPixelFormatMessage;
import uk.co.mholeys.vnc.message.server.FrameBufferUpdate;

public class VNCProtocol implements Runnable {

	public static final int FRAME_BUFFER_UDPATE = 0;
	public static final int RETRY_LIMIT = 5;
	
	public InetAddress address;
	public int port;
	
	public Socket socket;
	public OutputStream out;
	public InputStream in;
	public DataOutputStream dataOut;
	public DataInputStream dataIn;
	
	public EncodingSettings supportedEncodings;
	
	public UpdateManager updateManager;
	
	public IPasswordRequester password;
	
	public PixelFormat preferredFormat;
	public int width, height;
	public IUserInterface ui;
	public String name;
	public ZLibStream[] streams = new ZLibStream[5];
	
	public Logger logger;
	
	private boolean running = false;
	
	public VNCProtocol(IConnectionInformation connection, IUserInterface ui, Logger logger) throws UnknownHostException, IOException {
		this.address = connection.getAddress();
		this.port = connection.getPort();
		this.password = connection.getPasswordRequester();
		this.ui = ui;
		this.logger = logger;
		if (connection.hasPrefferedFormat()) {
			this.preferredFormat = connection.getPrefferedFormat();
			if (this.preferredFormat == null) {
				//this.preferredFormat = PixelFormat.DEFAULT_FORMAT;
			}
		} else {
			//this.preferredFormat = PixelFormat.DEFAULT_FORMAT;
		}
		if (connection.hasPrefferedEncoding()) {
			this.supportedEncodings = connection.getPrefferedEncoding();
			if (this.supportedEncodings == null) {
				this.supportedEncodings = EncodingSettings.DEFAULT_ENCODINGS;				
			}
		} else {
			this.supportedEncodings = EncodingSettings.DEFAULT_ENCODINGS;
		}
		running = true;
	}
	
	public void initSocket(InetAddress address, int port) throws IOException {
		if (socket != null) {
			socket.close();
		}
		this.socket = new Socket(address, port);
		in = new LogInputStream(socket.getInputStream());
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
			this.updateManager = new UpdateManager(width, height, ui.getScreen());
			ui.setUpdateManager(updateManager);
			ui.show();
			//if (preferredFormat != null) {
			sendFormat();
			//}
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
				if (ui.getMouseManager().sendLocalMouse()) {
					sendPointerUpdate();
				}
				if (ui.getKeyboardManager().sendKeys()) {
					sendKeyboardUpdate();
				}
				shouldRequest = true;
				if (dataIn.available() == 0) {
					continue;
				}
				logger.debugLn("Reading message id");
				int id = dataIn.readByte();
				
				switch (id) {
				case FRAME_BUFFER_UDPATE:
					readFrameBufferUpdate();
					shouldRequest = true;
					break;
				default:
					logger.verboseLn("Unknown message id: " + id);
				}
			}
		} catch (IOException e) {
			throw new VNCConnectionException("Connection ended");
		}
	}

	public boolean handshake() throws IOException {
		byte[] serverVersionBytes = new byte[12];
		logger.verboseLn("Reading server version");
		dataIn.read(serverVersionBytes);
		String serverVersion = new String(serverVersionBytes);
		logger.printLn("Server supports version: " + serverVersion);
		String version = "RFB 003.008\n";
		dataOut.writeBytes(version);

		//Get number of security types
		logger.debugLn("Reading number of security types");
		int number = dataIn.readByte();
		logger.printLn(number + " types of security");
		
		if (number == 0) {
			//Server denied connection and should return message
			logger.printLn("Connection error");
			logger.debugLn("Reading length of error message");
			int length = dataIn.readInt();
			byte[] message = new byte[length];
			logger.debugLn("Reading error message");
			dataIn.readFully(message);
			logger.printLn(new String(message));
		}
		
		boolean invalid = false, none = false, vnc_auth = false, tight_auth = false, realvnc = false;
		
		//Get the different types
		byte[] types = new byte[number];
		logger.debugLn("Reading security types");
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
		logger.printLn("Invalid " + invalid);
		logger.printLn("None " + none);
		logger.printLn("VNC " + vnc_auth);
		logger.printLn("TightVNC " + tight_auth);
		logger.printLn("RealVNC " + realvnc);
		
		String pass = password.getPassword();
		
		boolean tight = false;
		Authentication auth = null;
		if (tight_auth) {
			auth = new TightVNCAuthentication(socket, in, out, pass);
			tight = true;
		} else if (vnc_auth) {
			auth = new VNCAuthentication(socket, in, out, pass);
		} else if (none) {
			auth = new NoAuthentication(socket, in, out, null);
		}
		if (auth == null) {
			logger.printLn("No common authentication");
			dataOut.writeByte(-1);
			socket.close();
			return false;
		}
		dataOut.writeByte(auth.getSecurityId());
		boolean authenticated = auth.authenticate();
		
		if (!authenticated) {
			logger.printLn("Failed to authenticate");
			return false;
		}
		
		ClientInitMessage clientInit = new ClientInitMessage(socket, in, out);
		clientInit.sendMessage();
		
		if (tight) {
			logger.printLn("Connected via tight");
		}
		
		ServerInitMessage serverInit = new ServerInitMessage(socket, in, out, tight);
		serverInit.receiveMessage();
		name = serverInit.name;
		width = serverInit.framebufferWidth;
		height = serverInit.framebufferHeight;
		preferredFormat = serverInit.format;
		
		logger.printLn("Connected successfully to:");
		logger.printLn(serverInit.name);
		logger.printLn("Width: " + serverInit.framebufferWidth);
		logger.printLn("Height: " + serverInit.framebufferHeight);
		logger.printLn("Bits per pixel: " + preferredFormat.bitsPerPixel);
		logger.printLn("Depth: " + preferredFormat.depth);
		return true;
	}
	
	public void sendFormat() throws IOException {
		SetPixelFormatMessage pixelFormat = new SetPixelFormatMessage(socket, in, out);
		
		pixelFormat.format = preferredFormat;
		preferredFormat = preferredFormat;
		pixelFormat.sendMessage();
	}

	public void sendSetEncoding() throws IOException {
		SetEncodings encodings = new SetEncodings(socket, in, out);
		
		encodings.addEncodings(supportedEncodings);
		
		encodings.sendMessage();
	}
	
	public void sendFrameBufferUpdateRequest(boolean incremental) throws IOException {
		FramebufferUpdateRequest fbRequest = new FramebufferUpdateRequest(socket, in, out);
		fbRequest.incremental = (byte) (incremental ? 1 : 0);
		fbRequest.x = 0;
		fbRequest.y = 0;
		fbRequest.width = (short) width;
		fbRequest.height = (short) height;
		fbRequest.sendMessage();	
	}

	public void sendPointerUpdate() throws IOException {
		PointerPoint p = ui.getMouseManager().getLocalMouse();
		if (p != null) {
			PointerEvent pEvent = new PointerEvent(socket, in, out);
			pEvent.x = p.x;
			pEvent.y = p.y;
			pEvent.setClick(p.left, p.right, p.middle, p.mwUp, p.mwDown);
			pEvent.sendMessage();
		}
	}

	public void sendKeyboardUpdate() throws IOException {
		KeyboardUpdate k = ui.getKeyboardManager().getNext();
		if (k != null) {
			KeyEvent kEvent = new KeyEvent(socket, in, out);
			kEvent.key = k.key;
			kEvent.pressed = k.pressed;
			kEvent.sendMessage();
		}
	}
	
	public void readFrameBufferUpdate() throws IOException {
		FrameBufferUpdate update = new FrameBufferUpdate(socket, in, out, updateManager, preferredFormat, streams, supportedEncodings);
		update.receiveMessage();
	}
	
	public void disconnect() {
		ui.exit();
		try {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (dataOut != null) {
				dataOut.close();
			}
			if (dataIn != null) {
				dataIn.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}
}