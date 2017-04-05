package uk.co.mholeys.vnc.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientInitMessage extends SendMessage {

	public ClientInitMessage(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte(1);
	}

}
