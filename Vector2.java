import java.awt.Point;
import java.awt.geom.Point2D;


public class Vector2 {
	public double x, y;
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2(Point2D p) {
		this.x = p.getX();
		this.y = p.getY();
	}
	
	public double magnitude() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	public Vector2 normalize() {
		double mag = this.magnitude();
		return new Vector2(this.x / mag, this.y / mag);
	}
	
	public double dot(Vector2 o) {
		return this.x * o.x + this.y * o.y;
	}
	
	public Vector2 sub(Vector2 o) {
		return new Vector2(
			this.x - o.x,
			this.y - o.y
		);
	}
	
	public Vector3 cross(Vector2 o) {
		return new Vector3(this.x, this.y, 1).crossProduct(new Vector3(o.x, o.y, 1));
	}
	
	public Point2D toPoint() {
		final double _x = this.x;
		final double _y = this.y;
		return new Point2D() {

			public double x = _x;
			public double y = _y;
			
			@Override
			public double getX() {
				// TODO Auto-generated method stub
				return x;
			}

			@Override
			public double getY() {
				// TODO Auto-generated method stub
				return y;
			}

			@Override
			public void setLocation(double x, double y) {
				this.x = x;
				this.y = y;
			}
			
		};
	}
}
