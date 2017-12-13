package src.uk.co.mholeys.vnc.display.input;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.mholeys.vnc.display.input.FixedPassword;

public class FixedPasswordTest {

	@Test
	public void testLongPassword() {
		FixedPassword fp = new FixedPassword("passwordpasswordpasswordpasswordpasswordpassword");
		assertEquals(fp.getPassword(), "passwordpasswordpasswordpasswordpasswordpassword");
	}
	
	@Test
	public void testShortPassword() {
		FixedPassword fp = new FixedPassword("password");
		assertEquals(fp.getPassword(), "password");
	}
	
	@Test
	public void testEmptyPassword() {
		FixedPassword fp = new FixedPassword("");
		assertEquals(fp.getPassword(), "");
	}
	
	@Test
	public void testNullPassword() {
		FixedPassword fp = new FixedPassword(null);
		assertEquals(fp.getPassword(), null);
	}
	
}
