package com.mholeys.vnc.message.client;

import java.io.IOException;
import java.net.Socket;

import com.mholeys.vnc.data.PixelFormat;

public class SetPixelFormatMessage extends ClientSendMessage {

	public PixelFormat format;
	
	public SetPixelFormatMessage(Socket socket) {
		super(socket);
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte((byte)getId());
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
		
		dataOut.writeByte(format.bitsPerPixel);
		dataOut.writeByte(format.depth);
		dataOut.writeBoolean(format.bigEndianFlag);
		dataOut.writeBoolean(format.trueColorFlag);
		dataOut.writeShort(format.redMax);
		dataOut.writeShort(format.greenMax);
		dataOut.writeShort(format.blueMax);
		dataOut.writeByte(format.redShift);
		dataOut.writeByte(format.greenShift);
		dataOut.writeByte(format.blueShift);
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
	}
	
	@Override
	public int getId() {
		return 0;
	}

}
