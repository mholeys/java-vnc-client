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
		int compressionControl = dataIn.read();//.readUnsignedByte();
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
			//System.out.println("FILL");
			int color = readTightPixel(dataIn);
			screen.fillPixels(x, y, width, height, color);
			break;
		case JPEG:
			//System.out.println("JPEG");
			int length = readCompactInt(dataIn);
			byte[] jpegData = new byte[length];
			dataIn.readFully(jpegData);
			screen.drawJPEG(x, y, width, height, jpegData);
			break;
		default:
			int stream = (compressionControl & 0x30) >>> 4;
			int filterId = 0;
			if ((compressionControl & 0x40) >>> 6 == 1) {
				filterId = dataIn.readByte();
			}
			
			final int COPY = 0;
			final int PALETTE = 1;
			final int GRADIENT = 2;
			
			switch (filterId) {
			case PALETTE:
				//Decode palette filter
				int paletteSize = dataIn.readUnsignedByte()+1;
				System.out.println("Palette size: " + paletteSize);
				int[] palette = new int[256];
				for (int i = 0; i < paletteSize; i++) {
					palette[i] = readTightPixel(dataIn);
				}
				int paletteDataLength = paletteSize == 2 ?
						height * ((width + 7) / 8) :
						width * height;
				byte[] paletteData = new byte[paletteDataLength];
				paletteData = readCompressedData(dataIn, paletteDataLength, stream);
				screen.drawPalette(x, y, width, height, palette, paletteSize, paletteData);
				break;
			case GRADIENT:
				System.out.println("Gradient");
				
				break;
			case COPY:
				System.out.println("Copy");
				//Read compressed TightPixels
				byte[] copyData = readCompressedData(dataIn, width*height*3, stream);
				int[] pixels = convertDataToTightPixels(copyData, width*height, 3);
				//Decode copy filter
				//Read pixels
				screen.drawPixels(x, y, width, height, pixels);
				break;
			default: 
				break;
			}
			break;
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
		for (int i = 0; i < dataSize; i++) {
			byte[] p = new byte[4];
			System.arraycopy(data, i*size, p, 4-size, size);
			pixels[i] = ByteUtil.bytesToInt(p);
		}
		return pixels;
	}
	
}
