package src.uk.co.mholeys.vnc.data;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.mholeys.vnc.data.KeyboardUpdate;

public class KeyboardUpdateTest {

	static final int KEY_CODE = 123;
	
	@Test
	public void testPressed() {
		KeyboardUpdate ku = new KeyboardUpdate(KEY_CODE, true);
		
		assertEquals(ku.key, KEY_CODE);
		assertEquals(ku.pressed, true);
	}
	
	@Test
	public void testNotPressed() {
		KeyboardUpdate ku = new KeyboardUpdate(KEY_CODE, false);
		
		assertEquals(ku.key, KEY_CODE);
		assertEquals(ku.pressed, false);
	}

}
