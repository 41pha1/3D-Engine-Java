package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Maths 
{
	public static float root;
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	public static Vector3f subtractVectors(Vector3f V1, Vector3f V2)
	{
		Vector3f out = new Vector3f();
		Vector3f.sub(V1,V2,out);
		return out;
	}
	public static Vector3f mixColour(Vector3f Col1, Vector3f Col2, float factor)
	{
		float col1r=Col1.getX();
		float col1g=Col1.getY();
		float col1b=Col1.getZ();
		float col2r=Col2.getX();
		float col2g=Col2.getY();
		float col2b=Col2.getZ();
		float col3r=(col1r*factor+col2r*(1-factor));
		float col3g=(col1g*factor+col2g*(1-factor));
		float col3b=(col1b*factor+col2b*(1-factor));
		return new Vector3f(col3r, col3g, col3b);
	}
	public static Vector3f crossProductVectors(Vector3f V1, Vector3f V2)
	{	
        float u1, u2, u3, v1, v2, v3;

        u1 = V1.x;
        u2 = V1.y;
        u3 = V1.z;
        v1 = V2.x;
        v2 = V2.y;
        v3 = V2.z;

        float nx, ny, nz;
        nx = u2 * v3 - v2 * u3;
        ny = v1 * u1 - u1 * v3;
        nz = u3 * v2 - v1 * u2;
        
        float x,y,z;
        x=nx/(Math.abs(nx)+Math.abs(ny)+Math.abs(nz));
        y=ny/(Math.abs(nx)+Math.abs(ny)+Math.abs(nz));
        z=nz/(Math.abs(nx)+Math.abs(ny)+Math.abs(nz));
        
        Vector3f normalVector=new Vector3f(x,-z,y);
        
        return normalVector;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry,float rz, float scale) 
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		
		return matrix;
	}
	public static boolean getLowestRoot(float a, float b, float c, float maxR)
	{
		float determinat = b*b - 4.0f*a*c;
		if(determinat<0)
		{
			return false;
		}
		float sqrtD = (float)Math.sqrt(determinat);
		float r1 = (-b - sqrtD) / (2*a);
		float r2 = (-b + sqrtD) / (2*a);
		if(r1 > r2)
		{
			float temp = r2;
			r2 = r1;
			r1 = temp;
		}
		if(r1 > 0 && r1 < maxR) 
		{
			root = r1;
			return true;
		}
		if(r2 > 0 && r2 < maxR) 
		{
			root = r2;
			return true;
		}
		return false;
	}
	public static boolean checkPointInTriangle(Vector3f point, Vector3f p1, Vector3f p2, Vector3f p3)
	{
		Vector3f e10=new Vector3f();
		Vector3f.sub(p2, p1, e10);
		Vector3f e20=new Vector3f();
		Vector3f.sub(p3,p1,e20);
		
		float a = Vector3f.dot(e10, e10);
		float b = Vector3f.dot(e10, e20);
		float c = Vector3f.dot(e20, e20);
		float ac_bb = (a*c)-(b*b);
		Vector3f vp = new Vector3f(point.x-p1.x, point.y-p1.y, point.z-p1.z);
		
		float d = Vector3f.dot(vp, e10);
		float e = Vector3f.dot(vp, e20);
		float x = (d*c)-(e*b);
		float y = (e*a)-(d*b);
		float z = x+y-ac_bb;
		
		return (z < 0 && x >= 0 && y >= 0);
//	     float angles = 0;
//
//	        Vector3f v1 = new Vector3f();
//	        Vector3f.sub(point, p1 ,v1); 
//	        v1.normalise();
//	        Vector3f v2 = new Vector3f();
//	        Vector3f.sub(point, p2 ,v2); 
//	        v2.normalise();
//	        Vector3f v3 = new Vector3f();
//	        Vector3f.sub(point, p3 ,v3); 
//	        v3.normalise();
//
//	        angles += Math.acos(Vector3f.dot(v1, v2));
//	        angles += Math.acos(Vector3f.dot(v2, v3));
//	        angles += Math.acos(Vector3f.dot(v3, v1));
//	        return (Math.abs(angles  - 2*Math.PI) <= 0.005);
	}
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

}
