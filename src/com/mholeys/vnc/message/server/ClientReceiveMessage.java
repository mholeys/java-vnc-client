package com.mholeys.vnc.message.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.mholeys.vnc.message.RecieveMessage;

public abstract class ClientReceiveMessage extends RecieveMessage {
	
	public ClientReceiveMessage(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	public abstract int getId();

}
