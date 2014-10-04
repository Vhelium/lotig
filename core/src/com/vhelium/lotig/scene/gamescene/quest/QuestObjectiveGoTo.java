package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveGoTo extends QuestObjective
{
	String targetMap;
	String text;
	Quest quest;
	
	public QuestObjectiveGoTo(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		targetMap = st.nextToken();
		text = st.nextToken();
	}
	
	@Override
	public String getDescription()
	{
		return text;
	}
	
	@Override
	public String getStringFormat()
	{
		return "goto;" + targetMap + ";" + text;
	}
	
	@Override
	public void onMapLoaded(String name)
	{
		if(name.equalsIgnoreCase(targetMap))
			quest.onFinished();
	}
	
	@Override
	public void setProgress(int progress)
	{
		
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
