package com.mholeys.vnc.display;

public class FixedPassword implements IPasswordRequester {

	@Override
	public String getPassword() {
		return "superuse";
	}

}
