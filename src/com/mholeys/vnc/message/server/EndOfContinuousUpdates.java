package com.mholeys.vnc.message.server;

import java.io.IOException;
import java.net.Socket;

public class EndOfContinuousUpdates extends ClientReceiveMessage {

	public EndOfContinuousUpdates(Socket socket) {
		super(socket);
	}

	@Override
	public int getId() {
		return 150;
	}

	@Override
	public Object receiveMessage() throws IOException {
		return null;
	}

}
