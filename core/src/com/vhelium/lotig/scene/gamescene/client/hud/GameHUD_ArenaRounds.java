package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_ArenaRounds extends GameHUDMenu
{
	private int round;
	private Label text;
	
	public GameHUD_ArenaRounds(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		this.setZIndex(5);
	}
	
	@Override
	public void setParam(String str)
	{
		round = Integer.parseInt(str);
		updateText();
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(SceneManager.CAMERA_WIDTH / 2 - 187 / 2, 3);
		this.addActor(text);
	}
	
	private void updateText()
	{
		text.setText("Round: " + round);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
