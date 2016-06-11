package encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import data.PixelFormat;
import display.FrameBuffer;
import util.ByteUtil;

public class RawEncoding extends Encode {

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public RawEncoding(int x, int y, int width, int height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.format = format;
		pixels = new int[width * height];
	}
		 
	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i = 0; i < width * height; i++) {
			byte[] pixel = new byte[format.bitsPerPixel/8];
			dataIn.readFully(pixel);
			// TODO add colour shifting based on format
			pixels[i] = ByteUtil.bytesToInt(pixel);
		}
	}

	@Override
	public int[] getPixels() {
		return pixels;
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer) {
		
	}

}
