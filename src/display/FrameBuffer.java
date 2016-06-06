package display;

import data.PixelFormat;

public class FrameBuffer {

	public int width, height;
	public int[] pixels;
	public PixelFormat format;
	
	public FrameBuffer(int width, int height, PixelFormat format) {
		this.width = width;
		this.height = height;
		this.format = format;
		pixels = new int[width * height];
	}
	
	public void handleFrameBufferUpdate() {
		
	}

}
