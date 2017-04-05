package uk.co.mholeys.vnc.message;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VersionMessage extends Message {

	public VersionMessage(Socket socket, InputStream in, OutputStream out, int major, int minor) {
		super(socket, in, out);
	}
	
}
