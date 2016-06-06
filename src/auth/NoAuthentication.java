package auth;

import java.io.IOException;
import java.net.Socket;

public class NoAuthentication extends Authentication {

	public NoAuthentication(Socket socket, String[] args) throws IOException {
		super(socket, args);
	}

	@Override
	public boolean authenticate() throws IOException {
		return true;
	}

	@Override
	public int getSecurityId() {
		return 1;
	}

}
