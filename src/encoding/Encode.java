package encoding;

import java.io.IOException;
import java.io.InputStream;

public abstract class Encode {

	public abstract int[] getPixels();

	public abstract void readEncoding(InputStream in) throws IOException;
	
}
