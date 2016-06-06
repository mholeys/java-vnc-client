package display;

import data.PixelFormat;

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
	
	public void handleFrameBufferUpdate(int x, int y, int width, int height, int pixels[]) {
		for (int i = 0; i < pixels.length; i++) {
			this.pixels[x + y * width + i] = pixels[i];  
		}
		changed = true;
	}

}
