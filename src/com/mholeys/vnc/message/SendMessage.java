package com.mholeys.vnc.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class SendMessage extends Message {

	public SendMessage(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	public abstract void sendMessage() throws IOException;
	
}
