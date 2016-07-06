package com.mholeys.vnc.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
	
	SwingDisplay display;
	
	public Mouse(SwingDisplay display) {
		this.display = display;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			display.left = true;
			display.mouseChanged = true;
		}
		if (e.getButton() == MouseEvent.BUTTON2) {
			display.right = true;
			display.mouseChanged = true;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			display.middle = true;
			display.mouseChanged = true;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			display.mouseChanged = true;
			display.left = false;
		}
		if (e.getButton() == MouseEvent.BUTTON2) {
			display.right = false;
			display.mouseChanged = true;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			display.middle = false;
			display.mouseChanged = true;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		display.x = display.convertX((short) e.getX());
		display.y = display.convertY((short) e.getY());
		display.mouseChanged = true;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		display.mouseOnScreen = true;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		display.mouseOnScreen = false;
	}
	
}
