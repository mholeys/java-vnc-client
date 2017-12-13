package uk.co.mholeys.vnc.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class RecieveMessage extends Message {

	public RecieveMessage(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	public abstract Object receiveMessage() throws IOException;
	
}
