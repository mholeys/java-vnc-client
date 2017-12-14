package uk.co.mholeys.vnc.display.data;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.mholeys.vnc.display.data.CopyScreenUpdate;

public class CopyScreenUpdateTest {

	@Test
	public void testAcceptNormalValues() {
		CopyScreenUpdate cu = new CopyScreenUpdate(0, 0, 200, 100, 10, 10);
		assertEquals(cu.xSrc, 0);
		assertEquals(cu.ySrc, 0);
		assertEquals(cu.width, 200);
		assertEquals(cu.height, 100);
		assertEquals(cu.x, 10);
		assertEquals(cu.y, 10);		
	}
	
	@Test
	public void testAcceptZeroValues() {
		CopyScreenUpdate cu = new CopyScreenUpdate(0, 0, 0, 0, 0, 0);
		assertEquals(cu.xSrc, 0);
		assertEquals(cu.ySrc, 0);
		assertEquals(cu.width, 0);
		assertEquals(cu.height, 0);
		assertEquals(cu.x, 0);
		assertEquals(cu.y, 0);		
	}
	
	@Test
	public void testAcceptNegValues() {
		CopyScreenUpdate cu = new CopyScreenUpdate(-1, -2, -3, -4, -5, -6);
		assertEquals(cu.xSrc, -1);
		assertEquals(cu.ySrc, -2);
		assertEquals(cu.width, -3);
		assertEquals(cu.height, -4);
		assertEquals(cu.x, -5);
		assertEquals(cu.y, -6);		
	}
	
}
