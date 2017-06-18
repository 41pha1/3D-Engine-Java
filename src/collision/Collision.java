package collision;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.DrawInWorld;

import engineTester.WorldLoader;
import entities.Entity;

public class Collision
{
	private BroadPhase bp;
	private NarrowPhase np;
	private Entity player;
	private ArrayList<Entity> entities;
	private CollisionPackage colPack;
	private int collisionRecursionDepth;
	
	public Collision(BroadPhase bp, NarrowPhase np, Entity player, ArrayList<Entity> entities)
	{
		this.bp=bp;
		this.np=np;
		this.player=player;
		this.entities=entities;
		colPack = new CollisionPackage();
	}
	
	public Vector3f updatePosition(Vector3f position, Vector3f velocity, Vector3f gravity)
	{
		Vector3f eRadius = new Vector3f(np.geteRadius().x,
				np.geteRadius().y,np.geteRadius().z);
		colPack.R3Position = position;
		colPack.R3Velocity = velocity;
		colPack.eRadius = eRadius;
		
		Vector3f eSpacePosition = new Vector3f(position.x/eRadius.x,
						position.y/eRadius.y, position.z/eRadius.z);
		Vector3f eSpaceVelocity = new Vector3f(velocity.x/eRadius.x,
						velocity.y/eRadius.y, velocity.z/eRadius.z);
		collisionRecursionDepth = 0;
		
		Vector3f finalPosition = collideWithWorld(eSpacePosition,eSpaceVelocity);
		finalPosition = new Vector3f((float)(finalPosition.x*colPack.eRadius.x),
				(float)(finalPosition.y*colPack.eRadius.y),(float)(finalPosition.z*colPack.eRadius.z));
		return finalPosition;
	}
	float unitsPerMeter = 10.0f;
	
	public Vector3f collideWithWorld(Vector3f position, Vector3f velocity)
	{
		float unitScale = unitsPerMeter / 100.0f;
		float veryCloseDistance = 0.005f * unitScale;
		
		while(true)
		{
		
			if(collisionRecursionDepth > 5) 
			{
				System.out.println("Recursion to high");
				return position;
			}
	
			colPack.velocity = velocity;
/**/		colPack.normalizedVelocity = velocity.normalise(null);
			colPack.basePoint = position;
			colPack.foundCollision = false;
			ArrayList<Integer> possibleCollision= bp.checkBroadPhaseCollision(player, entities);
			
			if(possibleCollision.size() == 0) 
			{
				Vector3f newPosition = new Vector3f();
				Vector3f.add(position, velocity, newPosition);
				System.out.println("no near entities" + position);
				return newPosition;
			}
			
			colPack = np.checkNarrowPhaseCollision(colPack, entities.get(possibleCollision.get(0)).getAabb().
				getTransformationMatrix(), WorldLoader.getEntities().get(possibleCollision.get(0)));
			
			
			if(colPack.foundCollision == false)
			{
				Vector3f newPosition = new Vector3f();
				Vector3f.add(position, velocity, newPosition);
				System.out.println("no collision "+ newPosition);
				return newPosition;
			}
			
			System.out.println("colliding");
			DrawInWorld.addDot(colPack.intersectionPoint, 2);
			
//			if(true)
//			{
//				Vector3f newPosition = new Vector3f();
//				Vector3f.add(position, velocity, newPosition);
//				System.out.println("no collision "+ newPosition);
//				return newPosition;
//			}
			
			Vector3f destinationPoint = new Vector3f();
			Vector3f.add(position, velocity, destinationPoint);
			Vector3f newBasePoint = position;
			
			if(colPack.nearestDistance>=veryCloseDistance)
			{
				Vector3f V = velocity;
				V.normalise();
				float LengthOfVector =(float) (colPack.nearestDistance-veryCloseDistance);
				V = new Vector3f(V.x*LengthOfVector,V.y*LengthOfVector, V.z*LengthOfVector);
				
				Vector3f.add(colPack.basePoint,V,newBasePoint);
				System.out.println(V+", "+colPack.basePoint+", "+position+", "+ newBasePoint);
				V.normalise();
				Vector3f VcdxV = new Vector3f(veryCloseDistance*V.x, veryCloseDistance*V.y, veryCloseDistance*V.z);
				Vector3f.sub(colPack.intersectionPoint, VcdxV, colPack.intersectionPoint);
			}
			
			Vector3f slidePlaneOrigin = colPack.intersectionPoint;
			Vector3f slidePlaneNormal = new Vector3f();
			Vector3f.sub(newBasePoint, colPack.intersectionPoint, slidePlaneNormal);
			slidePlaneNormal.normalise();

			Plane slidingPlane = new Plane(slidePlaneOrigin, slidePlaneNormal);
			Vector3f newDestinationPoint = new Vector3f();
			Vector3f signedDistanceXnormal = new Vector3f(
					(float)(slidingPlane.signedDistanceTo(destinationPoint)*slidePlaneNormal.x), 
					(float)(slidingPlane.signedDistanceTo(destinationPoint)*slidePlaneNormal.y), 
					(float)(slidingPlane.signedDistanceTo(destinationPoint)*slidePlaneNormal.z));
			Vector3f.sub(destinationPoint, signedDistanceXnormal, newDestinationPoint);
			Vector3f newVelocityVector = new Vector3f();
			Vector3f.add(newDestinationPoint, colPack.intersectionPoint, newVelocityVector);		
			
			if(newVelocityVector.length() < veryCloseDistance)
			{
				System.out.println("newBasePoint");
				return newBasePoint;
			}
			
			collisionRecursionDepth++;
			position = newBasePoint;
			velocity = newVelocityVector;
			System.out.println("check again" + newVelocityVector);
		}
	}
}
