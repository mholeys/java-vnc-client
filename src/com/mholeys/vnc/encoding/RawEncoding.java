package com.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.util.ByteUtil;

public class RawEncoding extends Encode {

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public RawEncoding(int x, int y, int width, int height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = Math.abs(width);
		this.height = Math.abs(height);
		this.format = format;
		pixels = new int[this.width * this.height];
	}
		 
	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i = 0; i < width * height; i++) {
			byte[] pixel = new byte[format.bitsPerPixel/8];
			dataIn.read(pixel);
			// TODO add colour shifting based on format
			pixels[i] = ByteUtil.bytesToInt(pixel);
		}
		screen.drawPixels(x, y, width, height, pixels);
	}

}
