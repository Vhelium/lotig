package com.vhelium.lotig.scene.gamescene.client.items.weapon;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipBow extends EquipWeapon
{
	protected EquipBow()
	{
		super();
		NAME = "Bow";
		featuredAttribute = "DMG";
		playerClass = "Ranger";
		CATEGORY = ItemCategory.Weapon;
		
		maxDamagePossible = 150;
		possibleAttributesWithMax.put("STR", 100);
		possibleAttributesWithMax.put("DEX", 100);
		
		possibleAttributesWithMax.put("LPH", 60);
		possibleAttributesWithMax.put("MPH", 20);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipBow claymore = new EquipBow();
		claymore.randomStats(level, modifier);
		claymore.randomDamage(level);
		claymore.applyIcon();
		
		return claymore;
	}
	
	public static EquipBow createItemFromStringFormat(StringTokenizer st)
	{
		EquipBow res = new EquipBow();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
