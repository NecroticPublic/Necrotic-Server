package com.ruse.model.movement;

/**
 * Created with IntelliJ IDEA. User: Gabbe
 */
public class WalkPoint {

	private int x;

	private int y;

	public WalkPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}
