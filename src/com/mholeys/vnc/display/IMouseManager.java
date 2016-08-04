package com.mholeys.vnc.display;

import com.mholeys.vnc.data.PointerPoint;

public interface IMouseManager {

	public boolean sendLocalMouse();
	
	public PointerPoint getLocalMouse();
	
	public void setRemoteMouse(PointerPoint remote);
	
}
