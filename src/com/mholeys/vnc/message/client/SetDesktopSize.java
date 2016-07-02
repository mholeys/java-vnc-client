package com.mholeys.vnc.message.client;

import java.io.IOException;
import java.net.Socket;

public class SetDesktopSize extends ClientSendMessage {
	
	public short width, height;
	//TODO add screens
	// https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst#setdesktopsize
	
	public SetDesktopSize(Socket socket) {
		super(socket);
	}

	@Override
	public int getId() {
		return 251;
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte((byte)getId());
		dataOut.writeByte((byte)0);
		
	}

}
