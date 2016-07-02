package com.mholeys.vnc.message;

import java.io.IOException;
import java.net.Socket;

public class ClientInitMessage extends SendMessage {

	public ClientInitMessage(Socket socket) {
		super(socket);
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte(1);
	}

}
