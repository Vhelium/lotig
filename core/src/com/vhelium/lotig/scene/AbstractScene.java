package com.vhelium.lotig.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.BoundOrthographicCamera;
import com.vhelium.lotig.components.ShapeRendererBatch;

public abstract class AbstractScene implements Screen, InputProcessor
{
	protected Main activity;
	protected SceneManager sceneManager;
	protected SpriteBatch batch;
	protected ShapeRendererBatch shapeRenderer;
	protected BoundOrthographicCamera camera;
	
	protected boolean requiresInput = false;
	protected Stage stage;
	protected HUD currentHUD;
	public boolean isLoaded = false;
	
	public AbstractScene(Main activity, SceneManager sceneManager)
	{
		this.activity = activity;
		this.sceneManager = sceneManager;
		this.batch = sceneManager.getSpriteBatch();
		this.shapeRenderer = sceneManager.getShapeRenderer();
		this.camera = sceneManager.getCamera();
	}
	
	public void loadResources()
	{
		stage = new Stage(new StretchViewport(SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT, camera), batch);
	}
	
	@Override
	public void render(float delta)
	{
		update(delta);
		
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		stage.act(delta);
		stage.draw();
		
		if(currentHUD != null)
		{
			currentHUD.act(delta);
			currentHUD.draw();
		}
	}
	
	private void setInputProcessor()
	{
		InputMultiplexer plex = new InputMultiplexer();
		if(currentHUD != null)
			plex.addProcessor(currentHUD);
		if(requiresInput)
		{
			plex.addProcessor(stage);
			plex.addProcessor(this);
		}
		Gdx.input.setInputProcessor(plex);
		Gdx.input.setCatchBackKey(true);
	}
	
	@Override
	public void show()
	{
		setInputProcessor();
	}
	
	public abstract void update(float delta);
	
	public void setActiveHUD(HUD hud)
	{
		this.currentHUD = hud;
		setInputProcessor();
	}
	
	public HUD getHUD()
	{
		return currentHUD;
	}
	
	public Stage getStage()
	{
		return stage;
	}
	
	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, false);
		camera.setToOrtho(true, SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		
		if(currentHUD != null)
		{
			currentHUD.getViewport().update(width, height, false);
			((OrthographicCamera) currentHUD.getCamera()).setToOrtho(true, SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		}
	}
	
	public void initCamera()
	{
		camera.setBoundsEnabled(false);
		camera.setChaseActor(null);
		camera.position.set(SceneManager.CAMERA_WIDTH / 2, SceneManager.CAMERA_HEIGHT / 2, 0);
		camera.update();
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.BACK || keycode == Keys.ESCAPE)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean keyTyped(char character)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean scrolled(int amount)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
