package collision;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;
import entities.Entity;

public class NarrowPhase
{
	public CollisionPackage getColPack()
	{
		return colPack;
	}
	public static Matrix3f getMatrix()
	{
		return matrix;
	}
	private CollisionPackage colPack;
	private static Matrix3f matrix;
	public NarrowPhase(Vector3f player)
	{
		this.colPack=new CollisionPackage();
		matrix = new Matrix3f();
		matrix.m01=0;
		matrix.m02=0;
		matrix.m10=0;
		matrix.m12=0;
		matrix.m20=0;
		matrix.m21=0;
		matrix.m00=1/2f;
		matrix.m11=1/10f;
		matrix.m22=1/2f;
	}
	public Vector3f translateToWorldSpace(Vector3f vector, Matrix4f transformationMatrix)
	{
		float x1=vector.x;
		float y1=vector.y;
		float z1=vector.z;
		float x2 = (transformationMatrix.m00*x1) + (transformationMatrix.m01*y1) + (transformationMatrix.m02*z1) + (transformationMatrix.m30);
		float y2 = (transformationMatrix.m10*x1) + (transformationMatrix.m11*y1) + (transformationMatrix.m12*z1) + (transformationMatrix.m31);
		float z2 = (transformationMatrix.m20*x1) + (transformationMatrix.m21*y1) + (transformationMatrix.m22*z1) + (transformationMatrix.m32);
		return new Vector3f(x2,y2,z2);
	}
	public void checkNarrowPhaseCollision(Matrix4f transformationMatrix, Entity entity, Vector3f position, Vector3f velocity)
	{
		float[] vertices = entity.getModel().getRawModel().getVertices();
		int[] indices = entity.getModel().getRawModel().getIndices();
		for(int i = 0; i<indices.length; i++)
		{		
			colPack=new CollisionPackage();
			colPack.setRadius(new Vector3f(2,10,2));
			colPack.setR3Position(new Vector3f(position.x+2,position.y+10,position.z+2));
			colPack.setR3Velocity(velocity);
			colPack.setVelocity(translateToESpace(velocity));
			colPack.setBasePoint(translateToESpace(new Vector3f(position.x+2,position.y+10,position.z+2)));

			float p1, p2, p3;
			p1 = vertices[indices[i]*3];
			p2 = vertices[indices[i]*3+1];
			p3 = vertices[indices[i]*3+2];
			i++;
			Vector3f triangleCoords1  = translateToESpace(translateToWorldSpace(new Vector3f(p1,p2,p3), transformationMatrix));
			p1 = vertices[indices[i]*3];
			p2 = vertices[indices[i]*3+1];
			p3 = vertices[indices[i]*3+2];
			Vector3f triangleCoords2  = translateToESpace(translateToWorldSpace(new Vector3f(p1,p2,p3), transformationMatrix));
			i++;
			p1 = vertices[indices[i]*3];
			p2 = vertices[indices[i]*3+1];
			p3 = vertices[indices[i]*3+2];
			Vector3f triangleCoords3  = translateToESpace(translateToWorldSpace(new Vector3f(p1,p2,p3), transformationMatrix));
			checkTriangle(colPack, triangleCoords1, triangleCoords2, triangleCoords3);
		}
	}
	private Vector3f translateToESpace(Vector3f coords)
	{
		float p1, p2, p3;
		p1 = (matrix.m00*coords.x); 
		p2 = (matrix.m11*coords.y);
		p3 = (matrix.m22*coords.z);
//		System.out.println(p1+", "+p2+", "+p3);
		return (new Vector3f(p1,p2,p3));
	}
	private void checkTriangle(CollisionPackage colPack, Vector3f p1,Vector3f p2, Vector3f p3)
	{
		Plane plane=new Plane(p1, p2, p3);
//		if(plane.isFrontFacingTo(colPack.normalizedVelocity))
//		{
			double t0, t1;
			boolean embeddedInPlane = false;
			double signedDistToPlane = plane.signedDistanceTo(colPack.getBasePoint());
			float normalDotVelocity = Vector3f.dot(plane.getNormal(), colPack.getVelocity());
			if(normalDotVelocity==0.0f)
			{
				if(Math.abs(signedDistToPlane)>1.0f)
				{
					return;
				}
				else 
				{
					embeddedInPlane=true;
					t0 = 0.0;
					t1 = 1.0;
				}
			}
			else 
			{	
				t0=(-1.0-signedDistToPlane)/normalDotVelocity;
				t1=( 1.0-signedDistToPlane)/normalDotVelocity;
				if(t0>t1)
				{
					double temp = t1;
					t1 = t0;
					t0 = temp;
				}
				if(t0 > 1.0f || t1 <0.0f)
				{
					return;
				}
				if(t0<0.0)t0 = 0.0;
				if(t1<0.0)t1 = 0.0;
				if(t0>1.0)t0 = 1.0;
				if(t1>1.0)t1 = 1.0;
			}
			Vector3f collisionPoint = new Vector3f();
			boolean foundCollision = false;
			float t = 1.0f;
			if(!embeddedInPlane)
			{
				Vector3f planeIntersectionPoint = new Vector3f();
				Vector3f.sub(colPack.getBasePoint(), plane.getNormal(), planeIntersectionPoint);
				Vector3f.add(planeIntersectionPoint, new Vector3f((float)(t0*colPack.velocity.x),(float)(t0*colPack.velocity.y),(float)(t0*colPack.velocity.z)), planeIntersectionPoint);
				boolean pointInTriangle = Maths.checkPointInTriangle(planeIntersectionPoint, p1, p2, p3);
//				System.out.println(pointInTriangle+", "+planeIntersectionPoint+", "+p1+", "+p2+", "+p3);
				if(pointInTriangle)
				{	
					foundCollision = true;
					t = (float) t0;
					collisionPoint = planeIntersectionPoint;
				}
			}
			if(foundCollision == false) 
			{
				Vector3f velocity = colPack.velocity;
				Vector3f base = colPack.basePoint;
				float velocitySquaredLength = velocity.lengthSquared();
				float a,b,c;
				
				a=velocitySquaredLength;
				Vector3f base_p1 = new Vector3f();
				Vector3f.sub(base, p1, base_p1);
				b=2.0f*(Vector3f.dot(velocity, base_p1));
				Vector3f.sub(p1,base,base_p1);
				c=(base_p1.lengthSquared()-1.0f);
				if(Maths.getLowestRoot(a, b, c, t))
				{
					t = Maths.root;
					foundCollision = true;
					collisionPoint = p1;
				}
				
				Vector3f base_p2 = new Vector3f();
				Vector3f.sub(base, p2, base_p2);
				b = 2.0f*(Vector3f.dot(velocity, base_p2));
				Vector3f.sub(p2, base, base_p2);
				c = (base_p2.lengthSquared());
				if(Maths.getLowestRoot(a, b, c, t))
				{
					t = Maths.root;
					foundCollision = true;
					collisionPoint = p2;
				}
				
				Vector3f base_p3 = new Vector3f();
				Vector3f.sub(base, p2, base_p3);
				b = 2.0f*(Vector3f.dot(velocity, base_p3));
				Vector3f.sub(p3, base, base_p3);
				c = (base_p3.lengthSquared());
				if(Maths.getLowestRoot(a, b, c, t))
				{
					t = Maths.root;
					foundCollision = true;
					collisionPoint = p3;
				}
				
				Vector3f edge = new Vector3f();
				Vector3f.sub(p2, p1, edge);
				Vector3f baseToVertex = new Vector3f();
				Vector3f.sub(p1, base, baseToVertex);
				float edgeSquaredLength = edge.lengthSquared();
				float edgeDotVelocity = Vector3f.dot(edge, velocity);
				float edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				
				a = edgeSquaredLength*-velocitySquaredLength+edgeDotVelocity*edgeDotVelocity;
				b = edgeSquaredLength*(2*Vector3f.dot(velocity, baseToVertex))-2.0f*edgeDotVelocity*edgeDotBaseToVertex;
				c = edgeSquaredLength*(1-baseToVertex.lengthSquared())+edgeDotBaseToVertex*edgeDotBaseToVertex;
				if(Maths.getLowestRoot(a, b, c, t))
				{
					float f=(edgeDotVelocity*Maths.root-edgeDotBaseToVertex)/edgeSquaredLength;
					if(f>= 0.0f && f <= 1.0f)
					{
						t = Maths.root;
						foundCollision = true;
						Vector3f.add(p1,new Vector3f(f*edge.x,f*edge.y,f*edge.z), collisionPoint);
					}
				}
				Vector3f.sub(p3, p2, edge);
				Vector3f.sub(p2, base, baseToVertex);
				edgeSquaredLength = edge.lengthSquared();
				edgeDotVelocity = Vector3f.dot(edge, velocity);
				edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				
				a = edgeSquaredLength*-velocitySquaredLength+edgeDotVelocity*edgeDotVelocity;
				b = edgeSquaredLength*(2*Vector3f.dot(velocity, baseToVertex))-2.0f*edgeDotVelocity*edgeDotBaseToVertex;
				c = edgeSquaredLength*(1-baseToVertex.lengthSquared())+edgeDotBaseToVertex*edgeDotBaseToVertex;
				if(Maths.getLowestRoot(a, b, c, t))
				{
					float f=(edgeDotVelocity*Maths.root-edgeDotBaseToVertex)/edgeSquaredLength;
					if(f>= 0.0f && f <= 1.0f)
					{
						t = Maths.root;
						foundCollision = true;
						Vector3f.add(p2,new Vector3f(f*edge.x,f*edge.y,f*edge.z), collisionPoint);
					}
				}
				Vector3f.sub(p1, p3, edge);
				Vector3f.sub(p3, base, baseToVertex);
				edgeSquaredLength = edge.lengthSquared();
				edgeDotVelocity = Vector3f.dot(edge, velocity);
				edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				
				a = edgeSquaredLength*-velocitySquaredLength+edgeDotVelocity*edgeDotVelocity;
				b = edgeSquaredLength*(2*Vector3f.dot(velocity, baseToVertex))-2.0f*edgeDotVelocity*edgeDotBaseToVertex;
				c = edgeSquaredLength*(1-baseToVertex.lengthSquared())+edgeDotBaseToVertex*edgeDotBaseToVertex;
				if(Maths.getLowestRoot(a, b, c, t))
				{
					float f=(edgeDotVelocity*Maths.root-edgeDotBaseToVertex)/edgeSquaredLength;
					if(f>= 0.0f && f <= 1.0f)
					{
						t = Maths.root;
						foundCollision = true;
						Vector3f.add(p3,new Vector3f(f*edge.x,f*edge.y,f*edge.z), collisionPoint);
					}
				}
			}
			if(foundCollision == true)
			{
				float distToCollision = t*colPack.velocity.length();
				if(colPack.foundCollision == false || distToCollision < colPack.nearestDistance)
				{
					colPack.nearestDistance = distToCollision;
					colPack.intersectionPoint = collisionPoint;
					colPack.foundCollision = true;
					System.out.println("COLLIDING: "+collisionPoint);
				}
			}
//		}
	}
}

