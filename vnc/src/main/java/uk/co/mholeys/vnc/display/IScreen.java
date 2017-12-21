package uk.co.mholeys.vnc.display;

public interface IScreen {

	public void drawPixels(int x, int y, int width, int height, int[] pixels);
	
	public void drawPalette(int x, int y, int width, int height, int[] palette, int paletteSize, byte[] data);
	
	public void drawJPEG(int x, int y, int width, int height, byte[] jpegData);
	
	public void copyPixels(int xSrc, int ySrc, int width, int height, int xDest, int yDest);
	
	public void fillPixels(int x, int y, int width, int height, int pixel);

	public void setupCursor(int x, int y, int width, int height, int[] pixels);
	public void moveCursor(int x, int y);

	public int[] getPixels();
	
	public void setSize(int width, int height);
	
	public int getWidth();
	public int getHeight();
	
	public void process();
	
}
