package uk.co.mholeys.vnc.display.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.mholeys.vnc.display.data.PaletteScreenUpdate;

public class PaletteScreenUpdateTest {

	@Test
	public void testAcceptNormalValues() {
		int[] palette = new int[] {3000, 1222, 1245, 10};
		int paletteSize = palette.length;
		byte[] data = new byte[] { 1, 2, 3, 4, 5, 6};
		PaletteScreenUpdate psu = new PaletteScreenUpdate(10, 10, 200, 100, palette, paletteSize, data);
		
		assertEquals(psu.x, 10);
		assertEquals(psu.y, 10);
		assertEquals(psu.width, 200);
		assertEquals(psu.height, 100);
		
		assertArrayEquals(psu.palette, palette);
		assertEquals(psu.paletteSize, paletteSize);
		assertArrayEquals(psu.data, data);
	}
	
	@Test
	public void testAcceptNegValues() {
		int[] palette = new int[] {-3000, -1222, -1245, -10};
		int paletteSize = palette.length;
		byte[] data = new byte[] { 1, 2, 3, 4, 5, 6};
		PaletteScreenUpdate psu = new PaletteScreenUpdate(-10, -10, -200, -100, palette, paletteSize, data);
		
		assertEquals(psu.x, -10);
		assertEquals(psu.y, -10);
		assertEquals(psu.width, -200);
		assertEquals(psu.height, -100);
		assertArrayEquals(psu.palette, palette);
		assertEquals(psu.paletteSize, paletteSize);
		assertArrayEquals(psu.data, data);
	}
	
	@Test
	public void testAcceptNullData() {
		PaletteScreenUpdate psu = new PaletteScreenUpdate(10, 10, 200, 100, null, 0, null);
		
		assertEquals(psu.x, 10);
		assertEquals(psu.y, 10);
		assertEquals(psu.width, 200);
		assertEquals(psu.height, 100);
		
		assertArrayEquals(psu.palette, null);
		assertEquals(psu.paletteSize, 0);
		assertArrayEquals(psu.data, null);
	}
	
}
