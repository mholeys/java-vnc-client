package com.mholeys.vnc.message;

import java.io.DataInputStream;
import java.io.IOException;

import com.mholeys.vnc.log.Logger;

public class Capability {

	public int code;
	public String vendor;
	public String signature;
	
	public void read(DataInputStream dataIn) throws IOException {
		Logger.logger.debugLn("Reading capability's code");
		code = dataIn.readInt();
		byte[] d = new byte[4];
		Logger.logger.debugLn("Reading capability's vendor");
		dataIn.read(d);
		vendor = new String(d);
		d = new byte[8];
		Logger.logger.debugLn("Reading capability's signature");
		dataIn.read(d);
		signature = new String(d);
	}
	
	public String toString() {
		return code + " " + vendor + " " + signature;
	}
	
}
