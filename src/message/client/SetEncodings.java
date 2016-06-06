package message.client;

import java.io.IOException;
import java.net.Socket;

public class SetEncodings extends ClientSendMessage {

	public int[] encodings;
	
	public SetEncodings(Socket socket) {
		super(socket);
	}

	@Override
	public void sendMessage() throws IOException {
		if (encodings == null || encodings.length == 0) {
			return;
		}
		dataOut.writeByte((byte)getId());
		dataOut.writeByte(0);
		dataOut.writeShort(encodings.length);
		for (int i = 0; i < encodings.length; i++) {
			dataOut.writeInt(encodings[i]);
		}
	}

	@Override
	public int getId() {
		return 2;
	}

}
