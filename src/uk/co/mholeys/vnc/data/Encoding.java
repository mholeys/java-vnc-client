package uk.co.mholeys.vnc.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import uk.co.mholeys.vnc.encoding.CoRREEncoding;
import uk.co.mholeys.vnc.encoding.CopyRectEncoding;
import uk.co.mholeys.vnc.encoding.CursorPseudoEncoding;
import uk.co.mholeys.vnc.encoding.Decoder;
import uk.co.mholeys.vnc.encoding.HextileEncoding;
import uk.co.mholeys.vnc.encoding.RREEncoding;
import uk.co.mholeys.vnc.encoding.RawEncoding;
import uk.co.mholeys.vnc.encoding.TightEncoding;
import uk.co.mholeys.vnc.encoding.ZLibEncoding;
import uk.co.mholeys.vnc.encoding.ZLibStream;
import uk.co.mholeys.vnc.log.Logger;

public enum Encoding {

	RAW_ENCODING(0, RawEncoding.class),
	COPY_RECT_ENCODING(1, CopyRectEncoding.class),
	RRE_ENCODING(2, RREEncoding.class),
	CORRE_ENCODING(4, CoRREEncoding.class),
	HEXTILE_ENCODING(5, HextileEncoding.class),
	ZLIB_ENCODING(6, ZLibEncoding.class),
	TIGHT_ENCODING(7, TightEncoding.class),
	ZLIB_HEX_ENCODING(8, ZLibEncoding.class),
	ULTRA_ENCODING(9),
	ULTRA2_ENCODING(10),
	TRLE_ENCODING(15),
	ZRLE_ENCODING(16),
	HITACHI_ZYWRLE_ENCODING(17),
	H264_ENCODING(20),
	JPEG_ENCODING(21),
	JRLE_ENCODING(22),
	APPLE1_ENCODING(1000, 1002),
	APPLE2_ENCODING(1011),
	REALVNC_ENCODING(1024, 1099),
	APPLE3__ENCODING(1100, 1105),
	TIGHT_OPTIONS1_ENCODING(-22, -1),
	TIGHT_OPTIONS2_ENCODING(-222, -33),
	JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING(-26),
	JPEG_QUALITY_LEVEL_2_PSEUDO_ENCODING(-27),
	JPEG_QUALITY_LEVEL_3_PSEUDO_ENCODING(-28),
	JPEG_QUALITY_LEVEL_4_PSEUDO_ENCODING(-29),
	JPEG_QUALITY_LEVEL_5_PSEUDO_ENCODING(-30),
	JPEG_QUALITY_LEVEL_6_PSEUDO_ENCODING(-31),
	DESKTOP_SIZE_PSEUDO_ENCODING(-223),
	LAST_RECT_PSEUDO_ENCODING(-224),
	POINTER_POS_ENCODING(-225),
	TIGHT_OPTIONS3_ENCODING(-238, -226),
	CURSOR_PSEUDO_ENCODING(-239, CursorPseudoEncoding.class),
	X_CURSOR_PSEUDO_ENCODING(-240),
	TIGHT_OPTIONS4_ENCODING(-246, -241),
	COMPRESSION_LEVEL_0_PSEUDO_ENCODING(-247),
	COMPRESSION_LEVEL_1_PSEUDO_ENCODING(-248),
	COMPRESSION_LEVEL_2_PSEUDO_ENCODING(-249),
	COMPRESSION_LEVEL_3_PSEUDO_ENCODING(-250),
	COMPRESSION_LEVEL_4_PSEUDO_ENCODING(-251),
	COMPRESSION_LEVEL_5_PSEUDO_ENCODING(-252),
	COMPRESSION_LEVEL_6_PSEUDO_ENCODING(-253),
	COMPRESSION_LEVEL_7_PSEUDO_ENCODING(-254),
	COMPRESSION_LEVEL_8_PSEUDO_ENCODING(-255),
	COMPRESSION_LEVEL_9_PSEUDO_ENCODING(-256),
	QEMU_POINTER_MOTION_CHANGE_PSEUDO_ENCODING(-257),
	QEMU_EXTENDED_KEY_PSEUDO_ENCODING(-258),
	QEMU_AUDIO_PSEUDO_ENCODING(-259),
	QEMU_ENCODING(-272, -260),
	VMWARE_ENCODING(-273, -304),
	GII_PSEUDO_ENCODING(-305),
	POPA_ENCODING(-306),
	DESKTOP_NAME_PSEUDO_ENCODING(-307),
	EXTENDED_DESKTOP_SIZE_PSEUDO_ENCODING(-308),
	XVP_PSEUDO_ENCODING(-309),
	OLIVE_CALL_CONTROL_ENCODING(-310),
	CLIENT_REDIRECT_ENCODING(-311),
	FENCE_PSEUDO_ENCODING(-312),
	CONTINUOUS_UPDATES_PSEUDO_ENCODING(-313),
	JPEG_FINE_GRAINED_QUALITY_LEVEL_PSEUDO_ENCODING(-512, -412),
	CAR_CONECTIVITY_ENCODING(-528, -523),
	JPEG_SUBSAMPLING_LEVEL_PSEUDO_ENCODING(-768, -763),
	EXTENDED_CLIPBOARD_PSEUDO_ENCODING(0xc0a1e5ce);
	
	int startID, endID;
	Class<? extends Decoder> encodingClass;
	
	private Encoding(int id) {
		startID = id;
		endID = id;
	}
	
	private Encoding(int id, Class<? extends Decoder> encodingClass) {
		startID = id;
		endID = id;
		this.encodingClass = encodingClass;
	}
	
	private Encoding(int startId, int endId) {
		this.startID = startId;
		this.endID = endId;
	}
	
	public boolean sameID(int id) {
		if (id <= endID && id >= startID) {
			return true;
		}
		return false;
	}
	
	public static Encoding find(int id) {
		for (Encoding e: Encoding.values()) {
			if (e.sameID(id)) {
				return e;
			}
		}
		return null;
	}
	
	public int getStartID() {
		return startID;
	}
	
	public int getEndID() {
		return endID;
	}
	
	
	private static final Class<?>[] ENCODE_PARAMS_TYPE1 = {PixelRectangle.class, PixelFormat.class, ZLibStream[].class};
	private static final Class<?>[] ENCODE_PARAMS_TYPE2 = {PixelRectangle.class, PixelFormat.class};
	
	public Decoder getDecoder(PixelRectangle r, PixelFormat format, ZLibStream[] streams) {
		if (encodingClass == null) {
			Logger.logger.debugLn("Encoding class wasn't set: " + this);
		}
		Constructor<?>[] constructors = encodingClass.getConstructors();
		Decoder e = null;
		try {
			if (constructors.length > 0) {
				for (Constructor<?> c : constructors) {
					if (c.getParameterTypes().length > 0) {
						if (classListEquals(c.getParameterTypes(), ENCODE_PARAMS_TYPE1)) {
							e = (Decoder) c.newInstance(r, format, streams);
						} else if (classListEquals(c.getParameterTypes(), ENCODE_PARAMS_TYPE2)) {
							e = (Decoder) c.newInstance(r, format);
						} else {
							Logger.logger.debugLn("Failed to create instance of " + encodingClass);
						}
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return e;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean classListEquals(Class[] a, Class[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		
		return true;
	}
	
}
