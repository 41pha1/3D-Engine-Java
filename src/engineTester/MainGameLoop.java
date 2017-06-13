package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import collision.BroadPhase;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ModelData;
import renderEngine.OBJFileLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainGameLoop 
{
	public static void main(String[] args) 
	{
		DisplayManager.createDisplay();
		WaterShader waterShader=new WaterShader();
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WorldLoader  worldLoader= new WorldLoader();
		worldLoader.loadWorld();
		Camera camera = new Camera(worldLoader.player);
		GuiRenderer guiRenderer=new GuiRenderer(worldLoader.loader);
		MasterRenderer renderer = new MasterRenderer(worldLoader.loader, camera);
		WaterRenderer waterRenderer=new WaterRenderer(worldLoader.loader, waterShader, renderer.getProjectionMatrix(), fbos);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), worldLoader.terrain);
		while(!Display.isCloseRequested())
		{
			worldLoader.player.move(worldLoader.lights.get(0));
			camera.move();
//			picker.update();
//			Vector3f terrainPoint=picker.getCurrentTerrainPoint();
//			if(terrainPoint!=null)
//			{
//				terrainPoint.setY(terrainPoint.y + 10f);
//				worldLoader.lights.get(2).setPosition(terrainPoint);
//			}	
//			
//			fbos.bindReflectionFrameBuffer();
//			float distance = 2* (camera.getPosition().y - worldLoader.water.getHeight());
//			camera.getPosition().y -=distance;
//			camera.invertPitch();
//			renderer.renderSceneToFBO(worldLoader.entities, worldLoader.terrains, worldLoader.lights, camera, new Vector4f(0, 1, 0, -worldLoader.water.getHeight()));
//			camera.getPosition().y +=distance;
//			camera.invertPitch();
//			
//			fbos.bindRefractionFrameBuffer();
//			renderer.renderSceneToFBO(worldLoader.entities, worldLoader.terrains, worldLoader.lights, camera, new Vector4f(0, -1, 0, worldLoader.water.getHeight()+1f));
//			
//			fbos.unbindCurrentFrameBuffer();
//			
			
			renderer.renderScene(worldLoader.entities, worldLoader.terrains, worldLoader.lights, camera, new Vector4f(0,1,0,1000000));
//			waterRenderer.render(worldLoader.waterTiles, camera, worldLoader.getLights().get(1));
//			guiRenderer.render(worldLoader.guis);
			
			DisplayManager.updateDisplay();
		}
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		worldLoader.loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
