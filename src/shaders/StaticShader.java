package shaders;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import toolbox.Maths;

import entities.Camera;
import entities.Light;

public class StaticShader extends ShaderProgram{
	
	private static final int MAX_LIGHTS=4;
	
	private static final String VERTEX_FILE = "/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "/shaders/fragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_fakeLight;
	private int location_skyColour;
	private int location_density;
	private int location_gradient;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_cellShading;
	private int location_invertNormals;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_fakeLight= super.getUniformLocation("fakeLight");
		location_skyColour= super.getUniformLocation("skyColour");
		location_gradient= super.getUniformLocation("gradient");
		location_density= super.getUniformLocation("density");
		location_numberOfRows= super.getUniformLocation("numberOfRows");
		location_offset= super.getUniformLocation("offset");
		location_plane= super.getUniformLocation("plane");
		location_cellShading= super.getUniformLocation("cellShading");
		location_invertNormals= super.getUniformLocation("invertNormals");
		location_lightPosition=new int[MAX_LIGHTS];
		location_lightColour=new int[MAX_LIGHTS];
		location_attenuation=new int[MAX_LIGHTS];
		for(int i=0; i<MAX_LIGHTS;i++)
		{
			location_lightPosition[i]=super.getUniformLocation("lightPosition["+i+"]");
			location_lightColour[i]=super.getUniformLocation("lightColour["+i+"]");	
			location_attenuation[i]=super.getUniformLocation("attenuation["+i+"]");
		}
		
	}
	public void setInvertNormals(boolean invert)
	{
		super.loadBoolean(location_invertNormals, invert);
	}
	public void setCellShading(boolean cellShading)
	{
		super.loadBoolean(location_cellShading, cellShading);
	}
	public void loadClipPlane(Vector4f plane)
	{
		super.load4DVector(location_plane, plane);
	}
	public void loadNumberOfRows(int numberOfRows)
	{
		super.loadInt(location_numberOfRows, numberOfRows);
	}
	public void loadOffset(float x, float y)
	{
		super.load2DVector(location_offset, new Vector2f(x,y));
	}
	public void loadFogVariables(float density, float gradient)
	{
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}
	public void loadSkyColour(float r, float g, float b)
	{
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	public void loadFakeLightVariables(boolean fakeLight)
	{
		float fl=0;
		if(fakeLight)
		{
			fl=1;
		}
		super.loadFloat(location_fakeLight, fl);
	}
	
	public void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadLights(ArrayList<Light> lights)
	{
		for(int i=0; i<MAX_LIGHTS;i++)
		{
			if(i<lights.size())
			{
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenution());
			}
			else
			{
				super.loadVector(location_lightColour[i], new Vector3f(0,0,0));
				super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector(location_attenuation[i], new Vector3f(1,0,0));
			}
		}
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	

}
