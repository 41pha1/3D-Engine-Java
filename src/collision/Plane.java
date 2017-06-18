package collision;

import org.lwjgl.util.vector.Vector3f;

public class Plane
{
	public float[] equation;
	public Vector3f origin;
	public Vector3f normal;
	public boolean isFrontFacingTo;
	public double signedDistanceTo;
	
	public Plane(Vector3f origin, Vector3f normal)
	{
		this.normal=normal;
		this.origin=origin;
		equation = new float[4];
		equation[0]=normal.x;
		equation[1]=normal.y;
		equation[2]=normal.z;
		equation[3]=(normal.x*origin.x+normal.y*origin.y+normal.z*origin.z);

	}
	
	public Plane(Vector3f p1, Vector3f p2, Vector3f p3)
	{
		equation = new float[4];
		normal = new Vector3f();
		Vector3f p2p1 = new Vector3f();
		Vector3f p3p1 = new Vector3f();
		Vector3f.sub(p2, p1, p2p1);
		Vector3f.sub(p3, p1, p3p1);
		Vector3f.cross(p2p1, p3p1, normal);
		normal.normalise();
		origin=p1;
		equation[0]=normal.x;
		equation[1]=normal.y;
		equation[2]=normal.z;
		equation[3]=(normal.x*origin.x+normal.y*origin.y+normal.z*origin.z);
	}
	public boolean isFrontFacingTo(Vector3f direction)
	{
		double dot=Vector3f.dot(normal, direction);
		return (dot<=0);
	}
	public double signedDistanceTo(Vector3f point)
	{
		return (Vector3f.dot(point, normal)+equation[3]);
	}
	public float[] getEquation()
	{
		return equation;
	}
	public Vector3f getOrigin()
	{
		return origin;
	}
	public Vector3f getNormal()
	{
		return normal;
	}
	public boolean isFrontFacingTo()
	{
		return isFrontFacingTo;
	}
	public double getSignedDistanceTo()
	{
		return signedDistanceTo;
	}
}
