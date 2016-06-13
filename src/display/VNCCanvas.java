package display;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import org.omg.CORBA.FREE_MEM;

import message.client.EnableContinuousUpdates;
import message.client.FramebufferUpdateRequest;
import message.client.PointerEvent;
import message.client.SetEncodings;
import message.client.SetPixelFormatMessage;
import message.server.FrameBufferUpdate;
import data.Encoding;
import data.PixelFormat;
import data.PixelRectangle;
import data.PointerPoint;

public class VNCCanvas implements Runnable {

	public VNCConnector connector;
	public FrameBuffer frameBuffer;
	public IFrame frame;
	
	public Socket socket;
	public OutputStream out;
	public InputStream in;
	public DataOutputStream dataOut;
	public DataInputStream dataIn;
	
	
	private boolean running = false;
	
	public VNCCanvas(VNCConnector connector, IFrame frame) {
		this.connector = connector;
		frameBuffer = connector.getFrameBuffer();
		this.socket = connector.socket;
		this.in = connector.in;
		this.out = connector.out;
		this.dataIn = connector.dataIn;
		this.dataOut = connector.dataOut;
		running = true;
		this.frame = frame;
		this.frame.setFrameBuffer(frameBuffer);
		this.frame.start();
	}

	@Override
	public void run() {
		//Setup
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
		
		EnableContinuousUpdates enable = new EnableContinuousUpdates(socket);
		enable.enable = true;
		enable.width = (short) frameBuffer.width;
		enable.height = (short) frameBuffer.height;
		
		SetEncodings encodings = new SetEncodings(socket);
		encodings.encodings = new int[2];
		encodings.encodings[0] = Encoding.RAW_ENCODING.getStartID();
		encodings.encodings[1] = Encoding.ZLIB_ENCODING.getStartID();
		//encodings.encodings[1] = Encoding.COPY_RECT_ENCODING.getStartID();
		
		try {
			pixelFormat.sendMessage();
			encodings.sendMessage();
			enable.sendMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean shouldRequest = true;
		boolean first = true;
		while (running) {
			try {
				//Send a request for changes of whole screen
				if (shouldRequest) {
					FramebufferUpdateRequest fbRequest = new FramebufferUpdateRequest(socket);
					fbRequest.incremental = (byte) (first ? 0 : 1);
					fbRequest.width = (short) frameBuffer.width;
					fbRequest.height = (short) frameBuffer.height;
					fbRequest.sendMessage();
					shouldRequest = false;
					first = false;
				}
				int id = dataIn.readByte();
				switch (id) {
				case 0:
					FrameBufferUpdate update = new FrameBufferUpdate(socket, connector.format);
					PixelRectangle[] rectangles = (PixelRectangle[]) update.receiveMessage();
					for (PixelRectangle r : rectangles) {
						if (r.encode != null) {
							frameBuffer.handleFrameBufferUpdate(r.x, r.y, r.width, r.height, r.encode);
						}
					}
					shouldRequest = true;
					break;
					default:
						System.out.println(id);
					break;
				}
				// Check to see if mouse has moved
				if (frame.sendPointer()) {
					PointerPoint p = frame.getLocalPointer();
					if (p != null) {
						PointerEvent pEvent = new PointerEvent(socket);
						pEvent.x = p.x;
						pEvent.y = p.y;
						pEvent.setClick(p.left, p.right);
						pEvent.sendMessage();
						System.out.println("Sent mouse");
					}
				}
			} catch (IOException e) {
				if (e instanceof EOFException) {
					System.out.println("Server closed connection.");
					System.exit(0);
				}
				e.printStackTrace();
			}

		}
	}

}

