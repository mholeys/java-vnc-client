package uk.co.mholeys.vnc.display.input;

public class FixedPassword implements IPasswordRequester {

	String password;
	
	public FixedPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getPassword() {
		return password;
	}

}
