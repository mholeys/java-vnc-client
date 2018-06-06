package uk.co.mholeys.vnc.encoding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;

public class DesktopNamePseudoEncoding extends Decoder {

	@Override
	public void readEncoding(InputStream in) throws IOException {
		DataInputStream dataIn = new DataInputStream(in);
		int nameLength = dataIn.readInt();
		byte[] nameData = new byte[nameLength];
		dataIn.read(nameData);
		String name = new String(nameData);
		render.setName(name);
	}

}
