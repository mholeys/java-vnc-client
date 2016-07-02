package com.mholeys.vnc.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LogInputStream extends FilterInputStream {

	public static boolean print = true;
	
	public LogInputStream(InputStream in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
		int  i = super.read();
		if (print) {
			System.out.println(Integer.toHexString(i));
		}
		return i;
	}
	
	@Override
	public int read(byte b[], int off, int len) throws IOException {
		int i = super.read(b, off, len);
		if (print) {
			for (int k = off; k < len; k++) {
				System.out.println(Integer.toHexString(b[k]));
			}
		}
		return i;
	}

}
