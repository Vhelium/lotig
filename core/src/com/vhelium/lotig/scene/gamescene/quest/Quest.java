package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class Quest
{
	private QuestType type;
	private final int step;
	private String lair;
	public QuestObjective questObjective;
	private final int rewardGold;
	private final int rewardFame;
	private Item[] rewardItems = null;
	boolean isFinished = false;
	ClientLevel level;
	
	public Quest(String properties, ClientLevel level)//by tmxMap or StringFormat
	{
		this.level = level;
		StringTokenizer st = new StringTokenizer(properties, ";");
		String questType = st.nextToken();
		if(questType.equalsIgnoreCase("dungeon"))
			type = QuestType.Dungeon;
		else if(questType.equalsIgnoreCase("story"))
			type = QuestType.Story;
		else if(questType.equalsIgnoreCase("daily"))
			type = QuestType.Daily;
		else
			Log.e("Quest", "NO QUEST TYPE FOUND!");
		
		step = Integer.parseInt(st.nextToken());
		if(type == QuestType.Story)
			lair = st.nextToken();
		
		rewardGold = Integer.parseInt(st.nextToken());
		rewardFame = Integer.parseInt(st.nextToken());
		
		String items = st.nextToken();
		if(!items.equals("null"))
		{
			StringTokenizer stItems = new StringTokenizer(items, ",");
			rewardItems = new Item[stItems.countTokens()];
			int i = 0;
			while(stItems.hasMoreTokens())
			{
				rewardItems[i] = Item.getItemFromStringFormat(st.nextToken());
				i++;
			}
		}
		
		String objective = st.nextToken();
		if(objective.equals("kill"))
			questObjective = new QuestObjectiveKill(st, this);
		else if(objective.equals("goto"))
			questObjective = new QuestObjectiveGoTo(st, this);
		else if(objective.equals("killboss"))
			questObjective = new QuestObjectiveKillBoss(st, this);
		else if(objective.equals("gold"))
			questObjective = new QuestObjectiveGold(st, this);
		else if(objective.equals("fame"))
			questObjective = new QuestObjectiveFame(st, this);
		else if(objective.equals("killany"))
			questObjective = new QuestObjectiveKillAny(st, this);
		else if(objective.equals("arenarounds"))
			questObjective = new QuestObjectiveArenaRound(st, this);
		else if(objective.equals("speakto"))
			questObjective = new QuestObjectiveSpeakTo(st, this);
		else if(objective.equals("enterarea"))
			questObjective = new QuestObjectiveEnterArea(st, this);
		else
			Log.e("Quest", "NO QUEST OBJECTIVE FOUND!: " + objective);
	}
	
	public QuestType getQuestType()
	{
		return type;
	}
	
	public int getStep()
	{
		return step;
	}
	
	public String getStringFormat()
	{
		String res = "";
		switch(type)
		{
			case Story:
				res += "story;" + step + ";" + lair + ";" + rewardGold + ";" + rewardFame + ";" + getItemRewardString() + ";" + questObjective.getStringFormat();
				return res;
				
			case Daily:
				res += "daily;" + 0 + ";" + rewardGold + ";" + rewardFame + ";" + getItemRewardString() + ";" + questObjective.getStringFormat();
				return res;
				
			default:
				return null;
		}
	}
	
	private String getItemRewardString()
	{
		if(rewardItems == null || rewardItems.length == 0)
			return "null";
		else
		{
			String s = "";
			for(Item item : rewardItems)
				s += item.toStringFormat() + ",";
			return s.substring(0, s.length() - 1);
		}
	}
	
	public String getDescription()
	{
		return questObjective.getDescription();
	}
	
	public int getGoldReward()
	{
		return rewardGold;
	}
	
	public int getFameReward()
	{
		return rewardFame;
	}
	
	public Item[] getItemReward()
	{
		return rewardItems;
	}
	
	public void onFinished()
	{
		isFinished = true;
		level.onQuestFinished(this);
	}
	
	public boolean isFinished()
	{
		return isFinished;
	}
	
	public String getLair()
	{
		return lair;
	}
	
	public QuestObjective getQuestObjective()
	{
		return questObjective;
	}
	
	public void setProgress(int progress)
	{
		questObjective.setProgress(progress);
	}
}
