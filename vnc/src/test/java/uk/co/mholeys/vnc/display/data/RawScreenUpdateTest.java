package uk.co.mholeys.vnc.display.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.mholeys.vnc.display.data.RawScreenUpdate;

public class RawScreenUpdateTest {

	@Test
	public void testAcceptNormalValues() {
		int[] pixels = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9};
		RawScreenUpdate rsu = new RawScreenUpdate(10, 10, 200, 100, pixels);
		
		assertEquals(rsu.x, 10);
		assertEquals(rsu.y, 10);
		assertEquals(rsu.width, 200);
		assertEquals(rsu.height, 100);
		assertArrayEquals(rsu.pixels, pixels);
	}
	
	@Test
	public void testAcceptNegValues() {
		int[] pixels = new int[] { -1000 };
		RawScreenUpdate rsu = new RawScreenUpdate(-10, -10, -200, -100, pixels);
		
		assertEquals(rsu.x, -10);
		assertEquals(rsu.y, -10);
		assertEquals(rsu.width, -200);
		assertEquals(rsu.height, -100);
		assertArrayEquals(rsu.pixels, pixels);
	}
	
	@Test
	public void testAcceptNullData() {
		RawScreenUpdate rsu = new RawScreenUpdate(10, 10, 200, 100, null);
		
		assertEquals(rsu.x, 10);
		assertEquals(rsu.y, 10);
		assertEquals(rsu.width, 200);
		assertEquals(rsu.height, 100);
		assertArrayEquals(rsu.pixels, null);
	}
	
}
