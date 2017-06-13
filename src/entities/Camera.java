package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import terrains.Terrain;

public class Camera {
	
	private float zoom=50;
	private float Normalzoom=50;
	private float angleAroundPlayer=0;
	
	private Vector3f position = new Vector3f(0,5,0);
	private float pitch = 10;
	private float yaw ;
	private float roll;
	private Player player;
	
	public Camera(Player player)
	{
		this.player=player;
	}
	private float calculateYDistance()
	{
		return (float) (zoom*Math.sin(Math.toRadians(pitch)));
	}
	private float calculateXZDistance()
	{
		return (float) (zoom*Math.cos(Math.toRadians(pitch)));
	}
	private void calculateCameraPosition(float YDistance, float XZDistance)
	{
		float theta=player.getRotY()+angleAroundPlayer;
		float offsetX= (float) (XZDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ= (float) (XZDistance * Math.cos(Math.toRadians(theta)));
		position.y=player.getPosition().y+YDistance+15;
		position.x=player.getPosition().x-offsetX;
		position.z=player.getPosition().z-offsetZ;
	}
	public void move()
	{
		zoom=Normalzoom;
		calculateZoom();
		calculateAngles();
		float yDistance=calculateYDistance();
		float xzDistance=calculateXZDistance();
		calculateCameraPosition(yDistance, xzDistance);
		this.yaw=180-(player.getRotY()+angleAroundPlayer);
		for(int i=0; i<10; i++)
		{
			if(position.getY()-2<0)
			{
				zoom--;
				yDistance=calculateYDistance();
				xzDistance=calculateXZDistance();
				calculateCameraPosition(yDistance, xzDistance);
			}
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	public void calculateZoom()
	{
		float zoomLevel=Mouse.getDWheel()*0.05f;
		Normalzoom -= zoomLevel;
		if(Normalzoom<30)Normalzoom=30;
		if(Normalzoom>200)Normalzoom=200;
	}
	public void calculateAngles()
	{
		if(Mouse.isButtonDown(1))
		{
			float pitchChange=Mouse.getDY()*0.1f;
			float angleAroundPlayerChange=Mouse.getDX()*0.3f;
			pitch-=pitchChange;
			if (pitch<1f)pitch =1f;
			if (pitch>90f)pitch =90f;
			angleAroundPlayer-=angleAroundPlayerChange;
		}
	}
	public void invertPitch()
	{
		this.pitch=-pitch;
	}
	
	
	

}
