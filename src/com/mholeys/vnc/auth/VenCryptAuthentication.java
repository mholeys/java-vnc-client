package com.mholeys.vnc.auth;

import java.io.IOException;
import java.net.Socket;

public class VenCryptAuthentication extends Authentication {

	
	public final static byte VERSION_MAJOR = 0;
	public final static byte VERSION_MINOR = 2;
	
	public VenCryptAuthentication(Socket socket, String password) throws IOException {
		super(socket, password);
	}

	@Override
	public boolean authenticate() throws IOException {
		byte serverMajor = dataIn.readByte();
		byte serverMinor = dataIn.readByte();
		if (serverMajor < VERSION_MAJOR || serverMinor < VERSION_MINOR) {
			return false;
		}
		dataOut.writeByte(VERSION_MAJOR);
		dataOut.writeByte(VERSION_MINOR);
		byte ack = dataIn.readByte();
		if (ack != 0) {
			return false;
		}
		byte subtypeCount = dataIn.readByte();
		int[] subtypes = new int[subtypeCount];
		
		int[] none = new int[4];
		int[] tls = new int[4];
		int[] x509 = new int[4];
		final int NONE = 0;
		final int VNC = 1;
		final int PLAIN = 2;
		final int SASL = 3;
		
		for (int i = 0; i < subtypeCount; i++) {
			subtypes[i] = dataIn.readInt();
			switch (subtypes[i]) {
			case 256:
				//Plain
				none[PLAIN] = subtypes[i];
				break;
			case 257:
				//TLS None
				tls[NONE] = subtypes[i];
				break;
			case 258:
				//TLS VNC
				tls[VNC] = subtypes[i];
				break;
			case 259:
				//TLS Plain
				tls[PLAIN] = subtypes[i];
				break;
			case 260:
				//X509 None
				x509[NONE] = subtypes[i];
				break;
			case 261:
				//X509 VNC
				x509[VNC] = subtypes[i];
				break;
			case 262:
				//X509 Plain
				x509[PLAIN] = subtypes[i];
				break;
			case 263:
				//TLS Sasl
				tls[SASL] = subtypes[i];
				break;
			case 264:
				//X509 Sasl
				x509[SASL] = subtypes[i];
				break;
			}
		}
		
		// TODO add rest of authentication
		// needs tls stuff?
		
		return false;
	}

	@Override
	public int getSecurityId() {
		return 0;
	}

}
