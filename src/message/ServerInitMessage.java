package message;

import java.io.IOException;
import java.net.Socket;

import data.Encoding;
import data.PixelFormat;
import data.TightEncodings;

public class ServerInitMessage extends RecieveMessage {

	public int framebufferWidth;
	public int framebufferHeight;
	
	public PixelFormat format;
	
	public boolean tight = false;
	
	public String name;
	
	public ServerInitMessage(Socket socket, boolean tight) {
		super(socket);
		this.tight = tight;
	}

	@Override
	public Object receiveMessage() throws IOException {
		//Get the server init message
		//Includes width(2), height(2), pixel-format(16),
		//name-length(4), name
		framebufferWidth = dataIn.readShort();
		framebufferHeight = dataIn.readShort();
		format = new PixelFormat();
		format.bitsPerPixel = dataIn.readByte();
		format.depth = dataIn.readByte();
		format.bigEndianFlag = dataIn.readBoolean();
		format.trueColorFlag = dataIn.readBoolean();
		format.redMax = dataIn.readShort();
		format.greenMax = dataIn.readShort();
		format.blueMax = dataIn.readShort();
		format.redShift = dataIn.readByte();
		format.greenShift = dataIn.readByte();
		format.blueShift = dataIn.readByte();
		
		//Padding
		dataIn.readByte();
		dataIn.readByte();
		dataIn.readByte();
		
		int nameLength = dataIn.readInt();
		byte[] nameBytes = new byte[nameLength];
		dataIn.read(nameBytes);
		name = new String(nameBytes);
		
		if (tight) {
			TightEncodings encodings = new TightEncodings();
			short serverMessageCount = dataIn.readShort();
			short clientMessageCount = dataIn.readShort();
			short encodingCount = dataIn.readShort();
			dataIn.readByte(); // Padding
			dataIn.readByte(); // Padding
			for (int i = 0; i < serverMessageCount; i++) {
				Capability c = new Capability();
				c.read(dataIn);
				System.out.println(c);
			}
			for (int i = 0; i < clientMessageCount; i++) {
				Capability c = new Capability();
				c.read(dataIn);
				System.out.println(c);
			}
			for (int i = 0; i < encodingCount; i++) {
				Capability c = new Capability();
				c.read(dataIn);
				Encoding e = Encoding.find(c.code);
				encodings.addEncoding(e);
				System.out.println(c);
			}
		}
		
		return null;
	}	
	
}
