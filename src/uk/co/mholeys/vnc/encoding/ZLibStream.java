package uk.co.mholeys.vnc.encoding;

import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ZLibStream {

	public InflaterInputStream inflaterStream;
	public Inflater inflater;
	
	public ZLibStream(InputStream in, Inflater inflater) {
		this.inflater = inflater;
		inflaterStream = new InflaterInputStream(in, inflater);
	}
	
}
