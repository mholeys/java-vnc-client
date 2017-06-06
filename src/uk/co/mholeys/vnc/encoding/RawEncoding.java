package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class RawEncoding extends Encode {

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public RawEncoding(PixelRectangle r, PixelFormat format) {
		this.x = r.x;
		this.y = r.y;
		this.width = Math.abs(r.width);
		this.height = Math.abs(r.height);
		this.format = format;
		pixels = new int[this.width * this.height];
	}
		 
	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		for (int i = 0; i < width * height; i++) {
			byte[] pixel = new byte[format.bytesPerPixel];
			Logger.logger.debugLn("Reading pixel");
			dataIn.read(pixel);
			int p = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format));
			pixels[i] = p;
		}
		render.drawRaw(x, y, width, height, pixels);
	}

}
