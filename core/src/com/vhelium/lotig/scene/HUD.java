package com.vhelium.lotig.scene;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.vhelium.lotig.components.ShapeRendererBatch;

public abstract class HUD extends Stage
{
	public boolean isActive = false;
	protected SpriteBatch batch;
	protected ShapeRendererBatch shape;
	
	public HUD(SpriteBatch batch, ShapeRendererBatch shape)
	{
		super(new StretchViewport(SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT, generateCamera()), batch);
		this.batch = batch;
		this.shape = shape;
	}
	
	@Override
	public void draw()
	{
		getCamera().update();
		batch.setProjectionMatrix(getCamera().combined);
		shape.setProjectionMatrix(getCamera().combined);
		super.draw();
	}
	
	private static Camera generateCamera()
	{
		OrthographicCamera camera = new OrthographicCamera(SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		camera.setToOrtho(true, SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		return camera;
	}
}