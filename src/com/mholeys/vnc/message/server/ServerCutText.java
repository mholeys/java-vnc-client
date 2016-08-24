package com.mholeys.vnc.message.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.mholeys.vnc.log.Logger;

public class ServerCutText extends ClientReceiveMessage {

	public int length;
	
	public ServerCutText(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public int getId() {
		return 3;
	}

	@Override
	public Object receiveMessage() throws IOException {
		Logger.logger.debugLn("Reading 3 bytes of padding");
		dataIn.readByte();
		dataIn.readByte();
		dataIn.readByte();
		
		Logger.logger.debugLn("Reading length");
		length = dataIn.readInt();
		StringBuilder sb = new StringBuilder();
		Logger.logger.debugLn("Reading text");
		for (int i = 0; i < length; i++) {
			sb.append(new String(new byte[] {dataIn.readByte()}));
		}
		return sb.toString();
	}

}
