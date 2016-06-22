package display;

import data.PointerPoint;

public interface IDisplay extends Runnable {

	/*** 
	 * When this method is called the frame should draw its content.
	 */
	public void render();
	
	public boolean sendPointer();
	
	public PointerPoint getLocalPointer();
	
	public void start();
	
	public Thread getThread();
	
}
