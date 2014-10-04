package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public abstract class GameHUDMenu extends Group
{
	public boolean isActive = false;
	protected IGameHUDCallback hudCallback;
	public GameHUDMenu subMenu;
	protected PlayerClient player;
	protected Object param;
	
	public GameHUDMenu(IGameHUDCallback hudCallback)
	{
		this.hudCallback = hudCallback;
		this.setZIndex(10);
	}
	
	public abstract void showUp();
	
	public void hidden()
	{
		
	}
	
	public abstract void loadResources(Main activity);
	
	public void initHUD(PlayerClient player)
	{
		this.player = player;
	}
	
	public abstract void update(float delta);
	
	public void setParam(Object obj)
	{
		param = obj;
	}
	
	public void setParam(String str)
	{
		param = str;
	}
	
	public void setParam(Integer integer)
	{
		param = integer;
	}
	
	public void dispose()
	{
		
	}
}
