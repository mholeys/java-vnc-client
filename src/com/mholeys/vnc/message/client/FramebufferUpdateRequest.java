package com.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FramebufferUpdateRequest extends ClientSendMessage {

	public short x, y, width, height;
	public byte incremental;
	
	public FramebufferUpdateRequest(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte((byte)getId());
		dataOut.writeByte(incremental); // Incremental
		dataOut.writeShort(x); // x-pos
		dataOut.writeShort(y); // y-pos
		dataOut.writeShort(width); // width
		dataOut.writeShort(height); // height
	}

	@Override
	public int getId() {
		return 3;
	}

}
