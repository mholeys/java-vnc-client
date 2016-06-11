package display;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import message.ClientInitMessage;
import message.ServerInitMessage;
import auth.Authentication;
import auth.NoAuthentication;
import auth.TightVNCAuthentication;
import auth.VNCAuthentication;
import data.PixelFormat;


public class VNCConnector {

	public Socket socket;
	public OutputStream out;
	public InputStream in;
	public DataOutputStream dataOut;
	public DataInputStream dataIn;
	
	private String password;
	
	public PixelFormat format;
	private int width, height;
	private FrameBuffer frameBuffer;
	private String name;
	
	//TODO add options for choice of auth
	public VNCConnector(String address, int port, String password) {
		this.password = password;
		try {
			int count = 10;
			boolean retry = true;
			while (retry) {
				socket = new Socket(InetAddress.getByName(address), port);
				out = socket.getOutputStream();
				dataOut = new DataOutputStream(out);
				in = socket.getInputStream();
				dataIn = new DataInputStream(in);
				if (handshake()) {
					retry = false;
				} else if (count > 0) {
					retry = true;
					count--;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean handshake() throws IOException {
		String version = "RFB 003.007\n";
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
		
		boolean tight = false;
		Authentication auth = null;
		if (tight_auth) {
			auth = new TightVNCAuthentication(socket, new String[] {password});
			tight = true;
		} else if (vnc_auth) {
			auth = new VNCAuthentication(socket, new String[] {password});
		} else if (none) {
			auth = new NoAuthentication(socket, null);
		}
		if (auth == null) {
			System.err.println("No common authentication");
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
	
	public FrameBuffer getFrameBuffer() {
		if (frameBuffer == null) {
			frameBuffer = new FrameBuffer(width, height, format);
		}
		return frameBuffer;
	}

	public static void main(String[] args) {
		Thread.currentThread().setName("Main");
		VNCConnector connector = new VNCConnector("192.168.0.2", 5901, "superuse");
		Frame frame = new Frame();
		VNCCanvas canvas = new VNCCanvas(connector, frame);
		Thread t = new Thread(canvas);
		t.setName("Canvas");
		t.start();
	}
	
}
