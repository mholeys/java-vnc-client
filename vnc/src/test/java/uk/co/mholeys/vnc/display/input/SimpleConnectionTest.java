package uk.co.mholeys.vnc.display.input;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import uk.co.mholeys.vnc.data.EncodingSettings;
import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.display.input.FixedPassword;
import uk.co.mholeys.vnc.display.input.SimpleConnection;

public class SimpleConnectionTest {

	@Test
	public void testGetPort() {
		int port = 5900;
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, port, null, null, null);
			assertEquals(c.getPort(), port);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testGetAddress() {
		String address = "127.0.0.1";
		SimpleConnection c;
		try {
			InetAddress addressResult = InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
			c = new SimpleConnection(address, 0, null, null, null);
			assertEquals(c.getAddress(), addressResult);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testHasEncodingSettings() {
		EncodingSettings es = EncodingSettings.DEFAULT_ENCODINGS;
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, es, null, null);
			assertTrue(c.hasPrefferedEncoding());
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testNoEncodingSettings() {
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, null, null, null);
			assertFalse(c.hasPrefferedEncoding());
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testGetEncodingSettings() {
		EncodingSettings es = EncodingSettings.DEFAULT_ENCODINGS;
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, es, null, null);
			assertEquals(c.getPrefferedEncoding(), es);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testHasPixelFormat() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT;
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, null, format, null);
			assertTrue(c.hasPrefferedFormat());
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testNoPixelFormat() {
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, null, null, null);
			assertFalse(c.hasPrefferedFormat());
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testGetPixelFormat() {
		PixelFormat format = PixelFormat.DEFAULT_FORMAT;
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, null, format, null);
			assertEquals(c.getPrefferedFormat(), format);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testGetPassword() {
		FixedPassword p = new FixedPassword("");
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, null, null, p);
			assertEquals(c.getPasswordRequester(), p);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void testNullPassword() {
		SimpleConnection c;
		try {
			c = new SimpleConnection(null, 0, null, null, null);
			assertEquals(c.getPasswordRequester(), null);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
}
