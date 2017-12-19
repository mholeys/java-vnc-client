package uk.co.mholeys.vnc.util;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.util.ByteUtil;

public class ByteUtilTest {

	@Test
	public void testBytesToIntSize2LittleEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = false;
		
		byte[] input = {(byte) 0xA1, (byte) 0xB3};
		int expected = 0xB3A1;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println();
		assertEquals(output, expected);
	}

	@Test
	public void testBytesToIntSize3LittleEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = false;
		
		byte[] input = {(byte) 0x23, (byte) 0x22, (byte) 0x21};
		int expected = 0x232221;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println();
		assertEquals(output, expected);
	}
	
	@Test
	public void testBytesToIntSize4LittleEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = false;
		
		byte[] input = {(byte) 0xF3, (byte) 0xF2, (byte) 0xF1, (byte) 0xF0};
		int expected = 0xF0F1F2F3;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println();
		assertEquals(output, expected);
	}
	
	@Test
	public void testBytesToIntSize2BigEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = true;
		
		byte[] input = {(byte) 0xA1, (byte) 0xB3};
		int expected = 0xA1B3;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println();
		assertEquals(output, expected);
	}

	@Test
	public void testBytesToIntSize3BigEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = true;
		
		byte[] input = {(byte) 0x23, (byte) 0x22, (byte) 0x21};
		int expected = 0x212223;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println();
		assertEquals(output, expected);
	}
	
	@Test
	public void testBytesToIntSize4BigEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = true;
		
		byte[] input = {(byte) 0xF3, (byte) 0xF2, (byte) 0xF1, (byte) 0xF0};
		int expected = 0xF3F2F1F0;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println();
		assertEquals(output, expected);
	}
	
	@Test
	public void testBytesToIntFromIntLittleEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = false;
		
		byte[] input = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(0xF0F1F2F3).array();
		int expected = 0xF0F1F2F3;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println();
		assertEquals(output, expected);
	}
	
	@Test
	public void testBytesToIntFromIntBigEndian() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT.clone();
		format.bigEndianFlag = true;
		
		byte[] input = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(0xF0F1F2F3).array();
		int expected = 0xF0F1F2F3;
		
		int output = ByteUtil.bytesToInt(input, format);
		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + "\nExpected: " + Integer.toHexString(expected) + " got: " + Integer.toHexString(output)); System.out.println(); 
		assertEquals(output, expected);
	}

}
