package uk.co.mholeys.vnc.display.data;

public class LEDOtherUpdate extends ScreenUpdate  {

	boolean scroll = false;
	boolean num = false;
	boolean caps = false;
	
	public LEDOtherUpdate(boolean scroll, boolean num, boolean caps) {
		this.scroll = scroll;
		this.num = num;
		this.caps = caps;
	}
	
}
