import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import Jama.Matrix;

public class ChainBot {

	private static final Color COLOR1 = Color.BLUE;
	private static final Color COLOR2 = Color.RED;
	ArrayList<Joint> joints;
	
	public ChainBot(double numLinks) {
		this.joints = new ArrayList<Joint>();
		for (int i = 0; i < numLinks; i++) {
			this.joints.add(new Joint(Math.PI / 2, new Vector2(0, (i * Kinematics.LINK_LENGTH))));
		}
		//this.joints.add(new Joint(-Math.PI / 4, new Vector2(256, 512 - (44 * Kinematics.LINK_LENGTH))));
		//this.rotateLink(22, Math.PI / 2);
		//this.rotateLinkAbsolute(30, Math.PI / 2);
		this.moveEndEffector(new Vector2(50, -50));
	}
	
	public void draw(Graphics2D g) {
		boolean which = true;
		for (Joint j : this.joints) {
			j.draw(g, which ? ChainBot.COLOR1 : ChainBot.COLOR2);
			which = !which;
		}
	}
	
	public void rotateLink(Joint j, double amt) {
		this.rotateLink(this.joints.indexOf(j), amt);
	}
	
	public void rotateLinkAbsolute(Joint j, double amt) {
		this.rotateLinkAbsolute(this.joints.indexOf(j), amt);
	}
	
	public void rotateLink(int index, double amt) {
		Iterator<Joint> iter = this.joints.iterator();
		while (index > 0) {
			iter.next();
			index--;
		}
		Joint j = iter.next();
		AffineTransform at = new AffineTransform();
		at.rotate(amt, j.pos.x, j.pos.y);
		j.transform(at, amt);
		while (iter.hasNext()) {
			j = iter.next();
			j.transform(at, amt);
		}
	}
	
	public void rotateLinkAbsolute(int index, double amt) {
		Iterator<Joint> iter = this.joints.iterator();
		while (index > 0) {
			iter.next();
			index--;
		}
		Joint j = iter.next();
		AffineTransform at = new AffineTransform();
		amt -= j.angle;
		at.rotate(amt, j.pos.x, j.pos.y);
		j.transform(at, amt);
		while (iter.hasNext()) {
			j = iter.next();
			j.transform(at, amt);
		}
	}
	
	public void rotateLinks(Matrix deltaTheta){
		for(int i = 0; i < deltaTheta.getRowDimension(); i++){
			rotateLink(i, deltaTheta.get(i, 0));
		}
	}
	
	public void moveEndEffector(Vector2 move){
		int jacobSize = this.joints.size();
		Matrix jacob = new Matrix(2, jacobSize);
		Vector3 endEffect = getEndEffector();
		Vector3 v = new Vector3(0, 0, 1);
		Vector3 result;
		Vector3 j3;
		
		int col = 0;
		for(Joint j : this.joints){
			j3 = new Vector3(j.pos.x, j.pos.y, 1);
			result = v.crossProduct(endEffect.sub(j3));
			jacob.set(0, col, result.x);
			jacob.set(1, col, result.y);
			
			col++;
		}
		
		double damp = 1000000;
		Matrix jacobT = jacob.transpose();
		Matrix ident = Matrix.identity(jacobSize, jacobSize);
		Matrix inverse = jacobT.times(jacob);
		inverse = inverse.plus(ident.times(damp)).inverse();
		Matrix e = new Matrix(2, 1);
		e.set(0, 0, move.x);
		e.set(1, 0, move.y);
		//Matrix deltaTheta = jacobT.times(e);
		//deltaTheta = inverse.times(deltaTheta);
		Matrix deltaTheta = inverse.times(jacobT);
		deltaTheta = deltaTheta.times(e);
		
		rotateLinks(deltaTheta);
	}
	
	public Vector3 getEndEffector(){
		Joint lastJoint = this.joints.get(this.joints.size() - 1);
		
		double x = lastJoint.pos.x + Kinematics.LINK_LENGTH * Math.cos(lastJoint.angle);
		double y = lastJoint.pos.y + Kinematics.LINK_LENGTH * Math.sin(lastJoint.angle);
		
		return new Vector3(x, y, 1);
	}

	public Joint getNearestJoint(int x, int y) {
		for (Joint j : this.joints) {
			if (Point2D.distance(x, y, j.pos.x, j.pos.y) <= Kinematics.HIGHLIGHT_THRESHOLD)
				return j;
		}
		return null;
	}

	public Joint getEndJoint() {
		return this.joints.get(this.joints.size() - 1);
	}
}
