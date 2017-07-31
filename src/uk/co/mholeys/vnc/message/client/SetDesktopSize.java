package uk.co.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;

public class SetDesktopSize extends ClientSendMessage {
	
	public short width, height;
	//TODO add screens
	// https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst#setdesktopsize
	
	public SetDesktopSize(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public int getId() {
		return 251;
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte((byte)getId());
		dataOut.writeByte((byte)0);
		Logger.logger.verboseLn("SetDesktopSize message sent");
		
	}

}
