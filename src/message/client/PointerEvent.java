package message.client;

import java.io.IOException;
import java.net.Socket;

public class PointerEvent extends ClientSendMessage {

	public byte button;
	public short x, y;
	
	public PointerEvent(Socket socket) {
		super(socket);
	}

	@Override
	public void sendMessage() throws IOException {
		if (button != -1) {
			dataOut.writeByte((byte)getId());
			dataOut.writeByte(button);
			dataOut.writeShort(x);
			dataOut.writeShort(y);
		}
	}

	@Override
	public int getId() {
		return 5;
	}

}
