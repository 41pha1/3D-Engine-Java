package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float zoom=50;
	private float Normalzoom=50;
	private float angleAroundPlayer=0;
	
	private Vector3f position = new Vector3f(0,5,0);
	private float pitch = 10;
	private static float yaw ;
	private float roll;
	private Player player;
	
	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}
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
	public void move(Player player)
	{
		yaw=player.getYaw();
		pitch=player.getPitch();
		position.y=player.getPosition().y+30;
		position.x=player.getPosition().x;
		position.z=player.getPosition().z;
//		zoom=Normalzoom;
//		calculateZoom();
//		calculateAngles();
//		float yDistance=calculateYDistance();
//		float xzDistance=calculateXZDistance();
//		calculateCameraPosition(yDistance, xzDistance);
//		this.yaw=180-(player.getRotY()+angleAroundPlayer);
//		for(int i=0; i<10; i++)
//		{
//			if(position.getY()-2<0)
//			{
//				zoom--;
//				yDistance=calculateYDistance();
//				xzDistance=calculateXZDistance();
//				calculateCameraPosition(yDistance, xzDistance);
//			}
//		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public static float getYaw() {
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
	public void invertPitch()
	{
		this.pitch=-pitch;
	}
	
	
	

}
