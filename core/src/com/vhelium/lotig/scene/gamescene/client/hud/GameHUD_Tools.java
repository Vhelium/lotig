package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;

public class GameHUD_Tools extends GameHUDMenu
{
	ButtonSprite cmdTown;
	ButtonSprite cmdCharMenu;
	ButtonSprite cmdSettings;
	ButtonSprite cmdConnection;
	Sprite spriteTopRight;
	Sprite spriteCharMenuHighlighted;
	Main activity;
	
	public GameHUD_Tools(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void setParam(final Integer i)
	{
		if(i == 0)
		{
			spriteCharMenuHighlighted.clearActions();
			spriteCharMenuHighlighted.remove();
		}
		else if(i == 1)
		{
			cmdCharMenu.addActor(spriteCharMenuHighlighted);
			spriteCharMenuHighlighted.addAction(Actions.forever(Actions.sequence(Actions.alpha(1f, 0.5f), Actions.alpha(0f, 0.5f))));
		}
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		final TiledTextureRegion textureRegionCmdCharMenu = new TiledTextureRegion(GameHelper.$.getGuiAsset("cmdCharMenu"), 1, 2, true);
		final TiledTextureRegion textureRegionCmdTown = new TiledTextureRegion(GameHelper.$.getGuiAsset("cmdTown"), 1, 2, true);
		final TiledTextureRegion textureRegionCmdConnection = new TiledTextureRegion(GameHelper.$.getGuiAsset("cmdConnection"), 1, 2, true);
		final TiledTextureRegion textureRegionCmdSettings = new TiledTextureRegion(GameHelper.$.getGuiAsset("cmdSettings"), 1, 2, true);
		final TextureRegion textureRegionTopRight = GameHelper.$.getGuiAsset("topRight");
		
		spriteTopRight = new Sprite(587, 0, textureRegionTopRight);
		this.addActor(spriteTopRight);
		
		cmdTown = new ButtonSprite(589, 1, textureRegionCmdTown, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				hudCallback.townPort();
			}
		});
		this.addActor(cmdTown);
		
		cmdCharMenu = new ButtonSprite(641, 1, textureRegionCmdCharMenu, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				hudCallback.toggle("character");
				if(hudCallback.getCurrentOnScreenInfo() != null && hudCallback.getCurrentOnScreenInfo().id == 13/*Close Menu*/)
					hudCallback.hideOSI(13/*Close Menu*/);
			}
		});
		this.addActor(cmdCharMenu);
		
		cmdConnection = new ButtonSprite(693, 1, textureRegionCmdConnection, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				hudCallback.toggle("connection");
			}
		});
		this.addActor(cmdConnection);
		
		cmdSettings = new ButtonSprite(746, 1, textureRegionCmdSettings, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				hudCallback.toggle("menu");
			}
		});
		this.addActor(cmdSettings);
		
		spriteCharMenuHighlighted = new Sprite(0, 0, textureRegionCmdCharMenu.getTextureRegion(0));
		spriteCharMenuHighlighted.setColor(0, 1, 0, 1);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
