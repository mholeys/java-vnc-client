package com.mholeys.vnc.display;

public interface IDisplay extends Runnable {

	public void start();
	
	public Thread getThread();
	
}
