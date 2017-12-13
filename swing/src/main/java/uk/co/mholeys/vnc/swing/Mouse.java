package uk.co.mholeys.vnc.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;
import java.util.Queue;

import uk.co.mholeys.vnc.data.PointerPoint;
import uk.co.mholeys.vnc.display.IMouseManager;

public class Mouse extends MouseAdapter implements IMouseManager {
	
	SwingDisplay display;
	
	public Queue<PointerPoint> miceUpdates = new LinkedList<PointerPoint>();
	
	public boolean left, right, middle, mwUp, mwDown;
	
	public short localX, localY;
	public short remoteX, remoteY;

	public boolean mouseOnScreen;
	
	public Mouse(SwingDisplay display) {
		this.display = display;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		left = false;
		right = false;
		middle = false;
		if (e.getButton() == MouseEvent.BUTTON1) {
			left = true;
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			middle = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			right = true;
		}
		PointerPoint p = new PointerPoint(localX, localY);
		p.left = left;
		p.right = right;
		p.middle = middle;
		p.mwUp = mwUp;
		p.mwDown = mwDown;
		boolean allowed = miceUpdates.offer(p);
		if (!allowed) {
			System.out.println("Could not queue mouse");
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		left = false;
		right = false;
		middle = false;
		PointerPoint p = new PointerPoint(localX, localY);
		p.left = left;
		p.right = right;
		p.middle = middle;
		p.mwUp = mwUp;
		p.mwDown = mwDown;
		boolean allowed = miceUpdates.offer(p);
		if (!allowed) {
			System.out.println("Could not queue mouse");
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		PointerPoint reset = new PointerPoint(localX, localY);
		reset.left = left;
		reset.right = right;
		reset.middle = middle;
		reset.mwDown = false;
		reset.mwUp = false;
		miceUpdates.offer(reset);
		if (e.getWheelRotation() != 0) {
			if (e.getWheelRotation() > 0) {
				mwDown = true;
				mwUp = false;
			}
			if (e.getWheelRotation() < 0) {
				mwUp = true;
				mwDown = false;
			}
			PointerPoint p = new PointerPoint(localX, localY);
			p.left = left;
			p.right = right;
			p.middle = middle;
			p.mwUp = mwUp;
			p.mwDown = mwDown;
			boolean allowed = miceUpdates.offer(p);
			if (!allowed) {
				System.out.println("Could not queue mouse");
			}
			mwUp = false;
			mwDown = false;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		localX = display.convertX((short) e.getX());
		localY = display.convertY((short) e.getY());
		PointerPoint p = new PointerPoint(localX, localY);
		p.left = left;
		p.right = right;
		p.middle = middle;
		p.mwUp = mwUp;
		p.mwDown = mwDown;
		boolean allowed = miceUpdates.offer(p);
		if (!allowed) {
			System.out.println("Could not queue mouse");
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		localX = display.convertX((short) e.getX());
		localY = display.convertY((short) e.getY());
		PointerPoint p = new PointerPoint(localX, localY);
		p.left = left;
		p.right = right;
		p.middle = middle;
		p.mwUp = mwUp;
		p.mwDown = mwDown;
		boolean allowed = miceUpdates.offer(p);
		if (!allowed) {
			System.out.println("Could not queue mouse");
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseOnScreen = true;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouseOnScreen = false;
	}

	@Override
	public boolean sendLocalMouse() {
		/*if (mouseChanged) {
			mouseChanged = false;
			return true;
		}
		return false;*/
		return !miceUpdates.isEmpty();
	}

	@Override
	public PointerPoint getLocalMouse() {
		PointerPoint p = miceUpdates.poll();
		return p;
	}

	@Override
	public void setRemoteMouse(PointerPoint remote) {
		remoteX = remote.x;
		remoteY = remote.y;
	}
	
}
