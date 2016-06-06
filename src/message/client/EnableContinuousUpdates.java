package message.client;

import java.io.IOException;
import java.net.Socket;

public class EnableContinuousUpdates extends ClientSendMessage {

	public boolean enable;
	public short x, y;
	public short width, height;
	
	
	public EnableContinuousUpdates(Socket socket) {
		super(socket);
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
	}

}
