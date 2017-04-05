package com.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.util.ByteUtil;
import com.mholeys.vnc.util.ColorUtil;

public class RREEncoding extends Encode {

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public RREEncoding(int x, int y, int width, int height, PixelFormat format) {
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
		int subRectangles = dataIn.readInt();
		byte[] backgroundPixel = new byte[format.bytesPerPixel];
		dataIn.read(backgroundPixel);
		render.drawFill(x, y, width, height, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(backgroundPixel, format)));
		
		for (int i = 0 ; i < subRectangles; i++) {
			byte[] pixel = new byte[format.bytesPerPixel];
			dataIn.read(pixel);
			short x = dataIn.readShort();
			short y = dataIn.readShort();
			short width = dataIn.readShort();
			short height = dataIn.readShort();
			render.drawFill(this.x + x, this.y + y, width, height, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format)));
		}
	}

}
