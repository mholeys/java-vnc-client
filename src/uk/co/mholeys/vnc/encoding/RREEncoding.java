package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class RREEncoding extends Decoder {

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public RREEncoding(PixelRectangle r, PixelFormat format) {
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
		int subRectangles = dataIn.readInt();
		byte[] backgroundPixel = new byte[format.bytesPerPixel];
		dataIn.read(backgroundPixel);
		render.drawFill(x, y, width, height, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(backgroundPixel, format)));
		
		for (int i = 0 ; i < subRectangles; i++) {
			byte[] pixel = new byte[format.bytesPerPixel];
			dataIn.read(pixel);
			short x = dataIn.readShort();
			short y = dataIn.readShort();
			short width = dataIn.readShort();
			short height = dataIn.readShort();
			render.drawFill(this.x + (x & 0xFFFF), this.y + (y & 0xFFFF), width & 0xFFFF, height & 0xFFFF, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format)));
		}
	}

}
