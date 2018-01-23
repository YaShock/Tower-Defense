package utility;

import java.io.Serializable;

public class Vector2d implements Serializable {
	private static final long serialVersionUID = -6500732124351629358L;
	public double x = 0, y = 0;
	
	public Vector2d() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2d(Vector2d v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public void add(Vector2d v) {
		this.x += v.x;
		this.y += v.y;
	}
	
	static public Vector2d add(Vector2d v1, Vector2d v2) {
		Vector2d newVec = new Vector2d(v1.x + v2.x, v1.y + v2.y);
		return newVec;
	}
	
	public void subtract(Vector2d v) {
		this.x -= v.x;
		this.y -= v.y;
	}
	
	// Sutracts v2 from v1 and returns the difference
	static public Vector2d subtract(Vector2d v1, Vector2d v2) {
		Vector2d newVec = new Vector2d(v1.x - v2.x, v1.y - v2.y);
		return newVec;
	}
	
	// Multiplies the vector by a scalar
	public void multiply(double s) {
		this.x *= s;
		this.y *= s;
	}
	
	// Sutracts v2 from v1 and returns the difference
	static public Vector2d multiply(Vector2d v1, double s) {
		Vector2d newVec = new Vector2d(v1.x * s, v1.y * s);
		return newVec;
	}
	
	// Magnitude of the vector
	public double magnitude() {
		return Math.sqrt(x*x+y*y);
	}
	
	// Returns the distance between this and v vectors
	public Vector2d distance(Vector2d v) {
		return subtract(this, v);
	}
	
	// Returns the distance between two vectors
	static public Vector2d distance(Vector2d v1, Vector2d v2) {
		return subtract(v1, v2);
	}
	
	public void normalize() {
		double mag = magnitude();
		this.x /= mag;
		this.y /= mag;
	}
	
	static public Vector2d normalize(Vector2d v) {
		Vector2d newVec = new Vector2d(v);
		double mag = newVec.magnitude();
		newVec.x /= mag;
		newVec.y /= mag;
		return newVec;
	}
	
	public void print() {
		System.out.println(x + ":" + y);
	}
	
	@Override
	public boolean equals(Object v) {	
		if (this == v)
			return true;
		
		if(this == null || v == null)
			return false;
			
		if (v instanceof Vector2d) {
			final Vector2d rhs = (Vector2d)v;
			return (x == rhs.x) && (y == rhs.y);
		}
		return false;
	}
	
}
