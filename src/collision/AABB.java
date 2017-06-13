package collision;

import org.lwjgl.util.vector.Matrix4f;

public class AABB
{
	public float xmin,ymin,zmin;
	public float xmax,ymax,zmax;
	public float[] vertices;
	public Matrix4f transformationMatrix;
	
	public AABB(float[] vertices)
	{
		this.vertices=vertices;
	}
	public float[] transformVertices(float[] vertices, Matrix4f matrix)
	{
		float[] transformedVertices = new float[vertices.length];
		transformationMatrix=matrix;
//		System.out.println(matrix.m00+", "+matrix.m01+", "+matrix.m02+", "+matrix.m03+", "+matrix.m10+", "+matrix.m11+", "+matrix.m12+", "+matrix.m13+", "+matrix.m20+", "+matrix.m21+", "+matrix.m22+", "+matrix.m23+", "+matrix.m30+", "+matrix.m31+", "+matrix.m32+", "+matrix.m33);
		for(int i=0; i<vertices.length; i+=3)
		{
			float x1=vertices[i];
			float y1=vertices[i+1];
			float z1=vertices[i+2];
			float x2 = (matrix.m00*x1) + (matrix.m01*y1) + (matrix.m02*z1) + (matrix.m30);
			float y2 = (matrix.m10*x1) + (matrix.m11*y1) + (matrix.m12*z1) + (matrix.m31);
			float z2 = (matrix.m20*x1) + (matrix.m21*y1) + (matrix.m22*z1) + (matrix.m32);
			transformedVertices[i]=x2;
			transformedVertices[i+1]=y2;
			transformedVertices[i+2]=z2;
		}
		return transformedVertices;
	}
	public Matrix4f getTransformationMatrix()
	{
		return transformationMatrix;
	}
	public void setTransformationMatrix(Matrix4f transformationMatrix)
	{
		this.transformationMatrix = transformationMatrix;
	}
	public float getXmin()
	{
		return xmin;
	}
	public float getYmin()
	{
		return ymin;
	}
	public float getZmin()
	{
		return zmin;
	}
	public float getXmax()
	{
		return xmax;
	}
	public float getYmax()
	{
		return ymax;
	}
	public float getZmax()
	{
		return zmax;
	}
	public void printAABB()
	{
		System.out.println("x: "+xmin+", "+xmax+", y: "+ymin+", "+ymax+", z: "+zmin+", "+zmax);
	}
	public void createAABB(Matrix4f transormationMatrix)
	{
		float[] transformedVertices = transformVertices(vertices, transormationMatrix);
		xmin = transformedVertices[0];
		ymin = transformedVertices[1]; 
		zmin = transformedVertices[2];
		xmax= transformedVertices[0];
		ymax = transformedVertices[1]; 
		zmax = transformedVertices[2];
		for(int i=0; i<transformedVertices.length-2;)
		{
			if(transformedVertices[i]>xmax)   xmax=transformedVertices[i];
			if(transformedVertices[i]<xmin) xmin=transformedVertices[i];
			i++;
			if(transformedVertices[i]>ymax)   ymax=transformedVertices[i];
			if(transformedVertices[i]<ymin) ymin=transformedVertices[i];
			i++;
			if(transformedVertices[i]>zmax)   zmax=transformedVertices[i];
			if(transformedVertices[i]<zmin) zmin=transformedVertices[i];
			i++;
		}
	}
}
