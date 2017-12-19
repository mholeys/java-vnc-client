package uk.co.mholeys.vnc.display;

import java.util.LinkedList;
import java.util.Queue;

import uk.co.mholeys.vnc.display.data.CopyScreenUpdate;
import uk.co.mholeys.vnc.display.data.CursorScreenUpdate;
import uk.co.mholeys.vnc.display.data.FillScreenUpdate;
import uk.co.mholeys.vnc.display.data.JPEGScreenUpdate;
import uk.co.mholeys.vnc.display.data.PaletteScreenUpdate;
import uk.co.mholeys.vnc.display.data.RawScreenUpdate;
import uk.co.mholeys.vnc.display.data.ScreenUpdate;
import uk.co.mholeys.vnc.log.Logger;

public class UpdateManager {

	int width, height;
	int[] pixels;
	IScreen screen;
	public boolean ready = false;
	
	public UpdateManager(int width, int height, IScreen screen) {
		this.width = width;
		this.height = height;
		this.screen = screen;
	}
	
	Queue<ScreenUpdate> updates = new LinkedList<ScreenUpdate>();
	
	public void drawRaw(int x, int y, int width, int height, int[] pixels) {
		Logger.logger.verboseLn("Added Raw update");
		updates.offer(new RawScreenUpdate(x, y, width, height, pixels));
		screen.process();
	}
	
	public void drawPalette(int x, int y, int width, int height, int[] palette, int paletteSize, byte[] data) {
		Logger.logger.verboseLn("Added Palette update");
		updates.offer(new PaletteScreenUpdate(x, y, width, height, palette, paletteSize, data));
		screen.process();
	}
	
	public void drawJPEG(int x, int y, int width, int height, byte[] jpegData) {
		Logger.logger.verboseLn("Added JPEG update");
		updates.offer(new JPEGScreenUpdate(x, y, width, height, jpegData));
		screen.process();
	}
	
	public void drawCopy(int xSrc, int ySrc, int width, int height, int xDest, int yDest) {
		Logger.logger.verboseLn("Added Copy update");
		updates.offer(new CopyScreenUpdate(xSrc, ySrc, width, height, xDest, yDest));
		screen.process();
	}
	
	public void drawFill(int x, int y, int width, int height, int pixel) {
		Logger.logger.verboseLn("Added Fill update");
		updates.offer(new FillScreenUpdate(x, y, width, height, pixel));
		screen.process();
	}

	public void drawCursor(int x, int y, int width, int height, int[] pixels) {
		Logger.logger.verboseLn("Added Cursor updated");
		updates.offer(new CursorScreenUpdate(x, y, width, height, pixels));
		screen.process();
	}
	
	public boolean hasUpdates() {
		return !updates.isEmpty();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width*height];
		while (!updates.isEmpty()) {
			// could change to implement desktop size change
			updates.poll();
		}
	}
	
	public ScreenUpdate getUpdate() {
		return updates.poll();
	}
	
	public boolean isReady() {
		return ready & hasUpdates();
	}
	
	public void setReady() {
		ready = true;
	}
	
	public void setComplete() {
		ready = false;
	}
	
}
