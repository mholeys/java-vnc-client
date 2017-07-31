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
import uk.co.mholeys.vnc.message.client.SetPixelFormat;
import uk.co.mholeys.vnc.message.server.FrameBufferUpdate;

/**
 * TODO:
 * @author Matthew Holey
 *
 */
public class VNCProtocol implements Runnable {

	/** Exit status code for when a connection fails when the retry limit is reached */
	public static final int CONNECTION_ATTEMPT_LIMIT_HIT_EXIT = 567;
	
	/** Header id for the frame buffer update message */
	public static final int FRAME_BUFFER_UDPATE = 0;
	/** Number of times to attempt to connect to the server */
	public static final int RETRY_LIMIT = 5;
	
	/** The address of the vnc server */
	public InetAddress address;
	/** The port that the vnc server is running on */
	public int port;
	
	/** The client's socket with the vnc server */
	public Socket socket;
	/** The connection's output stream */
	public OutputStream out;
	/** The connection's input stream */
	public InputStream in;
	/** The connection's output stream (DataOutputStream for ease of use) */
	public DataOutputStream dataOut;
	/** The connection's input stream (DataInputStream for ease of use) */
	public DataInputStream dataIn;
	
	/** 
	 * The encodings that the client supports.
	 * These will be sent to the server to say what the client 
	 * can support.
	 */
	public EncodingSettings supportedEncodings;
	
	public UpdateManager updateManager;
	
	/** The password holder/getter to allow the user to enter their password */
	public IPasswordRequester password;
	
	/** The client's preferred "pixel" format */
	public PixelFormat preferredFormat;
	/** The server's preferred "pixel" format */
	public PixelFormat serverPreferredFormat;
	/** The width of the server's display */
	public int width;
	/** The height of the server's display */
	public int height;
	/** The client's ui that will be drawing the server's display and provide input */
	public IUserInterface ui;
	/** The server's "name" something like "User's Desktop (Address:port)" */
	public String name;
	/** The ZLib compression stream used to encode/decode the data */
	public ZLibStream[] streams = new ZLibStream[5];
	
	/** The logger that all the normal/verbose/debug output will be sent */
	public Logger logger;
	
	/** Thread running status to kill the client */
	private boolean running = false;
	
	/**
	 * TODO:
	 * @param connection
	 * @param ui
	 * @param logger
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public VNCProtocol(IConnectionInformation connection, IUserInterface ui, Logger logger) throws UnknownHostException, IOException {
		this.address = connection.getAddress();
		this.port = connection.getPort();
		this.password = connection.getPasswordRequester();
		this.ui = ui;
		this.logger = logger;
		// Ensure that we have a supported/preferred pixel format
		if (connection.hasPrefferedFormat()) {
			this.preferredFormat = connection.getPrefferedFormat();
		}
		// Ensure that we have a list of supported/preferred encodings
		if (connection.hasPrefferedEncoding()) {
			this.supportedEncodings = connection.getPrefferedEncoding();
			if (this.supportedEncodings == null) {
				this.supportedEncodings = EncodingSettings.DEFAULT_ENCODINGS;				
			}
		} else {
			this.supportedEncodings = EncodingSettings.DEFAULT_ENCODINGS;
		}
		// Setup the thread to be ready to run
		running = true;
	}
	
	/**
	 * TODO:
	 * @param address
	 * @param port
	 * @throws IOException
	 */
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
				System.exit(CONNECTION_ATTEMPT_LIMIT_HIT_EXIT);
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
		logger.detailedLn("Invalid " + invalid);
		logger.detailedLn("None " + none);
		logger.detailedLn("VNC " + vnc_auth);
		logger.detailedLn("TightVNC " + tight_auth);
		logger.detailedLn("RealVNC " + realvnc);
		
		String pass = password.getPassword();
		
		boolean tight = false;
		Authentication auth = null;
		if (tight_auth) {
			auth = new TightVNCAuthentication(socket, in, out, pass);
			tight = true;
		} else if (vnc_auth) {
			auth = new VNCAuthentication(socket, in, out, pass);
		} else if (none) {
			auth = new NoAuthentication(socket, in, out);
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
			logger.verboseLn("Connected via tight");
		}
		
		ServerInitMessage serverInit = new ServerInitMessage(socket, in, out, tight);
		serverInit.receiveMessage();
		name = serverInit.name;
		width = serverInit.framebufferWidth;
		height = serverInit.framebufferHeight;
		serverPreferredFormat = serverInit.format;
		ui.setServerFormat(serverPreferredFormat);
		
		logger.printLn("Connected successfully to:");
		logger.printLn(serverInit.name);
		logger.detailedLn("Width: " + serverInit.framebufferWidth);
		logger.detailedLn("Height: " + serverInit.framebufferHeight);
		logger.detailedLn("BigEndian: " + serverPreferredFormat.bigEndianFlag);
		logger.detailedLn("Bits per pixel: " + serverPreferredFormat.bitsPerPixel);
		logger.detailedLn("Depth: " + serverPreferredFormat.depth);
		
		logger.detailedLn("Red colour offset: " + serverPreferredFormat.redShift);
		logger.detailedLn("Green colour offset: " + serverPreferredFormat.greenShift);
		logger.detailedLn("Blue colour offset: " + serverPreferredFormat.blueShift);
		
		logger.detailedLn("Red colour max: " + serverPreferredFormat.redMax);
		logger.detailedLn("Green colour max: " + serverPreferredFormat.greenMax);
		logger.detailedLn("Blue colour max: " + serverPreferredFormat.blueMax);
		
		// Use the server's format if the client hasn't requested one
		if (preferredFormat == null) {
			//preferredFormat = serverPreferredFormat;
		}
		return true;
	}
	
	public void sendFormat() throws IOException {
		if (preferredFormat != null) {
		SetPixelFormat pixelFormat = new SetPixelFormat(socket, in, out);
		pixelFormat.format = preferredFormat;
			pixelFormat.sendMessage();
		}
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
		FrameBufferUpdate update = new FrameBufferUpdate(socket, in, out, updateManager, serverPreferredFormat, streams, supportedEncodings);
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
