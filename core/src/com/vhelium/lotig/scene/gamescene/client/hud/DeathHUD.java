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
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;

public class DeathHUD extends HUD
{
	private Label txtDeathMessage;
	private Label txtPenalty;
	private TextButton cmdRespawn;
	private float respawnLeft;
	
	public DeathHUD(SpriteBatch spriteBatch, ShapeRendererBatch shape)
	{
		super(spriteBatch, shape);
	}
	
	public void loadResources(Main activity, final ClientLevel level)
	{
		TextureRegion textureRegionCmd = GameHelper.$.getGuiAsset("cmdDeath");
		
		txtDeathMessage = new Label("YOU HAVE DIED", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 24), Color.WHITE));
		txtDeathMessage.setAlignment(Align.top | Align.center);
		txtDeathMessage.setPosition(0, 90);
		this.addActor(txtDeathMessage);
		
		txtPenalty = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 22), Color.WHITE));
		txtPenalty.setAlignment(Align.top | Align.center);
		txtPenalty.setPosition(0, 135);
		this.addActor(txtPenalty);
		
		cmdRespawn = new TextButton(0, 280, textureRegionCmd, GameHelper.getInstance().getMainFont(FontCategory.InGame, 22), "Press to continue", new Color(190 / 255f, 0, 0, 1), new Color(0.7f, 0.7f, 0.7f, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				level.shareRequestRespawn();
			}
		});
		this.addActor(cmdRespawn);
		
		cmdRespawn.setX(SceneManager.CAMERA_WIDTH / 2 - cmdRespawn.getWidth() / 2);
		txtDeathMessage.setX(SceneManager.CAMERA_WIDTH / 2 - txtDeathMessage.getWidth() / 2);
		txtPenalty.setX(SceneManager.CAMERA_WIDTH / 2 - txtDeathMessage.getWidth() / 2);
	}
	
	public void setPenalty(int[] fameGold)
	{
		if(fameGold != null)
		{
			txtPenalty.setText("Death takes its toll:\n" + fameGold[1] + " gold lost\n" + fameGold[0] + " fame lost");
			txtPenalty.setX(SceneManager.CAMERA_WIDTH / 2 - txtDeathMessage.getWidth() / 2);
		}
		else
			txtPenalty.setText("");
	}
	
	public void setRespawn(float duration)
	{
		respawnLeft = duration;
		if(respawnLeft > 0)
		{
			cmdRespawn.setEnabled(false);
			cmdRespawn.setText("Respawn in: " + ((int) (respawnLeft / 1000f)));
		}
		else
		{
			cmdRespawn.setEnabled(true);
			cmdRespawn.setText("Press to continue");
		}
	}
	
	public void update(float delta)
	{
		if(respawnLeft > 0)
		{
			respawnLeft -= delta;
			if(respawnLeft <= 0)
			{
				cmdRespawn.setEnabled(true);
				cmdRespawn.setText("Press to continue");
			}
			else
			{
				cmdRespawn.setEnabled(false);
				cmdRespawn.setText("Respawn in: " + ((int) (respawnLeft / 1000f) + 1));
			}
		}
	}
}