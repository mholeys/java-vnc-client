package uk.co.mholeys.vnc.util;

import uk.co.mholeys.vnc.data.PixelFormat;

public class ColorUtil {
	
	public static int convertTo8888ARGB(PixelFormat format, int pixel) {
		/*if (format.bigEndianFlag) {
			pixel = Integer.reverseBytes(pixel);
		}
		int mask = 0;
		for (int i = 0; i < format.depth; i++) {
			mask = mask << 1;
			mask += 1;
		}
		pixel = pixel & mask;

		int redPreMultiplied = (pixel & (format.redMax << format.redShift)) >>> format.redShift;
		int red = 0;
		if (format.redMax == 255) {
			red = redPreMultiplied;
		} else {
			red = (int) Math.round(((double) redPreMultiplied / (double) format.redMax) * 255);
		}
		int greenPreMultiplied = (pixel & (format.greenMax << format.greenShift)) >>> format.greenShift;
		int green = 0;
		if (format.greenMax == 255) {
			green = greenPreMultiplied;
		} else {
			green = (int) Math.round(((double) greenPreMultiplied / (double) format.greenMax) * 255);
		}
		int bluePreMultiplied = (pixel & (format.blueMax << format.blueShift)) >>> format.blueShift;
		int blue = 0;
		if (format.blueMax == 255) {
			blue = bluePreMultiplied;
		} else {
			blue = (int) Math.round(((double) bluePreMultiplied / (double) format.blueMax) * 255);
		}
		int color = (red << 16) | (green << 8) | blue;
		return color;*/
		
		if (format.colorMap == null) {
			int r = 255 * (pixel >> format.redShift & format.redMax) / format.redMax << 16 |  255 * (pixel >> format.greenShift & format.greenMax) / format.greenMax << 8 | 255 * (pixel >> format.blueShift & format.blueMax) / format.blueMax;
			return r;
		} else {
			return format.colorMap[pixel];
		}
	}
	
	public static int convertTo8888ARGBTrueColour(PixelFormat format, int pixel) {
		return 255 * (pixel >> format.redShift & format.redMax) / format.redMax << 16 |  255 * (pixel >> format.greenShift & format.greenMax) / format.greenMax << 8 | 255 * (pixel >> format.blueShift & format.blueMax) / format.blueMax;
	}
	
	public static int convertToOther(PixelFormat inFormat, PixelFormat outFormat, int pixel) {
		if (inFormat.colorMap == null) {
			return outFormat.redMax * (pixel >> inFormat.redShift & inFormat.redMax) / inFormat.redMax << outFormat.redShift |  outFormat.greenMax * (pixel >> inFormat.greenShift & inFormat.greenMax) / inFormat.greenMax << outFormat.greenShift | outFormat.blueMax * (pixel >> inFormat.blueShift & inFormat.blueMax) / inFormat.blueMax << outFormat.blueShift;
		} else {			
			//return inFormat.colorMap[pixel];
			return -1;
		}
	}
		
}
