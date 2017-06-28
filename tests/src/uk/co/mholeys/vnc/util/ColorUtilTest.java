package src.uk.co.mholeys.vnc.util;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.mholeys.vnc.data.PixelFormat;
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
	
}
