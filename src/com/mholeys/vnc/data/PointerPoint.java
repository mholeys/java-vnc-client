package com.mholeys.vnc.data;

public class PointerPoint {

	public short x, y;
	public boolean left = false;
	public boolean right = false;
	public boolean middle = false;
	
	public PointerPoint(short x, short y) {
		this.x = x;
		this.y = y;
	}

}
