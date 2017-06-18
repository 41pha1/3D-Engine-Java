package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final int WIDTH = 900;
	private static final int HEIGHT = 450;
	private static final int FPS_CAP = 60;
	private static long lastFrameTime;
	private static float delta;
	private static byte fpsUpdate=0;
	
	public static void createDisplay()
	{		
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			Display.create(new PixelFormat().withSamples(4).withDepthBits(12), attribs);
			Display.setTitle("RIM");
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, WIDTH, HEIGHT);
		lastFrameTime=getCurrentTime();
		try
		{
			Mouse.create();
		} catch (LWJGLException e)
		{
			e.printStackTrace();
		}
		Mouse.setGrabbed(true);
	}
	
	public static void updateDisplay(){
		
		Display.sync(FPS_CAP);
		Display.update();
		fpsUpdate++;
		if(fpsUpdate>30)
		{
			long currentFrameTime=getCurrentTime();
			delta=(currentFrameTime-lastFrameTime);
			lastFrameTime=currentFrameTime;	
			System.out.println("FPS: "+(int)((1000/delta)*30));
			fpsUpdate=0;
		}
	}
	public static float getFrameTimeSeconds()
	{
		return 0.02f;
	}
	public static long getCurrentTime()
	{
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
	public static void closeDisplay()
	{
		Mouse.destroy();
		Display.destroy();
	}

}
