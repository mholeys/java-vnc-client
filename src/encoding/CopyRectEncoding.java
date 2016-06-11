package encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import data.PixelFormat;
import display.FrameBuffer;

public class CopyRectEncoding extends Encode {

	public FrameBuffer frameBuffer;
	public short x, y;
	public short width, height;
	public PixelFormat pixelFormat;
	public int[] pixels;
	private short localX;
	private short localY;
	
	public CopyRectEncoding(short x, short y, short width, short height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.pixelFormat = format;
	}

	@Override
	public int[] getPixels() {
		if (pixels == null && frameBuffer != null) {
			pixels = new int[width*height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					pixels[x+this.x+y+this.y*width] = frameBuffer.pixels[(localX+x) + (localY+y) * frameBuffer.width]; 
				}
			}
		}
		return pixels;
	}

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		localX = dataIn.readShort();
		localY = dataIn.readShort();
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
	}

}
