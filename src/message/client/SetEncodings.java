package message.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class SetEncodings extends ClientSendMessage {

	public ArrayList<Integer> encodings = new ArrayList<Integer>();
	
	public SetEncodings(Socket socket) {
		super(socket);
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
