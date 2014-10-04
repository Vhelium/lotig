package com.vhelium.lotig.scene.gamescene.client.items.armor.mod;

import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.EquipModification;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipArmor;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public abstract class ArmorModification extends EquipModification
{
	public static final String BLINK = "BLINK!";//chance to port when hit
	public static final String FIRE = "FIRE!";//chance to set attacker on fire
	public static final String RECOVER = "RECOVER!";//chance to restore hp
	public static final String BULWARK = "BULWARK!";//Armor attr only, but HIGH
	
	public static ArmorModification getRandomMod()
	{
		int rnd = GameHelper.$.getRandom().nextInt(4);
		switch(rnd)
		{
			case 0:
				return new ModBlink();
			case 1:
				return new ModFire();
			case 2:
				return new ModRecover();
			case 3:
				return new ModBulwark();
			default:
				return new ModRecover();
		}
	}
	
	public abstract void onPlayerHit(PlayerClient player, int attackerId);
	
	public void apply(EquipArmor equip)
	{
		
	}
}
