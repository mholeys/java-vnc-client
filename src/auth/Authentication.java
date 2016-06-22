package auth;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public abstract class Authentication {

	protected Socket socket;
	protected InputStream in;
	protected OutputStream out;
	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	protected String password;
	
	public Authentication(Socket socket, String password) throws IOException {
		this.socket = socket;
		in = this.socket.getInputStream();
		out = this.socket.getOutputStream();
		dataIn = new DataInputStream(in);
		dataOut = new DataOutputStream(out);
		this.password = password;
	}
	
	public abstract boolean authenticate() throws IOException;
	
	public abstract int getSecurityId();
	
}
