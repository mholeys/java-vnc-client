package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.log.Logger;

public class CopyRectEncoding extends Decoder {

	public short x, y;
	public short width, height;
	public PixelFormat pixelFormat;
	public int[] pixels;
	private short xSrc;
	private short ySrc;
	
	public CopyRectEncoding(PixelRectangle r, PixelFormat format) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		this.pixelFormat = format;
	}

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		Logger.logger.debugLn("Reading source pos x");
		xSrc = dataIn.readShort();
		Logger.logger.debugLn("Reading source pos y");
		ySrc = dataIn.readShort();
		render.drawCopy(xSrc, ySrc, width, height, x, y);
	}

}
