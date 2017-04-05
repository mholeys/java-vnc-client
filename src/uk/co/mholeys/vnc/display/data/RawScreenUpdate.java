package uk.co.mholeys.vnc.display.data;

public class RawScreenUpdate extends ScreenUpdate {

	public int[] pixels;
	
	public RawScreenUpdate(int x, int y, int width, int height, int[] pixels) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}

}
