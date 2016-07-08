package com.mholeys.vnc.display;

public interface IUserInterface {

	public IDisplay getDisplay();
	public IScreen getScreen();
	public void setSize(int width, int height);
	public void show();
	public void exit();
	
}