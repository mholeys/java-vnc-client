package encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import data.PixelFormat;
import display.FrameBuffer;

public class TightEncoding extends Encode {

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	
	public TightEncoding(int x, int y, int width, int height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.format = format;
		pixels = new int[width * height];
	}
	
	@Override
	public int[] getPixels() {
		return pixels;
	}

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		byte compressionControl = dataIn.readByte();
		boolean b0 = (compressionControl & 0x1) == 1;
		boolean b1 = (compressionControl & 0x2) == 1;
		boolean b2 = (compressionControl & 0x4) == 1;
		boolean b3 = (compressionControl & 0x8) == 1;
		boolean b4 = (compressionControl & 0x10) == 1;
		boolean b5 = (compressionControl & 0x20) == 1;
		boolean b6 = (compressionControl & 0x40) == 1;
		boolean b7 = (compressionControl & 0x80) == 1;
		// Need to reset streams?
		
		if (b7) {
			
		}
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer) {

	}

}
