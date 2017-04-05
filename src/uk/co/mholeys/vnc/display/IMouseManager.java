package uk.co.mholeys.vnc.display;

import uk.co.mholeys.vnc.data.PointerPoint;

public interface IMouseManager {

	public boolean sendLocalMouse();
	
	public PointerPoint getLocalMouse();
	
	public void setRemoteMouse(PointerPoint remote);
	
}
