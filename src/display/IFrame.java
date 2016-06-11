package display;

import data.PointerPoint;

public interface IFrame extends Runnable {

	/*** 
	 * When this method is called the frame should draw its content.
	 */
	public void render();
	
	/***
	 * Sets the frame buffer to be drawn to this display frame.
	 * @param frameBuffer - The frame buffer to draw data from.
	 */
	public void setFrameBuffer(FrameBuffer frameBuffer);
	
	public FrameBuffer getFrameBuffer();
	
	public boolean sendPointer();
	
	public PointerPoint getLocalPointer();
	
	public void start();
}
