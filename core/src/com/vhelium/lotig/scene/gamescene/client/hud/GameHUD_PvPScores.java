package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.StringTokenizer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_PvPScores extends GameHUDMenu
{
	private int scoreRed = 0;
	private int scoreBlue = 0;
	private Label text;
	
	public GameHUD_PvPScores(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		this.setZIndex(5);
	}
	
	@Override
	public void setParam(String str)
	{
		param = str;
		if(param != null)
		{
			StringTokenizer st = new StringTokenizer(param.toString(), ";");
			scoreRed = Integer.parseInt(st.nextToken());
			scoreBlue = Integer.parseInt(st.nextToken());
			updateText();
		}
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		final TextureRegion textureRegionPvpScores = GameHelper.$.getGuiAsset("pvpScores");
		
		Sprite sprite = new Sprite(SceneManager.CAMERA_WIDTH / 2 - 187 / 2, 0, 187, 24, textureRegionPvpScores);
		this.addActor(sprite);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(sprite.getX() + 5, 3);
		this.addActor(text);
	}
	
	private void updateText()
	{
		text.setText("Red: " + scoreRed + "             Blue: " + scoreBlue);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
