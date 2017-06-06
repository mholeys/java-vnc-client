package uk.co.mholeys.vnc.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.util.ByteUtil;

public class VNCAuthentication extends Authentication {
	
	public VNCAuthentication(Socket socket, InputStream in, OutputStream out, String password) throws IOException {
		super(socket, in, out, password);
	}

	@Override
	public boolean authenticate() throws IOException {
		byte[] challenge = new byte[16];
		Logger.logger.debugLn("Reading challenge");
		dataIn.read(challenge);
		Logger.logger.debugLn("challenge is " + ByteUtil.convertToBits(challenge));
		
		byte [] key = new byte[8];
        System.arraycopy(password.getBytes(), 0, key, 0, Math.min(key.length, password.getBytes().length));
		try {
			DESKeySpec desKeySpec = new DESKeySpec(ByteUtil.reverseBitsInBytes(key));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
			desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] result = desCipher.doFinal(challenge);
			Logger.logger.debugLn("Sent response to challenge as " + ByteUtil.convertToBits(result));
			dataOut.write(result);
		} catch (Exception e) {
			System.err.println("Failed to connect to server because of problem responding to connection challenge.");
			e.printStackTrace();
		}

        int success = -1;
        Logger.logger.debugLn("Reading result of authentication");
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
