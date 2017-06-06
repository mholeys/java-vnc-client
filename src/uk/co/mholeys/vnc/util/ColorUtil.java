package uk.co.mholeys.vnc.util;

import java.nio.ByteBuffer;

import uk.co.mholeys.vnc.data.PixelFormat;

public class ColorUtil {
	
	public static void main(String[] args) {
		int cIn = 0xf6;
		PixelFormat format = new PixelFormat()
				.setBitsPerPixel((byte) 8)
				.setDepth((byte) 8)
				.setBigEndianFlag(false)
				.setTrueColorFlag(true)
				.setRedMax((byte) 7)
				.setGreenMax((byte) 7)
				.setBlueMax((byte) 3)
				.setRedShift((byte) 0)
				.setGreenShift((byte) 3)
				.setBlueShift((byte) 6);
		int cOut = convertTo8888ARGB(format, cIn);
		System.out.println(Integer.toHexString(cOut));
		System.out.println(Integer.toBinaryString(cOut));
		System.out.println(cOut);
		
		int test = 0x0000FF00;
		byte[] testIntIn = ByteBuffer.allocate(4).putInt(test).array();
		byte[] testIntOut = new byte[3];
		System.arraycopy(testIntIn, 0, testIntOut, 0, 3);
		int out = ByteBuffer.allocate(4).put(testIntOut).getInt();
		System.out.println(Integer.toHexString(out));
	}

	public static int convertTo8888ARGB(PixelFormat format, int pixel) {
		if (!format.bigEndianFlag) {
			pixel = Integer.reverseBytes(pixel);
		}
		int mask = 0;
		for (int i = 0; i < format.depth; i++) {
			mask = mask << 1;
			mask += 1;
		}
		pixel = pixel & mask;
		int red = (int)(((pixel >>> format.redShift & format.redMax) / (double)format.redMax) * 255);
		int green = (int)(((pixel >>> format.greenShift & format.greenMax) / (double)format.greenMax) * 255);
		int blue = (int)(((pixel >>> format.blueShift & format.blueMax) / (double)format.blueMax) * 255);
		int color = (red << 16) | (green << 8) | blue;
		return color;
	}
	
}