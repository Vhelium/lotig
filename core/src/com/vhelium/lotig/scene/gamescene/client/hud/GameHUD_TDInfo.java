package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_TDInfo extends GameHUDMenu
{
	private int cash, round, hp;
	private Label text;
	
	public GameHUD_TDInfo(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		this.setZIndex(5);
	}
	
	public void updateCash(int cash)
	{
		this.cash = cash;
		updateText();
	}
	
	public void updateRound(int round)
	{
		this.round = round;
		updateText();
	}
	
	public void updateHP(int hp)
	{
		this.hp = hp;
		updateText();
	}
	
	private void updateText()
	{
		text.setText("Round: " + round + "      $$$: " + cash + "      HP: " + hp);
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
//		final TextureRegion textureRegionPvpScores = GameHelper.$.getGuiAsset("pvpScores");
//		Sprite sprite = new Sprite(SceneManager.CAMERA_WIDTH / 2 - 187 / 2, 0, 187, 24, textureRegionPvpScores, activity.getVertexBufferObjectManager());
//		this.attachChild(sprite);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(50, 3);
		this.addActor(text);
	}
	
	public int getCash()
	{
		return cash;
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
