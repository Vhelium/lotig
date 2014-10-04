package com.vhelium.lotig.scene;

import com.badlogic.gdx.Input.Keys;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.ISettingsCallback;
import com.vhelium.lotig.scene.gamescene.client.hud.GameHUD_Settings;

public class OptionsScene extends AbstractScene
{
	public OptionsScene(Main activity, SceneManager sceneManager)
	{
		super(activity, sceneManager);
		requiresInput = true;
	}
	
	private GameHUD_Settings hud_Settings;
	
	@Override
	public void loadResources()
	{
		super.loadResources();
		hud_Settings = new GameHUD_Settings(null);
		hud_Settings.loadResources(activity);
		
		hud_Settings.isActive = true;
		hud_Settings.showUp();
		
		if(hud_Settings.subMenu != null)
		{
			hud_Settings.subMenu.isActive = true;
			hud_Settings.subMenu.showUp();
		}
		
		this.stage.addActor(hud_Settings);
		isLoaded = true;
	}
	
	public void setListener(ISettingsCallback listener)
	{
		hud_Settings.addListener(listener);
	}
	
	@Override
	public void show()
	{
		super.show();
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.BACK || keycode == Keys.ESCAPE)
		{
			SoundManager.playSound(SoundFile.menu_selected);
			sceneManager.setScene(new MainMenuScene(activity, sceneManager));
			return true;
		}
		return false;
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
		
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
