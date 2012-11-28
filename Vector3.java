
public class Vector3 {
	public double x, y, z;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3 crossProduct(Vector3 vec){
		/* i  j  k  i  j  k
		 * x1 y1 z1 x1 y1 z1
		 * x2 y2 z2 x2 y2 z2
		 */
		return new Vector3(this.y * vec.z - this.z * vec.y,
						   this.z * vec.x - this.x * vec.z,
						   this.x * vec.y - this.y * vec.x);
	}
	
	public Vector3  sub(Vector3 vec){
		return new Vector3(this.x - vec.x, this.y - vec.y, this.z - vec.z);
	}
}
