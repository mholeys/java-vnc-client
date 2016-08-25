package com.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PointerEvent extends ClientSendMessage {

	public byte button;
	public short x, y;
	
	public PointerEvent(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
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

	public void setClick(boolean left, boolean right, boolean middle, boolean mwUp, boolean mwDown) {
		button = 0;
		if (left) {
			button |= 1; 
		} else {
			button &= 0xFF-1;
		}
		if (middle) {
			button |= 2; 
		} else {
			button &= 0xFF-2;
		}
		if (right) {
			button |= 4; 
		} else {
			button &= 0xFF-4;
		}
		if (mwUp) {
			button |= 8; 
		} else {
			button &= 0xFF-8;
		}
		if (mwDown) {
			button |= 16; 
		} else {
			button &= 0xFF-16;
		}
	}

}
