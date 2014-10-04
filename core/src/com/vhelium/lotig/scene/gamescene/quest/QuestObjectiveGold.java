package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveGold extends QuestObjective
{
	int amountToGather;
	int amountGathered;
	Quest quest;
	
	public QuestObjectiveGold(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		amountToGather = Integer.parseInt(st.nextToken());
		if(st.hasMoreTokens())
			amountGathered = Integer.parseInt(st.nextToken());
	}
	
	@Override
	public String getDescription()
	{
		return "Gather " + amountToGather + " gold. Total gold gathered: " + amountGathered + ".";
	}
	
	@Override
	public String getStringFormat()
	{
		return "gold;" + amountToGather + ";" + amountGathered;
	}
	
	@Override
	public void onGoldChanged(int amount)
	{
		amountGathered += amount;
		if(amountGathered >= amountToGather)
			quest.onFinished();
	}
	
	@Override
	public void setProgress(int progress)
	{
		this.amountGathered = progress;
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
