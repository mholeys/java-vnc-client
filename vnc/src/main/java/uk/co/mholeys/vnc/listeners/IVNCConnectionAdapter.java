package uk.co.mholeys.vnc.listeners;

import uk.co.mholeys.vnc.data.PixelFormat;

public class IVNCConnectionAdapter implements IVNCConnectionListener {

	@Override
	public void onAuthenticated() {	}

	@Override
	public void onFailedAuthentication() { }

	@Override
	public void onFormatChanged(PixelFormat format) { }

	@Override
	public void onDisconnect() { }

}
