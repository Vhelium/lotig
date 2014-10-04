package com.vhelium.lotig;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.vhelium.lotig.components.BoundOrthographicCamera;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundManager;

public class Main extends Game implements ApplicationListener
{
	private SceneManager sceneManager;
	
	@Override
	public void create()
	{
		BoundOrthographicCamera camera = new BoundOrthographicCamera(SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		camera.setToOrtho(true, SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		
		sceneManager = new SceneManager(this, camera);
		sceneManager.loadSplashScreenResources();
		sceneManager.setScene(sceneManager.getSplashScene());
		sceneManager.getSplashScene().loadGame();
	}
	
	@Override
	public void dispose()
	{
		SoundManager.stopMusic();
		SoundManager.unload();
		GameHelper.$.unloadTextures();
		if(sceneManager != null)
			sceneManager.dispose();
		super.dispose();
	}
	
	@Override
	public void render()
	{
		super.render();
		SoundManager.update(Gdx.graphics.getDeltaTime() * 1000);
	}
	
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
	}
	
	@Override
	public void pause()
	{
		super.pause();
	}
	
	@Override
	public void resume()
	{
		super.resume();
	}
}
