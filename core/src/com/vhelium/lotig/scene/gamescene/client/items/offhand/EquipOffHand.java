package com.vhelium.lotig.scene.gamescene.client.items.offhand;

import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public abstract class EquipOffHand extends Equip
{
	public String playerClass;
	
	@Override
	public String getDescription()
	{
		return NAME + " (" + score + "):\nClass: " + playerClass + "\n\n" + statsToString();
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return player.getPlayerClass().equalsIgnoreCase(playerClass);
	}
}
