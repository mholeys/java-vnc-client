package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.util.ByteUtil;

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
		
		byte[] subRectPixel = new byte[format.bytesPerPixel];
		
		int subRectangles = 0;
		int subRectX = 0;
		int subRectY = 0;
		int subRectWidth = 0;
		int subRectHeight = 0;
		
		boolean backgroundSet = false; 
		boolean foregroundSet = false; 
		boolean anySubRect = false; 
		boolean colourSubRecs = false;
		
		if ((subencoding & BACKGROUND_SPECIFIED) > 0 ) {
			backgroundSet = true;
			dataIn.read(backgroundPixel);
			lastBackground = backgroundPixel;			
		} else {
			backgroundPixel = lastBackground;
		}
		
		if ((subencoding & FOREGROUND_SPECIFIED) > 0 ) {
			foregroundSet = true;
			dataIn.read(foregroundPixel);
			lastForeground = foregroundPixel;
		} else {
			foregroundPixel = lastForeground;
		}
		
		if ((subencoding & ANY_SUB_RECTANGLE) > 0 ) {
			anySubRect = true;
			subRectangles = dataIn.read();
		}
		
		if ((subencoding & SUB_RECTANGLES_COLOURED) > 0 ) {
			colourSubRecs = true;
			dataIn.read(subRectPixel);
			int xy = dataIn.read();
			subRectX = (xy >> 1 & 0xF);
			subRectY = (xy & 0xF);
			int wh = dataIn.read();
			subRectWidth = (wh >> 1 & 0xF);
			subRectHeight = (wh & 0xF);
		}
		
		if (subRectangles == 0 && foregroundSet) {
			render.drawFill(subRectX, subRectY, subRectWidth, subRectHeight, ByteUtil.bytesToInt(backgroundPixel, format));
		} else {
			
		}
		
		
	}

}

