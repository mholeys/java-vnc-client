package message.server;

import java.io.IOException;
import java.net.Socket;

public class ServerCutText extends ClientReceiveMessage {

	public int length;
	
	public ServerCutText(Socket socket) {
		super(socket);
	}

	@Override
	public int getId() {
		return 3;
	}

	@Override
	public Object receiveMessage() throws IOException {
		dataIn.readByte();
		dataIn.readByte();
		dataIn.readByte();
		
		length = dataIn.readInt();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(new String(new byte[] {dataIn.readByte()}));
		}
		return sb.toString();
	}

}
