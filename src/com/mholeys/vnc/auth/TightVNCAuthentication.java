package com.mholeys.vnc.auth;

import java.io.IOException;
import java.net.Socket;

import com.mholeys.vnc.message.Capability;

public class TightVNCAuthentication extends Authentication {

	public TightVNCAuthentication(Socket socket, String password) throws IOException {
		super(socket, password);
	}

	@Override
	public boolean authenticate() throws IOException {
		int tunnelCount = dataIn.readInt();
		boolean noTunnel = false;
		for (int i = 0 ; i < tunnelCount; i++) {
			int code = dataIn.readInt();
			byte[] d = new byte[4];
			dataIn.read(d);
			String vendor = new String(d);
			d = new byte[8];
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
		boolean none = false, vnc = false, vencrypt = false, sasl = false, unix = false, external = false;
		int authTypeCount = dataIn.readInt();
		for (int i = 0; i < authTypeCount; i++) {
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
		
		Authentication auth = null;
		boolean authenticated = false;
		if (vnc) {
			auth = new VNCAuthentication(socket, password);
		} else if (none) {
			auth = new NoAuthentication(socket, password);
		} else if (authTypeCount == 0) {
			auth = new NoAuthentication(socket, password);
		}
		if (auth != null) {
			dataOut.writeInt(auth.getSecurityId());
			authenticated = auth.authenticate();
		} else {
			return false;
		}
		return authenticated;
	}

	@Override
	public int getSecurityId() {
		return 16;
	}

}
