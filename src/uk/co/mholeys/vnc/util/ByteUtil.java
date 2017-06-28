package uk.co.mholeys.vnc.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.co.mholeys.vnc.data.PixelFormat;

public class ByteUtil {

	public static int[] unsignedBytesToInts(byte[] bytes) {
		int[] ints = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			ints[i] = bytes[i] & 0xFF;
		}
		return ints;
	}
	
	public static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}
	
	public static String convertToBits(byte[] bytes) {
		int[] ints = unsignedBytesToInts(bytes);
		String s = "";
		for (int i = 0; i < ints.length; i++) {
			String bitString = Integer.toBinaryString(ints[i]);
			int length = bitString.length();
			if (length > 8) {
				bitString.substring(length-8, length);
			} else {
				for (int l = length; l < bytes.length*8; l++) {
					bitString = "0"+bitString;
				}
			}
			s += bitString + " ";
		}
		return s;
	}
	
	public static String convertToBits(byte b) {
		return convertToBits(new byte[] {b});
	}
/*	
	public static int bytesToInt(byte[] b, PixelFormat format, boolean reverse) {
		if (reverse) {
			//Reverse bytes
			format = format.clone().setBigEndianFlag(!format.bigEndianFlag);
		}
		System.out.println(reverse);
		return bytesToInt(b, format);
	}*/
	
	/*public static int bytesToInt(byte[] b, PixelFormat format) {
		byte[] bytes = new byte[4];
		if (b.length == 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		ByteBuffer buff = ByteBuffer.allocate(4);
		if (b.length == 1) {
			return (int) b[0] & 0xff;
		}
		if (b.length == 2) {
			ByteOrder bo = format.bigEndianFlag ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
			return (int)((ByteBuffer) ByteBuffer.allocate(2).put(b).order(bo).flip()).getShort() & 0xFFFF;
		}
		if (b.length == 3) {
			byte[] c = new byte[4];
			int offset = (format.bigEndianFlag ? 1 : 0); 
			System.arraycopy(b, 0, c, offset, b.length);
			b = c;
		}
		
		ByteOrder bo = !format.bigEndianFlag ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		buff.put(b);
		buff.order(bo);
		buff.flip();
		return buff.getInt();
	}*/
	
	public static int bytesToInt(byte[] b, PixelFormat format) {
		//ByteBuffer buff = ByteBuffer.wrap(b);
		//ByteOrder bo = !format.bigEndianFlag ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		//buff = buff.order(bo);
		//buff.flip();
		
		/*int result = 0;
		int diff = -1;
		int j = b.length-1;
		if (format.bigEndianFlag) {
			diff = -1;
			j = 0;
		}
		
		for (int i = 0; i < b.length;) {
			int f = b[i] & 0xFF;
			//System.out.println(Integer.toHexString(f));
			result |= (b[i] & 0xFF) << j * 8;
			i++;
			j += diff;
		}
		//System.out.println(Integer.toHexString(result));
		return result;*/
		
		
		/*if (format.bigEndianFlag) {
			if (b.length == 1) {
				int i = 0;
				i += (b[0] & 0xFF) << 24;
				return i;
			}
			if (b.length == 2) {
				int i = 0;
				i += (b[0] & 0xFF) << 24;
				i += (b[1] & 0xFF) << 16;
				return i;
			}
			if (b.length == 3) {
				int i = 0;
				i += (b[0] & 0xFF) << 24;
				i += (b[1] & 0xFF) << 16;
				i += (b[2] & 0xFF) << 8;
				return i;
			}
			if (b.length == 4) {
				int i = 0;
				i += (b[0] & 0xFF) << 24;
				i += (b[1] & 0xFF) << 16;
				i += (b[2] & 0xFF) << 8;
				i += (b[3] & 0xFF) << 0;
				return i;
			}
		} else {
			if (b.length == 1) {
				int i = 0;
				i += (b[0] & 0xFF) << 0;
				return i;
			}
			if (b.length == 2) {
				int i = 0;
				i += (b[0] & 0xFF) << 0;
				i += (b[1] & 0xFF) << 8;
				return i;
			}
			if (b.length == 3) {
				int i = 0;
				i += (b[0] & 0xFF) << 0;
				i += (b[1] & 0xFF) << 8;
				i += (b[2] & 0xFF) << 16;
				return i;
			}
			if (b.length == 4) {
				int i = 0;
				i += (b[0] & 0xFF) << 0;
				i += (b[1] & 0xFF) << 8;
				i += (b[2] & 0xFF) << 16;
				i += (b[3] & 0xFF) << 24;
				return i;
			}
		}*/
		/*int value = 0;
	    for (int i = 4-b.length; i < b.length; i++) {
	        int shift = (4 - 1 - i) * 8;
	        value += (b[i] & 0x000000FF) << shift;
	    }
	    return value;
		//return -1;
		 */

		byte[] intSized = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(intSized);
		System.arraycopy(b, 0, intSized, 0, b.length);
		ByteOrder bo = format.bigEndianFlag ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		bb = bb.order(bo);
		return bb.getInt();
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
	
	public static byte bitsToByte(boolean[] b, boolean reverse) {
		return bitsToByte(b, 0, 8, reverse);
	}
	
	public static byte bitsToByte(boolean[] b) {
		return bitsToByte(b, 0, 8, false);
	}
	
	public static byte bitsToByte(boolean[] b, int s, int e, boolean reverse) {
		int start = s;
		int end = e;
		if (reverse) {
			end = s;
			start = e;
		}
		byte value = 0;
		for (int i = start; (start < end ? (i < end) : (i > end)); i += ((start < end) ? 1 : -1)) {
			value |= (b[i] ? 1 : 0) << i;
		}
		return value;
	}
	
	public static int bitsToInt(boolean[] b, int s, int e) {
		int start = s;
		int end = e;
		int value = 0;
		int shift = 0;
		for (int i = 0; i < b.length ; i++) {
			if (i%8 >= start && i%8 < end) {
				value |= (b[i] ? 1 : 0) << shift;
				shift++;
			}
		}
		return value;
	}
	
	public static int bytesToInts(byte[] b, int s, int e, boolean reverseBytes) {
		int[] ints = unsignedBytesToInts(b);
		boolean[][] byteBits = new boolean[b.length][8];
		for (int i = 0; i < byteBits.length; i++) {
			byteBits[i] = byteToBits((byte) ints[i]);
		}
		boolean[] bits = new boolean[byteBits.length*8];
		for (int i = 0; i < byteBits.length; i++) {
			int pos = i;
			if (reverseBytes) {
				pos = byteBits.length-1-i;
			}
			System.arraycopy(byteBits[pos], 0, bits, i*8, 8);
		}
		return bitsToInt(bits, s, e);
	}
	
	public static boolean[] bytesToBits(byte[] b) {
		boolean[] bits = new boolean[b.length*8];
		for (int i = 0; i < b.length; i++) {
			boolean[] byteBits = byteToBits(b[i]);
			System.arraycopy(byteBits, 0, bits, i*8, 8);
		}
		return bits;
	}
	
	public static boolean[] byteToBits(byte b) {
		int ints = unsignedByteToInt(b);
		boolean[] bits = new boolean[8];
		String bitString = Integer.toBinaryString(ints);
		int length = bitString.length();
		if (length > bits.length) {
			bitString.substring(length-8, length);
		} else {
			for (int l = length; l < bits.length; l++) {
				bitString = "0"+bitString;
			}
		}
		char[] bitChars = bitString.toCharArray();
		for (int i = 0; i < bits.length; i++) {
			bits[i] = bitChars[bits.length-1-i] == '1'; 
		}
		/*for (int i = 0; i < bits.length; i++) {
			bits[i] = (ints & (1 << i))>>>i == 1;
		}*/
		return bits;
	}
	
	public static byte[] reverseBitsInBytes(byte[] bytes) {
		byte[] result = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result[i] = reverseBitsInByte(bytes[i]);
		}
		return result;
	}
	
	public static byte reverseBitsInByte(byte b) {
		byte result = 0;
		for (int i = 0; i < 8; i++) {
			result |= (b&(1<<i))>> i << 7-i;
		}
		return result;
	}
	
	public static Object[] reverse(Object[] arr) {
        List<Object> list = Arrays.asList(arr);
        Collections.reverse(list);
        return list.toArray();
    }
	
}
