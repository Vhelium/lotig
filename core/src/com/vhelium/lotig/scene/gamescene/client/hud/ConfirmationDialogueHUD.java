package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.ShapeRendererBatch;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.HUD;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;

public class ConfirmationDialogueHUD extends HUD
{
	private Label text;
	private TextButton cmdYes;
	private TextButton cmdNo;
	
	public ConfirmationDialogueHUD(SpriteBatch spriteBatch, ShapeRendererBatch shape)
	{
		super(spriteBatch, shape);
	}
	
	public void loadResources(Main activity, final ClientLevel level)
	{
		TextureRegion textureRegionCmd = GameHelper.$.getGuiAsset("cmdDeath");
		
		text = new Label("Are you sure to open a new level?\nThe current lair map will be closed.\nAll players will be ported.", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 24), Color.WHITE));
		text.setAlignment(Align.top | Align.center);
		text.setPosition(0, 90);
		this.addActor(text);
		
		cmdYes = new TextButton(0, 280, textureRegionCmd, GameHelper.getInstance().getMainFont(FontCategory.InGame, 22), "Yes", Color.WHITE, Color.RED, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				onYes();
			}
		});
		this.addActor(cmdYes);
		
		cmdNo = new TextButton(0, 280, textureRegionCmd, GameHelper.getInstance().getMainFont(FontCategory.InGame, 22), "No", Color.WHITE, Color.RED, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				onNo();
			}
		});
		this.addActor(cmdNo);
		
		cmdYes.setX(SceneManager.CAMERA_WIDTH / 2 - cmdYes.getWidth() / 2 - 120);
		cmdNo.setX(SceneManager.CAMERA_WIDTH / 2 - cmdYes.getWidth() / 2 + 120);
		text.setX(SceneManager.CAMERA_WIDTH / 2 - text.getWidth() / 2);
	}
	
	public void onYes()
	{
		
	}
	
	public void onNo()
	{
		
	}
}
