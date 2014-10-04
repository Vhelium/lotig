package com.vhelium.lotig.scene.gamescene.client.items.weapon.mod;

import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.EquipModification;

public abstract class WeaponModification extends EquipModification
{
	public static final String MULTI_SHOT = "MULTI-SHOT!";//multiple bullets, less dmg each
	public static final String DOOM_SHOTS = "DOOM SHOT!";//extreme dmg, low shoot speed
	public static final String HAXXX0R = "HAXXX0R!";//extreme atk spd, low dmg
	public static final String PSYCHO = "PSYCHO!";//random directions, high dmg!
	public static final String SNIPER = "SNIPER!";//special auto-attack: FAST & PIERCING
	public static final String ORBS = "ORBS!";//special auto-attack: BIG bullets & piercing & slow
	
	public static WeaponModification getRandomMod()
	{
		int rnd = GameHelper.$.getRandom().nextInt(6);
		switch(rnd)
		{
			case 0:
				return new ModMultiShot();
			case 1:
				return new ModDoomShot();
			case 2:
				return new ModHaxxx0r();
			case 3:
				return new ModPsycho();
			case 4:
				return new ModSniper();
			case 5:
				return new ModOrbs();
			default:
				return new ModMultiShot();
		}
	}
	
	public abstract float getDmgFactor();
	
	public abstract float getAtkSpdFactor();
	
	public abstract float getRangeFactor();
	
	public abstract boolean isPiercing();
	
	public abstract String getSpecialBulletAsset();
}
