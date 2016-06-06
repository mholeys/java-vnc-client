import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import auth.VNCEncrypt;


/**
 * Made using https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst
 * @author Matthew Holey
 *
 */
public class Main {

	public static void main(String[] args) {
		new Main("192.168.0.2", 5901, "superuse");
	}
	
	public Main(String address, int port, String password) {
		try {
			socket = new Socket(InetAddress.getByName(address), port);
			out = socket.getOutputStream();
			dataOut = new DataOutputStream(out);
			in = socket.getInputStream();
			dataIn = new DataInputStream(in);
			this.password = password;
			handshake();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	private String password;
	
	public void handshake() {
		System.out.println(new String(readBytes(12)));
		String version = "RFB 003.007\n";
		sendBytes(version.getBytes());

		//Get number of security types
		int number = readBytes(1)[0];
		System.out.println(number + " types of security");
		
		if (number == 0) {
			System.out.println("Connection error");
			int length = bytesToInt(readBytes(4));
			System.out.println(new String(readBytes(length)));
		}
		
		boolean invalid = false, none = false, vnc_auth = false, tight = false, realvnc = false;
		
		//Get the different types
		byte[] types = readBytes(number);
		
		for (int i = 0; i < types.length; i++) {
			System.out.println(types[i]);
			switch (types[i] & 0xFF) {
			case 0:
				invalid = true;
				break;
			case 1:
				none = true;
				break;
			case 2:
				vnc_auth = true;
				break;
			case 3:
			case 4:
				realvnc = true;
			case 16:
				tight = true;
				break;
			}
		}
		System.out.println("Invalid " + invalid);
		System.out.println("None " + none);
		System.out.println("VNC " + vnc_auth);
		System.out.println("TightVNC " + tight);
		System.out.println("RealVNC " + realvnc);
		
		//Send the vnc auth type for now
		sendBytes(new byte[] {2});
		
		//Compute the stuff needed for vnc authentication
		VNCAuth(password);
		
		//Get the Security Result
		int result = bytesToInt(readBytes(4));
		System.out.println("Result " + result);
		if (result != 0) {
			System.out.println(new String(readBytes(100)));
		}
		
		//Entering init phase
		
		//Tell server to kick other clients
		sendBytes(new byte[] {0});				
		
		
		
		/*
		 * Explains red,green,blue values
		 * If true-colour-flag is non-zero (true) then the last six items specify how to extract the red, green and blue intensities from the pixel value. Red-max is the maximum red value (= 2^n - 1 where n is the number of bits used for red). Note this value is always in big endian order. Red-shift is the number of shifts needed to get the red value in a pixel to the least significant bit. Green-max, green-shift and blue-max, blue-shift are similar for green and blue. For example, to find the red value (between 0 and red-max) from a given pixel, do the following:
		 * Swap the pixel value according to big-endian-flag (e.g. if big-endian-flag is zero (false) and host byte order is big endian, then swap).
		 * Shift right by red-shift. 
		 * AND with red-max (in host byte order).
		 * 
		 * If true-colour-flag is zero (false) then the server uses pixel values which are not directly composed from the red, green and blue intensities, but which serve as indices into a colour map. Entries in the colour map are set by the server using the SetColourMapEntries message (SetColourMapEntries).
		 */
		
		int nameLength = bytesToInt(readBytes(4));
		String name = new String(readBytes(nameLength));
		System.out.println("Connected to: " + name);
		System.out.println("Size: " + framebufferWidth +","+ framebufferHeight);
		
		
	}
	
	public void VNCAuth(String pass) {
		byte[] challenge = readBytes(16);
		printBits(challenge);
		
		if (pass.length() > 8) {
			pass = pass.substring(0, 8);
		}
		int firstZero = pass.indexOf(0);
	    if (firstZero != -1)
	    	pass = pass.substring(0, firstZero);
		byte[] ekey = {0, 0, 0, 0, 0, 0, 0, 0};
	    System.arraycopy(pass.getBytes(), 0, ekey, 0, pass.length());
		
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
		
		printBits(r);
		sendBytes(r);
		
		/*DesCipher des = new DesCipher(ekey);

	    des.encrypt(challenge, 0, challenge, 0);
	    des.encrypt(challenge, 8, challenge, 8);
	    printBits(challenge);
	    sendBytes(challenge);*/
	    
		
		/*try {
			if (pass.length() > 8) {
				pass = pass.substring(0, 8);
			}
			byte[] ekey = {0, 0, 0, 0, 0, 0, 0, 0};
		    System.arraycopy(reverseBits(pass.getBytes()), 0, ekey, 0, pass.length());
			
	        Key key = new SecretKeySpec(ekey, "DES"); 
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] response = new byte[16];
			byte[] r = cipher.doFinal(challenge);
		    System.arraycopy(r, r.length-16, response, 0, 16);
		    printBits(response);
		    
		    sendBytes(response);
		    
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}*/
	}
	
	public void sendBytes(byte[] data) {
		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] readBytes(int number) {
	    byte[] data = new byte[number];
	    try {
			in.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return data;
	}
	
	
	public byte[] reverseBits(byte[] bytes) {
		byte[] r = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			for (int b = 0; b < 8; b++) {
				byte bit = (byte) ((bytes[i]>>b) & 0x01);
				r[i] = (byte) (r[i]<<1);
				if (bit == 1) {
					r[i] += 1;
				}
			}
		}
		return r;
	}
	
	public int bytesToInt(byte[] bytes) {
		if (bytes.length == 4) {
			byte[] l = readBytes(4);
			int length = l[0] << 3 & l[1] << 2 & l[2] << 1 & l[3]; 
			return length;
		}
		if (bytes.length == 3) {
			byte[] l = readBytes(4);
			int length = l[0] << 3 & l[1] << 2 & l[2] << 1; 
			return length;
		}
		if (bytes.length == 2) {
			byte[] l = readBytes(4);
			int length = l[0] << 3 & l[1] << 2; 
			return length;
		}
		if (bytes.length == 1) {
			byte[] l = readBytes(4);
			int length = l[0]; 
			return length;
		}
		return -1;
	}
	
	public void printBits(byte[] b) {
		String s = "";
		for (int i = 0; i < b.length; i++) {
			for (int bit = 0; bit < 8; bit++) {
				s += ""+ ((b[i] & 1<<bit)>>bit);
			}
			s += " ";
		}
		System.out.println(s);
	}
	
}
