package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
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
			Logger.logger.verboseLn("Fill mode");
			Logger.logger.debugLn("Reading tight pixel for fill");
			int color = readTightPixel(dataIn, format);
			render.drawFill(x, y, width, height, color);
			break;
		case JPEG:
			Logger.logger.verboseLn("JPEG mode");
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
				
				byte[] paletteDataNew = new byte[paletteDataLength];
				for (int i = 0; i < paletteDataLength-format.bytesPerTPixel; i+= format.bytesPerTPixel) {
					paletteDataNew[i] = paletteData[i+2];
					paletteDataNew[i+1] = paletteData[i+1];
					paletteDataNew[i+2] = paletteData[i];
				}
				
				render.drawPalette(x, y, width, height, palette, paletteSize, paletteDataNew);
				break;
			case GRADIENT:
				Logger.logger.verboseLn("Gradient. No code. This will cause problems if this is reached");
				Logger.logger.debugLn("Gradient filter. Depth should be 24 it is: " + format.depth);
				render.drawFill(x, y, width, height, 0x00FF00);
				
				byte[] data = readCompressedData(dataIn, width*height*format.bytesPerTPixel, stream);
				int[] gradientPixels = convertDataToTightPixels(data, width*height, format);

				int[] newPixels = new int[width*height];
				System.arraycopy(gradientPixels, 0, newPixels, 0, width*height);
				
				/* 
				 * Code based on 
				 * https://github.com/BinaryAnalysisPlatform/deprecated-qemu-tracer/blob/master/ui/vnc-enc-tight.c#L547
				 */
				byte[] here = new byte[3]; // Array holding the colour components at the current x, y
				byte[] upper = new byte[3]; // Array holding the colour components at the current x, y-1
				byte[] left = new byte[3]; // Array holding the colour components at the current x-1, y
				byte[] upperleft = new byte[3]; // Array holding the colour components at the current x-1, y-1
				
				int[] shift = new int[3]; // Array holding the colour shifts for each component. Copied for easy access
				shift[0] = format.redShift;
				shift[1] = format.greenShift;
				shift[2] = format.blueShift;
				
				int d = 0;
				int currentPixel = 0;
				
				int prev = 0;
				int[] prevPixels = new int[width * height];
				
				int prediction = 0;
				
				for (int srcY = 0; srcY < height; srcY++) {
					// Loop over each colour component
/*					for (int c = 0; c < 3; c++) {
						upper[c] = 0;
						here[c] = 0;
					}*/
					for (int srcX = 0; srcX < width; srcX++) {
						currentPixel = gradientPixels[d++];
						Logger.logger.debugLn("Read " + Integer.toHexString(currentPixel));
						
						for (int c = 0; c < 3; c++) {
							upperleft[c] = upper[c];
							left[c] = here[c];				
							if (srcX < 0 || srcX > width || (srcY-1) < 0 || (srcY-1) > height) {
								upper[c] = 0;
							} else {
								upper[c] = (byte) prevPixels[prev]; //(byte) ((newPixels[srcX + (srcY-1)*width] & (0xFF << shift[c])) >> shift[c]);
							}
							// Get the component for the current pixel
							here[c] = (byte) (currentPixel >> shift[c] & 0xFF);
							prevPixels[prev++] = here[c];
							
							prediction = left[c] + upper[c] - upperleft[c];
							if (prediction < 0) {
								prediction = 0;
							} else if (prediction > 0xFF) {
								prediction = 0xFF;
							} 
							newPixels[srcX + srcY * width] |= (here[c] - prediction) << shift[c];
							System.out.println(x + ", " + y);
							render.drawRaw(x, y, width, height, newPixels);
							render.drawFill(srcX+x+1, srcY+y+1, srcX+x+10, srcY+y+10, newPixels[srcX + srcY * width]);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
				}
				render.drawRaw(x, y, width, height, newPixels);
				
				//System.exit(444);
				break;
			case COPY:
				Logger.logger.verboseLn("Copy mode");
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
		return ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(b, format, true));
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
		//FIXME probably this causing the colour problem (orange on bgr)
		int size = format.bytesPerTPixel;
		int[] pixels = new int[dataSize];
		for (int i = 0; i < dataSize; i++) {
			byte[] p = new byte[size];
			System.arraycopy(data, i*size, p, 0, size);
			
			pixels[i] = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(p, format, true));
		}
		return pixels;
	}
	
}
