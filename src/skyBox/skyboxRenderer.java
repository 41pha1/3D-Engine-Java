package skyBox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import engineTester.WorldLoader;
import entities.Camera;
import entities.Light;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.Maths;
import models.RawModel;

public class skyboxRenderer
{
private static final float SIZE = 5000f;
private static Vector3f normalSkyColour;
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};	private String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
	private String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};
	
	private RawModel cube;
	private int texture;
	private int nightTexture;
	private SkyboxShader shader;
	private float time=0;
	
	public skyboxRenderer(Loader loader, Matrix4f projectionMatrix)
	{
		cube=loader.loadToVAO(VERTICES, 3);
		texture=loader.loadCubeMap(TEXTURE_FILES);
		nightTexture=loader.loadCubeMap(NIGHT_TEXTURE_FILES);	
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		normalSkyColour=new Vector3f(0.5f, 0.5f, 0.9f);
	}
	public void render(Camera camera, Light sun, float r, float g, float b)
	{
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColour(r, g, b);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		bindTextures(sun);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	private void bindTextures(Light sun)
	{
		time += DisplayManager.getFrameTimeSeconds() *100;
		if(time>24000)time=0;
		float blendFactor=0;
		float lightColour=0;
		
		if(time<6000)
		{
			blendFactor=time*2/12000;	
			lightColour=time*4/12000;
			if(time>3000)
			{
				lightColour=(12000-((time-3000)*4))/12000;
			}
		}else
		if(time<12000)
		{
			blendFactor=1;
			lightColour=0;
		}else
		if(time<18000)
		{
			blendFactor=(((24000-time)/12000)-0.5f)*2;
			lightColour=(time-12000)*4/12000;
			if(time>15000)
			{
				lightColour=(12000-((time-15000)*4))/12000;
			}
		}else
		if(time<24000)
		{
			blendFactor=0;
			lightColour=0;
		}
		MasterRenderer.setSkyColour(new Vector3f(normalSkyColour.x*1-blendFactor/2+0.5f,normalSkyColour.y*1-blendFactor/2+0.5f,normalSkyColour.z*1-blendFactor/2+0.5f ));
		MasterRenderer.setSkyColour(Maths.mixColour(MasterRenderer.getSkyColour(), new Vector3f(1.0f,  0.8f, 0.1f),1-lightColour));
		WorldLoader.getLights().get(1).setColour(new Vector3f((1-blendFactor)*1, (1-blendFactor)*1, (1-blendFactor)*1));
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTexture);
		shader.loadBlendFactor(blendFactor);
	}
}











