package uk.co.mholeys.vnc.message.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import uk.co.mholeys.vnc.data.ColourMapEntry;
import uk.co.mholeys.vnc.log.Logger;

public class SetColourMapEntries extends ClientReceiveMessage {

	public short numberOfColors;
	public short firstColor;
	
	public SetColourMapEntries(Socket socket, InputStream in, OutputStream out) {
		super(socket, in, out);
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Object receiveMessage() throws IOException {
		Logger.logger.debugLn("Reading padding");
		dataIn.readByte();
		Logger.logger.debugLn("Reading first color of colour map");
		firstColor = dataIn.readShort();
		Logger.logger.debugLn("Reading number of colours");
		numberOfColors = dataIn.readShort();
		ColourMapEntry[] colours = new ColourMapEntry[numberOfColors];
		for (int i = 0; i < numberOfColors; i++) {
			colours[i] = new ColourMapEntry();
			Logger.logger.debugLn("Reading red of colour map");
			colours[i].red = dataIn.readShort();
			Logger.logger.debugLn("Reading green of colour map");
			colours[i].green = dataIn.readShort();
			Logger.logger.debugLn("Reading blue of color map");
			colours[i].blue = dataIn.readShort();
		}
		Logger.logger.verboseLn("SetColorMapEntry message received");
		return null;
	}

}
