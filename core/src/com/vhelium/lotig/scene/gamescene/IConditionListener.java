package com.vhelium.lotig.scene.gamescene;

import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;

public interface IConditionListener
{
	public void onDied(EntityServerMixin target);
	
	public void onApplied(EntityServerMixin target);
}
