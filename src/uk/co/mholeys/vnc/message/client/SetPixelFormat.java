package uk.co.mholeys.vnc.message.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.log.Logger;

public class SetPixelFormat extends ClientSendMessage {

	public PixelFormat format;
	
	public SetPixelFormat(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public void sendMessage() throws IOException {
		dataOut.writeByte((byte)getId());
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
		
		dataOut.writeByte(format.bitsPerPixel);
		dataOut.writeByte(format.depth);
		dataOut.writeBoolean(format.bigEndianFlag);
		dataOut.writeBoolean(format.trueColorFlag);
		dataOut.writeShort(format.redMax);
		dataOut.writeShort(format.greenMax);
		dataOut.writeShort(format.blueMax);
		dataOut.writeByte(format.redShift);
		dataOut.writeByte(format.greenShift);
		dataOut.writeByte(format.blueShift);
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
		dataOut.writeByte((byte)0);
		Logger.logger.verboseLn("SetPixelFormat message sent");
	}
	
	@Override
	public int getId() {
		return 0;
	}

}
