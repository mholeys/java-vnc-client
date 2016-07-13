package com.mholeys.vnc.display;

public class FixedPassword implements IPasswordRequester {

	private String password;
	
	public FixedPassword(String password) { 
		this.password = password;
	}
	
	@Override
	public String getPassword() {
		return password;
	}

}
