package com.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.net.LogInputStream;
import com.mholeys.vnc.util.ByteUtil;

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
	
	
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(new LogInputStream(in));
		System.out.println("Reading compression control byte");
		int compressionControl = dataIn.readUnsignedByte();
		boolean[] bit = ByteUtil.byteToBits((byte) compressionControl);
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
		final int FILL = 0x80;
		final int JPEG = 0x90;
		int mode = compressionControl & 0xF0;
		switch (mode) {
		case FILL:
			System.out.println("FILL");
			int color = readTightPixel(dataIn);
			screen.fillPixels(x, y, width, height, color);
			break;
		case JPEG:
			System.out.println("JPEG");
			System.out.println("Reading length (compact)");
			int length = readCompactInt(dataIn);
			LogInputStream.print = false;
			byte[] jpegData = new byte[length];
			System.out.println("Reading data");
			dataIn.readFully(jpegData);
			LogInputStream.print = true;
			screen.drawJPEG(x, y, width, height, jpegData);
			break;
		default:
			int stream = (compressionControl & 0x10) | (compressionControl & 0x8);
			int filterId = dataIn.read();
			
			final int COPY = 0;
			final int PALETTE = 1;
			final int GRADIENT = 2;
			
			switch (filterId) {
			case PALETTE:
				//Decode palette filter
				System.out.println("Palette");
				System.out.println("Reading palette size");
				int paletteSize = dataIn.readUnsignedByte()+1;
				System.out.println("Palette size: " + paletteSize);
				int[] palette = new int[256];
				for (int i = 0; i < paletteSize; i++) {
					System.out.println("Reading color palette");
					palette[i] = readTightPixel(dataIn);
				}
				int paletteDataLength = paletteSize == 2 ?
						height * ((width + 7) / 8) :
						width * height;
				System.out.println("Palette data length: " + paletteDataLength);
				byte[] paletteData = new byte[paletteDataLength];
				System.out.println("Reading palette color data");
				dataIn.readFully(paletteData);
				screen.drawPalette(x, y, width, height, palette, paletteData);
				
				break;
			case GRADIENT:
				System.out.println("Gradient");
				
				break;
			case COPY:
				/*System.out.println("Copy");
				//Read compressed TightPixels
				byte[] copyData = readCompressedData(dataIn, width*height*3, stream);
				int[] pixels = convertDataToTightPixels(copyData, width*height, 3);
				screen.drawPixels(x, y, width, height, pixels);*/
				//Decode copy filter
				System.out.println("Copy");
				//Read pixels
				System.out.println("Reading copy data");
				byte[] p = readCompressedData(dataIn, width*height*3, stream);
				int[] pixels = new int[width*height];
				for (int i = 0; i < width*height; i++) {
					byte[] pixel = new byte[4];
					System.arraycopy(p, i*3, pixel, 1, 3);
					pixels[i] = ByteUtil.bytesToInt(pixel);
				}
				screen.drawPixels(x, y, width, height, pixels);
				break;
			default: 
				break;
			}
			break;
		}
	}
	
	public void readncoding(InputStream in) throws IOException {
		System.out.println("TIGHT " + width + ", " + height);
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
				byte[] pData = new byte[3];
				dataIn.read(pData);
				int pixel = ByteUtil.bytesToInt(pData);
				screen.fillPixels(x, y, width, height, pixel);
			} else if (bit[7] && !bit[6] && !bit[5] && bit[4]) {
				//Jpeg
				int length = readCompactInt(dataIn);
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
				byte[] p = readCompressedData(dataIn, width*height*3, stream);
				int[] pixels = new int[width*height];
				for (int i = 0; i < width*height; i++) {
					byte[] pixel = new byte[4];
					System.arraycopy(p, i*3, pixel, 1, 3);
					pixels[i] = ByteUtil.bytesToInt(pixel);
				}
				screen.drawPixels(x, y, width, height, pixels);
			} else if (palette) {
				//Decode palette filter
				System.out.println("Palette");
				int paletteSize = dataIn.readUnsignedByte()+1;
				System.out.println("Palette size: " + paletteSize);
				int[] paletteColours = new int[256];
				for (int i = 0; i < paletteSize; i++) {
					paletteColours[i] = readTightPixel(dataIn);
				}
				int paletteDataLength = paletteSize == 2 ?
						height * ((width + 7) / 8) :
						width * height;
				System.out.println("Palette data length: " + paletteDataLength);
				byte[] paletteData = new byte[paletteDataLength];
				dataIn.read(paletteData);
				screen.drawPalette(x, y, width, height, paletteColours, paletteData);
			} else if (gradient) {
				//Decode gradient filter
				System.out.println("Gradient");
				byte[] p = new byte[width*height];
				byte[] v = new byte[width*height];
				//TODO NO IDEA
				//Read pixels
				readCompressedData(dataIn, width*height, stream);
				/*int dataLength = readCompactInt(in);
				byte[] data = new byte[dataLength];
				dataIn.read(data);*/
			} else {
				//Failed to understand filter something is probably wrong
				System.out.println("Could not determine format of tight message");
			}
		}
	}
	
	public static int readTightPixel(DataInputStream dataIn) throws IOException {
		byte[] c = new byte[3];
		dataIn.readFully(c);
		byte[] b = new byte[4];
		System.arraycopy(c, 0, b, 1, 3);
		return ByteUtil.bytesToInt(b);
	}
	
	public static int readCompactInt(DataInputStream dataIn) throws IOException {
		int length = 0;
		byte b0 = dataIn.readByte();
		byte b1;
		byte b2;
		length = b0 & 0x7F;
		//System.out.println("b0 " + Integer.toBinaryString(b0) + " " + ((b0 & 0x80)>>>7) + " " + length);
		if ((b0 & 0x80)>>>7 != 0) {
			b1 = dataIn.readByte();
			length += (b1 & 0x7F)<<7;
			//System.out.println("b1 " + Integer.toBinaryString(b1) + " " + ((b1 & 0x80)>>>7) + " " + length);
			if (((b1 & 0x80)>>>7) != 0) {
				b2 = dataIn.readByte();
				length += b2 << 14;
				//System.out.println("b2 " + Integer.toBinaryString((b2 & 0x80)>>>7) + " " + ((b2 & 0x80)>>>7) + " " + length);
				if ((b2 & 0x80)>>>7 != 0) {
					return -1;
				}
			}
		}
		//System.out.println("L: " + length + " " + Integer.toBinaryString(length));
		return length;
	}

	public byte[] readCompressedData(DataInputStream dataIn, int length, int stream) throws IOException {
		byte[] data;
		byte[] p = new byte[length];
		if (length > 12) {
			int compressedLength = readCompactInt(dataIn);
			data = new byte[compressedLength];
			dataIn.readFully(data);
			streams[stream].inflater.setInput(data);
			try {
				streams[stream].inflater.inflate(p);
			} catch (DataFormatException e) {
				e.printStackTrace();
			}
		} else {
			dataIn.readFully(p);
		}
		return p;
	}
	
	public static int[] convertDataToTightPixels(byte[] data, int dataSize, int size) {
		int[] pixels = new int[dataSize];
		for (int i = 0; i < data.length; i+=size) {
			byte[] p = new byte[4];
			System.arraycopy(data, i, p, 4-size, size);
			pixels[i] = ByteUtil.bytesToInt(p);
		}
		return pixels;
	}
	
}
