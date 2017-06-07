package uk.co.mholeys.vnc.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.message.Capability;

public class TightVNCAuthentication extends Authentication {

	boolean none = false;
	boolean vnc = false;
	boolean vencrypt = false;
	boolean sasl = false; 
	boolean unix = false; 
	boolean external = false;
	
	public TightVNCAuthentication(Socket socket, InputStream in, OutputStream out, String password) throws IOException {
		super(socket, in, out, password);
	}

	@Override
	public boolean authenticate() throws IOException {
		Logger.logger.debugLn("Reading tunnel count");
		int tunnelCount = dataIn.readInt();
		boolean noTunnel = false;
		for (int i = 0 ; i < tunnelCount; i++) {
			Logger.logger.debugLn("Reading tunnel code");
			int code = dataIn.readInt();
			byte[] d = new byte[4];
			Logger.logger.debugLn("Reading vendor");
			dataIn.read(d);
			String vendor = new String(d);
			d = new byte[8];
			Logger.logger.debugLn("Reading signature");
			dataIn.read(d);
			String signature = new String(d);
			if (code == 0) {
				if (vendor.equals("TGHT")) {
					if (signature.equals("NOTUNNEL")) {
						noTunnel = true;
					}
				}
			}
		}
		if (tunnelCount != 0) {
			if (noTunnel) {
				dataOut.writeInt(0);
			}
		}
		
		Logger.logger.debugLn("Reading number of authentications");
		int authTypeCount = dataIn.readInt();
		readAuthType(authTypeCount);
		
		return authenticateSubType(authTypeCount);
	}
	
	public void readAuthType(int authTypeCount) throws IOException {
		for (int i = 0; i < authTypeCount; i++) {
			Logger.logger.verboseLn("Reading capability");
			Capability c = new Capability();
			c.read(dataIn);
			if (c.code == 1 && c.vendor.equals("STDV") && c.signature.equals("NOAUTH")) {
				none = true;
			} else if (c.code == 2 && c.vendor.equals("STDV") && c.signature.contains("VNCAUTH")) {
				vnc = true;
			} else if (c.code == 19 && c.vendor.equals("VENC") && c.signature.equals("VENCRYPT")) {
				vencrypt = true;
			} else if (c.code == 20 && c.vendor.equals("GTKV") && c.signature.contains("SASL")) {
				sasl = true;
			} else if (c.code == 129 && c.vendor.equals("TGHT") && c.signature.equals("ULGNAUTH")) {
				unix = true;
			} else if (c.code == 130 && c.vendor.equals("TGHT") && c.signature.equals("XTRNAUTH")) {
				external = true;
			}
		}
	}

	public boolean authenticateSubType(int authTypeCount) throws IOException {
		Authentication auth = null;
		if (vnc) {
			auth = new VNCAuthentication(socket, in, out, password);
		} else if (none) {
			auth = new NoAuthentication(socket, in, out, password);
		} else if (authTypeCount == 0) {
			auth = new NoAuthentication(socket, in, out, password);
		}
		if (auth != null) {
			dataOut.writeInt(auth.getSecurityId());
			return auth.authenticate();
		} else {
			return false;
		}
	}
	
	@Override
	public int getSecurityId() {
		return 16;
	}

}
