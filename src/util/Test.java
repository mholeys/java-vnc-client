package util;

import java.io.IOException;
import java.net.Socket;

import auth.VNCAuthentication;

public class Test {

	public static void main(String[] args) throws IOException {
		int b0 = 0x90;
		int b1 = 0x4E;
		System.out.println(Integer.toBinaryString(b0));
		boolean[] testIn0 = ByteUtil.byteToBits((byte) b0);
		boolean[] testIn1 = ByteUtil.byteToBits((byte) b1);
		System.out.println(ByteUtil.convertToBits((byte)b0));
		System.out.println(ByteUtil.convertToBits((byte)b1));
		boolean[] testIn = new boolean[testIn0.length + testIn1.length];
		System.arraycopy(testIn0, 0, testIn, 0, 8);
		System.arraycopy(testIn1, 0, testIn, 8, 8);
		//byte testOut0 = ByteUtil.bitsToByte(testIn0, 1, 7, false);
		//byte testOut1 = ByteUtil.bitsToByte(testIn1, 1, 7, false);
		int testOut = ByteUtil.bitsToInt(testIn, 0, 7);
		System.out.println(testOut);
		System.out.println(Integer.toBinaryString(testOut));
		
		
		byte[] b = new byte[] {(byte) 0x90, 0x4E};
		System.out.println(Integer.toBinaryString(ByteUtil.bytesToInts(b, 0, 7, false)));
		
		
		
		System.out.println("Clear");
		System.out.println(ByteUtil.convertToBits(ByteUtil.reverseBitsInByte((byte) 0xA)));
		
	}
	
}
