package com.mholeys.vnc.message.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

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

public class FrameBufferUpdate extends ClientReceiveMessage {

	public IScreen screen;
	public PixelFormat format;
	public ZLibStream[] streams;
	
	public FrameBufferUpdate(Socket socket, IScreen screen, PixelFormat format, ZLibStream[] streams) {
		super(socket);
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
		dataIn.readByte();
		int rectangle = dataIn.readUnsignedShort();
		PixelRectangle[] rectangles = new PixelRectangle[rectangle];
		for (int i = 0; i < rectangle; i++) {
			PixelRectangle r = new PixelRectangle();
			r.x = dataIn.readShort();
			r.y = dataIn.readShort();
			r.width = dataIn.readShort();
			r.height = dataIn.readShort();
			r.encodingType = dataIn.readInt();
			if (Encoding.RAW_ENCODING.sameID(r.encodingType)) {
				System.out.println("Raw");
				r.encode = new RawEncoding(r.x, r.y, r.width, r.height, format);
			} else if (Encoding.COPY_RECT_ENCODING.sameID(r.encodingType)) {
				System.out.println("Copy encoding																																																");
				r.encode = new CopyRectEncoding(r.x, r.y, r.width, r.height, format);
			} else if (Encoding.ZLIB_ENCODING.sameID(r.encodingType)) {
				System.out.println("Zlib");
				r.encode = new ZLibEncoding(r.x, r.y, r.width, r.height, format, streams[4]);
			} else if (Encoding.TIGHT_ENCODING.sameID(r.encodingType)) {
				System.out.println("Tight");
				r.encode = new TightEncoding(r.x, r.y, r.width, r.height, format, streams);
			} else if (Encoding.CURSOR_PSEUDO_ENCODING.sameID(r.encodingType)) {
				System.out.println("Cursor");
				r.encode = new CursorPseudoEncoding(r.x, r.y, r.width, r.height, format);
			}
			if (r.encode != null) {
				r.encode.setScreen(screen);
				r.encode.readEncoding(in);
			}
			rectangles[i] = r;
		}
		return rectangles;
	}

}