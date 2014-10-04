package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_Fame extends GameHUDMenu
{
	private int fame = 0;
	private Label text;
	
	public GameHUD_Fame(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		this.setZIndex(6);
	}
	
	@Override
	public void setParam(String s)
	{
		setParam(String.valueOf(s));
	}
	
	@Override
	public void setParam(Integer i)
	{
		fame = i;
		updateText();
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		final TextureRegion textureRegionPvpScores = GameHelper.$.getGuiAsset("fame");
		
		Sprite sprite = new Sprite(587 - 81, 3, 34, 34, textureRegionPvpScores);
		this.addActor(sprite);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 22), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(587 - 45, 13);
		this.addActor(text);
		
		updateText();
	}
	
	private void updateText()
	{
		text.setText(String.valueOf(fame));
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
