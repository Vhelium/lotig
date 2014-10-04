package com.vhelium.lotig.scene.gamescene.client.items;

import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ArmorModification;
import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ModBlink;
import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ModBulwark;
import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ModFire;
import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ModRecover;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.ModDoomShot;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.ModHaxxx0r;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.ModMultiShot;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.ModOrbs;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.ModPsycho;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.ModSniper;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.WeaponModification;

public abstract class EquipModification
{
	public abstract String getName();
	
	public static EquipModification getModFromName(String name)
	{
		if(name.equals(WeaponModification.MULTI_SHOT))
			return new ModMultiShot();
		else if(name.equals(WeaponModification.DOOM_SHOTS))
			return new ModDoomShot();
		else if(name.equals(WeaponModification.HAXXX0R))
			return new ModHaxxx0r();
		else if(name.equals(WeaponModification.PSYCHO))
			return new ModPsycho();
		else if(name.equals(WeaponModification.SNIPER))
			return new ModSniper();
		else if(name.equals(WeaponModification.ORBS))
			return new ModOrbs();
		else if(name.equals(ArmorModification.BLINK))
			return new ModBlink();
		else if(name.equals(ArmorModification.BULWARK))
			return new ModBulwark();
		else if(name.equals(ArmorModification.FIRE))
			return new ModFire();
		else if(name.equals(ArmorModification.RECOVER))
			return new ModRecover();
		else
			return null;
	}
}
