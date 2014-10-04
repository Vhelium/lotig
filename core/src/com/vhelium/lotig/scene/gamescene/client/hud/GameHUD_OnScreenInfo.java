package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_OnScreenInfo extends GameHUDMenu
{
	private Label text;
	private Sprite hud;
	
	public GameHUD_OnScreenInfo(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		this.setZIndex(5);
	}
	
	@Override
	public void setParam(Integer integer)
	{
		
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		text.setAlignment(Align.top | Align.center);
		text.setPosition(0, -20);
		hud.addActor(text);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
