package com.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.log.Logger;
import com.mholeys.vnc.util.ByteUtil;
import com.mholeys.vnc.util.ColorUtil;

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
		for (int i = 0; i < width * height; i++) {
			byte[] pixel = new byte[format.bytesPerPixel];
			Logger.logger.debugLn("Reading pixel");
			dataIn.read(pixel);
			int p = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format));
			pixels[i] = p;
		}
		screen.drawPixels(x, y, width, height, pixels);
	}

}
