package com.mholeys.vnc.data;

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

	public static final PixelFormat DEFAULT_FORMAT = new PixelFormat()
			.setBitsPerPixel((byte) 32)
			.setDepth((byte) 24)
			.setBigEndianFlag(true)
			.setTrueColorFlag(true)
			.setRedMax((byte) 255)
			.setGreenMax((byte) 255)
			.setBlueMax((byte) 255)
			.setRedShift((byte) 16)
			.setGreenShift((byte) 8)
			.setBlueShift((byte) 0);
	
}

