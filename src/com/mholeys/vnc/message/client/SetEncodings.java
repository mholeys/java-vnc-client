package com.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.mholeys.vnc.data.Encoding;
import com.mholeys.vnc.data.EncodingSettings;

public class SetEncodings extends ClientSendMessage {

	public ArrayList<Integer> encodings = new ArrayList<Integer>();
	
	public SetEncodings(Socket socket, InputStream in, OutputStream out) {
	super(socket, in, out);
	}
	
	public void addEncoding(int e) {
		this.encodings.add(e);
	}
	
	public void addEncoding(Encoding e) {
		this.encodings.add(e.getStartID());
	}
	
	public void addEncodings(EncodingSettings encodings) {
		for (Encoding e : encodings.getEncodings()) {
			this.encodings.add(e.getStartID());
		}
	}
	
	public void setEncodings(EncodingSettings encodings) {
		this.encodings.clear();
		addEncodings(encodings);
	}

	@Override
	public void sendMessage() throws IOException {
		if (encodings == null || encodings.size() == 0) {
			return;
		}
		dataOut.writeByte((byte)getId());
		dataOut.writeByte(0);
		dataOut.writeShort(encodings.size());
		for (int i = 0; i < encodings.size(); i++) {
			dataOut.writeInt(encodings.get(i));
		}
	}

	@Override
	public int getId() {
		return 2;
	}

}
