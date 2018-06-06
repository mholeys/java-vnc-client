package uk.co.mholeys.vnc.encoding;

import java.io.IOException;
import java.io.InputStream;

public class LEDStatePseudoEncoding extends Decoder {

	@Override
	public void readEncoding(InputStream in) throws IOException {
		byte b = (byte) in.read();
		boolean scroll = false, num = false, caps = false;
		if ((b | 0b1) > 0) {
			scroll = true;
		}
		if ((b | 0b10) > 0) {
			num = true;
		}
		if ((b | 0b100) > 0) {
			caps = true;
		}
		
		render.setLEDs(scroll, num, caps);
	}

}
