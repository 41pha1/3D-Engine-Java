package entities;

import java.util.ArrayList;

import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import collision.BroadPhase;
import collision.NarrowPhase;

import engineTester.MainGameLoop;
import engineTester.WorldLoader;

import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity
{
	private BroadPhase bpCollision;
	private NarrowPhase npCollision;
	private float currentSpeed=0;
	private float currentTurnSpeed=0;
	private static final float GRAVITY=-50;
	private static final float JUMP_POWER=20;
	private static final float SPEED=100;
	private static final float TURN_SPEED=160;
	private static float velocityY=0;
	private boolean onGround=true;
	private static ArrayList<Integer> possibleCollision;
	private static ArrayList<Integer> collidingEntities;
	private static Vector3f position;
	private static float rotX, rotY, rotZ, scale;

	public Player(TexturedModel model, Vector3f position, float rotX,float rotY, float rotZ, float scale)
	{
		super(model, position, rotX, rotY, rotZ, scale);
		this.position=position;
		this.rotX=rotX;
		this.rotY=rotY;
		this.rotZ=rotZ;
		this.scale=scale;
		bpCollision=new BroadPhase();
		npCollision=new NarrowPhase(new Vector3f(
				this.getAabb().getXmax()-this.getAabb().getXmin(),
				this.getAabb().getYmax()-this.getAabb().getYmin(),
				this.getAabb().getZmax()-this.getAabb().getZmin()));
		possibleCollision = new ArrayList<Integer>();
	}
	
	public void move(Light light)
	{
		updateAABB();
		possibleCollision = bpCollision.checkBroadPhaseCollision(this, WorldLoader.entities);
		checkKeyboardInputs();
		super.increaseRotation(0, currentTurnSpeed*DisplayManager.getFrameTimeSeconds(), 0);
		float distance=currentSpeed*DisplayManager.getFrameTimeSeconds();
		float dx=(float) (distance*Math.sin(Math.toRadians(super.getRotY())));
		float dz=(float) (distance*Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		velocityY+=GRAVITY*DisplayManager.getFrameTimeSeconds();
		possibleCollision.add(1);
		if(possibleCollision.size()>0 && (dx !=0 || dz !=0))
		{
			for(int i = 0; i<possibleCollision.size();i++)
			{
				npCollision.checkNarrowPhaseCollision(
						WorldLoader.getEntities().get(possibleCollision.get(i))
						.getAabb().getTransformationMatrix(), 
						WorldLoader.getEntities().get(possibleCollision.get(i)), 
						this.getPosition(), new Vector3f(dx,0,dz));
			}
		}
		super.increasePosition(0, velocityY * DisplayManager.getFrameTimeSeconds(), 0);
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
	private void checkKeyboardInputs()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			Jump();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			this.currentSpeed = SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			this.currentSpeed = -SPEED;
		}else
		{
			this.currentSpeed=0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			this.currentTurnSpeed = TURN_SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			this.currentTurnSpeed = -TURN_SPEED;
		}else
		{
			this.currentTurnSpeed=0;
		}
	}
}
