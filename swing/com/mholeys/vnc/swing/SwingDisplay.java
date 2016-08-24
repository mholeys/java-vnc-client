package com.mholeys.vnc.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import com.mholeys.vnc.data.Encoding;
import com.mholeys.vnc.data.EncodingSettings;
import com.mholeys.vnc.display.IDisplay;
import com.mholeys.vnc.display.IScreen;
import com.mholeys.vnc.display.input.IConnectionInformation;
import com.mholeys.vnc.display.input.SimpleConnection;
import com.mholeys.vnc.log.Logger;
import com.mholeys.vnc.net.VNCProtocol;

public class SwingDisplay extends Canvas implements IDisplay {

	private static final long serialVersionUID = 1L;

	public JFrame frame;
	private SwingInterface intf;
	public IScreen screen;
	boolean running = false;
	private BufferStrategy bs;
	private BufferedImage image;
	private int[] pixels;
	
	private Mouse mouse;
	
	private Thread thread;
	
	private int width, height;

	
	public SwingDisplay(SwingInterface intf) {
		this.intf = intf;
		this.screen = intf.getScreen();
	}
	
	public void start() {
		this.width = screen.getWidth();
		this.height = screen.getHeight();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.frame = new JFrame();
		this.setPreferredSize(new Dimension(width, height));
		mouse = new Mouse(this);
		this.intf.mouse = mouse;
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		running = true;
		
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
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final int MAX_UPDATES = 10;
		final double NS = 1000000000.0 / MAX_UPDATES;
		double delta = 0;
		int frames = 0;
		boolean alwaysRender = false;
		boolean shouldRender = false;
		while (running) {
			shouldRender = false;
			long now = System.nanoTime();
			delta += (now - lastTime) / NS;
			lastTime = now;
			while (delta >= 1) {
				delta--;
				shouldRender = true;
			}
			if (shouldRender || alwaysRender) {
				render();
				frames++;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle("FPS: " + frames);
				frames = 0;
			}
		}
	}

	public void render() {
		bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		int[] newPixels = screen.getPixels();
		
		for (int i = 0; i < width*height; i++) {
			pixels[i] = newPixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(image, 0, 0, width, height, null);
		
		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		SwingInterface i = new SwingInterface();
		EncodingSettings es = new EncodingSettings();
		es.addEncoding(Encoding.TIGHT_ENCODING);
		es.addEncoding(Encoding.ZLIB_ENCODING);
		es.addEncoding(Encoding.RAW_ENCODING);
		es.addEncoding(Encoding.JPEG_QUALITY_LEVEL_1_PSEUDO_ENCODING);
		es.addEncoding(Encoding.COMPRESSION_LEVEL_0_PSEUDO_ENCODING);
		es.addEncoding(Encoding.CURSOR_PSEUDO_ENCODING);
		
		IConnectionInformation connection;
		try {
			connection = new SwingConnection(es, new SwingPassword());
			VNCProtocol vnc = new VNCProtocol(connection, i, new Logger(System.out, Logger.LOG_LEVEL_DEBUG));
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