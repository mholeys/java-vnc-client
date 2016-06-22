package display;

public interface IUserInterface {

	public IDisplay getDisplay();
	public IScreen getScreen();
	public void setSize(int width, int height);
	public void show();
	
}
