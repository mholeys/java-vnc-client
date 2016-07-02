package com.mholeys.vnc.message;

import java.io.IOException;
import java.net.Socket;

public abstract class RecieveMessage extends Message {

	public RecieveMessage(Socket socket) {
		super(socket);
	}

	public abstract Object receiveMessage() throws IOException;
	
}
