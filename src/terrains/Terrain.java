package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain {
	
	private static final float SIZE = 8000;
	private static final float MaxHeight=800;
	private static final float MAX_PIXEL_COLOUR=256*256*256;
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	private float [][] heights;
	
	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap){
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, heightMap);
	}
	
	
	
	public float getX() {
		return x;
	}



	public float getZ() {
		return z;
	}



	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack()
	{
		return texturePack;
	}



	public TerrainTexture getBlendMap()
	{
		return blendMap;
	}



	private RawModel generateTerrain(Loader loader, String heightMap)
	{
		BufferedImage image=null;
		try
		{
			image = ImageIO.read(new File("res/"+heightMap+".png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		int VERTEX_COUNT=image.getHeight()/5;
		heights=new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				float height=getHeight(j*5, i*5, image);
				heights[j][i]=height;
				vertices[vertexPointer*3+1] = height;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				normals[vertexPointer*3] =0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] =0;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				

				Vector3f V1=new Vector3f(vertices[topLeft*3],vertices[topLeft*3+1],vertices[topLeft*3+2]);
				Vector3f V2=new Vector3f(vertices[bottomLeft*3],vertices[bottomLeft*3+1],vertices[bottomLeft*3+2]);
				Vector3f V3=new Vector3f(vertices[topRight*3],vertices[topRight*3+1],vertices[topRight*3+2]);
				
				Vector3f normalVector=new Vector3f();
				Vector3f.cross(Maths.subtractVectors(V2, V1), Maths.subtractVectors(V3, V1), normalVector);
				normalVector.normalise();
				
				normals[topLeft*3]=normalVector.x;
				normals[topLeft*3+1]=normalVector.y;
				normals[topLeft*3+2]=normalVector.z;
//				
				V1=new Vector3f(vertices[topRight*3],vertices[topRight*3+1],vertices[topRight*3+2]);
				V2=new Vector3f(vertices[bottomLeft*3],vertices[bottomLeft*3+1],vertices[bottomLeft*3+2]);
				V3=new Vector3f(vertices[bottomRight*3],vertices[bottomRight*3+1],vertices[bottomRight*3+2]);
				
				normalVector=new Vector3f();
				Vector3f.cross(Maths.subtractVectors(V2, V1), Maths.subtractVectors(V3, V1), normalVector);
				normalVector.normalise();

				normals[topRight*3]=normalVector.x;
				normals[topRight*3+1]=normalVector.y;
				normals[topRight*3+2]=normalVector.z;
				
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;

				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	public float getHeightOfTerrain(float worldX, float worldZ)
	{
		float terrainX=worldX-this.x;
		float terrainZ=worldZ-this.z;
		float gridSquareSize=SIZE/((float)heights.length-1);
		int gridX=(int)Math.floor(terrainX/gridSquareSize);
		int gridZ=(int)Math.floor(terrainZ/gridSquareSize);
		if(gridX>=heights.length-1||gridZ>=heights.length-1|| gridX<0||gridZ<0)
		{
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize)/gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize)/gridSquareSize;
		float answer;
		if (xCoord <= (1-zCoord)) 
		{
			answer = Maths
					.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} 
		else 
		{
			answer = Maths
					.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}
	private float getHeight(int x, int z, BufferedImage image)
	{
		if(x<0 || x>=image.getHeight() || z<0 || z>image.getHeight())
		{
			return 0;
		}
		float height= image.getRGB(x, z);
		height+=MAX_PIXEL_COLOUR/2f;
		height/=MAX_PIXEL_COLOUR;
		height*=MaxHeight;
		return height;
	}
}
