package uk.co.mholeys.vnc.message;

import java.io.DataInputStream;
import java.io.IOException;

import uk.co.mholeys.vnc.log.Logger;

public class Capability {

	public int code;
	public String vendor;
	public String signature;
	
	public void read(DataInputStream dataIn) throws IOException {
		Logger.logger.debugLn("Reading capability's code");
		code = dataIn.readInt();
		Logger.logger.debugLn(""+code);
		byte[] d = new byte[4];
		Logger.logger.debugLn("Reading capability's vendor");
		dataIn.read(d);
		vendor = new String(d);
		Logger.logger.debugLn(vendor);
		d = new byte[8];
		Logger.logger.debugLn("Reading capability's signature");
		dataIn.read(d);
		signature = new String(d);
		Logger.logger.debugLn(signature);
	}
	
	public String toString() {
		return code + " " + vendor + " " + signature;
	}
	
}
