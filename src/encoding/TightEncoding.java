package encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import util.ByteUtil;
import data.PixelFormat;
import display.FrameBuffer;

public class TightEncoding extends Encode {

	public int[] pixels;
	public int x;
	public int y;
	public int width;
	public int height;
	public PixelFormat format;
	public ZLibStream[] streams;
	
	public TightEncoding(int x, int y, int width, int height, PixelFormat format, ZLibStream[] streams) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.format = format;
		pixels = new int[width * height];
		this.streams = streams;
	}
	
	@Override
	public int[] getPixels() {
		return pixels;
	}

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		byte compressionControl = dataIn.readByte();
		boolean[] bit = ByteUtil.byteToBits(compressionControl);
		if (bit[0]) {
			streams[0].inflaterStream.reset();
		}
		if (bit[1]) {
			streams[1].inflaterStream.reset();
		}
		if (bit[2]) {
			streams[2].inflaterStream.reset();
		}
		if (bit[3]) {
			streams[3].inflaterStream.reset();
		}
		if (bit[7]) {
			if (bit[7] && !bit[6] && !bit[5] && !bit[4]) {
				//Fill
			} else if (bit[7] && !bit[6] && !bit[5] && bit[4]) {
				//Jpeg
				boolean[] lengthBits;
				byte[] lengthBytes = new byte[0];
				for (int i = 0; i < 3; i++) {
					byte l = dataIn.readByte();
					lengthBits = ByteUtil.byteToBits(l);
					if (lengthBits[0]) {
						if (i == 2) {
							//Problem
						}
						byte[] nLength = new byte[lengthBytes.length+1];
						System.arraycopy(lengthBytes, 0, nLength, 0, lengthBytes.length);
						nLength[lengthBytes.length] = l;
					} else {
						break;
					}
				}
				//Read in jpeg data
				int length = ByteUtil.bytesToInts(lengthBytes, 0, 7, false);
				System.out.println(length);
			} else {
				//Problem
			}
		} else {
			//Basic
			if (bit[6]) {
				int filterId = dataIn.read();
				if (filterId == 0) {
					//Copy filter
				} else if (filterId == 1) {
					//Palette filter
				} else if (filterId == 2) {
					//Gradient filter
				}
			} else {
				//Copy filter
			}
		}
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer) {

	}

}
