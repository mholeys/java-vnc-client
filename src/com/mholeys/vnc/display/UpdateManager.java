package com.mholeys.vnc.display;

import java.util.LinkedList;
import java.util.Queue;

import com.mholeys.vnc.display.data.CopyScreenUpdate;
import com.mholeys.vnc.display.data.FillScreenUpdate;
import com.mholeys.vnc.display.data.JPEGScreenUpdate;
import com.mholeys.vnc.display.data.PaletteScreenUpdate;
import com.mholeys.vnc.display.data.RawScreenUpdate;
import com.mholeys.vnc.display.data.ScreenUpdate;
import com.mholeys.vnc.log.Logger;

public class UpdateManager {

	int width, height;
	int[] pixels;
	IScreen screen;
	
	public UpdateManager(int width, int height, IScreen screen) {
		this.width = width;
		this.height = height;
		this.screen = screen;
	}
	
	Queue<ScreenUpdate> updates = new LinkedList<ScreenUpdate>();
	
	public void drawRaw(int x, int y, int width, int height, int[] pixels) {
		Logger.logger.debugLn("Added Raw update");
		updates.offer(new RawScreenUpdate(x, y, width, height, pixels));
		screen.process();
	}
	
	public void drawPalette(int x, int y, int width, int height, int[] palette, int paletteSize, byte[] data) {
		Logger.logger.debugLn("Added Palette update");
		updates.offer(new PaletteScreenUpdate(x, y, width, height, palette, paletteSize, data));
		screen.process();
	}
	
	public void drawJPEG(int x, int y, int width, int height, byte[] jpegData) {
		Logger.logger.debugLn("Added JPEG update");
		updates.offer(new JPEGScreenUpdate(x, y, width, height, jpegData));
		screen.process();
	}
	
	public void drawCopy(int xSrc, int ySrc, int width, int height, int xDest, int yDest) {
		Logger.logger.debugLn("Added Copy update");
		updates.offer(new CopyScreenUpdate(xSrc, ySrc, width, height, xDest, yDest));
		screen.process();
	}
	
	public void drawFill(int x, int y, int width, int height, int pixel) {
		Logger.logger.debugLn("Added Fill update");
		updates.offer(new FillScreenUpdate(x, y, width, height, pixel));
		screen.process();
	}

	public void drawCursor(int x, int y, int width, int height, byte[] cursorData) {
		Logger.logger.debugLn("Cursor updated ignored");
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
	
}
