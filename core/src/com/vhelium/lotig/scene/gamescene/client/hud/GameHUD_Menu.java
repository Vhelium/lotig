package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;

public class GameHUD_Menu extends GameHUDMenu
{
	private TextButton cmdSave;
	private TextButton cmdMainMenu;
	private TextButton cmdOptions;
	private TextButton cmdTipps;
	
	public GameHUD_Menu(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void hidden()
	{
		hudCallback.hide("settings");
		hudCallback.hide("tipps");
	}
	
	@Override
	public void loadResources(Main activity)
	{
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("right");
		final TextureRegion textureRegionCmdPort = GameHelper.$.getGuiAsset("cmdMenuOption");
		
		Sprite spriteRight = new Sprite(587, 48, textureRegionRight);
		
		cmdSave = new TextButton(18, 18, textureRegionCmdPort, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Save Game", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				player.getLevel().saveGame();
				hudCallback.postMessage("game saved!", true);
			}
		});
		spriteRight.addActor(cmdSave);
		
		cmdOptions = new TextButton(18, 67, textureRegionCmdPort, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Options", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				hudCallback.toggle("settings");
			}
		});
		spriteRight.addActor(cmdOptions);
		
		cmdTipps = new TextButton(18, 117, textureRegionCmdPort, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Tipps", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				hudCallback.toggle("tipps");
			}
		});
		spriteRight.addActor(cmdTipps);
		
		cmdMainMenu = new TextButton(18, 167, textureRegionCmdPort, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Main Menu", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				player.getLevel().saveGame();
				player.getLevel().returnToMainMenu();
			}
		});
		spriteRight.addActor(cmdMainMenu);
		
		//next button pos: 18 | 166
		
		this.addActor(spriteRight);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
