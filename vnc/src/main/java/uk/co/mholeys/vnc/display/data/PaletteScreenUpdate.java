package uk.co.mholeys.vnc.display.data;

public class PaletteScreenUpdate extends ScreenUpdate {

	public int[] palette;
	public int paletteSize;
	public byte[] data;
	
	public PaletteScreenUpdate(int x, int y, int width, int height, int[] palette, int paletteSize, byte[] data) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.palette = palette;
		this.paletteSize = paletteSize;
		this.data = data;
	}

}
