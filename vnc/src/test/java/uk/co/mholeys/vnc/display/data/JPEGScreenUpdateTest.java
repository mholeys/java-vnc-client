package src.uk.co.mholeys.vnc.display.data;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.mholeys.vnc.display.data.JPEGScreenUpdate;

public class JPEGScreenUpdateTest {

	@Test
	public void testAcceptNormalValues() {
		byte[] jpeg = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 111, 111, 127, 10};
		JPEGScreenUpdate jsu = new JPEGScreenUpdate(10, 10, 200, 100, jpeg);
		
		assertEquals(jsu.x, 10);
		assertEquals(jsu.y, 10);
		assertEquals(jsu.width, 200);
		assertEquals(jsu.height, 100);
		assertArrayEquals(jsu.jpegData, jpeg);
	}
	
	@Test
	public void testAcceptNegValues() {
		byte[] jpeg = new byte[] {-127, -127, -127, -127, -127, -127, -127, -127, -127, -127, -127, -127};
		JPEGScreenUpdate jsu = new JPEGScreenUpdate(-10, -10, -200, -100, jpeg);
		
		assertEquals(jsu.x, -10);
		assertEquals(jsu.y, -10);
		assertEquals(jsu.width, -200);
		assertEquals(jsu.height, -100);
		assertArrayEquals(jsu.jpegData, jpeg);
	}
	
	@Test
	public void testAcceptNullData() {
		JPEGScreenUpdate jsu = new JPEGScreenUpdate(10, 10, 200, 100, null);
		
		assertEquals(jsu.x, 10);
		assertEquals(jsu.y, 10);
		assertEquals(jsu.width, 200);
		assertEquals(jsu.height, 100);
		assertArrayEquals(jsu.jpegData, null);
	}
	
}
