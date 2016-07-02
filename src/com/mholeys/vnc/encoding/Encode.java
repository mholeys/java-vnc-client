package com.mholeys.vnc.encoding;

import java.io.IOException;
import java.io.InputStream;

import com.mholeys.vnc.display.IScreen;

public abstract class Encode {
	
	protected IScreen screen;

	public abstract void readEncoding(InputStream in) throws IOException;
	
	public void setScreen(IScreen screen) {
		this.screen = screen;
	}
	
}
