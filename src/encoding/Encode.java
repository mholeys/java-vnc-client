package encoding;

import java.io.IOException;
import java.io.InputStream;

import display.FrameBuffer;

public abstract class Encode {

	public abstract int[] getPixels();

	public abstract void readEncoding(InputStream in) throws IOException;
	
	public abstract void setFrameBuffer(FrameBuffer frameBuffer);
	
}
