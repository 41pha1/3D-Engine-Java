package engineTester;

import java.util.ArrayList;
import java.util.Random;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import renderEngine.ModelData;
import renderEngine.OBJFileLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterTile;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiTexture;

public class WorldLoader
{
	Loader loader = new Loader();
	public static ArrayList<Light> lights =new ArrayList<Light>();
	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	public static ArrayList<Terrain> terrains = new ArrayList<Terrain>();
	public static ArrayList<WaterTile> waterTiles = new ArrayList<WaterTile>();
	public static ArrayList<GuiTexture> guis=new ArrayList<GuiTexture>();
	public Loader getLoader()
	{
		return loader;
	}

	public static ArrayList<Light> getLights()
	{
		return lights;
	}

	public static ArrayList<Entity> getEntities()
	{
		return entities;
	}

	public static ArrayList<Terrain> getTerrains()
	{
		return terrains;
	}

	public static ArrayList<WaterTile> getWaterTiles()
	{
		return waterTiles;
	}

	public static ArrayList<GuiTexture> getGuis()
	{
		return guis;
	}

	public static Terrain getTerrain()
	{
		return terrain;
	}

	public static Player getPlayer()
	{
		return player;
	}

	public static WaterTile getWater()
	{
		return water;
	}

	public static Terrain terrain;
	public static Player player;
	public static WaterTile water;
	
	public void loadWorld()
	{
		ModelData data= OBJFileLoader.loadOBJ("player");
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		ModelTexture texture=new ModelTexture(loader.loadTexture("player"));
		texture.setReflectivity(0);
		texture.setShineDamper(10);
		TexturedModel texturedPlayerModel = new TexturedModel(model,texture);
		player=new Player(texturedPlayerModel, new Vector3f(0, 0, 0), 0,0,0,1f);
		entities.add(player);
		
		data=OBJFileLoader.loadOBJ("triangle");
		model=loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		texture=new ModelTexture(loader.loadTexture("greyTerrain"));
		texture.setCellShading(false);
		texture.setRenderBack(true);
		TexturedModel caveModel=new TexturedModel(model, texture);
		entities.add(new Entity(caveModel, new Vector3f(0,0,0),-45,180,0,50));	
		
		
//		TerrainTexture backgroundTexture=new TerrainTexture(loader.loadTexture("greenTerrain"));
//		TerrainTexture rTexture=new TerrainTexture(loader.loadTexture("brownTerrain"));
//		TerrainTexture gTexture=new TerrainTexture(loader.loadTexture("greyTerrain"));
//		TerrainTexture bTexture=new TerrainTexture(loader.loadTexture("white"));
//		TerrainTexturePack texturePack=new TerrainTexturePack(backgroundTexture, rTexture, bTexture, gTexture);
//		TerrainTexture blendMap=new TerrainTexture(loader.loadTexture("blendMap"));
//		terrain = new Terrain(0,-1,loader, texturePack, blendMap, "perlinNoise");
//		terrains.add(terrain);
//		
		lights.add(new Light(new Vector3f(0,0,0),new Vector3f(0.4f,0.4f,0.1f),new Vector3f(1, 0.1f,0.002f)));
		lights.add(new Light(new Vector3f(0,1000000, 1000000),new Vector3f(1,1,1)));
		lights.add(new Light(new Vector3f(200,10,0),new Vector3f(1,1,1),new Vector3f(1, 0.001f, 0.001f)));
		lights.add(new Light(new Vector3f(200,50,-400),new Vector3f(1,1,1)));
//	
//		water = new WaterTile(4000, -4000, 0);
//		waterTiles.add(water);
//		
//		data=OBJFileLoader.loadOBJ("bobbleTree");
//		model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
//		texture=new ModelTexture(loader.loadTexture("bobbleTree"));
//		TexturedModel treeModel = new TexturedModel(model,texture);
//		Random random = new Random();
//		for(int i=0;i<100;i++)
//		{
//			float x=random.nextFloat()*8000;
//			float z=random.nextFloat()*-8000;
//			float y=terrain.getHeightOfTerrain(x, z);
//			if(y>0)
//			{
//				entities.add(new Entity(treeModel, new Vector3f(x,y,z),0,0,0,2));
//			}
//			else i--;
//		}
//		
//		data= OBJFileLoader.loadOBJ("stone");
//		model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
//		texture=new ModelTexture(loader.loadTexture("stone"));
//		texture.setReflectivity(0.5f);
//		texture.setShineDamper(10);
//		texture.setNumberOfRows(2);
//		TexturedModel stoneModel = new TexturedModel(model,texture);
//		random = new Random();
//		for(int i=0;i<100;i++)
//		{
//			float x=random.nextFloat()*8000;
//			float z=random.nextFloat()*-8000;
//			float y=terrain.getHeightOfTerrain(x, z);
//			entities.add(new Entity(stoneModel, new Vector3f(x,y,z),0,random.nextFloat()*360,0,(random.nextFloat()*2)+1));
//		}
		
		GuiTexture collision = new GuiTexture(loader.loadTexture("greenTerrain"),new Vector2f(0.5f,0.5f),new Vector2f(0.5f,0.5f));
		guis.add(collision);
	}
}
