package display;

import data.PixelFormat;
import encoding.Encode;

public class FrameBuffer {

	public int width, height;
	public int[] pixels;
	public PixelFormat format;
	public boolean changed;
	
	public FrameBuffer(int width, int height, PixelFormat format) {
		this.width = width;
		this.height = height;
		this.format = format;
		pixels = new int[width * height];
	}
	
	public void handleFrameBufferUpdate(int xS, int yS, int width, int height, Encode e) {
		e.setFrameBuffer(this);
		int[] pixels = e.getPixels();
		for (int y = yS; y < height; y++) {
			for (int x = xS; x < width; x++) {
				this.pixels[x + y * this.width] = pixels[(x-xS) + (y-yS) * width];  
			}
		}
		changed = true;
	}

}
