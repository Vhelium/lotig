package com.vhelium.lotig.scene.gamescene.client.items.armor.mod;

import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class ModBlink extends ArmorModification
{
	public static final int PORT_RANGE = 800;
	
	@Override
	public void onPlayerHit(PlayerClient player, int attackerId)
	{
		int rnd = GameHelper.$.getRandom().nextInt(100);
		if(rnd < Constants.modProccBlink)
		{
			player.getLevel().requestRandomBlink();
		}
	}
	
	@Override
	public String getName()
	{
		return ArmorModification.BLINK;
	}
	
}
