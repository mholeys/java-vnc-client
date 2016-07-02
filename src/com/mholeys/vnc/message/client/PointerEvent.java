package com.mholeys.vnc.message.client;

import java.io.IOException;
import java.net.Socket;

import com.mholeys.vnc.util.ByteUtil;

public class PointerEvent extends ClientSendMessage {

	public byte button;
	public short x, y;
	
	public PointerEvent(Socket socket) {
		super(socket);
	}

	@Override
	public void sendMessage() throws IOException {
		if (button != -1) {
			dataOut.writeByte((byte)getId());
			dataOut.writeByte(button);
			dataOut.writeShort(x);
			dataOut.writeShort(y);
		}
	}

	@Override
	public int getId() {
		return 5;
	}

	public void setClick(boolean left, boolean right) {
		button = 0;
		if (left) {
			button |= 1; 
		} else {
			button &= 0xFF-1;
		}
		if (right) {
			button |= 2; 
		} else {
			button &= 0xFF-2;
		}
	}

}
