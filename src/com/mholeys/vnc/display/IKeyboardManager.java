package com.mholeys.vnc.display;

import com.mholeys.vnc.data.KeyboardUpdate;

public interface IKeyboardManager {

	public boolean sendKeys();
	
	public KeyboardUpdate getNext();
	
}
