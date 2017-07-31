package uk.co.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;

public class KeyEvent extends ClientSendMessage {

	public boolean pressed;
	public int key; 
	
	public KeyEvent(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte((byte)getId());
		dataOut.writeByte(pressed ? 1 : 0); // down-flag true/false = pressed/released
		dataOut.writeByte(0); // Padding
		dataOut.writeByte(0); // Padding
		dataOut.writeInt(key); // Key
		Logger.logger.verboseLn("KeyEvent message sent");
	}

	@Override
	public int getId() {
		return 4;
	}

}
