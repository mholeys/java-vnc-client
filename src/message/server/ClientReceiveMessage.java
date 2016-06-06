package message.server;

import java.net.Socket;

import message.RecieveMessage;

public abstract class ClientReceiveMessage extends RecieveMessage {
	
	public ClientReceiveMessage(Socket socket) {
		super(socket);
	}

	public abstract int getId();

}
