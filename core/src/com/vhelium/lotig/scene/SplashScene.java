package com.vhelium.lotig.scene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class SplashScene extends AbstractScene
{
	private Texture splashTexture;
	Image image;
	
	public SplashScene(Main activity, SceneManager sceneManager)
	{
		super(activity, sceneManager);
	}
	
	@Override
	public void loadResources()
	{
		super.loadResources();
		
		image = new Image(GameHelper.getInstance().loadTextureRegion("gfx/splash.png"));
		image.setWidth(SceneManager.CAMERA_WIDTH);
		image.setHeight(SceneManager.CAMERA_HEIGHT);
		
		image.getColor().a = 1f;
		
		stage.addActor(image);
		
		isLoaded = true;
	}
	
	private boolean loadGame = false;
	
	public void loadGame()
	{
		loadGame = true;
	}
	
	@Override
	public void render(float delta)
	{
		super.render(delta);
		if(loadGame)
		{
			loadGame = false;
			Timer.schedule(new Task()
			{
				@Override
				public void run()
				{
					sceneManager.loadGameSceneResources();
					
					image.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable()
					{
						@Override
						public void run()
						{
							sceneManager.setScene(new MainMenuScene(activity, sceneManager));
						}
					})));
				}
				
			}, 0.2f);
			
		}
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	@Override
	public void show()
	{
		super.show();
	}
	
	@Override
	public void hide()
	{
		
	}
	
	@Override
	public void pause()
	{
		
	}
	
	@Override
	public void resume()
	{
		
	}
	
	@Override
	public void dispose()
	{
		splashTexture.dispose();
	}
}
