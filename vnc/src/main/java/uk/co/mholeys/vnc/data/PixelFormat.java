package uk.co.mholeys.vnc.data;

public class PixelFormat {

	public byte bytesPerPixel;
	public byte bytesPerTPixel;
	
	public byte bitsPerPixel;
	public byte depth;
	public boolean bigEndianFlag;
	public boolean trueColorFlag;
	public short redMax;
	public short greenMax;
	public short blueMax;
	public byte redShift;
	public byte greenShift;
	public byte blueShift;
	
	public int[] colorMap;
	
	public PixelFormat clone() {
		PixelFormat copy = new PixelFormat();
		copy.bytesPerPixel = this.bytesPerPixel;
		copy.bytesPerTPixel = this.bytesPerTPixel;
		copy.bitsPerPixel = this.bitsPerPixel;
		copy.depth = this.depth;
		copy.bigEndianFlag = this.bigEndianFlag;
		copy.trueColorFlag = this.trueColorFlag;
		copy.redMax = this.redMax;
		copy.greenMax = this.greenMax;
		copy.blueMax = this.blueMax;
		copy.redShift = this.redShift;
		copy.greenShift = this.greenShift;
		copy.blueShift = this.blueShift;
		return copy;
	}
	
	public PixelFormat setBitsPerPixel(byte bitsPerPixel) {
		this.bitsPerPixel = bitsPerPixel;
		checkTight();
		return this;
	}

	public PixelFormat setDepth(byte depth) {
		this.depth = depth;
		checkTight();
		return this;
	}

	public PixelFormat setBigEndianFlag(boolean bigEndianFlag) {
		this.bigEndianFlag = bigEndianFlag;
		checkTight();
		return this;
	}

	public PixelFormat setTrueColorFlag(boolean trueColorFlag) {
		this.trueColorFlag = trueColorFlag;
		checkTight();
		return this;
	}

	public PixelFormat setRedMax(short redMax) {
		this.redMax = redMax;
		checkTight();
		return this;
	}

	public PixelFormat setGreenMax(short greenMax) {
		this.greenMax = greenMax;
		checkTight();
		return this;
	}

	public PixelFormat setBlueMax(short blueMax) {
		this.blueMax = blueMax;
		checkTight();
		return this;
	}

	public PixelFormat setRedShift(byte redShift) {
		this.redShift = redShift;
		checkTight();
		return this;
	}

	public PixelFormat setGreenShift(byte greenShift) {
		this.greenShift = greenShift;
		checkTight();
		return this;
	}

	public PixelFormat setBlueShift(byte blueShift) {
		this.blueShift = blueShift;
		checkTight();
		return this;
	}
	
	private void checkTight() {
		if (depth == 24 && bitsPerPixel == 32 && trueColorFlag && redMax == 255 && greenMax == 255 && blueMax == 255) {
			bytesPerPixel = 4;
			bytesPerTPixel = 3;
		} else {
			bytesPerPixel = (byte)Math.ceil(bitsPerPixel/8D);
			bytesPerTPixel = (byte)Math.ceil(bitsPerPixel/8D);
		}
	}
	
	public void setupColourMap(int size) {
		colorMap = new int[size];
		System.out.println("Colour attempt!!");
	}
	
	public void addColourMapEntry(int i, int c) {
		if (colorMap != null) {			
			colorMap[i] = c;
			System.out.println("Colour map entry added!!");
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BytesPerPixel: " + bytesPerPixel + "\n");
		sb.append("BytesPerTPixel: " + bytesPerTPixel + "\n");
		sb.append("BitsPerPixel: " + bitsPerPixel + "\n");
		sb.append("Colour Depth: " + depth + "\n");
		sb.append("Big Endian: " + bigEndianFlag + "\n");
		sb.append("True colour mode: " + trueColorFlag + "\n");
		sb.append("Max red value: " + redMax + "\n");
		sb.append("Red offset: " + redShift + "\n");
		sb.append("Max green value: " + greenMax + "\n");
		sb.append("Green offset: " + greenShift + "\n");
		sb.append("Max blue value: " + blueMax + "\n");
		sb.append("Blue offset: " + blueShift + "\n");
		
		return sb.toString();
	}
	
	public static final PixelFormat DEFAULT_FORMAT = new PixelFormat()
			.setBitsPerPixel((byte) 32)
			.setDepth((byte) 24)
			.setBigEndianFlag(false)
			.setTrueColorFlag(true)
			.setRedMax((short) 255)
			.setGreenMax((short) 255)
			.setBlueMax((short) 255)
			.setRedShift((byte) 16)
			.setGreenShift((byte) 8)
			.setBlueShift((byte) 0);
	
}

