package encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import util.ByteUtil;
import data.PixelFormat;
import display.FrameBuffer;

public class ZLibEncoding extends Encode {

	int x, y, width, height;
	PixelFormat format;
	FrameBuffer frameBuffer;
	int[] pixels;
	
	public ZLibEncoding(int x, int y, int width, int height, PixelFormat format) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.format = format;
		pixels = new int[width * height];
	}
	
	@Override
	public int[] getPixels() {
		return pixels;
	}

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		int length = dataIn.readInt();
		byte[] zlibData = new byte[length];
		dataIn.read(zlibData);
		Inflater decompressor = new Inflater();
		decompressor.setInput(zlibData);
		byte[] result = new byte[width*height];
		int resultLength = -1;
		try {
			resultLength = decompressor.inflate(result);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < width * height; i++) {
			byte[] pixel = new byte[format.bitsPerPixel/8];
			System.arraycopy(result, i*format.bitsPerPixel/8, pixel, 0, format.bitsPerPixel/8);
			// TODO add colour shifting based on format and save code/reduce (similar code in raw)
			pixels[i] = ByteUtil.bytesToInt(pixel);
		}
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
	}

}
