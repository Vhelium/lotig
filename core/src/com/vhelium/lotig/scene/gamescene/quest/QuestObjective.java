package com.vhelium.lotig.scene.gamescene.quest;

public abstract class QuestObjective implements IQuestListener
{
	public String getDescription()
	{
		return null;
	}
	
	public String getStringFormat()
	{
		return null;
	}
	
	@Override
	public void onLevelObjectActivated(String name)
	{
		
	}
	
	@Override
	public void onEntityDied(String name)
	{
		
	}
	
	@Override
	public void onMapLoaded(String name)
	{
		
	}
	
	@Override
	public void onBossDied()
	{
		
	}
	
	@Override
	public void onGoldChanged(int amount)
	{
		
	}
	
	@Override
	public void onFameChanged(int amount)
	{
		
	}
	
	@Override
	public void onArenaRoundFinished()
	{
		
	}
	
	public abstract void setProgress(int progress);
}
