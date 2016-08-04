package com.mholeys.vnc.display;

import com.mholeys.vnc.data.PointerPoint;

public interface IDisplay extends Runnable {

	/*** 
	 * When this method is called the frame should draw its content.
	 */
	public void render();
	
	public void start();
	
	public Thread getThread();
	
}
