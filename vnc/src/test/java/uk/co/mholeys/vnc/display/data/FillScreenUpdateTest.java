package uk.co.mholeys.vnc.display.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.mholeys.vnc.display.data.FillScreenUpdate;

public class FillScreenUpdateTest {

	@Test
	public void testAcceptNormalValues() {
		FillScreenUpdate fsu = new FillScreenUpdate(10, 10, 200, 100, 0xFF00FF);
		assertEquals(fsu.x, 10);
		assertEquals(fsu.y, 10);
		assertEquals(fsu.width, 200);
		assertEquals(fsu.height, 100);
		assertEquals(fsu.pixel, 0xFF00FF);
	}
	
	@Test
	public void testAcceptNegValues() {
		FillScreenUpdate fsu = new FillScreenUpdate(-10, -10, -200, -100, 0xFF00FF);
		assertEquals(fsu.x, -10);
		assertEquals(fsu.y, -10);
		assertEquals(fsu.width, -200);
		assertEquals(fsu.height, -100);
		assertEquals(fsu.pixel, 0xFF00FF);
	}
	
}
