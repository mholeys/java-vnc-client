package com.mholeys.vnc.message.server;

import java.io.IOException;
import java.net.Socket;

import com.mholeys.vnc.data.ColourMap;

public class SetColourMapEntries extends ClientReceiveMessage {

	public short numberOfColors;
	public short firstColor;
	
	public SetColourMapEntries(Socket socket) {
		super(socket);
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Object receiveMessage() throws IOException {
		dataIn.readByte();
		firstColor = dataIn.readShort();
		numberOfColors = dataIn.readShort();
		ColourMap[] colours = new ColourMap[numberOfColors];
		for (int i = 0; i < numberOfColors; i++) {
			colours[i] = new ColourMap();
			colours[i].red = dataIn.readShort();
			colours[i].green = dataIn.readShort();
			colours[i].blue = dataIn.readShort();
		}
		return null;
	}

}
