package com.mholeys.vnc.swing;

import com.mholeys.vnc.display.IDisplay;
import com.mholeys.vnc.display.IScreen;
import com.mholeys.vnc.display.IUserInterface;

public class SwingInterface implements IUserInterface {
	
	private SwingDisplay display;
	private SwingScreen screen;
	
	@Override
	public IDisplay getDisplay() {
		return display;
	}

	@Override
	public IScreen getScreen() {
		return screen;
	}

	@Override
	public void show() {
		display.start();
	}
	
	@Override
	public void exit() {
		display.frame.dispose();
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
			display = new SwingDisplay(screen);
		}
	}

}
