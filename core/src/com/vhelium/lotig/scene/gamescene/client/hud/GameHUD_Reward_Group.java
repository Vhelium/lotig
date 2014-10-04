package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class GameHUD_Reward_Group extends Group
{
	float posY = 110;
	float offsetX = 15;
	
	public GameHUD_Reward_Group(final GameHUD_Reward owner, String message, int gold, int fame, Item[] items, final Main activity)
	{
		TextureRegion txtBackground = GameHelper.$.getGuiAsset("cmdReward");
		this.setPosition(0, posY);
		
		int width = 0;
		
		if(gold > 0)
		{
			GameHUD_Reward_Amount s = new GameHUD_Reward_Amount(gold, width, 0, txtBackground, GameHelper.$.getGuiAsset("gold"));
			this.addActor(s);
			width += s.getWidth() + offsetX;
		}
		if(fame > 0)
		{
			GameHUD_Reward_Amount s = new GameHUD_Reward_Amount(fame, width, 0, txtBackground, GameHelper.$.getGuiAsset("fame"));
			this.addActor(s);
			width += s.getWidth() + offsetX;
		}
		if(items.length > 0)
		{
			for(Item item : items)
			{
				Sprite s = new Sprite(txtBackground);
				s.setX(width);
				s.addActor(item.getIcon());
				this.addActor(s);
				width += s.getWidth() + offsetX;
			}
		}
		
		if(width > 0)
			width -= offsetX;
		
		this.setX(SceneManager.CAMERA_WIDTH / 2 - width / 2);
		
		Label text = new Label(message, new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.pack();
		text.setX(width / 2 - text.getWidth() / 2);
		text.setY(-text.getHeight() - 5);
		this.addActor(text);
		
		this.getColor().a = 0;
		this.addAction(Actions.sequence(Actions.alpha(1, 1f, Interpolation.exp10Out), Actions.delay(2.5f), Actions.alpha(0, 0.5f, Interpolation.circleIn), Actions.delay(.3f), Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				GameHUD_Reward_Group.this.remove();
				owner.onEndReached(GameHUD_Reward_Group.this);
			}
		})));
	}
}
