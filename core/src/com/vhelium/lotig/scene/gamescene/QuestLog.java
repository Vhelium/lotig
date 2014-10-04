package com.vhelium.lotig.scene.gamescene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.client.hud.GameSubHUD_Quests;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class QuestLog extends Group
{
	private final Main activity;
	private Label text;
	public boolean isActive;
	private final float seenDelay = 200;
	private float seenDelayElapsed;
	
	public QuestLog(Main activity)
	{
		this.activity = activity;
	}
	
	public void initialize()
	{
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(280, 120);
		this.addActor(text);
	}
	
	public void updateText(PlayerClient player)
	{
		text.setText(GameSubHUD_Quests.getQuestText(player, text.getStyle().font));
		text.pack();
		text.setX(SceneManager.CAMERA_WIDTH / 2 - text.getWidth() / 2);
	}
	
	public void toggled(boolean visible)
	{
		isActive = visible;
		seenDelayElapsed = 0;
	}
	
	public void update(float delta, PlayerClient player)
	{
		if(isActive)
			if(seenDelayElapsed < seenDelay)
			{
				seenDelayElapsed += delta;
				if(seenDelayElapsed >= seenDelay)
					player.onQuestHUDOpened();
			}
	}
}
