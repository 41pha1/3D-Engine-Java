package renderEngine;

import java.util.ArrayList;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;
import entities.Pixel;

public class DrawInWorld
{
	private static Loader loader = new Loader();
	private static ModelData data= OBJFileLoader.loadOBJ("plane");
	private static RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
	private static ModelTexture texture=new ModelTexture(loader.loadTexture("red"));
	private static TexturedModel PixelModel = new TexturedModel(model,texture);
	
	public static void addDot(Vector3f position, float thickness)
	{
		Pixel dot = new Pixel(PixelModel, position, 0,0,0, new Vector3f(thickness, thickness, thickness));
		pixels.add(dot);
	}
	public static void addLine(Vector3f position,  Vector3f vector, float thickness)
	{
		float rotY, rotZ;
		float x,y,z;
		x = vector.x;
		y = vector.y;
		z = vector.z;
		
		float r = (float) Math.sqrt(x*x + y*y + z*z);
		rotY = (float) Math.atan(y/x);
		rotZ = (float) Math.acos(z/r);
		Pixel line = new Pixel(PixelModel,position, 0,rotY,rotZ, new Vector3f(r, thickness, thickness));
	}
	
	public static void loadPixels()
	{
		Pixel pixel=new Pixel(PixelModel, new Vector3f(0, 0, 0), 0,0,0, new Vector3f(0,0,0));
		pixels.add(pixel);
	}
	
	static private ArrayList<Pixel> pixels= new ArrayList<Pixel>();
	private StaticShader shader;

	public DrawInWorld(StaticShader shader,Matrix4f projectionMatrix) 
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	public void render()
	{
		preparePixels(pixels.get(0).getModel());
		for(Pixel pixel: pixels)
		{
			preparePixel(pixel);
			GL11.glDrawElements(GL11.GL_TRIANGLES, pixel.getModel().getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		pixels = new ArrayList<Pixel>();
		loadPixels();
	}
	public void preparePixels(TexturedModel model)
	{
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		if(texture.getNumberOfRows()>1)
		{
			shader.loadNumberOfRows(texture.getNumberOfRows());
		}
		
		if(texture.isRenderBack())
		{
			MasterRenderer.disableCulling();
		}
		if(texture.isRenderOnlyInside())
		{
			MasterRenderer.invertCulling();
			shader.setInvertNormals(true);
		}
		else shader.setInvertNormals(false);
		if(texture.isFakeLight())
		{
			shader.loadFakeLightVariables(true);
		}else shader.loadFakeLightVariables(false);
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		shader.setCellShading(texture.isCellShading());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	private void preparePixel (Pixel pixel) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(pixel.getPosition(),
				pixel.getRotX(), pixel.getRotY(), pixel.getRotZ(), pixel.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
}
