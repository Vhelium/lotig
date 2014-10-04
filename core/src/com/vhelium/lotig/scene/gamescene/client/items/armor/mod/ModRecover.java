package com.vhelium.lotig.scene.gamescene.client.items.armor.mod;

import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class ModRecover extends ArmorModification
{
	@Override
	public void onPlayerHit(PlayerClient player, int attackerId)
	{
		int rnd = GameHelper.$.getRandom().nextInt(100);
		if(rnd < Constants.modProccRecover)
		{
			player.getLevel().requestPlayerRecoverHeal(player.getMaxHp() / 10);
		}
	}
	
	@Override
	public String getName()
	{
		return ArmorModification.RECOVER;
	}
	
}
