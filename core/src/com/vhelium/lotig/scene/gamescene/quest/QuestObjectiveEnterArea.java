package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveEnterArea extends QuestObjective
{
	String area;
	String text;
	Quest quest;
	
	public QuestObjectiveEnterArea(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		area = st.nextToken();
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
		return "enterarea;" + area + ";" + text;
	}
	
	@Override
	public void onMapLoaded(String name)
	{
		
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
		if(areaName.equalsIgnoreCase(area))
			quest.onFinished();
	}
}
