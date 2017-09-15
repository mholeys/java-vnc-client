package uk.co.mholeys.vnc.message.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.log.Logger;

public class SetColourMapEntries extends ClientReceiveMessage {

	public short numberOfColors;
	public short firstColor;
	public PixelFormat format;
	
	public SetColourMapEntries(Socket socket, InputStream in, OutputStream out, PixelFormat format) {
		super(socket, in, out);
		this.format = format;
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public Object receiveMessage() throws IOException {
		Logger.logger.debugLn("Reading padding");
		dataIn.readByte();
		Logger.logger.debugLn("Reading first color of colour map");
		firstColor = dataIn.readShort();
		Logger.logger.debugLn("Reading number of colours " + (firstColor & 0xFFFF));
		numberOfColors = dataIn.readShort();
		format.setupColourMap(numberOfColors & 0xFFFF);
		for (int i = 0 & 0xFFFF; i < (numberOfColors & 0xFFFF); i++) {
			int red, green, blue;
			Logger.logger.debugLn("Reading red of colour map");
			red = dataIn.readShort();
			Logger.logger.debugLn("Reading green of colour map");
			green = dataIn.readShort();
			Logger.logger.debugLn("Reading blue of color map");
			blue = dataIn.readShort();
			format.addColourMapEntry(i, red << 16 | green << 8 | blue);
		}
		Logger.logger.verboseLn("SetColorMapEntry message received");
		return null;
	}

}
