package com.vhelium.lotig.scene.gamescene.quest;

import java.util.StringTokenizer;

public class QuestObjectiveArenaRound extends QuestObjective
{
	int count;
	int done;
	Quest quest;
	
	public QuestObjectiveArenaRound(StringTokenizer st, Quest quest)
	{
		this.quest = quest;
		count = Integer.parseInt(st.nextToken());
		if(st.hasMoreTokens())
			done = Integer.parseInt(st.nextToken());
	}
	
	@Override
	public String getDescription()
	{
		return "Survive " + count + " rounds in the monster arena. " + done + " rounds already survived in total.";
	}
	
	@Override
	public String getStringFormat()
	{
		return "arenarounds;" + count + ";" + done;
	}
	
	@Override
	public void onArenaRoundFinished()
	{
		done++;
		if(done >= count)
			quest.onFinished();
	}
	
	@Override
	public void setProgress(int progress)
	{
		this.done = progress;
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
