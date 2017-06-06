package uk.co.mholeys.vnc.encoding;

import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.display.UpdateManager;

public abstract class Encode {
	
	protected UpdateManager render;

	public abstract void readEncoding(InputStream in) throws IOException;
	
	public void setRender(UpdateManager render) {
		this.render = render;
	}
	
}