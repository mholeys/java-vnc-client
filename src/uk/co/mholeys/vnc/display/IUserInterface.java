package uk.co.mholeys.vnc.display;

import uk.co.mholeys.vnc.data.PixelFormat;

public interface IUserInterface {

	public IDisplay getDisplay();
	public IScreen getScreen();
	public IMouseManager getMouseManager();
	public IKeyboardManager getKeyboardManager();
	public UpdateManager getUpdateManager();
	public PixelFormat getServerFormat();
	public void setUpdateManager(UpdateManager updateManager);
	public void setSize(int width, int height);
	public void setServerFormat(PixelFormat format);
	public void show();
	public void exit();
	
}
