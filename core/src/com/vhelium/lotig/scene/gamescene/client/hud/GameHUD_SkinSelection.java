package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.DynamicSelectionMenu;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_SkinSelection extends GameHUDMenu
{
	private Label text;
	private DynamicSelectionMenu skins;
	
	public GameHUD_SkinSelection(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		skins.setContent(player.getUnlockedSkins(), true, player.asset);
	}
	
	@Override
	public void loadResources(Main activity)
	{
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("right");
		final TextureRegion textureRegionCmdLoot = GameHelper.$.getGuiAsset("cmdEnchanting");
		
		Sprite spriteRight = new Sprite(587, 48, textureRegionRight);
		
		text = new Label("Select a skin:", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 16), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.pack();
		text.setPosition(spriteRight.getWidth() / 2 - text.getWidth() / 2, 17);
		spriteRight.addActor(text);
		
		skins = new DynamicSelectionMenu(activity, 0, text.getY() + text.getHeight() + 14, textureRegionCmdLoot, GameHelper.getInstance().getMainFont(FontCategory.InGame, 18))
		{
			@Override
			public void onButtonClicked(String text)
			{
				player.changeSkin(skins.getSelectedItem());
			}
		};
		skins.setX(spriteRight.getWidth() / 2 - skins.getWidth() / 2);
		
		spriteRight.addActor(skins);
		
		this.addActor(spriteRight);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
