package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveKillBoss extends QuestObjective
{
	int count;
	int killed;
	Quest quest;
	
	public QuestObjectiveKillBoss(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		count = Integer.parseInt(st.nextToken());
		if(st.hasMoreTokens())
			killed = Integer.parseInt(st.nextToken());
	}
	
	@Override
	public String getDescription()
	{
		return "Kill " + count + " boss monster! Boss monster slain: " + killed;
	}
	
	@Override
	public String getStringFormat()
	{
		return "killboss;" + count + ";" + killed;
	}
	
	@Override
	public void onBossDied()
	{
		killed++;
		if(killed >= count)
			quest.onFinished();
	}
	
	@Override
	public void setProgress(int progress)
	{
		this.killed = progress;
	}
	
	@Override
	public void onDialogueFinished(int uniqueId)
	{
		
	}
	
	@Override
	public void onAreaEntered(String areaName)
	{
		
	}
}
