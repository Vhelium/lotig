package com.vhelium.lotig.scene.gamescene.client.items.armor.mod;

import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipArmor;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class ModBulwark extends ArmorModification
{
	@Override
	public void onPlayerHit(PlayerClient player, int attackerId)
	{
		
	}
	
	@Override
	public String getName()
	{
		return ArmorModification.BULWARK;
	}
	
	@Override
	public void apply(EquipArmor equip)
	{
		int armor = equip.getAttributes().containsKey("ARMOR") ? 200 + equip.getAttributes().get("ARMOR") * 2 : 666;
		equip.getAttributes().clear();
		equip.getAttributes().put("ARMOR", armor);
	}
}
