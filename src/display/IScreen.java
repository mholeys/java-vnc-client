package display;

public interface IScreen {

	public void drawPixels(int x, int y, int width, int height, int[] pixels);
	
	public void drawJPEG(int x, int y, int width, int height, byte[] jpegData);
	
	public void copyPixels(int xSrc, int ySrc, int width, int height, int xDest, int yDest);
	
	public int[] getPixels();
	
	public void setSize(int width, int height);
	
	public int getWidth();
	public int getHeight();
	
}
