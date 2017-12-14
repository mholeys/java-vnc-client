package uk.co.mholeys.vnc.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;

/**
 * Authentication class representing the authentication type for the 
 * connection that will always be authenticated successfully.
 * <p>
 * This type of authentication does not read/write any data to the network
 * and it will always authenticate as the server does not require anything
 * method/process to authenticate.
 * @author Matthew Holey
 *
 */
public class NoAuthentication extends Authentication {

	/**
	 * 
	 * @param socket
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public NoAuthentication(Socket socket, InputStream in, OutputStream out) throws IOException {
		super(socket, in, out, null);
	}

	@Override
	public boolean authenticate() throws IOException {
		Logger.logger.verboseLn("Autheticated using NoAuth");
		return true;
	}

	/**
	 * The ID for NoAuthentication is 1.
	 */
	@Override
	public int getSecurityId() {
		return 1;
	}

}
