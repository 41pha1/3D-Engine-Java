package collision;

import org.lwjgl.util.vector.Vector3f;

public class CollisionPackage
{
	Vector3f eRadius;
	Vector3f R3Velocity;
	Vector3f R3Position;
	Vector3f velocity;
	Vector3f normalizedVelocity;
	Vector3f basePoint;
	boolean foundCollision;
	double nearestDistance;
	Vector3f intersectionPoint;
	public Vector3f geteRadius()
	{
		return eRadius;
	}
	public void setRadius(Vector3f eRadius)
	{
		this.eRadius = eRadius;
	}
	public Vector3f getR3Velocity()
	{
		return R3Velocity;
	}
	public void setR3Velocity(Vector3f r3Velocity)
	{
		R3Velocity = r3Velocity;
	}
	public Vector3f getR3Position()
	{
		return R3Position;
	}
	public void setR3Position(Vector3f r3Position)
	{
		R3Position = r3Position;
	}
	public Vector3f getVelocity()
	{
		return velocity;
	}
	public void setVelocity(Vector3f velocity)
	{
		this.velocity = velocity;
		normalizedVelocity = velocity.normalise(normalizedVelocity);
	}
	public Vector3f getNormalizedVelocity()
	{
		return normalizedVelocity;
	}
	public void setNormalizedVelocity(Vector3f normalizedVelocity)
	{
		this.normalizedVelocity = normalizedVelocity;
	}
	public Vector3f getBasePoint()
	{
		return basePoint;
	}
	public void setBasePoint(Vector3f basePoint)
	{
		this.basePoint = basePoint;
	}
	public boolean isFoundCollision()
	{
		return foundCollision;
	}
	public void setFoundCollision(boolean foundCollision)
	{
		this.foundCollision = foundCollision;
	}
	public double getNearestDistance()
	{
		return nearestDistance;
	}
	public void setNearestDistance(double nearestDistance)
	{
		this.nearestDistance = nearestDistance;
	}
	public Vector3f getIntersectionPoint()
	{
		return intersectionPoint;
	}
	public void setIntersectionPoint(Vector3f intersectionPoint)
	{
		this.intersectionPoint = intersectionPoint;
	}
}
