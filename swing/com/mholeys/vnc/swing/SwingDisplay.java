package com.mholeys.vnc.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mholeys.vnc.data.Encoding;
import com.mholeys.vnc.data.EncodingSettings;
import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.display.IDisplay;
import com.mholeys.vnc.display.IScreen;
import com.mholeys.vnc.display.UpdateManager;
import com.mholeys.vnc.display.input.IConnectionInformation;
import com.mholeys.vnc.log.Logger;
import com.mholeys.vnc.net.VNCProtocol;

public class SwingDisplay extends JPanel implements IDisplay {

	private static final long serialVersionUID = 1L;

	public JFrame frame;
	private SwingInterface intf;
	public IScreen screen;
	public UpdateManager updateManager;
	private BufferedImage image;
	private int[] pixels;
	
	private Mouse mouse;
	private Keyboard keyboard;
	
	private Thread thread;
	
	private int width, height;

	
	public SwingDisplay(SwingInterface intf) {
		this.intf = intf;
		this.updateManager = intf.getUpdateManager();
		this.screen = intf.getScreen();
	}
	
	public void start() {
		this.width = screen.getWidth();
		this.height = screen.getHeight();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.frame = new JFrame();
		this.setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
		mouse = new Mouse(this);
		keyboard = new Keyboard(this);
		this.intf.mouse = mouse;
		this.intf.keyboard = keyboard;
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addKeyListener(keyboard);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		thread = new Thread(this);
		thread.setName("Frame buffer thread");
		thread.start();
	}
	
	public short convertX(short x) {
		return (short) Math.round(((double) x / (double)this.getWidth()) *width);
	}
	
	public short convertY(short y) {
		return (short) Math.round(((double) y / (double)this.getHeight()) *height);
	}
	
	public void run() {

	}

	@Override
	public void paint(Graphics g) {

		int[] newPixels = screen.getPixels();
		
		for (int i = 0; i < width*height; i++) {
			pixels[i] = newPixels[i];
		}
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}

	public static void main(String[] args) {
		SwingInterface i = new SwingInterface();
		EncodingSettings es = new EncodingSettings();
		//es.addEncoding(Encoding.TIGHT_ENCODING);
		//es.addEncoding(Encoding.ZLIB_ENCODING);
		es.addEncoding(Encoding.RRE_ENCODING);
		es.addEncoding(Encoding.CORRE_ENCODING);
		es.addEncoding(Encoding.COPY_RECT_ENCODING);
		es.addEncoding(Encoding.RAW_ENCODING);
		es.addEncoding(Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
		es.addEncoding(Encoding.COMPRESSION_LEVEL_1_PSEUDO_ENCODING);
		es.addEncoding(Encoding.CURSOR_PSEUDO_ENCODING);
		
		//PixelFormat preferredFormat = PixelFormat.DEFAULT_FORMAT.clone();
		
		IConnectionInformation connection;
		try {
			connection = new SwingConnection(es, null, new SwingPassword());
			VNCProtocol vnc = new VNCProtocol(connection, i, new Logger(System.out, Logger.LOG_LEVEL_NONE));
			Thread t = new Thread(vnc);
			t.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Thread getThread() {
		return thread;
	}	

}