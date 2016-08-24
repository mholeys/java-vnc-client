package com.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.log.Logger;

public class CopyRectEncoding extends Encode {

	public short x, y;
	public short width, height;
	public PixelFormat pixelFormat;
	public int[] pixels;
	private short xSrc;
	private short ySrc;
	
	public CopyRectEncoding(short x, short y, short width, short height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.pixelFormat = format;
	}

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		Logger.logger.debugLn("Reading source pos x");
		xSrc = dataIn.readShort();
		Logger.logger.debugLn("Reading source pos y");
		ySrc = dataIn.readShort();
		screen.copyPixels(xSrc, ySrc, width, height, x, y);
	}

}
