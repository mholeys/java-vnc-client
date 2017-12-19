package uk.co.mholeys.vnc.util;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class ColorUtilTest {

	@Test
	public void RGB888Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 16;
		format.greenShift = 8;
		format.blueShift = 0;
		
		int colorIn = 0xF1F0E1;
		int colorExpectedOut = 0xF1F0E1;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void BGR888Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.blueShift = 16;
		format.greenShift = 8;
		format.redShift = 0;
		
		int colorIn = 0xF1F0E1;
		int colorExpectedOut = 0xE1F0F1;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void GBR888Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.greenShift = 16;
		format.blueShift = 8;
		format.redShift = 0;
		
		int colorIn = 0xF1F0E1;
		int colorExpectedOut = 0xE1F1F0;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void RBG888Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 16;
		format.blueShift = 8;
		format.greenShift = 0;
		
		int colorIn = 0xA1A2A3;
		int colorExpectedOut = 0xA1A3A2;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	

	@Test
	public void RGB332Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 5;
		format.redMax = 7;
		format.greenShift = 2;
		format.greenMax = 7;
		format.blueShift = 0;
		format.blueMax = 3;
		format.bitsPerPixel = 8;
		format.depth = 8;
		
		int colorIn = 0b11101110;
		int colorExpectedOut = 0xFF6DAA;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void BGR233Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 0;
		format.redMax = 7;
		format.greenShift = 3;
		format.greenMax = 7;
		format.blueShift = 6;
		format.blueMax = 3;
		format.bitsPerPixel = 8;
		format.depth = 8;
		
		int colorIn = 0b10000001;
		int colorExpectedOut = 0x2400AA;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void GBR323Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 0;
		format.redMax = 7;
		format.greenShift = 5;
		format.greenMax = 7;
		format.blueShift = 3;
		format.blueMax = 3;
		format.bitsPerPixel = 8;
		format.depth = 8;
		
		int colorIn = 0b10010001;
		int colorExpectedOut = 0x2491AA;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void RBG323Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 5;
		format.redMax = 7;
		format.greenShift = 0;
		format.greenMax = 7;
		format.blueShift = 3;
		format.blueMax = 3;
		format.bitsPerPixel = 8;
		format.depth = 8;
		
		int colorIn = 0b10001001;
		int colorExpectedOut = 0x912455;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void RGB565Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 11;
		format.redMax = 31;
		format.greenShift = 5;
		format.greenMax = 63;
		format.blueShift = 0;
		format.blueMax = 31;
		format.bitsPerPixel = 16;
		format.depth = 16;
		
		int colorIn = 0b1100010000000100;
		int colorExpectedOut = 0xC58120;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void BGR565Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 0;
		format.redMax = 31;
		format.greenShift = 5;
		format.greenMax = 63;
		format.blueShift = 11;
		format.blueMax = 31;
		format.bitsPerPixel = 16;
		format.depth = 16;
		
		int colorIn = 0b0010010000011000;
		int colorExpectedOut = 0xC58120;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void GBR655Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 0;
		format.redMax = 31;
		format.greenShift = 10;
		format.greenMax = 63;
		format.blueShift = 5;
		format.blueMax = 31;
		format.bitsPerPixel = 16;
		format.depth = 16;
		
		int colorIn = 0b1000000010011000;
		int colorExpectedOut = 0xC58120;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void RBG556Test() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 11;
		format.redMax = 31;
		format.greenShift = 0;
		format.greenMax = 63;
		format.blueShift = 6;
		format.blueMax = 31;
		format.bitsPerPixel = 16;
		format.depth = 16;
		
		int colorIn = 0b1100000100100000;
		int colorExpectedOut = 0xC58120;
		int colorOut = ColorUtil.convertTo8888ARGB(format, colorIn);
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void RGB888FromBytesTest() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.redShift = 16;
		format.greenShift = 8;
		format.blueShift = 0;
		
		byte[] colorIn = {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD};
		
		int colorExpectedOut = 0xFFFEFD;
		int colorOut = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(colorIn, format));
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
	@Test
	public void BGR888FromBytesTest() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.blueShift = 16;
		format.greenShift = 8;
		format.redShift = 0;
		
		byte[] colorIn = {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD};
		
		int colorExpectedOut = 0xFDFEFF;
		int colorOut = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(colorIn, format));
		
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(colorExpectedOut) + " got: " + Integer.toHexString(colorOut)); System.out.println();
		assertEquals(colorOut, colorExpectedOut);
	}
	
}
