package uk.co.mholeys.vnc.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;

import uk.co.mholeys.vnc.data.KeyboardUpdate;
import uk.co.mholeys.vnc.display.IKeyboardManager;
import uk.co.mholeys.vnc.log.Logger;

public class Keyboard implements IKeyboardManager, KeyListener {

	SwingDisplay display;
	
	public Queue<KeyboardUpdate> keyboardUpdates = new LinkedList<KeyboardUpdate>();

	public Keyboard(SwingDisplay display) {
		this.display = display;
	}
	
	public void keyPressed(KeyEvent e) {
		/*if ((e.getModifiers() & KeyEvent.VK_ALT) == KeyEvent.VK_ALT) {
			KeyEvent ke = new KeyEvent(null, KeyEvent.KEY_FIRST, 0, 0, KeyEvent.VK_ALT, ' ');
			keyboardUpdates.offer(new KeyboardUpdate(lookupKeyCode(ke), true));			
		}
		if ((e.getModifiers() & KeyEvent.VK_CONTROL) == KeyEvent.VK_CONTROL) {
			KeyEvent ke = new KeyEvent(null, KeyEvent.KEY_FIRST, 0, 0, KeyEvent.VK_CONTROL, ' ');
			keyboardUpdates.offer(new KeyboardUpdate(lookupKeyCode(ke), true));			
		}*/
		keyboardUpdates.offer(lookupKeyCode(e, true));
	}
	
	public void keyReleased(KeyEvent e) {
		keyboardUpdates.offer(lookupKeyCode(e, false));
	}
	
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public boolean sendKeys() {
		return keyboardUpdates.peek() != null;
	}

	@Override
	public KeyboardUpdate getNext() {
		return keyboardUpdates.poll();
	}
	
	KeyboardUpdate lookupKeyCode(KeyEvent e, boolean pressed) {
		if ((e.getModifiers() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK) {
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				keyboardUpdates.offer(new KeyboardUpdate(0xffe9, pressed));
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
				keyboardUpdates.offer(new KeyboardUpdate(0xffea, pressed));
		}
		if ((e.getModifiers() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				keyboardUpdates.offer(new KeyboardUpdate(0xffe3, pressed));
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
				keyboardUpdates.offer(new KeyboardUpdate(0xffe4, pressed));
		}
		if ((e.getModifiers() & KeyEvent.META_DOWN_MASK) == KeyEvent.META_DOWN_MASK) {
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				keyboardUpdates.offer(new KeyboardUpdate(0xffe7, pressed));
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
				keyboardUpdates.offer(new KeyboardUpdate(0xffe8, pressed));
		}
		switch (e.getKeyCode()) { 
		case KeyEvent.VK_BACK_SPACE:
			return new KeyboardUpdate(0xff08, pressed);
		case KeyEvent.VK_TAB:
			return new KeyboardUpdate(0xff09, pressed);
		case KeyEvent.VK_ENTER:
			return new KeyboardUpdate(0xff0d, pressed);
		case KeyEvent.VK_ESCAPE:
			return new KeyboardUpdate(0xff1b, pressed);
		case KeyEvent.VK_INSERT:
			return new KeyboardUpdate(0xff63, pressed);
		case KeyEvent.VK_HOME:
			return new KeyboardUpdate(0xff50, pressed);
		case KeyEvent.VK_LEFT:
			return new KeyboardUpdate(0xff51, pressed);
		case KeyEvent.VK_UP:
			return new KeyboardUpdate(0xff52, pressed);
		case KeyEvent.VK_RIGHT:
			return new KeyboardUpdate(0xff53, pressed);
		case KeyEvent.VK_DOWN:
			return new KeyboardUpdate(0xff54, pressed);
		case KeyEvent.VK_PAGE_UP:
			return new KeyboardUpdate(0xff55, pressed);
		case KeyEvent.VK_PAGE_DOWN:
			return new KeyboardUpdate(0xff56, pressed);
		case KeyEvent.VK_END:
			return new KeyboardUpdate(0xff57, pressed);
		case KeyEvent.VK_F1:
			return new KeyboardUpdate(0xffbe, pressed);
		case KeyEvent.VK_F2:
			return new KeyboardUpdate(0xffbf, pressed);
		case KeyEvent.VK_F3:
			return new KeyboardUpdate(0xffc0, pressed);
		case KeyEvent.VK_F4:
			return new KeyboardUpdate(0xffc1, pressed);
		case KeyEvent.VK_F5:
			return new KeyboardUpdate(0xffc2, pressed);
		case KeyEvent.VK_F6:
			return new KeyboardUpdate(0xffc3, pressed);
		case KeyEvent.VK_F7:
			return new KeyboardUpdate(0xffc4, pressed);
		case KeyEvent.VK_F8:
			return new KeyboardUpdate(0xffc5, pressed);
		case KeyEvent.VK_F9:
			return new KeyboardUpdate(0xffc6, pressed);
		case KeyEvent.VK_F10:
			return new KeyboardUpdate(0xffc7, pressed);
		case KeyEvent.VK_F11:
			return new KeyboardUpdate(0xffc8, pressed);
		case KeyEvent.VK_F12:
			return new KeyboardUpdate(0xffc9, pressed);
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				return new KeyboardUpdate(0xffe1, pressed);
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
				return new KeyboardUpdate(0xffe2, pressed);
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				return new KeyboardUpdate(0xffe3, pressed);
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
				return new KeyboardUpdate(0xffe4, pressed);
		case KeyEvent.VK_META:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				return new KeyboardUpdate(0xffe7, pressed);
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
				return new KeyboardUpdate(0xffe8, pressed);
		case KeyEvent.VK_ALT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				return new KeyboardUpdate(0xffe9, pressed);
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
				return new KeyboardUpdate(0xffea, pressed);
		case KeyEvent.VK_DELETE:
			return new KeyboardUpdate(0xffff, pressed);
		default:
			if (e.getKeyChar() != 0xffff) {
				return new KeyboardUpdate(e.getKeyChar(), pressed);
			}
			if ((e.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK) {
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
					keyboardUpdates.offer(new KeyboardUpdate(0xffe1, true));
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) 
					keyboardUpdates.offer(new KeyboardUpdate(0xffe2, true));
			}
			return new KeyboardUpdate(e.getKeyCode(), pressed);
		}
	}
	
}
