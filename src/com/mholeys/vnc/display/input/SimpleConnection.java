package com.mholeys.vnc.display.input;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.mholeys.vnc.data.EncodingSettings;
import com.mholeys.vnc.data.PixelFormat;

public class SimpleConnection implements IConnectionInformation {

	private InetAddress address;
	private int port;
	private EncodingSettings settings;
	private PixelFormat format;
	private IPasswordRequester password;
	
	public SimpleConnection(String address, int port, EncodingSettings settings, PixelFormat format, IPasswordRequester password) throws UnknownHostException {
		this.address = InetAddress.getByName(address);
		this.port = port;
		this.settings = settings;
		this.format = format;
		this.password = password;
	}
	
	@Override
	public InetAddress getAddress() {
		return address;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public boolean hasPrefferedFormat() {
		return format != null;
	}

	@Override
	public PixelFormat getPrefferedFormat() {
		return format;
	}

	@Override
	public boolean hasPrefferedEncoding() {
		return settings != null;
	}

	@Override
	public EncodingSettings getPrefferedEncoding() {
		return settings;
	}

	@Override
	public IPasswordRequester getPasswordRequester() {
		return password;
	}

}
