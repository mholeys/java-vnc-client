package com.mholeys.vnc.swing;

import com.mholeys.vnc.display.IDisplay;
import com.mholeys.vnc.display.IMouseManager;
import com.mholeys.vnc.display.IScreen;
import com.mholeys.vnc.display.IUserInterface;
import com.mholeys.vnc.display.UpdateManager;

public class SwingInterface implements IUserInterface {
	
	private SwingDisplay display;
	private SwingScreen screen;
	private UpdateManager updateManager;
	public Mouse mouse;
	
	@Override
	public IDisplay getDisplay() {
		return display;
	}

	@Override
	public IScreen getScreen() {
		return screen;
	}
	
	public IMouseManager getMouseManager() {
		return mouse;
	}

	@Override
	public UpdateManager getUpdateManager() {
		return updateManager;
	}
	
	@Override
	public void setUpdateManager(UpdateManager updateManager) {
		this.updateManager = updateManager; 
		this.screen.updateManager = updateManager;
	}

	@Override
	public void show() {
		display.start();
	}

	@Override
	public void setSize(int width, int height) {
		if (screen != null) {
			screen.setSize(width, height);
		} else {
			screen = new SwingScreen(width, height);
		}
		if (display != null) {
			display.screen = screen;
		} else {
			display = new SwingDisplay(this);
		}
		screen.display = display;
	}

	@Override
	public void exit() {
		
	}


}
