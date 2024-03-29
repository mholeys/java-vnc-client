package uk.co.mholeys.vnc.encoding;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import uk.co.mholeys.vnc.data.PixelFormat;
import uk.co.mholeys.vnc.data.PixelRectangle;
import uk.co.mholeys.vnc.log.Logger;
import uk.co.mholeys.vnc.util.ByteUtil;
import uk.co.mholeys.vnc.util.ColorUtil;

public class ZLibEncoding extends Decoder {

	int x, y, width, height;
	PixelFormat format;
	int[] pixels;
	ZLibStream stream;
	
	public ZLibEncoding(PixelRectangle r, PixelFormat format, ZLibStream[] streams) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		this.format = format;
		pixels = new int[width * height];
		this.stream = streams[4];
	}
	
	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		Logger.logger.debugLn("Reading length of zlib compressed data");
		int length = dataIn.readInt();
		
		byte[] b = new byte[length];
		int inOffset = 0;
		Logger.logger.debugLn("Reading zlib compressed data");
		while (inOffset < length) {
            int inCount = in.read(b, inOffset, length - inOffset);
            if (inCount == -1) {
            	System.out.println("EOF?");
                break;
            }
            inOffset += inCount;
        }
		//dataIn.read(b);
		byte[] p = new byte[width*height*format.bytesPerPixel];
		try {
			stream.inflater.setInput(b);
			stream.inflater.inflate(p);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream buff = new ByteArrayInputStream(p);
		for (int i = 0; i < width*height; i++) {
			byte[] pixel = new byte[format.bytesPerPixel];
			buff.read(pixel);
			pixels[i] = ColorUtil.convertTo8888ARGB(format, ByteUtil.bytesToInt(pixel, format));
		}
		render.drawRaw(x, y, width, height, pixels);
	}

}
