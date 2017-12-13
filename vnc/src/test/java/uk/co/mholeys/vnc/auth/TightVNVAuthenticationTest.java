package src.uk.co.mholeys.vnc.auth;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import uk.co.mholeys.vnc.auth.TightVNCAuthentication;

public class TightVNVAuthenticationTest {

	final String SHORT_PASSWORD = "password";
	final String NORMAL_PASSWORD = "password";
	final String LONG_PASSWORD = "passward with length > 8";
	final String ODD_PASSWORD = "!((&&:~@}+?>h!¬_-*";
	final String[] TEST_PASSWORDS = {SHORT_PASSWORD, NORMAL_PASSWORD, LONG_PASSWORD, ODD_PASSWORD};
	
	@Test
	public void testCorrectPassword() {
		fail("not implemented");
	}
	
	@Test
	public void testIncorrectPassword() {
		fail("not implemented");
	}
	
	@Test
	public void testNoData() {
		fail("not implemented");
	}
	
	@Test
	public void testReadAuthTypeNone() {
		fail("not implemented");
	}
	
	@Test
	public void testReadAuthTypeVNC() {
		fail("not implemented");
	}
	
	@Test
	public void testReadAuthTypeVenCrypt() {
		fail("not implemented");
	}
	
	@Test
	public void testAuthenticateSubTypeNone() {
		fail("not implemented");
	}
	
	@Test
	public void testAuthenticateSubTypeVNC() {
		fail("not implemented");
	}
	
	@Test
	public void testAuthenticateSubTypeVenCrypt() {
		fail("not implemented");
	}
	
	private static TightVNCAuthentication createVNCAuthentication(String password, byte[] data) {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		TightVNCAuthentication noAuth = null;
		try {
			noAuth = new TightVNCAuthentication(null, null, null, password);
		} catch (IOException e) {
			// Test should fail at this point as the test is setup incorrectly
			e.printStackTrace();
			fail();
		}
		return noAuth;
	}

}
