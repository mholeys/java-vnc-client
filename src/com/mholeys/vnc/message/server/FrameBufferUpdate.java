	package com.mholeys.vnc.message.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.mholeys.vnc.data.Encoding;
import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.data.PixelRectangle;
import com.mholeys.vnc.display.IScreen;
import com.mholeys.vnc.encoding.CopyRectEncoding;
import com.mholeys.vnc.encoding.CursorPseudoEncoding;
import com.mholeys.vnc.encoding.RawEncoding;
import com.mholeys.vnc.encoding.TightEncoding;
import com.mholeys.vnc.encoding.ZLibEncoding;
import com.mholeys.vnc.encoding.ZLibStream;
import com.mholeys.vnc.log.Logger;

public class FrameBufferUpdate extends ClientReceiveMessage {

	public IScreen screen;
	public PixelFormat format;
	public ZLibStream[] streams;
	
	public FrameBufferUpdate(Socket socket, InputStream in, OutputStream out, IScreen screen, PixelFormat format, ZLibStream[] streams) {
		super(socket, in, out);
		this.screen = screen;
		this.format = format;
		this.streams = streams;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Object receiveMessage() throws IOException {
		Logger.logger.debugLn("Reading padding");
		dataIn.readByte();
		Logger.logger.debugLn("Reading number of rectangles");
		int rectangle = dataIn.readUnsignedShort();
		PixelRectangle[] rectangles = new PixelRectangle[rectangle];
		Logger.logger.verboseLn("Reading " + rectangle + " rectangles");
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
			if (Encoding.RAW_ENCODING.sameID(r.encodingType)) {
				r.encode = new RawEncoding(r.x, r.y, r.width, r.height, format);
			} else if (Encoding.COPY_RECT_ENCODING.sameID(r.encodingType)) {
				r.encode = new CopyRectEncoding(r.x, r.y, r.width, r.height, format);
			} else if (Encoding.ZLIB_ENCODING.sameID(r.encodingType)) {
				r.encode = new ZLibEncoding(r.x, r.y, r.width, r.height, format, streams[4]);
			} else if (Encoding.TIGHT_ENCODING.sameID(r.encodingType)) {
				r.encode = new TightEncoding(r.x, r.y, r.width, r.height, format, streams);
			} else if (Encoding.CURSOR_PSEUDO_ENCODING.sameID(r.encodingType)) {
				r.encode = new CursorPseudoEncoding(r.x, r.y, r.width, r.height, format);
			}
			if (r.encode != null) {
				r.encode.setScreen(screen);
				r.encode.readEncoding(in);
			} else {
				Logger.logger.printLn("Does not support this encoding");
			}
			rectangles[i] = r;
		}
		return rectangles;
	}

}