package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_Reward_Amount extends Sprite
{
	public GameHUD_Reward_Amount(int amount, TextureRegion background, TextureRegion amountIcon)
	{
		this(amount, 0, 0, background, amountIcon);
	}
	
	public GameHUD_Reward_Amount(int amount, float x, float y, TextureRegion background, TextureRegion amountIcon)
	{
		super(x, y, background);
		
		Sprite icon = new Sprite(11, 4, 28, 28, amountIcon);
		this.addActor(icon);
		
		Label text = new Label("x" + amount, new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 12), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.pack();
		text.setX(this.getWidth() / 2 - text.getWidth() / 2);
		text.setY(this.getHeight() - text.getHeight() - 3);
		this.addActor(text);
	}
}
