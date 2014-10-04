package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveKillAny extends QuestObjective
{
	int count;
	int killed;
	Quest quest;
	
	public QuestObjectiveKillAny(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		count = Integer.parseInt(st.nextToken());
		if(st.hasMoreTokens())
			killed = Integer.parseInt(st.nextToken());
	}
	
	@Override
	public String getDescription()
	{
		return "Kill " + count + " enemies. " + killed + " enemies slain.";
	}
	
	@Override
	public String getStringFormat()
	{
		return "killany;" + count + ";" + killed;
	}
	
	@Override
	public void onEntityDied(String name)
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
