package display;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Frame extends Canvas implements Runnable {

	public JFrame frame;
	public FrameBuffer framebuffer;
	boolean running = false;
	private BufferStrategy bs;
	private BufferedImage image;
	private int[] pixels;
	
	public Frame(FrameBuffer framebuffer) {
		this.framebuffer = framebuffer;
		image = new BufferedImage(framebuffer.width, framebuffer.height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.frame = new JFrame();
		this.setPreferredSize(new Dimension(framebuffer.width, framebuffer.height));
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		running = true;
	}
	
	public void run() {
		long timer = System.currentTimeMillis();
		int frames = 0;
		frame.requestFocus();
		while (running) {
			render();
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle("FPS: " + frames);
				frames = 0;
			}
		}
	}

	private void render() {
		bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = framebuffer.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	
		g.dispose();
		bs.show();
	}

}
