package message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Message {

	protected Socket socket;
	protected InputStream in;
	protected OutputStream out;
	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	
	public Message(Socket socket) {
		this.socket = socket;
		try {
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.dataIn = new DataInputStream(in);
		this.dataOut = new DataOutputStream(out);
	}
	
}
