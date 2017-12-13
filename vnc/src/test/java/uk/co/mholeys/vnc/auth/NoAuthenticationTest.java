package src.uk.co.mholeys.vnc.auth;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import uk.co.mholeys.vnc.auth.NoAuthentication;

public class NoAuthenticationTest {
	
	@Test
	public void alwaysAuthenticateAsSucessful() {
		NoAuthentication auth = createNoAuthentication();
		
		try {
			assertTrue(auth.authenticate());
		} catch (IOException e) {
			fail();
		}
	}

	private static NoAuthentication createNoAuthentication() {
		NoAuthentication noAuth = null;
		try {
			noAuth = new NoAuthentication(null, null, null);
		} catch (IOException e) {
			// Test should fail at this point as the test is setup incorrectly
			e.printStackTrace();
			fail();
		}
		return noAuth;
	}

}
