package com.mholeys.vnc.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Message {

	protected Socket socket;
	protected InputStream in;
	protected OutputStream out;
	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	
	public Message(Socket socket, InputStream in, OutputStream out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.dataIn = new DataInputStream(in);
		this.dataOut = new DataOutputStream(out);
	}
	
}
