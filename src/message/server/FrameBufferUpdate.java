package message.server;

import java.io.IOException;
import java.net.Socket;

import data.Encoding;
import data.PixelFormat;
import data.PixelRectangle;
import encoding.CopyRectEncoding;
import encoding.RawEncoding;
import encoding.ZLibEncoding;

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
			System.out.println(r.encodingType);
			if (Encoding.RAW_ENCODING.sameID(r.encodingType)) {
				r.encode = new RawEncoding(r.x, r.y, r.width, r.height, format);
				r.encode.readEncoding(in);
			} else if (Encoding.COPY_RECT_ENCODING.sameID(r.encodingType)) {
				r.encode = new CopyRectEncoding(r.x, r.y, r.width, r.height, format);
				r.encode.readEncoding(in);
			} else if (Encoding.ZLIB_ENCODING.sameID(r.encodingType)) {
				r.encode = new ZLibEncoding(r.x, r.y, r.width, r.height, format);
				r.encode.readEncoding(in);
				System.out.println("ZLIB");
			}
			rectangles[i] = r;
		}
		return rectangles;
	}

}