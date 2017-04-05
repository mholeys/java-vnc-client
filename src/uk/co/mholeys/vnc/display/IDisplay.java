package uk.co.mholeys.vnc.display;

public interface IDisplay extends Runnable {

	public void start();
	
	public Thread getThread();
	
}
