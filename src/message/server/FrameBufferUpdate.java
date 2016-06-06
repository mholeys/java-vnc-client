package message.server;

import java.io.IOException;
import java.net.Socket;

import data.Encoding;
import data.PixelFormat;
import data.PixelRectangle;
import encoding.RawEncoding;

public class FrameBufferUpdate extends ClientReceiveMessage {

	public PixelFormat format;
	
	public FrameBufferUpdate(Socket socket, PixelFormat format) {
		super(socket);
		this.format = format;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Object receiveMessage() throws IOException {
		dataIn.readByte();
		short rectangle = dataIn.readShort();
		PixelRectangle[] rectangles = new PixelRectangle[rectangle];
		for (int i = 0; i < rectangle; i++) {
			PixelRectangle r = new PixelRectangle();
			r.x = dataIn.readShort();
			r.y = dataIn.readShort();
			r.width = dataIn.readShort();
			r.height = dataIn.readShort();
			r.encodingType = dataIn.readInt();
			Encoding.find(r.encodingType);
			if (Encoding.RAW_ENCODING.sameID(r.encodingType)) {
				r.encode = new RawEncoding(r.width, r.height, format);
				r.encode.readEncoding(in);
			}
			rectangles[i] = r;
		}
		return rectangles;
	}

}