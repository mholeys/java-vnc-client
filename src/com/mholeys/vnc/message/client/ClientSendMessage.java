package com.mholeys.vnc.message.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.mholeys.vnc.message.SendMessage;

public abstract class ClientSendMessage extends SendMessage {

	public ClientSendMessage(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}
	
	public abstract int getId();

}
