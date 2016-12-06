package com.mholeys.vnc.display.data;

public class CopyScreenUpdate extends ScreenUpdate {

	public int xSrc;
	public int ySrc;

	public CopyScreenUpdate(int xSrc, int ySrc, int width, int height, int xDest, int yDest) {
		this.xSrc = xSrc;
		this.ySrc = ySrc;
		this.width = width;
		this.height = height;
		this.x = xDest;
		this.y = yDest;
	}
	
}
