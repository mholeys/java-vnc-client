package uk.co.mholeys.vnc.data;

public class KeyboardUpdate {

	public int key;
	public boolean pressed;

	public KeyboardUpdate(int key, boolean pressed) {
		this.key = key;
		this.pressed = pressed;
	}
	
	public KeyboardUpdate clone() {
		return new KeyboardUpdate(key, pressed);
	}
	
}
