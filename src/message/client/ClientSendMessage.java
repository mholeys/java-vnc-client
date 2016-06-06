package message.client;

import java.net.Socket;

import message.SendMessage;

public abstract class ClientSendMessage extends SendMessage {

	public ClientSendMessage(Socket socket) {
		super(socket);
	}
	
	public abstract int getId();

}
