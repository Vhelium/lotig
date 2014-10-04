package com.vhelium.lotig.scene.gamescene.client.items.consumable;

import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public abstract class ConsumableItem extends StackableItem
{
	public abstract boolean use(PlayerClient player);
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return true;
	}
}
