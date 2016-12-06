package com.mholeys.vnc.display;

public interface IUserInterface {

	public IDisplay getDisplay();
	public IScreen getScreen();
	public IMouseManager getMouseManager();
	public UpdateManager getUpdateManager();
	public void setUpdateManager(UpdateManager updateManager);
	public void setSize(int width, int height);
	public void show();
	public void exit();
	
}
