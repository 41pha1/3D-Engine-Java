package entities;

import java.util.ArrayList;

import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import collision.BroadPhase;
import collision.Collision;
import collision.NarrowPhase;

import engineTester.MainGameLoop;
import engineTester.WorldLoader;

import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity
{
	private BroadPhase bpCollision;
	private NarrowPhase npCollision;
	private Collision collision;
	private float currentSpeed=0;
	private static final float GRAVITY=-50;
	private static final float JUMP_POWER=20;
	private static final float SPEED=10;
	private float accelerationX;
	private float accelerationY; 
	private static float velocityY=0;
	private boolean onGround=true;
	private float yaw, pitch;
	private static ArrayList<Integer> possibleCollision;

	public Player(TexturedModel model, Vector3f position, float rotX,float rotY, float rotZ, float scale)
	{
		super(model, position, rotX, rotY, rotZ, scale);
		bpCollision=new BroadPhase();
		npCollision=new NarrowPhase(new Vector3f(2,10,2));
		collision=new Collision(bpCollision,npCollision,this, WorldLoader.entities);
		possibleCollision = new ArrayList<Integer>();
	}
	
	public void move(Light light)
	{
		updateAABB();
		calculateAngles();
		checkKeyboardInputs();
		super.setRotY(-Camera.getYaw()+180);
		float distance=accelerationX*DisplayManager.getFrameTimeSeconds();
		float dx=(float) (distance*Math.sin(Math.toRadians(super.getRotY())));
		float dz=(float) (distance*Math.cos(Math.toRadians(super.getRotY())));
		distance=accelerationY*DisplayManager.getFrameTimeSeconds();
		dx+=(float) (distance*Math.sin(Math.toRadians(super.getRotY()+90)));
		dz+=(float) (distance*Math.cos(Math.toRadians(super.getRotY()+90)));
//		velocityY+=GRAVITY*DisplayManager.getFrameTimeSeconds();
		this.setPosition(collision.updatePosition(this.getPosition(), new Vector3f(dx, 0, dz), new Vector3f(0,0,0)));
		
		float terrainHeight=0;
		if(super.getPosition().y<=terrainHeight)
		{
			onGround=true;
			velocityY=0;
			super.getPosition().y=terrainHeight;
		}
		light.setPosition(new Vector3f(this.getPosition().x,this.getPosition().y+10, this.getPosition().z));
	}
	private void Jump()
	{
		if(onGround)
		{
			onGround=false;
			this.velocityY=JUMP_POWER;
		}
	}
	public void calculateAngles()
	{
		float pitchChange=Mouse.getDY()*0.1f;
		float yawChange=Mouse.getDX()*0.3f;
		setPitch(getPitch() - pitchChange);
		if (getPitch()<1f)setPitch(0f);;
		if (getPitch()>90f)setPitch(90f);
		setYaw(getYaw() + yawChange);
		this.setRotY(yaw);
	}
	private void checkKeyboardInputs()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			Jump();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			accelerationX = SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			accelerationX = -SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			accelerationY = SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			accelerationY = -SPEED;
		}
		else
		{
			accelerationY = 0;
			accelerationX = 0;
		}
	}

	public float getYaw()
	{
		return yaw;
	}

	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}

	public float getPitch()
	{
		return pitch;
	}

	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}
}
