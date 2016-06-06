package auth;

import java.io.IOException;
import java.net.Socket;

public class VNCAuthentication extends Authentication {
	
	public VNCAuthentication(Socket socket, String[] args) throws IOException {
		super(socket, args);
	}

	@Override
	public boolean authenticate() throws IOException {
		byte[] challenge = new byte[16];
		dataIn.read(challenge);
		
		String password = args[0];
		if (password.length() > 8) {
			password = password.substring(0, 8);
		}
		int firstZero = password.indexOf(0);
	    if (firstZero != -1)
	    	password = password.substring(0, firstZero);
		byte[] ekey = {0, 0, 0, 0, 0, 0, 0, 0};
	    System.arraycopy(password.getBytes(), 0, ekey, 0, password.length());
		
		VNCEncrypt encrypt = new VNCEncrypt(ekey);
		
		byte[] c1 = new byte[8]; 
		byte[] c2 = new byte[8];
		System.arraycopy(challenge, 0, c1, 0, 8);
		System.arraycopy(challenge, 8, c2, 0, 8);
		byte[] s = encrypt.encrypt(c1);
		byte[] e = encrypt.encrypt(c2);
		
		byte[] r = new byte[16];
		System.arraycopy(s, 0, r, 0, 8);
		System.arraycopy(e, 0, r, 8, 8);
		
		dataOut.write(r);
		
		int result = -1;
		result = dataIn.readInt();
		if (result == 0) {
			return true;
		}
		return false;
	}

	@Override
	public int getSecurityId() {
		return 2;
	}
	
	

}
