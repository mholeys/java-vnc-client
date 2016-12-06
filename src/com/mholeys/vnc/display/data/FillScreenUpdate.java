package com.mholeys.vnc.display.data;

public class FillScreenUpdate extends ScreenUpdate {

	public int pixel;
	
	public FillScreenUpdate(int x, int y, int width, int height, int pixel) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.pixel = pixel;
	}

}
