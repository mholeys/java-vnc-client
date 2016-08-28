package com.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.log.Logger;
import com.mholeys.vnc.net.LogInputStream;
import com.mholeys.vnc.util.ByteUtil;
import com.mholeys.vnc.util.ColorUtil;

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
		DataInputStream dataIn = new DataInputStream(in);
		Logger.logger.debugLn("Reading compression control byte");
		int compressionControl = dataIn.read();
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
			Logger.logger.verboseLn("Fill mode");
			Logger.logger.debugLn("Reading tight pixel for fill");
			int color = readTightPixel(dataIn, format);
			screen.fillPixels(x, y, width, height, color);
			break;
		case JPEG:
			Logger.logger.verboseLn("JPEG mode");
			Logger.logger.debugLn("Reading length of jpeg data");
			int length = readCompactInt(dataIn);
			byte[] jpegData = new byte[length];
			Logger.logger.debugLn("Reading jpeg JFIF data");
			dataIn.readFully(jpegData);
			screen.drawJPEG(x, y, width, height, jpegData);
			break;
		default:
			int stream = (compressionControl & 0x30) >>> 4;
			int filterId = 0;
			if ((compressionControl & 0x40) >>> 6 == 1) {
				Logger.logger.debugLn("Reading compression control byte");
				filterId = dataIn.readByte();
			}
			
			final int COPY = 0;
			final int PALETTE = 1;
			final int GRADIENT = 2;
			
			switch (filterId) {
			case PALETTE:
				//Decode palette filter
				Logger.logger.debugLn("Reading palette size");
				int paletteSize = dataIn.readUnsignedByte()+1;
				Logger.logger.verboseLn("Palette Size: " + paletteSize);
				int[] palette = new int[256];
				for (int i = 0; i < paletteSize; i++) {
					Logger.logger.debugLn("Reading palette pixel");
					palette[i] = readTightPixel(dataIn, format);
				}
				int paletteDataLength = paletteSize == 2 ?
						height * ((width + 7) / 8) :
						width * height;
				byte[] paletteData = new byte[paletteDataLength];
				Logger.logger.debugLn("Reading compressed palette data");
				paletteData = readCompressedData(dataIn, paletteDataLength, stream);
				screen.drawPalette(x, y, width, height, palette, paletteSize, paletteData);
				break;
			case GRADIENT:
				Logger.logger.verboseLn("Gradient. No code. This will cause problems if this is reached");
				System.exit(444);
				break;
			case COPY:
				Logger.logger.verboseLn("Copy mode");
				//Read compressed TightPixels
				Logger.logger.debugLn("Reading copy mode TPixels");
				int byteDataSize = format.bytesPerTPixel; 
				byte[] copyData = readCompressedData(dataIn, width*height*byteDataSize, stream);
				//Decode copy filter
				int[] pixels = convertDataToTightPixels(copyData, width*height, format);
				//Read pixels
				screen.drawPixels(x, y, width, height, pixels);
				break;
			default: 
				break;
			}
			break;
		}
	}
	
	public static int readTightPixel(DataInputStream dataIn, PixelFormat format) throws IOException {
		byte[] b = new byte[format.bytesPerTPixel];
		dataIn.readFully(b);
		return ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(b, format));
	}
	
	public static int readCompactInt(DataInputStream dataIn) throws IOException {
		int b = dataIn.readUnsignedByte();
		int size = b & 0x7F;
		if ((b & 0x80) != 0) {
			b = dataIn.readUnsignedByte();
			size += (b & 0x7F) << 7;
			if ((b & 0x80) != 0) {
				size += dataIn.readUnsignedByte() << 14;
			}
		}
		return size;
		
		
		/*int length = 0;
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
		return length;*/
	}

	public byte[] readCompressedData(DataInputStream dataIn, int length, int stream) throws IOException {
		byte[] data;
		byte[] p = new byte[length];
		if (length < 12) {
			Logger.logger.debugLn("Reading data as uncompressed because length (" + length  + ") was less than 12");
			dataIn.readFully(p);
		} else {
			Logger.logger.debugLn("Reading size of compressed data");
			int compressedLength = readCompactInt(dataIn);
			data = new byte[compressedLength];
			Logger.logger.debugLn("Reading compressed data");
			dataIn.readFully(data);
			streams[stream].inflater.setInput(data);
			try {
				streams[stream].inflater.inflate(p);
			} catch (DataFormatException e) {
				e.printStackTrace();
			}
		}
		return p;
	}
	
	public static int[] convertDataToTightPixels(byte[] data, int dataSize, PixelFormat format) {
		//FIXME probably this causing the colour problem (orange on bgr)
		int size = format.bytesPerTPixel;
		int[] pixels = new int[dataSize];
		for (int i = 0; i < dataSize; i++) {
			byte[] p = new byte[size];
			System.arraycopy(data, i*size, p, 0, size);
			pixels[i] = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(p, format));
		}
		return pixels;
	}
	
}
