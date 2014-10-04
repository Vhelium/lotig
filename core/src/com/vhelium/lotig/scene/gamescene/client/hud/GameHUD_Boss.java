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

public class GameHUD_Boss extends GameHUDMenu
{
	private float bossHpPercent = 1f;
	private String name = "null";
	private Label text;
	Sprite hud;
	Sprite hp;
	
	public GameHUD_Boss(IGameHUDCallback hudCallback)
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
			name = st.nextToken();
			bossHpPercent = Math.max(0, Math.min(1, Float.parseFloat(st.nextToken())));
			hp.setWidth(bossHpPercent * hud.getWidth());
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
		final TextureRegion textureRegionHud = GameHelper.$.getGuiAsset("bossHud");
		final TextureRegion textureRegionHp = GameHelper.$.getGuiAsset("bossHudHp");
		
		hud = new Sprite(0, 460, textureRegionHud);
		hud.setX(SceneManager.CAMERA_WIDTH / 2 - hud.getWidth() / 2);
		
		hp = new Sprite(0, 0, textureRegionHp);
		hud.addActor(hp);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		text.setAlignment(Align.top | Align.center);
		text.setPosition(0, -20);
		hud.addActor(text);
		updateText();
		
		this.addActor(hud);
	}
	
	private void updateText()
	{
		text.setText(name);
		text.setX(hud.getWidth() / 2 - text.getWidth() / 2);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
