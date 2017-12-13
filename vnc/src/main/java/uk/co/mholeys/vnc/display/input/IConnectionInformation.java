package uk.co.mholeys.vnc.display.input;

import java.net.InetAddress;

import uk.co.mholeys.vnc.data.EncodingSettings;
import uk.co.mholeys.vnc.data.PixelFormat;

public interface IConnectionInformation {

	public InetAddress getAddress();
	public int getPort();
	
	public boolean hasPrefferedFormat();
	public PixelFormat getPrefferedFormat();
	
	public boolean hasPrefferedEncoding();
	public EncodingSettings getPrefferedEncoding();
	
	public IPasswordRequester getPasswordRequester();
	
}
