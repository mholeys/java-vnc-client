package com.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mholeys.vnc.data.PixelFormat;

public class HextileEncoding extends Encode {
	
	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public HextileEncoding(int x, int y, int width, int height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = Math.abs(width);
		this.height = Math.abs(height);
		this.format = format;
		pixels = new int[this.width * this.height];
	}
	
	@Override
	public void readEncoding(InputStream in) throws IOException {
		//TODO Incomplete
		DataInputStream dataIn = new DataInputStream(in);
		int subencoding = dataIn.read();
		
		final int RAW = 1;
		final int BACKGROUND_SPECIFIED = 2;
		final int FOREGROUND_SPECIFIED = 4;
		final int ANY_SUB_RECTANGLE = 8;
		final int SUB_RECTANGLES_COLOURED = 16;
		
		byte[] backgroundPixel = new byte[format.bytesPerPixel];
		byte[] foregroundPixel = new byte[format.bytesPerPixel];
		
		byte[] subRectPixel = new byte[format.bytesPerPixel];
		
		int subRectangles = 0;
		int subRectX = 0;
		int subRectY = 0;
		int subRectWidth = 0;
		int subRectHeight = 0;
		
		if ((subencoding & BACKGROUND_SPECIFIED) > 0 ) {
			dataIn.read(backgroundPixel);
		}
		
		if ((subencoding & FOREGROUND_SPECIFIED) > 0 ) {
			dataIn.read(foregroundPixel);
		}
		
		if ((subencoding & ANY_SUB_RECTANGLE) > 0 ) {
			subRectangles = dataIn.read();
		}
		
		if ((subencoding & SUB_RECTANGLES_COLOURED) > 0 ) {
			dataIn.read(subRectPixel);
			int xy = dataIn.read();
			subRectX = (xy >> 1 & 0xF);
			subRectY = (xy & 0xF);
			int wh = dataIn.read();
			subRectWidth = (wh >> 1 & 0xF);
			subRectHeight = (wh & 0xF);
		}
		
		
	}

}

