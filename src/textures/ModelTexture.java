package textures;

public class ModelTexture {
	
	private int textureID;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	private boolean renderOnlyInside=false;
	private boolean cellShading=true;
	private boolean renderBack=false;
	private boolean fakeLight=false;
	
	private int numberOfRows=1;
	
	
	public boolean isRenderOnlyInside()
	{
		return renderOnlyInside;
	}

	public void setRenderOnlyInside(boolean renderOnlyInside)
	{
		this.renderOnlyInside = renderOnlyInside;
	}

	public boolean isCellShading()
	{
		return cellShading;
	}

	public void setCellShading(boolean cellShading)
	{
		this.cellShading = cellShading;
	}

	public int getNumberOfRows()
	{
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows)
	{
		this.numberOfRows = numberOfRows;
	}

	public ModelTexture(int texture){
		this.textureID = texture;
	}
	
	public boolean isRenderBack()
	{
		return renderBack;
	}

	public boolean isFakeLight()
	{
		return fakeLight;
	}

	public void setFakeLight(boolean fakeLight)
	{
		this.fakeLight = fakeLight;
	}

	public void setRenderBack(boolean renderBack)
	{
		this.renderBack = renderBack;
	}

	public int getID(){
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	

}
