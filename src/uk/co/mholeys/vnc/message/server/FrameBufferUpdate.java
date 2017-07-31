	package uk.co.mholeys.vnc.message.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.data.Encoding;
import uk.co.mholeys.vnc.data.EncodingSettings;
import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.display.UpdateManager;
import uk.co.mholeys.vnc.encoding.ZLibStream;
import uk.co.mholeys.vnc.log.Logger;

public class FrameBufferUpdate extends ClientReceiveMessage {

	public UpdateManager updateManager;
	public PixelFormat format;
	public ZLibStream[] streams;
	public EncodingSettings encodings;
	
	public FrameBufferUpdate(Socket socket, InputStream in, OutputStream out, UpdateManager updateManager, PixelFormat format, ZLibStream[] streams, EncodingSettings encodings) {
		super(socket, in, out);
		this.updateManager = updateManager;
		this.format = format;
		this.streams = streams;
		this.encodings = encodings;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Object receiveMessage() throws IOException {
		Logger.logger.verboseLn("EndOfContinuousUpdates message received");
		
		Logger.logger.debugLn("Reading padding");
		dataIn.readByte();
		Logger.logger.debugLn("Reading number of rectangles");
		int rectangle = dataIn.readUnsignedShort();
		PixelRectangle[] rectangles = new PixelRectangle[rectangle];
		Logger.logger.debugLn("Reading " + rectangle + " rectangles");
		for (int i = 0; i < rectangle; i++) {
			PixelRectangle r = new PixelRectangle();
			Logger.logger.debugLn("Reading x");
			r.x = dataIn.readShort();
			Logger.logger.debugLn("x: " + r.x);
			Logger.logger.debugLn("Reading y");
			r.y = dataIn.readShort();
			Logger.logger.debugLn("y: " + r.y);
			Logger.logger.debugLn("Reading width");
			r.width = dataIn.readShort();
			Logger.logger.debugLn("width: " + r.width);
			Logger.logger.debugLn("Reading height");
			r.height = dataIn.readShort();
			Logger.logger.debugLn("height: " + r.height);
			Logger.logger.debugLn("Reading encoding type");
			r.encodingType = dataIn.readInt();
			for (Encoding e : encodings.getEncodings()) {
				if (e.sameID(r.encodingType)) {
					r.encode = e.getDecoder(r, format, streams);
				}		
			}
			if (r.encode != null) {
				r.encode.setRender(updateManager);
				r.encode.readEncoding(in);
			} else {
				Logger.logger.printLn("Does not support this encoding " + r.encodingType);
			}
			rectangles[i] = r;
		}
		updateManager.setReady();
		return rectangles;
	}

}