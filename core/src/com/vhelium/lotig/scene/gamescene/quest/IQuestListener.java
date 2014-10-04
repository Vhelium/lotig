package com.vhelium.lotig.scene.gamescene.quest;

public interface IQuestListener
{
	public void onEntityDied(String name);
	
	public void onLevelObjectActivated(String name);
	
	public void onMapLoaded(String name);
	
	public void onBossDied();
	
	public void onGoldChanged(int amount);
	
	public void onFameChanged(int amount);
	
	public void onArenaRoundFinished();
	
	public void onDialogueFinished(int uniqueId);
	
	public void onAreaEntered(String areaName);
}
