package encoding;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import util.ByteUtil;
import data.PixelFormat;
import display.FrameBuffer;

public class ZLibEncoding extends Encode {

	int x, y, width, height;
	PixelFormat format;
	FrameBuffer frameBuffer;
	int[] pixels;
	ZLibStream stream;
	
	public ZLibEncoding(int x, int y, int width, int height, PixelFormat format, ZLibStream stream) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.format = format;
		pixels = new int[width * height];
		this.stream = stream;
	}
	
	@Override
	public int[] getPixels() {
		return pixels;
	}

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		int length = dataIn.readInt();
		
		byte[] b = new byte[length];
		int inOffset = 0;
		while (inOffset < length) {
            int inCount = in.read(b, inOffset, length - inOffset);
            if (inCount == -1) {
            	System.out.println("EOF?");
                break;
            }
            inOffset += inCount;
        }
		byte[] p = new byte[width*height*format.bitsPerPixel/8];
		try {
			stream.inflater.setInput(b);
			int l = stream.inflater.inflate(p);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < width*height; i++) {
			byte[] pixel = new byte[format.bitsPerPixel/8];
			System.arraycopy(p, i*pixel.length, pixel, 0, pixel.length);
			pixels[i] = ByteUtil.bytesToInt(pixel);
		}
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
	}

}
