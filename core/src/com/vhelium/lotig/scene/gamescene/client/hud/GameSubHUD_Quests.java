package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.vhelium.lotig.components.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.Utility;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.IPlayerAttributeListener;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.quest.Quest;
import com.vhelium.lotig.scene.gamescene.quest.QuestType;

public class GameSubHUD_Quests extends GameHUDMenu implements IPlayerAttributeListener
{
	Label text;
	BitmapFont font;
	
	public GameSubHUD_Quests(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		updateText();
		if(hudCallback.getCharacterMenu().isActive)
		{
			player.onQuestHUDOpened();
			hudCallback.getCharacterMenu().highlightButton("quests");
		}
	}
	
	@Override
	public void loadResources(Main activity)
	{
		font = GameHelper.getInstance().getMainFont(FontCategory.InGame, 16);
		text = new Label("", new LabelStyle(font, Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(240, 15);
		this.addActor(text);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public void updateText()
	{
		text.setText(getQuestText(player, font));
	}
	
	public static final String getQuestText(PlayerClient player, BitmapFont font)
	{
		String s = "";
		if(player.getLevel().isHost())
		{
			for(Quest quest : player.getQuests().values())
				s += Utility.getNormalizedText(font, quest.getQuestType().toString() + " Quest:\n\n" + quest.getDescription() + "\n\n\n", 350);
		}
		else
		{
			if(player.getAvailableStoryQuest() != null)
				s += "Story Quest (Player 1):\n\n" + Utility.getNormalizedText(font, player.getAvailableStoryQuest().getDescription(), 350) + "\n\n\n";
			
			if(player.getQuests().containsKey(QuestType.Dungeon))
				s += player.getQuests().get(QuestType.Dungeon).getQuestType().toString() + " Quest:\n\n\n" + Utility.getNormalizedText(font, player.getQuests().get(QuestType.Dungeon).getDescription(), 350) + "\n\n";
			
			if(player.getQuests().containsKey(QuestType.Daily))
				s += player.getQuests().get(QuestType.Daily).getQuestType().toString() + " Quest:\n\n\n" + Utility.getNormalizedText(font, player.getQuests().get(QuestType.Daily).getDescription(), 350);
		}
		return s;
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		player.addAttributeListener(this);
		super.initHUD(player);
	}
	
	@Override
	public void playerAttributeChanged()
	{
		
	}
}