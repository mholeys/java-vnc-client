package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class CoRREEncoding extends Encode {

	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public CoRREEncoding(int x, int y, int width, int height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.format = format;
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
			int x = dataIn.read();
			int y = dataIn.read();
			int width = dataIn.read();
			int height = dataIn.read();
			render.drawFill(this.x+x, this.y+y, width, height, ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format)));
		}
	}

}
