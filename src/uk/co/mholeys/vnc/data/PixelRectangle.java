package uk.co.mholeys.vnc.data;

import uk.co.mholeys.vnc.encoding.Decoder;

public class PixelRectangle {

	public short x, y, width, height;
	public int encodingType;
	public Decoder encode;
	
	public PixelRectangle clone() {
		PixelRectangle r = new PixelRectangle();
		r.x = x;
		r.y = y;
		r.width = width;
		r.height = height;
		r.encodingType = encodingType;
		try {
			r.encode = encode.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return r;
	}
	
}
