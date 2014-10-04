package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveFame extends QuestObjective
{
	int amountToGather;
	int amountGathered;
	Quest quest;
	
	public QuestObjectiveFame(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		amountToGather = Integer.parseInt(st.nextToken());
		if(st.hasMoreTokens())
			amountGathered = Integer.parseInt(st.nextToken());
	}
	
	@Override
	public String getDescription()
	{
		return "Gain " + amountToGather + " fame. Total fame gained: " + amountGathered + ".";
	}
	
	@Override
	public String getStringFormat()
	{
		return "fame;" + amountToGather + ";" + amountGathered;
	}
	
	@Override
	public void onFameChanged(int amount)
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
