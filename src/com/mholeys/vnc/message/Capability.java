package com.mholeys.vnc.message;

import java.io.DataInputStream;
import java.io.IOException;

public class Capability {

	public int code;
	public String vendor;
	public String signature;
	
	public void read(DataInputStream dataIn) throws IOException {
		code = dataIn.readInt();
		byte[] d = new byte[4];
		dataIn.read(d);
		vendor = new String(d);
		d = new byte[8];
		dataIn.read(d);
		signature = new String(d);
	}
	
	public String toString() {
		return code + " " + vendor + " " + signature;
	}
	
}
