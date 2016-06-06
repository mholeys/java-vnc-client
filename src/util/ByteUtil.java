package util;

import java.nio.ByteBuffer;

public class ByteUtil {

	public static String convertToBits(byte[] bytes) {
		String s = "";
		for (int i = 0; i < bytes.length; i++) {
			for (int bit = 0; bit < 8; bit++) {
				s += ""+ ((bytes[i] & 1<<bit)>>bit);
			}
			s += " ";
		}
		return s;
	}
	
	public static int bytesToInt(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		int i = bb.getInt();
		return i;
	}
	
	public static short bytesToShort(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		short s = bb.getShort();
		return s;
	}
	
	public static long bytesToLong(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		long l = bb.getLong();
		return l;
	}
	
}
