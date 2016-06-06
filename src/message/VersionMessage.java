package message;

import java.net.Socket;

public class VersionMessage extends Message {

	public VersionMessage(Socket socket, int major, int minor) {
		super(socket);
	}
	
}
