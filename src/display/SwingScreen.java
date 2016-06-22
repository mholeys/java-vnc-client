package display;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

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
		ByteArrayInputStream buff = new ByteArrayInputStream(jpegData); 
		try {
			BufferedImage img = ImageIO.read(buff);
			if (img != null) {
				BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			    Graphics g = convertedImg.getGraphics();
			    g.drawImage(img, 0, 0, null);
			    g.dispose();
			    int[] imgPixels = ((DataBufferInt) convertedImg.getRaster().getDataBuffer()).getData();
				drawPixels(x, y, width, height, imgPixels);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
