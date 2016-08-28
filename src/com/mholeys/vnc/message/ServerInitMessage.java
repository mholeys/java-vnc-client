package com.mholeys.vnc.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.mholeys.vnc.data.Encoding;
import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.data.TightEncodings;
import com.mholeys.vnc.log.Logger;

public class ServerInitMessage extends RecieveMessage {

	public int framebufferWidth;
	public int framebufferHeight;
	
	public PixelFormat format;
	
	public boolean tight = false;
	
	public String name;
	
	public ServerInitMessage(Socket socket, InputStream in, OutputStream out, boolean tight) {
		super(socket, in, out);
		this.tight = tight;
	}

	@Override
	public Object receiveMessage() throws IOException {
		//Get the server init message
		//Includes width(2), height(2), pixel-format(16),
		//name-length(4), name
		Logger.logger.debugLn("Reading width");
		framebufferWidth = dataIn.readShort();
		Logger.logger.debugLn("Reading height");
		framebufferHeight = dataIn.readShort();
		format = new PixelFormat();
		Logger.logger.debugLn("Reading bits/pixel");
		format.setBitsPerPixel(dataIn.readByte());
		Logger.logger.debugLn("Reading color depth");
		format.setDepth(dataIn.readByte());
		Logger.logger.debugLn("Reading big endian flag");
		format.setBigEndianFlag(dataIn.readBoolean());
		Logger.logger.debugLn("Reading true color flag");
		format.setTrueColorFlag(dataIn.readBoolean());
		Logger.logger.debugLn("Reading red max");
		format.setRedMax(dataIn.readShort());
		Logger.logger.debugLn("Reading green max");
		format.setGreenMax(dataIn.readShort());
		Logger.logger.debugLn("Reading blue max");
		format.setBlueMax(dataIn.readShort());
		Logger.logger.debugLn("Reading red shift");
		format.setRedShift(dataIn.readByte());
		Logger.logger.debugLn("Reading green shift");
		format.setGreenShift(dataIn.readByte());
		Logger.logger.debugLn("Reading blue shift");
		format.setBlueShift(dataIn.readByte());
		
		//Padding
		Logger.logger.debugLn("Reading 3 bytes of padding");
		dataIn.readByte();
		dataIn.readByte();
		dataIn.readByte();
		
		Logger.logger.debugLn("Reading name lenght");
		int nameLength = dataIn.readInt();
		byte[] nameBytes = new byte[nameLength];
		Logger.logger.debugLn("Reading name");
		dataIn.read(nameBytes);
		name = new String(nameBytes);
		
		if (tight) {
			TightEncodings encodings = new TightEncodings();
			Logger.logger.debugLn("Reading server message count");
			short serverMessageCount = dataIn.readShort();
			Logger.logger.debugLn("Reading client message count");
			short clientMessageCount = dataIn.readShort();
			Logger.logger.debugLn("Reading encoding count");
			short encodingCount = dataIn.readShort();
			Logger.logger.debugLn("Reading 2 bytes of padding");
			dataIn.readByte(); // Padding
			dataIn.readByte(); // Padding
			Logger.logger.debugLn("Reading capabilities");
			for (int i = 0; i < serverMessageCount; i++) {
				Capability c = new Capability();
				Logger.logger.debugLn("Reading server capability");
				c.read(dataIn);
				Logger.logger.printLn(c.toString());
			}
			for (int i = 0; i < clientMessageCount; i++) {
				Capability c = new Capability();
				Logger.logger.debugLn("Reading client capabiltiy");
				c.read(dataIn);
				Logger.logger.printLn(c.toString());
			}
			for (int i = 0; i < encodingCount; i++) {
				Capability c = new Capability();
				Logger.logger.debugLn("Reading encoding capability");
				c.read(dataIn);
				Encoding e = Encoding.find(c.code);
				encodings.addEncoding(e);
				Logger.logger.printLn(c.toString());
			}
		}
		
		return null;
	}	
	
}
