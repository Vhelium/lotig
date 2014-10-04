package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveKill extends QuestObjective
{
	String target;
	int count;
	int killed;
	Quest quest;
	
	public QuestObjectiveKill(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		target = st.nextToken();
		count = Integer.parseInt(st.nextToken());
		if(st.hasMoreTokens())
			killed = Integer.parseInt(st.nextToken());
	}
	
	@Override
	public String getDescription()
	{
		return "Kill " + count + " " + target + ". " + target + " slain: " + killed + ".";
	}
	
	@Override
	public String getStringFormat()
	{
		return "kill;" + target + ";" + count + ";" + killed;
	}
	
	@Override
	public void onEntityDied(String name)
	{
		if(name.equals(target))
		{
			killed++;
			if(killed >= count)
				quest.onFinished();
		}
	}
	
	public String getTargetName()
	{
		return target;
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
