package display;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import data.PointerPoint;

public class Frame extends Canvas implements IFrame {

	private static final long serialVersionUID = 1L;

	public JFrame frame;
	public FrameBuffer frameBuffer;
	boolean running = false;
	private BufferStrategy bs;
	private BufferedImage image;
	private int[] pixels;
	
	private short x, y;
	private boolean left, right, middle;
	private boolean mouseOnScreen = false;
	private boolean mouseChanged = false;

	public void start() {
		image = new BufferedImage(frameBuffer.width, frameBuffer.height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.frame = new JFrame();
		this.setPreferredSize(new Dimension(frameBuffer.width, frameBuffer.height));
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
		
		
		Thread fThread = new Thread(this);
		fThread.setName("Frame buffer thread");
		fThread.start();
	}
	
	public short convertX(short x) {
		return (short) Math.round((double) x / (double)(this.getWidth())*frameBuffer.width);
	}
	
	public short convertY(short y) {
		return (short) Math.round((double) y / (double)(this.getHeight())*frameBuffer.height);
	}
	
	boolean shouldRender = true;
	public void run() {
		long timer = System.currentTimeMillis();
		int frames = 0;
		frame.requestFocus();
		while (running) {
			shouldRender = true;
			if (shouldRender) {
				shouldRender = false;
				render();
				frames++;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle("FPS: " + frames);
				frames = 0;
			}
			if (frameBuffer.changed) {
				shouldRender = true;
				frameBuffer.changed = false;
			}
		}
	}

	public void render() {
		bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			shouldRender = true;
			return;
		}

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = frameBuffer.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	
		g.dispose();
		bs.show();
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
	}

	@Override
	public FrameBuffer getFrameBuffer() {
		return frameBuffer;
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
	

}