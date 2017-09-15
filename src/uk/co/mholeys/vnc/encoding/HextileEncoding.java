package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class HextileEncoding extends Decoder {
	
	public static byte[] lastBackground;
	public static byte[] lastForeground;
	
	final int RAW = 1;
	final int BACKGROUND_SPECIFIED = 2;
	final int FOREGROUND_SPECIFIED = 4;
	final int ANY_SUB_RECTANGLE = 8;
	final int SUB_RECTANGLES_COLOURED = 16;

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public HextileEncoding(PixelRectangle r, PixelFormat format) {
		this.x = r.x;
		this.y = r.y;
		this.width = Math.abs(r.width);
		this.height = Math.abs(r.height);
		this.format = format;
		pixels = new int[this.width * this.height];
	}
	
	@Override
	public void readEncoding(InputStream in) throws IOException {
		// TODO: Implement sub rectangle
		// TODO: Finish
		DataInputStream dataIn = new DataInputStream(in);
		int subencoding = dataIn.read();
		
		byte[] backgroundPixel = new byte[format.bytesPerPixel];
		byte[] foregroundPixel = new byte[format.bytesPerPixel];
		
		int subRectangles = 0;
		int subRectX = 0;
		int subRectY = 0;
		int subRectWidth = 0;
		int subRectHeight = 0;
		
		if ((subencoding & RAW) > 0) {
			for (int i = 0; i < width*height; i++) {
				byte[] pixel = new byte[format.bytesPerPixel];
				dataIn.readFully(pixel);
				pixels[i] = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format));
			}
			render.drawRaw(x, y, width, height, pixels);
		} else {
			if ((subencoding & BACKGROUND_SPECIFIED) > 0 ) {
				dataIn.read(backgroundPixel);
				lastBackground = backgroundPixel;			
			} else {
				backgroundPixel = lastBackground;
			}
			
			if ((subencoding & FOREGROUND_SPECIFIED) > 0 ) {
				if ((subencoding & SUB_RECTANGLES_COLOURED) > 0) {
					System.err.println("DEBUG: SubEncoding was coloured but foreground was specified see Hextile code");
				}
				dataIn.read(foregroundPixel);
				lastForeground = foregroundPixel;
			} else {
				//foregroundPixel = lastForeground;
			}
			
			if ((subencoding & ANY_SUB_RECTANGLE) > 0 ) {
				subRectangles = dataIn.read();
			}
			
			if ((subencoding & SUB_RECTANGLES_COLOURED) > 0 ) {
				if (subRectangles > 0) {
					for (int i = 0; i < subRectangles; i++) {
						byte[] subRectPixel = new byte[format.bytesPerPixel];
						dataIn.read(subRectPixel);
						int xy = dataIn.read();
						subRectX = (xy >> 1 & 0xF);
						subRectY = (xy & 0xF);
						int wh = dataIn.read();
						subRectWidth = (wh >> 1 & 0xF) + 1;
						subRectHeight = (wh & 0xF) + 1;
						render.drawFill(subRectX, subRectY, subRectWidth, subRectHeight, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(subRectPixel, format)));
					}
				}
			} else {
				if (subRectangles > 0) {
					for (int i = 0; i < subRectangles; i++) {
						int xy = dataIn.read();
						subRectX = (xy >> 1 & 0xF);
						subRectY = (xy & 0xF);
						int wh = dataIn.read();
						subRectWidth = (wh >> 1 & 0xF) + 1;
						subRectHeight = (wh & 0xF) + 1;
						render.drawFill(subRectX, subRectY, subRectWidth, subRectHeight, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(foregroundPixel, format)));
					}
				}
			}
			
			if (subRectangles == 0) {
				// No sub-rectangles so tiles is just background colour
				render.drawFill(x, y, width, height, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(backgroundPixel, format)));
			}
			
		}
	}

}

