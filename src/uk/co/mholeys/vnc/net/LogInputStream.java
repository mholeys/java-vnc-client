package uk.co.mholeys.vnc.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.mholeys.vnc.log.Logger;

public class LogInputStream extends FilterInputStream {

	public static boolean print = true;
	public Logger logger;
	
	public LogInputStream(InputStream in) {
		super(in);
		logger = Logger.logger;
	}
	
	@Override
	public int read() throws IOException {
		int  i = super.read();
		if (print) {
			String h = Integer.toHexString(i);
			int l = h.length();
			if (l > 2) {
				h = h.substring(l, l);
			} else if (l == 1) {
				h = "0"+h;
			}
			
			logger.debug(h + "\n");
		}
		return i;
	}
	
	@Override
	public int read(byte b[], int off, int len) throws IOException {
		int i = super.read(b, off, len);
		if (print) {
			for (int k = off; k < len; k++) {
				String h = Integer.toHexString(b[k]);
				int l = h.length();
				if (l > 2) {
					h = h.substring(l-2, l);
				} else if (l == 1) {
					h = "0"+h;
				}
				logger.debug(h);
			}
		}
		logger.debugLn("");
		return i;
	}

}
