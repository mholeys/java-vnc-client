package message.server;

import java.io.IOException;
import java.net.Socket;

public class Bell extends ClientReceiveMessage {

	public Bell(Socket socket) {
		super(socket);
	}

	@Override
	public int getId() {
		return 2;
	}

	@Override
	public Object receiveMessage() throws IOException {
		return null;
	}

}
