package uk.co.mholeys.vnc.message.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.log.Logger;

public class EndOfContinuousUpdates extends ClientReceiveMessage {

	public EndOfContinuousUpdates(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public int getId() {
		return 150;
	}

	@Override
	public Object receiveMessage() throws IOException { 
		Logger.logger.verboseLn("EndOfContinuousUpdates message received");
		return null;
	}

}
