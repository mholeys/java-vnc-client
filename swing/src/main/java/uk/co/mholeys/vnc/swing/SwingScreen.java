package uk.co.mholeys.vnc.swing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import uk.co.mholeys.vnc.display.IScreen;
import uk.co.mholeys.vnc.display.UpdateManager;
import uk.co.mholeys.vnc.display.data.CopyScreenUpdate;
import uk.co.mholeys.vnc.display.data.FillScreenUpdate;
import uk.co.mholeys.vnc.display.data.JPEGScreenUpdate;
import uk.co.mholeys.vnc.display.data.PaletteScreenUpdate;
import uk.co.mholeys.vnc.display.data.RawScreenUpdate;
import uk.co.mholeys.vnc.display.data.ScreenUpdate;

public class SwingScreen implements IScreen {

	public int width, height;
	public int[] pixels;
	public UpdateManager updateManager;
	public SwingDisplay display;
	
	
	public SwingScreen(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width*height];
	}
	
	@Override
	public synchronized void drawPixels(int x, int y, int width, int height, int[] pixels) {
		for (int yA = 0; yA < height; yA++) {
			for (int xA = 0; xA < width; xA++) {
				this.pixels[(xA+x) + (yA+y) * this.width] = pixels[xA + (yA * width)];
			}
		}
	}
	
	@Override
	public synchronized void drawPalette(int x, int y, int width, int height, int[] palette, int paletteSize, byte[] data) {
		if (2 == paletteSize) {
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
		int[] copy = pixels.clone();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[(x+xDest) + (y+yDest) * this.width] = copy[(x+xSrc) + (y+ySrc) * this.width];
			}
		}
	}

	@Override
	public synchronized void fillPixels(int x, int y, int width, int height, int pixel) {
		for (int yA = y; yA < y+height; yA++) {
			for (int xA = x; xA < x+width; xA++) {
				this.pixels[xA + yA * this.width] = pixel;
			}
		}
	}
	
	@Override
	public void drawCursor(int x, int y, int width, int height, byte[] cursorData) {
		for (int yA = 0; yA < height; y++) {
			for (int xA = 0; xA < width; x++) {
				if ((cursorData[xA + yA * width] & 0xFF000000) != 0x00000000) {
					pixels[(xA+x) + (yA+y) * this.width] = cursorData[xA + yA * width];
				}
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

	public void process() {
		while (updateManager.isReady()) {
			while (updateManager.hasUpdates()) {
				ScreenUpdate update = updateManager.getUpdate();
				if (update == null) continue;
				int x = update.x;
				int y = update.y;
				int width = update.width;
				int height = update.height;
				if (update instanceof RawScreenUpdate) {
					RawScreenUpdate raw = (RawScreenUpdate) update;
					drawPixels(x, y, width, height, raw.pixels);
				} else if (update instanceof PaletteScreenUpdate) {
					PaletteScreenUpdate palette = (PaletteScreenUpdate) update;
					drawPalette(x, y, width, height, palette.palette, palette.paletteSize, palette.data);
				} else if (update instanceof JPEGScreenUpdate) {
					JPEGScreenUpdate jpeg = (JPEGScreenUpdate) update;
					drawJPEG(x, y, width, height, jpeg.jpegData);
				} else if (update instanceof CopyScreenUpdate) {
					CopyScreenUpdate copy = (CopyScreenUpdate) update;
					copyPixels(copy.xSrc, copy.ySrc, width, height, x, y);
				} else if (update instanceof FillScreenUpdate) {
					FillScreenUpdate fill = (FillScreenUpdate) update;
					fillPixels(x, y, width, height, fill.pixel);
				}
			}
			updateManager.setComplete();
		}
		display.repaint();
	}
	
}
