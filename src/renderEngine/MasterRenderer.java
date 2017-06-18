package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import shaders.StaticShader;
import shaders.TerrainShader;
import skyBox.skyboxRenderer;
import terrains.Terrain;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MasterRenderer {
	
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 10000;
	
	private static Vector3f skyColour=new Vector3f(0.235f,0.266f,0.333f);
	public static Vector3f getSkyColour()
	{
		return skyColour;
	}
	public static void setSkyColour(Vector3f skyColour)
	{
		MasterRenderer.skyColour = skyColour;
	}

	private static final float density=0.0005f;
	private static final float gradient=3f;
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	private DrawInWorld  DIW;
	
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel,List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private skyBox.skyboxRenderer skyboxRenderer;
	
	public MasterRenderer(Loader loader, Camera camera)
	{
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader,projectionMatrix);
		DIW = new DrawInWorld(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
		skyboxRenderer = new skyboxRenderer(loader, projectionMatrix);
	}
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	public static void enableCulling()
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	public static void disableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void renderScene(ArrayList<Entity> entities, ArrayList<Terrain> terrains, ArrayList<Light> lights, Camera camera, Vector4f clipPlane)
	{
		for(Terrain terrain: terrains)
		{
			processTerrain(terrain);
		}
		for(Entity entity : entities)
		{
			processEntity(entity);
		}
		render(lights, camera, clipPlane);
	}
	public void renderSceneToFBO(ArrayList<Entity> entities, ArrayList<Terrain> terrains, ArrayList<Light> lights, Camera camera, Vector4f clipPlane)
	{
		for(Terrain terrain: terrains)
		{
			processTerrain(terrain);
		}
		for(Entity entity : entities)
		{
			processEntity(entity);
		}
		renderToFBO(lights, camera, clipPlane);
	}
	public void renderToFBO(ArrayList<Light> lights,Camera camera, Vector4f clipPlane){
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		prepare();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadFogVariables(density, gradient);
		shader.loadSkyColour(skyColour.x, skyColour.y,  skyColour.z);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadFogVariables(density, gradient);
		terrainShader.loadSkyColor(skyColour.x, skyColour.y,  skyColour.z);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		skyboxRenderer.render(camera, lights.get(1), skyColour.x, skyColour.y, skyColour.z);
		terrains.clear();
		entities.clear();
	}
	
	public void render(ArrayList<Light> lights,Camera camera, Vector4f clipPlane){
		prepare();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadFogVariables(density, gradient);
		shader.loadSkyColour(skyColour.x, skyColour.y,  skyColour.z);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		DIW.render();
		shader.stop();
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadFogVariables(density, gradient);
		terrainShader.loadSkyColor(skyColour.x, skyColour.y,  skyColour.z);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyboxRenderer.render(camera, lights.get(1), skyColour.x, skyColour.y, skyColour.z);
		terrains.clear();
		entities.clear();
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);		
		}
	}
	public void cleanUp()
	{
		shader.cleanUp();
		terrainShader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(skyColour.x,skyColour.y,skyColour.z,1);
	}
	
	 private void createProjectionMatrix(){
	    	projectionMatrix = new Matrix4f();
			float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
			float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
			float x_scale = y_scale / aspectRatio;
			float frustum_length = FAR_PLANE - NEAR_PLANE;

			projectionMatrix.m00 = x_scale;
			projectionMatrix.m11 = y_scale;
			projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
			projectionMatrix.m33 = 0;
	    }
	public static void invertCulling()
	{
		GL11.glCullFace(GL11.GL_FRONT);
	}
	

}
