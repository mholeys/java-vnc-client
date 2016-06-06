package message;

import java.io.IOException;
import java.net.Socket;

public abstract class SendMessage extends Message {

	public SendMessage(Socket socket) {
		super(socket);
	}

	public abstract void sendMessage() throws IOException;
	
}
