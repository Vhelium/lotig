package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveSpeakTo extends QuestObjective
{
	int speakerUniqueId;
	String text;
	Quest quest;
	
	public QuestObjectiveSpeakTo(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		speakerUniqueId = Integer.parseInt(st.nextToken());
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
		return "speakto;" + speakerUniqueId + ";" + text;
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
		if(uniqueId == speakerUniqueId)
			quest.onFinished();
	}
	
	@Override
	public void onAreaEntered(String areaName)
	{
		
	}
}
