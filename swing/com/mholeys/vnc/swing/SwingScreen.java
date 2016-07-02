package com.mholeys.vnc.swing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.mholeys.vnc.display.IScreen;

public class SwingScreen implements IScreen {

	public int width, height;
	public int[] pixels;
	
	public SwingScreen(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width*height];
	}
	
	@Override
	public synchronized void drawPixels(int x, int y, int width, int height, int[] pixels) {
		for (int yA = y; yA < y+height; yA++) {
			for (int xA = x; xA < x+width; xA++) {
				this.pixels[xA + yA * this.width] = pixels[(xA-x) + ((yA-y) * width)];
			}
		}
	}
	
	@Override
	public synchronized void drawPalette(int x, int y, int width, int height, int[] palette, byte[] data) {
		if (2 == palette.length) {
            int dx, dy, n;
            int i = y * this.width + x;
            int rowBytes = (width + 7) / 8;
            byte b;

            for (dy = 0; dy < height; dy++) {
                for (dx = 0; dx < width / 8; dx++) {
                    b = data[dy * rowBytes + dx];
                    for (n = 7; n >= 0; n--) {
                        pixels[i++] = palette[b >> n & 1];
                    }
                }
                for (n = 7; n >= 8 - width % 8; n--) {
                    pixels[i++] = palette[data[dy * rowBytes + dx] >> n & 1];
                }
                i += this.width - width;
            }
        } else {
            // 3..255 colors (assuming bytesPixel == 4).
            int i = 0;
            for (int ly = y; ly < y + height; ++ly) {
                for (int lx = x; lx < x + width; ++lx) {
                    int pixelsOffset = ly * this.width + lx;
                    int d = data[i++] & 0xFF;
                    pixels[pixelsOffset] = palette[d];
                }
            }
        }
		/*int drawPos = x + y * this.width;
		for (int i = 0; i < data.length; i++) {
			pixels[drawPos + i] = palette[data[i]];
		}*/
	}

	@Override
	public synchronized void drawJPEG(int x, int y, int width, int height, byte[] jpegData) {
		ByteArrayInputStream buff = new ByteArrayInputStream(jpegData); 
		try {
			BufferedImage img = ImageIO.read(buff);
			if (img != null) {
				BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			    Graphics g = convertedImg.getGraphics();
			    g.drawImage(img, 0, 0, null);
			    int[] imgPixels = ((DataBufferInt) convertedImg.getRaster().getDataBuffer()).getData();
				drawPixels(x, y, width, height, imgPixels);
				g.dispose();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void copyPixels(int xSrc, int ySrc, int width, int height, int xDest, int yDest) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[(x+xDest) + (y+yDest) * this.width] = pixels[(x+xSrc) + (y+ySrc) * this.width];
			}
		}
	}

	@Override
	public synchronized void fillPixels(int x, int y, int width, int height, int pixel) {
		for (int yA = y; yA < height; yA++) {
			for (int xA = x; xA < width; xA++) {
				this.pixels[xA + yA * this.width] = pixel;
			}
		}
	}

	@Override
	public synchronized int[] getPixels() {
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
