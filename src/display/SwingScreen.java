package display;

public class SwingScreen implements IScreen {

	public int width, height;
	public int[] pixels;
	
	public SwingScreen(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width*height];
	}
	
	@Override
	public void drawPixels(int x, int y, int width, int height, int[] pixels) {
		for (int yA = y; yA < height; yA++) {
			for (int xA = x; xA < width; xA++) {
				this.pixels[xA + yA * this.width] = pixels[(xA-x) + (yA-y) * width];
			}
		}
	}

	@Override
	public void drawJPEG(int x, int y, int width, int height, byte[] jpegData) {
	}

	@Override
	public void copyPixels(int xSrc, int ySrc, int width, int height, int xDest, int yDest) {
	}

	@Override
	public int[] getPixels() {
		return pixels;
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width*height];
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
}
