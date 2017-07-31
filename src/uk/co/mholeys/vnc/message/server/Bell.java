package uk.co.mholeys.vnc.message.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;

public class Bell extends ClientReceiveMessage {

	public Bell(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public int getId() {
		return 2;
	}

	@Override
	public Object receiveMessage() throws IOException {
		Logger.logger.verboseLn("Bell message received");
		return null;
	}

}
