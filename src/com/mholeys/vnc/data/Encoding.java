package com.mholeys.vnc.data;

public enum Encoding {

	RAW_ENCODING(0),
	COPY_RECT_ENCODING(1),
	RRE_ENCODING(2),
	CORRE_ENCODING(4),
	HEXTILE_ENCODING(5),
	ZLIB_ENCODING(6),
	TIGHT_ENCODING(7),
	ZLIB_HEX_ENCODING(8),
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
	JPEG_QUALITY_LEVEL_PSEUDO_ENCODING(-32,-23),
	DESKTOP_SIZE_PSEUDO_ENCODING(-223),
	LAST_RECT_PSEUDO_ENCODING(-224),
	POINTER_POS_ENCODING(-225),
	TIGHT_OPTIONS3_ENCODING(-238, -226),
	CURSOR_PSEUDO_ENCODING(-239),
	X_CURSOR_PSEUDO_ENCODING(-240),
	TIGHT_OPTIONS4_ENCODING(-246, -241),
	COMPRESSION_LEVEL_PSEUDO_ENCODING(-256, -247),
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
	
	private Encoding(int id) {
		startID = id;
		endID = id;
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
	
}