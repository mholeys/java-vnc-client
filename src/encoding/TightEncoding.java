package encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import util.ByteUtil;
import data.PixelFormat;

public class TightEncoding extends Encode {

	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	public ZLibStream[] streams;
	
	public TightEncoding(int x, int y, int width, int height, PixelFormat format, ZLibStream[] streams) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.format = format;
		this.streams = streams;
	}
	
	@Override
	public void readEncoding(InputStream in) throws IOException {
		System.out.println("TIGHT");
		DataInputStream dataIn = new DataInputStream(in);
		byte compressionControl = dataIn.readByte();
		boolean[] bit = ByteUtil.byteToBits(compressionControl);
		if (bit[0]) {
			streams[0].inflater.reset();
		}
		if (bit[1]) {
			streams[1].inflater.reset();
		}
		if (bit[2]) {
			streams[2].inflater.reset();
		}
		if (bit[3]) {
			streams[3].inflater.reset();
		}
		if (bit[7]) {
			if (bit[7] && !bit[6] && !bit[5] && !bit[4]) {
				//Fill
				System.out.println("Fill");
				byte[] data = new byte[width*height*3];
				dataIn.read(data);
				int[] pixels = new int[width*height];
				for (int i = 0; i < width*height; i++) {
					byte[] p = new byte[3];
					System.arraycopy(data, i*3, p, 0, 3);
					pixels[i] = ByteUtil.bytesToInt(p);
				}
				screen.drawPixels(x, y, width, height, pixels);
			} else if (bit[7] && !bit[6] && !bit[5] && bit[4]) {
				//Jpeg
				int length = readCompactInt(in, 3);
				System.out.println("TIGHT JPEG LENGTH: " + length);
				//Read JFIF stream and convert to pixel data
				byte[] jpegData = new byte[length];
				dataIn.read(jpegData);
				screen.drawJPEG(x, y, width, height, jpegData);
			} else {
				//Problem
				System.out.println("Wrong compressionbyte format");
			}
		} else {
			//Basic
			boolean copy = false, palette = false, gradient = false;
			int stream = ((bit[5] ? 1 : 0) << 1) & ((bit[4] ? 1 : 0) << 1);
			System.out.println("Stream: " + stream);
			if (bit[6]) {
				int filterId = dataIn.read();
				if (filterId == 0) {
					copy = true;
				} else if (filterId == 1) {
					palette = true;
				} else if (filterId == 2) {
					gradient = true;
				}
			} else {
				copy = true;
			}
			if (copy) {
				//Decode copy filter
				System.out.println("Copy");
				//Read pixels
				int dataLength = readCompactInt(in, 3);
				byte[] data = new byte[dataLength];
				dataIn.read(data);
				byte[] p = new byte[width*height*3];
				if (dataLength > 12) {
					streams[stream].inflater.setInput(data);
					try {
						streams[stream].inflater.inflate(p);
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
				} else {
					System.arraycopy(data, 0, p, 0, data.length);
				}
				int[] pixels = new int[width*height];
				for (int i = 0; i < width*height; i++) {
					byte[] pixel = new byte[3];
					System.arraycopy(p, i*3, p, 0, 3);
					pixels[i] = ByteUtil.bytesToInt(pixel);
				}
				screen.drawPixels(x, y, width, height, pixels);
			} else if (palette) {
				//Decode palette filter
				System.out.println("Palette");
				//Read number of colours
				int number = dataIn.readUnsignedByte()+1;
				int bitsPerPixel = 8;
				if (number == 2) {
					bitsPerPixel = 1;
				}
				//Read palette 
				byte[] colors = new byte[number];
				dataIn.read(colors);
				//TODO add 1 bit mode
				//Read pixels
				int dataLength = readCompactInt(in, 3);
				byte[] compressedData = new byte[dataLength];
				streams[stream].inflater.setInput(compressedData);
				byte[] data = new byte[width*height];
				try {
					streams[stream].inflater.inflate(data);
				} catch (DataFormatException e) {
					e.printStackTrace();
				}
				int[] pixels = new int[width*height];
				for (int i = 0; i < width*height; i++) {
					pixels[i] = ByteUtil.unsignedByteToInt(data[i]);
				}
				screen.drawPixels(x, y, width, height, pixels);
			} else if (gradient) {
				//Decode gradient filter
				System.out.println("Gradient");
				byte[] p = new byte[width*height];
				byte[] v = new byte[width*height];
				//TODO NO IDEA
				//Read pixels
				int dataLength = readCompactInt(in, 3);
				byte[] data = new byte[dataLength];
				dataIn.read(data);
			} else {
				//Failed to understand filter something is probably wrong
				System.out.println("Could not determine format of tight message");
			}
		}
	}
	
	private int readCompactInt(InputStream in, int bytes) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		if (bytes > 4) {
			bytes = 4;
		} else if (bytes < 1) {
			bytes = 1;
			return dataIn.readUnsignedByte();
		}
		boolean[] lengthBits;
		byte[] lengthBytes = new byte[0];
		for (int i = 0; i < bytes; i++) {
			byte l = dataIn.readByte();
			System.out.println(l);
			lengthBits = ByteUtil.byteToBits(l);
			byte[] nLength = new byte[lengthBytes.length+1];
			System.arraycopy(lengthBytes, 0, nLength, 0, lengthBytes.length);
			nLength[lengthBytes.length] = l;
			lengthBytes = nLength;
			if (!lengthBits[0]) {
				break;
			}
		}
		int length = ByteUtil.bytesToInts(lengthBytes, 0, 7, false);
		return length;
	}

}
