package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class TightEncoding extends Decoder {

	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	public ZLibStream[] streams;
	
	public TightEncoding(PixelRectangle r, PixelFormat format, ZLibStream[] streams) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
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
			Logger.logger.verboseLn("Tight Fill mode");
			Logger.logger.debugLn("Reading tight pixel for fill");
			int color = readTightPixel(dataIn, format);
			render.drawFill(x, y, width, height, color);
			break;
		case JPEG:
			Logger.logger.verboseLn("Tight JPEG mode");
			Logger.logger.debugLn("Reading length of jpeg data");
			int length = readCompactInt(dataIn);
			byte[] jpegData = new byte[length];
			Logger.logger.debugLn("Reading jpeg JFIF data");
			dataIn.readFully(jpegData);
			render.drawJPEG(x, y, width, height, jpegData);
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
				Logger.logger.verboseLn("Tight Palette mode");
				Logger.logger.debugLn("Reading palette size");
				int paletteSize = dataIn.readUnsignedByte()+1;
				Logger.logger.debugLn("Palette Size: " + paletteSize);
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
				
				render.drawPalette(x, y, width, height, palette, paletteSize, paletteData);
				break;
			case GRADIENT:
				Logger.logger.verboseLn("Tight Gradient mode");
				Logger.logger.debugLn("Gradient filter. Depth should be 24 it is: " + format.depth);
				
				byte[] data = readCompressedData(dataIn, width*height*format.bytesPerTPixel, stream);
				int[] gradientPixels = convertDataToTightPixels(data, width*height, format);
				
				int[] newPixels = new int[width*height];
				System.arraycopy(gradientPixels, 0, newPixels, 0, width*height);
				
				int[] shift = new int[3]; // Array holding the colour shifts for each component. Copied for easy access
				if (format.bigEndianFlag) {
					shift[0] = format.blueShift;
					shift[1] = format.greenShift;
					shift[2] = format.redShift;
				} else {
					shift[0] = format.redShift;
					shift[1] = format.greenShift;
					shift[2] = format.blueShift;
				}
				
				int[] max = new int[3]; // Array holding the max values for each component. Copied for easy access
				if (format.bigEndianFlag) {
					max[0] = format.blueMax;
					max[1] = format.greenMax;
					max[2] = format.redMax;
				} else {
					max[0] = format.redMax;
					max[1] = format.greenMax;
					max[2] = format.blueMax;
				}
				
				int[] currentRow = new int[width+2];
				int[] previousRow = new int[width+2];
				
				for (int srcY = 1; srcY < height+1; srcY++) {
					previousRow = currentRow;
					currentRow = new int[width+2];
					System.arraycopy(newPixels, (srcY-1) * width, currentRow, 1, width);
					for (int srcX = 1; srcX < width+1; srcX++) {
						int colour = 0;
						for (int c = 0; c < 3; c++) {
							int left = (currentRow[srcX-1] >> shift[c]) & max[c];
							int upper = (previousRow[srcX]  >> shift[c]) & max[c];
							int upperLeft = (previousRow[srcX-1] >> shift[c]) & max[c];
							int here = (currentRow[srcX] >> shift[c]) & max[c];
							int predicted = upper + left - upperLeft;
							if (predicted > max[c]) {
								predicted = max[c];
							} else if (predicted < 0) {
								predicted = 0;
							}
							colour = colour | (((here + predicted) & max[c]) << shift[c]); 
						}
						currentRow[srcX] = colour;
					}	
					System.arraycopy(currentRow, 1, newPixels, (srcY-1)*width, width);
				}
				
				render.drawRaw(x, y, width, height, newPixels);
				break;
			case COPY:
				Logger.logger.verboseLn("Tight Copy mode");
				//Read compressed TightPixels
				Logger.logger.debugLn("Reading copy mode TPixels");
				byte[] copyData = readCompressedData(dataIn, width*height*format.bytesPerTPixel, stream);
				//Decode copy filter
				int[] pixels = convertDataToTightPixels(copyData, width*height, format);
				//Read pixels
				render.drawRaw(x, y, width, height, pixels);
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
		/*int b = dataIn.readUnsignedByte();
		int size = b & 0x7F;
		if ((b & 0x80) != 0) {
			b = dataIn.readUnsignedByte();
			size += (b & 0x7F) << 7;
			if ((b & 0x80) != 0) {
				size += dataIn.readUnsignedByte() << 14;
			}
		}
		return size;*/
		
		
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
		//FIXME probably this causing the colour problem (orange on bgr) and other things
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
