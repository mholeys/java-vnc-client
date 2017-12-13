package uk.co.mholeys.vnc.display;

import uk.co.mholeys.vnc.data.KeyboardUpdate;

public interface IKeyboardManager {

	public boolean sendKeys();
	
	public KeyboardUpdate getNext();
	
}
