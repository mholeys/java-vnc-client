package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class CursorPseudoEncoding extends Decoder {

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
		byte[] cursorPixels = new byte[width*height*format.bytesPerPixel];
		Logger.logger.debugLn("Reading cursor pixels");
		dataIn.readFully(cursorPixels);
		int lineWidth = (int) Math.floor((width + 7) / 8);
		byte[] bitmask = new byte[lineWidth * height];
		Logger.logger.debugLn("Reading cursor bitmask");
		dataIn.readFully(bitmask);

		int[] pixels = new int[width * height];
		
		for (int yA = 0; yA < height; yA++) {
			for (int xA = 0; xA < width; xA++) {
				boolean valid = (bitmask[xA/8 + yA * lineWidth] & (1 << (7-(xA % 8)))) > 0;
				if (valid) {
					byte[] pixel = new byte[format.bytesPerPixel];
					System.arraycopy(cursorPixels, xA + yA * width, pixel, 0, format.bytesPerPixel);
					
					int p = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format));
					
					pixels[xA + yA * width] = p;
				} else {
					pixels[xA + yA * width] = 0x99000000;
				}
			}
		}
		
		render.drawCursor(x, y, width, height, pixels);
		
		
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
