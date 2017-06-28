package uk.co.mholeys.vnc.swing;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.display.IDisplay;
import uk.co.mholeys.vnc.display.IKeyboardManager;
import uk.co.mholeys.vnc.display.IMouseManager;
import uk.co.mholeys.vnc.display.IScreen;
import uk.co.mholeys.vnc.display.IUserInterface;
import uk.co.mholeys.vnc.display.UpdateManager;

public class SwingInterface implements IUserInterface {
	
	private SwingDisplay display;
	private SwingScreen screen;
	private UpdateManager updateManager;
	public Mouse mouse;
	public Keyboard keyboard;
	public PixelFormat format;
	
	@Override
	public IDisplay getDisplay() {
		return display;
	}

	@Override
	public IScreen getScreen() {
		return screen;
	}
	
	public IKeyboardManager getKeyboardManager() {
		return keyboard;
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
	public PixelFormat getServerFormat() {
		return format;
	}

	@Override
	public void setServerFormat(PixelFormat format) {
		this.format = format;
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
