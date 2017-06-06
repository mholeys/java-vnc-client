package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.util.ByteUtil;

public class CursorPseudoEncoding extends Encode {

	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public CursorPseudoEncoding(PixelRectangle r, PixelFormat format) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		this.format = format;
	}
	
	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		byte[] cursorData = new byte[width*height*format.bytesPerPixel];
		Logger.logger.debugLn("Reading cursor data");
		dataIn.readFully(cursorData);
		int lineWidth = (width + 7) / 8;
		byte[] bitmask = new byte[lineWidth * height];
		Logger.logger.debugLn("Reading bitmask");
		dataIn.readFully(bitmask);
		boolean[] bits = ByteUtil.bytesToBits(bitmask);
		
		// TODO: Finish decoding and drawing
		/*for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (bits[y * lineWidth + x / 8 + x%8]) {
					
				}
				if ((bitmask[x/8 + y * lineWidth] & (1 << (7-(x % 8)))) == 0) {
					cursorData[x + y * width] = 0;
				} else {
					cursorData[x + y * width] =  & 0xFFFFFFFF;
				}
			}
		}
		screen.drawCursor(x, y, width, height, cursorData);*/
	}

}
