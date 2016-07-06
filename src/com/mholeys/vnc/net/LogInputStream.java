package com.mholeys.vnc.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LogInputStream extends FilterInputStream {

	public static boolean print = false;
	
	public LogInputStream(InputStream in) {
		super(in);
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
			System.out.println(h);
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
				System.out.println(h);
			}
		}
		return i;
	}

}
