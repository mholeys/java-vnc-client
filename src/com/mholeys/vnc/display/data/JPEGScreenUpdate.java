package com.mholeys.vnc.display.data;

public class JPEGScreenUpdate extends ScreenUpdate {

	public byte[] jpegData;
	
	public JPEGScreenUpdate(int x, int y, int width, int height, byte[] jpegData) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.jpegData = jpegData;
	}

}
