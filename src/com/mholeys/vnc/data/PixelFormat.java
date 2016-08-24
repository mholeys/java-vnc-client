package com.mholeys.vnc.data;

public class PixelFormat {

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
		return this;
	}

	public PixelFormat setDepth(byte depth) {
		this.depth = depth;
		return this;
	}

	public PixelFormat setBigEndianFlag(boolean bigEndianFlag) {
		this.bigEndianFlag = bigEndianFlag;
		return this;
	}

	public PixelFormat setTrueColorFlag(boolean trueColorFlag) {
		this.trueColorFlag = trueColorFlag;
		return this;
	}

	public PixelFormat setRedMax(short redMax) {
		this.redMax = redMax;
		return this;
	}

	public PixelFormat setGreenMax(short greenMax) {
		this.greenMax = greenMax;
		return this;
	}

	public PixelFormat setBlueMax(short blueMax) {
		this.blueMax = blueMax;
		return this;
	}

	public PixelFormat setRedShift(byte redShift) {
		this.redShift = redShift;
		return this;
	}

	public PixelFormat setGreenShift(byte greenShift) {
		this.greenShift = greenShift;
		return this;
	}

	public PixelFormat setBlueShift(byte blueShift) {
		this.blueShift = blueShift;
		return this;
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
