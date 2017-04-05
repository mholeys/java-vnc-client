package uk.co.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientCutText extends ClientSendMessage {

	public String text;
	
	public ClientCutText(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public void sendMessage() throws IOException {
		if (text.endsWith("\r\n")) {
			text = text.substring(0, text.length()-2) + "\n";
		} else if (!text.endsWith("\n")) {
			text = text + "\n";
		}
		dataOut.writeByte((byte)getId());
		dataOut.writeByte(0);
		dataOut.writeByte(0);
		dataOut.writeByte(0);
		dataOut.writeInt(text.length());
		dataOut.writeBytes(text);
	}

	@Override
	public int getId() {
		return 6;
	}

}