package display;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import message.client.FramebufferUpdateRequest;
import message.client.SetEncodings;
import message.client.SetPixelFormatMessage;
import message.server.FrameBufferUpdate;
import data.Encoding;
import data.PixelFormat;
import data.PixelRectangle;

public class VNCCanvas implements Runnable {

	public VNCConnector connector;
	public FrameBuffer frameBuffer;
	
	public Socket socket;
	public OutputStream out;
	public InputStream in;
	public DataOutputStream dataOut;
	public DataInputStream dataIn;
	
	private boolean running = false;
	
	public VNCCanvas(VNCConnector connector) {
		this.connector = connector;
		frameBuffer = connector.getFrameBuffer();
		this.socket = connector.socket;
		this.in = connector.in;
		this.out = connector.out;
		this.dataIn = connector.dataIn;
		this.dataOut = connector.dataOut;
		running = true;
		
		Frame f = new Frame(frameBuffer);
		Thread fThread = new Thread(f);
		fThread.setName("Frame buffer thread");
		fThread.start();
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
		SetEncodings encodings = new SetEncodings(socket);
		encodings.encodings = new int[1];
		encodings.encodings[0] = Encoding.RAW_ENCODING.getStartID();
		
		try {
			pixelFormat.sendMessage();
			encodings.sendMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean shouldRequest = true;
		while (running) {
			try {
				if (shouldRequest) {
					FramebufferUpdateRequest fbRequest = new FramebufferUpdateRequest(socket);
					fbRequest.incremental = 1;
					fbRequest.width = (short) frameBuffer.width;
					fbRequest.height = (short) frameBuffer.height;
					fbRequest.sendMessage();
					shouldRequest = false;
				}
				int id = dataIn.readByte();
				switch (id) {
				case 0:
					FrameBufferUpdate update = new FrameBufferUpdate(socket, connector.format);
					PixelRectangle[] rectangles = (PixelRectangle[]) update.receiveMessage();
					for (PixelRectangle r : rectangles) {
						if (r.encode != null) {
							int[] pixels = r.encode.getPixels();
							for (int i = 0; i < r.width * r.height; i++) {
								frameBuffer.pixels[r.x + r.y * r.width + i] = pixels[i];  
							}
						}
					}
					shouldRequest = true;
					break;
				default:
					System.out.println(id);
					break;
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
