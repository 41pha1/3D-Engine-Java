package models;

public class RawModel {
	
	private int vaoID;
	private int vertexCount;
	private float[] vertices;
	private int[] indices;
	
	public RawModel(int vaoID, int vertexCount, float[] verticies, int[] indicies){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vertices = verticies;
		this.indices = indicies;
	}

	public RawModel(int vaoID2, int i, float[] positions)
	{
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vertices = positions;
	}

	public float[] getVertices()
	{
		return vertices;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int[] getIndices()
	{
		return indices;
	}
}
