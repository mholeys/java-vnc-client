package uk.co.mholeys.vnc.auth;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This abstract class is the superclass for all of 
 * the VNC authentication methods.
 * <p>
 * The authentication system works as follows:
 *  The method of authentication is read from the connection, via 
 *  a integer identifier for each type. Then the correct method is
 *  found via its {@code getSecurityId()} return value.
 *  After this the {@code authenticate()} method is called.
 *  
 * @see NoAuthentication
 * @see TightVNCAuthentication
 * @see VNCAuthentication
 * 
 * @author Matthew Holey
 *
 */
public abstract class Authentication {

	/** The VNC connection socket */
	protected Socket socket;
	/** The input stream of the connection */
	protected InputStream in;
	/** The output stream of the connection */
	protected OutputStream out;
	/** The easy to use data input stream of the connection */
	protected DataInputStream dataIn;
	/** The easy to use data output stream of the connection */
	protected DataOutputStream dataOut;
	/** The password that the user has entered to use for authenticating */
	protected String password;
	
	/**
	 * Authenticator constructor to setup all needed streams.
	 * @param socket - The socket of the connection
	 * @param in - The input stream of the connection
	 * @param out - The output stream of the connection 
	 * @param password - The password to use when authenticating (if a password/key is required)
	 */
	public Authentication(Socket socket, InputStream in, OutputStream out, String password) {
		this.socket = socket;
		this.in = in;
		this.out = out;
		dataIn = new DataInputStream(in);
		dataOut = new DataOutputStream(out);
		this.password = password;
	}
	
	/**
	 * Authentication method, that will perform the authentication handshake.  
	 * <p>
	 * This will use the password given (if the authentication method uses it)
	 * to agree with the server if the client has entered the correct password and 
	 * has the privilege to access the server.
	 * A subclass must provide an implementation of this method
	 * @return If the authentication was successful or not 
	 * @throws IOException Will only be thrown if the subclass implements network
	 * 					   communication, and causes an exception on the network.
	 */
	public abstract boolean authenticate() throws IOException;
	
	/**
	 * @return The id of this authentication type. Normally defined in the VNC protocol list of approved ids.
	 */
	public abstract int getSecurityId();
	
}
