package uk.co.mholeys.vnc.listeners;

import uk.co.mholeys.vnc.data.PixelFormat;

public interface IVNCConnectionListener {

	public void onAuthenticated();
	public void onFailedAuthentication();
	
	public void onFormatChanged(PixelFormat format);
	
	public void onDisconnect();
	
}
