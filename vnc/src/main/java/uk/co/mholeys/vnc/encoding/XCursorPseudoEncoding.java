package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class XCursorPseudoEncoding extends Decoder {

	public int x; // Click point of the mouse
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public XCursorPseudoEncoding(PixelRectangle r, PixelFormat format) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		this.format = format;
	}
	
	@Override
	public void readEncoding(InputStream in) throws IOException {
		if (width == 0 || height == 0) {
			// No Data included
			return;
		}
		DataInputStream dataIn = new DataInputStream(in);
		byte[] primaryData = new byte[3];
		byte[] secondaryData = new byte[3];
		dataIn.read(primaryData);
		dataIn.read(secondaryData);
		int primaryColour = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(primaryData, format));
		int secondaryColour = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(secondaryData, format));
		
		
		int lineWidth = (int) Math.floor((width + 7) / 8);
		
		byte[] bitmap = new byte[lineWidth * height];
		Logger.logger.debugLn("Reading cursor bitmap");
		dataIn.readFully(bitmap);
		byte[] bitmask = new byte[lineWidth * height];
		Logger.logger.debugLn("Reading cursor bitmask");
		dataIn.readFully(bitmask);
		
		int[] pixels = new int[width * height];
		
		for (int yA = 0; yA < height; yA++) {
			for (int xA = 0; xA < width; xA++) {
				boolean valid = (bitmask[xA/8 + yA * lineWidth] & (1 << (7-(xA % 8)))) > 0;
				boolean primary = (bitmap[xA/8 + yA * lineWidth] & (1 << (7-(xA % 8)))) > 0;
				if (valid) {
					if (primary) {
						pixels[xA + yA * width] = primaryColour;
					} else {
						pixels[xA + yA * width] = secondaryColour;
					}
				} else {
					pixels[xA + yA * width] = 0x99000000;
				}
			}
		}
		
		render.setupCursor(x, y, width, height, pixels);
		
	}

}
