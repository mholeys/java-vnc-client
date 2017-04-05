package uk.co.mholeys.vnc.log;

import java.io.IOException;
import java.io.OutputStream;

public class Logger {

	public static Logger logger = new Logger(null);
	
	public static final int LOG_LEVEL_DEBUG = 0xFFF;
	public static final int LOG_LEVEL_VERBOSE = 0xFFE;
	public static final int LOG_LEVEL_NORMAL = 0xFFD;
	public static final int LOG_LEVEL_NONE = 0xFFC;
	
	public int logLevel = LOG_LEVEL_DEBUG;
	
	public OutputStream out;

	
	public Logger(OutputStream out) {
		if (out != null) { 
			this.out = out; 
		} else {
			this.out = System.out;
		}
		logger = this;
	}
	
	public Logger(OutputStream out, int level) {
		if (out != null) { 
			this.out = out; 
		} else {
			this.out = System.out;
		}
		this.logLevel = level;
		logger = this;
	}
	
	public void write(String string, int level) {
		if (level <= logLevel) {
			try {
				out.write(string.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void debug(String string) {
		write(string, LOG_LEVEL_DEBUG);
	}
	
	public void verbose(String string) {
		write(string, LOG_LEVEL_VERBOSE);
	}
	
	public void print(String string) {
		write(string, LOG_LEVEL_NORMAL);
	}
	
	public void debugLn(String string) {
		write(string+"\n", LOG_LEVEL_DEBUG);
	}
	
	public void verboseLn(String string) {
		write(string+"\n", LOG_LEVEL_VERBOSE);
	}
	
	public void printLn(String string) {
		write(string+"\n", LOG_LEVEL_NORMAL);
	}
	
	
}
