package com.mholeys.vnc.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import com.mholeys.vnc.data.PointerPoint;
import com.mholeys.vnc.display.FixedPassword;
import com.mholeys.vnc.display.IDisplay;
import com.mholeys.vnc.display.IScreen;
import com.mholeys.vnc.net.VNCProtocol;

public class SwingDisplay extends Canvas implements IDisplay {

	private static final long serialVersionUID = 1L;

	public JFrame frame;
	public IScreen screen;
	boolean running = false;
	private BufferStrategy bs;
	private BufferedImage image;
	private int[] pixels;
	
	private Thread thread;
	
	private int width, height;
	
	private short x, y;
	private boolean left, right, middle;
	private boolean mouseOnScreen = false;
	private boolean mouseChanged = false;
	
	public SwingDisplay(SwingScreen screen) {
		this.screen = screen;
	}
	
	public void start() {
		this.width = screen.getWidth();
		this.height = screen.getHeight();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.frame = new JFrame();
		this.setPreferredSize(new Dimension(width, height));
		MouseAdapter mouse = new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					left = true;
					mouseChanged = true;
				}
				if (e.getButton() == MouseEvent.BUTTON2) {
					right = true;
					mouseChanged = true;
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					middle = true;
					mouseChanged = true;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					mouseChanged = true;
					left = false;
				}
				if (e.getButton() == MouseEvent.BUTTON2) {
					right = false;
					mouseChanged = true;
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					middle = false;
					mouseChanged = true;
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				x = convertX((short) e.getX());
				y = convertY((short) e.getY());
				mouseChanged = true;
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOnScreen = true;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				mouseOnScreen = false;
			}
			
		};
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

	@Override
	public boolean sendPointer() {
		if (mouseOnScreen) {
			if (mouseChanged) {
				mouseChanged = false;
				return true;
			}
		}
		return false;
	}

	@Override
	public PointerPoint getLocalPointer() {
		PointerPoint p = new PointerPoint(x, y);
		p.left = left;
		p.right = right;
		return p;
	}

	public static void main(String[] args) {
		SwingInterface i = new SwingInterface();
		try {
			VNCProtocol vnc = new VNCProtocol("192.168.0.2", 5901, new FixedPassword(), i);
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