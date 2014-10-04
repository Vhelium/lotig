package com.vhelium.lotig.scene.gamescene.server;

import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.client.player.MovementModifier;

public class EntityServerFake extends EntityServerMixin
{
	int teamNr;
	
	public EntityServerFake(float x, float y, int teamNr)
	{
		rectangle = new Rectangle(x, y, 0, 0);
		this.teamNr = teamNr;
	}
	
	@Override
	public void addCondition(Condition condition)
	{
		
	}
	
	@Override
	public void removeCondition(String condition)
	{
		
	}
	
	@Override
	public int getTeamNr()
	{
		return teamNr;
	}
	
	@Override
	public void addMovementModifier(String name, MovementModifier modifier)
	{
		
	}
	
	@Override
	public void addMovementModifier(String name, float dirX, float dirY, float velocity, float decay)
	{
		
	}
}
