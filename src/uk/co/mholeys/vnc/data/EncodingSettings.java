package uk.co.mholeys.vnc.data;

import java.util.ArrayList;

public class EncodingSettings {

	public static final EncodingSettings DEFAULT_ENCODINGS = new EncodingSettings()
			.addEncoding(Encoding.TIGHT_ENCODING)
			.addEncoding(Encoding.ZLIB_ENCODING)
			.addEncoding(Encoding.RAW_ENCODING)
			.addEncoding(Encoding.CORRE_ENCODING)
			.addEncoding(Encoding.RRE_ENCODING)
			.addEncoding(Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING)
			.addEncoding(Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING)
			.addEncoding(Encoding.CURSOR_PSEUDO_ENCODING);
	
	ArrayList<Encoding> encodings = new ArrayList<Encoding>(); 
	
	public EncodingSettings addEncoding(Encoding e) {
		encodings.add(e);
		return this;
	}
	
	public EncodingSettings addEncoding(ArrayList<Encoding> encodings) {
		this.encodings.addAll(encodings);
		return this;
	}
	
	public ArrayList<Encoding> getEncodings() {
		return encodings;
	}
	
}
