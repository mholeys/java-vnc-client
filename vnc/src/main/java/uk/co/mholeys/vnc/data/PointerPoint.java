package uk.co.mholeys.vnc.data;

public class PointerPoint {

	public short x, y;
	public boolean left = false;
	public boolean right = false;
	public boolean middle = false;
	
	public boolean mwUp = false;
	public boolean mwDown = false;
	
	public PointerPoint(short x, short y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointerPoint other = (PointerPoint) obj;
		if (left != other.left)
			return false;
		if (middle != other.middle)
			return false;
		if (mwDown != other.mwDown)
			return false;
		if (mwUp != other.mwUp)
			return false;
		if (right != other.right)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public String toString() {
		return "x: " + x + " y: " + y + " l: " + left + " r: " + right + " m: " + middle + " u: " + mwUp + " d: " + mwDown;
	}
	
	public PointerPoint clone() {
		PointerPoint p = new  PointerPoint(x, y);
		p.left = left;
		p.right = right;
		p.middle = middle;
		p.mwUp = mwUp;
		p.mwDown = mwDown;
		return p;
	}

}
