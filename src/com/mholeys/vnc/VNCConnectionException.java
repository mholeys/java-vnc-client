package com.mholeys.vnc;

public class VNCConnectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public String reason;
	
	public VNCConnectionException(String reason) {
		this.reason = reason;
	}
	
	public String toString() {
		return reason;
	}
	
}
