package uk.co.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;

public class EnableContinuousUpdates extends ClientSendMessage {

	public boolean enable;
	public short x, y;
	public short width, height;
	
	
	public EnableContinuousUpdates(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public int getId() {
		return 150;
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte((byte)getId());
		dataOut.writeBoolean(enable);
		dataOut.writeShort(x);
		dataOut.writeShort(y);
		dataOut.writeShort(width);
		dataOut.writeShort(height);
		
		Logger.logger.verboseLn("EnableContinuousUpdates message sent");
	}

}
