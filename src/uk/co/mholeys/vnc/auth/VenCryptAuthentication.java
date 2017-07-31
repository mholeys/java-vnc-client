package uk.co.mholeys.vnc.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;

public class VenCryptAuthentication extends Authentication {

	
	public final static byte VERSION_MAJOR = 0;
	public final static byte VERSION_MINOR = 2;
	
	public VenCryptAuthentication(Socket socket, InputStream in, OutputStream out, String password) throws IOException {
		super(socket, in, out, password);
	}

	@Override
	public boolean authenticate() throws IOException {
		Logger.logger.verboseLn("Reading server version");
		Logger.logger.debugLn("Reading server major version");
		byte serverMajor = dataIn.readByte();
		Logger.logger.debugLn("Reading server minor version");
		byte serverMinor = dataIn.readByte();
		if (serverMajor < VERSION_MAJOR || serverMinor < VERSION_MINOR) {
			Logger.logger.printLn("Versions do not match so cannot authenticate");
			return false;
		}
		dataOut.writeByte(VERSION_MAJOR);
		dataOut.writeByte(VERSION_MINOR);
		Logger.logger.debugLn("Reading acknowledgment");
		byte ack = dataIn.readByte();
		if (ack != 0) {
			return false;
		}
		Logger.logger.debugLn("Reading subtype count");
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
			Logger.logger.debugLn("Reading subtype");
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
		
		Logger.logger.verboseLn("Failed to autheticate using VenCryptAuth, cause unimplemented");
		return false;
	}

	@Override
	public int getSecurityId() {
		return 0;
	}

}
