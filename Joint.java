import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Joint {

	double angle;
	Vector2 pos;
	
	public Joint(double a, Vector2 p) {
		this.angle = a;
		this.pos = p;
	}
	
	public void draw(Graphics2D g, Color c) {
		g.setColor(c);
		AffineTransform at = new AffineTransform();
		at.translate(256, 0);
		Vector2 temp = new Vector2(at.transform(this.pos.toPoint(), null));
		g.drawLine(
				(int) temp.x,
				512 - (int) temp.y,
				(int) (temp.x + Math.cos(this.angle) * Kinematics.LINK_LENGTH),
				512 - (int) (temp.y + Math.sin(this.angle) * Kinematics.LINK_LENGTH)
		);
	}

	public void transform(AffineTransform at, double amt) {
		this.pos = new Vector2(at.transform(this.pos.toPoint(), null));
		this.angle = (this.angle + amt) % (2 * Math.PI);
	}
	
	public boolean onJoint(Vector2 v) {
		Vector2 a = this.pos;
		Vector2 b = new Vector2(this.pos.x + Math.cos(this.angle) * Kinematics.LINK_LENGTH, this.pos.y + Math.sin(this.angle) * Kinematics.LINK_LENGTH);
		Vector2 c = v;
		double crossZ = (c.y - a.y) * (b.y - a.x) - (c.x - a.x) * (b.y - a.y);
		if (Math.abs(crossZ) > .001)
			return false;
		
		double dot = (c.x - a.x) * (b.x - a.x) + (c.y - a.y) * (b.y - a.y);
		if (dot < 0)
			return false;
		
		double sqLen = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);
		if (dot > sqLen)
			return false;
		
		return true;
		
	}
	
}
