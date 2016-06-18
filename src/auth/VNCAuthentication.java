package auth;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import util.ByteUtil;

public class VNCAuthentication extends Authentication {
	
	public VNCAuthentication(Socket socket, String[] args) throws IOException {
		super(socket, args);
	}

	@Override
	public boolean authenticate() throws IOException {
		byte[] challenge = new byte[16];
		dataIn.read(challenge);
		
		String password = args[0];
		
		byte [] key = new byte[8];
        System.arraycopy(password.getBytes(), 0, key, 0, Math.min(key.length, password.getBytes().length));
		try {
			DESKeySpec desKeySpec = new DESKeySpec(ByteUtil.reverseBitsInBytes(key));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
			desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] result = desCipher.doFinal(challenge);
			dataOut.write(result);
		} catch (Exception e) {
			System.err.println("Failed to connect to server because of problem responding to connection challenge.");
			e.printStackTrace();
		}

        int success = -1;
		success = dataIn.readInt();
		if (success == 0) {
			return true;
		}
		return false;
	}

	@Override
	public int getSecurityId() {
		return 2;
	}
	
	

}
