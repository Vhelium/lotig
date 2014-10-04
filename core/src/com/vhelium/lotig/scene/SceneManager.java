package com.vhelium.lotig.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.BoundOrthographicCamera;
import com.vhelium.lotig.components.ShapeRendererBatch;
import com.vhelium.lotig.scene.gamescene.GameScene;

public class SceneManager
{
	public enum SceneType
	{
		SPLASH, TITLE, MAINGAME, OPTIONS
	}
	
	public final static float CAMERA_WIDTH = 800;
	public final static float CAMERA_HEIGHT = 480;
	
	private final Main activity;
	private final BoundOrthographicCamera camera;
	
	private AbstractScene currentScene;
	private final SpriteBatch batch;
	private final ShapeRendererBatch shapeRenderer;
	
	private SplashScene splashScene;
	private GameScene gameScene;
	
	public SceneManager(Main activity, BoundOrthographicCamera camera)
	{
		this.activity = activity;
		this.camera = camera;
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRendererBatch();
	}
	
	public void loadSplashScreenResources()
	{
		splashScene = new SplashScene(activity, this);
		splashScene.loadResources();
	}
	
	public void loadGameSceneResources()
	{
		gameScene = new GameScene(activity, this);
		gameScene.loadResources();
	}
	
	public void loadScene(AbstractScene scene)
	{
		if(!scene.isLoaded)
		{
			scene.loadResources();
		}
	}
	
	public void setScene(AbstractScene scene)
	{
		if(currentScene == scene)
			return;
		
		currentScene = scene;
		
		if(!scene.isLoaded)
		{
			scene.loadResources();
		}
		
		scene.initCamera();
		
		activity.setScreen(scene);
	}
	
	public AbstractScene getCurrentScene()
	{
		return currentScene;
	}
	
	public GameScene getGameScene()
	{
		return gameScene;
	}
	
	public SplashScene getSplashScene()
	{
		return splashScene;
	}
	
	public SpriteBatch getSpriteBatch()
	{
		return batch;
	}
	
	public ShapeRendererBatch getShapeRenderer()
	{
		return shapeRenderer;
	}
	
	public BoundOrthographicCamera getCamera()
	{
		return camera;
	}
	
	public void dispose()
	{
		batch.dispose();
	}
}
