package com.vhelium.lotig.scene.gamescene.client.items.weapon;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipWand extends EquipWeapon
{
	protected EquipWand()
	{
		super();
		NAME = "Wand";
		featuredAttribute = "DMG";
		playerClass = "Sorcerer";
		CATEGORY = ItemCategory.Weapon;
		
		minDamage = 80;
		maxDamagePossible = 140;
		possibleAttributesWithMax.put("STR", 100);
		possibleAttributesWithMax.put("DEX", 100);
		
		possibleAttributesWithMax.put("LPH", 60);
		possibleAttributesWithMax.put("MPH", 20);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipWand claymore = new EquipWand();
		claymore.randomStats(level, modifier);
		claymore.randomDamage(level);
		claymore.applyIcon();
		
		return claymore;
	}
	
	public static EquipWand createItemFromStringFormat(StringTokenizer st)
	{
		EquipWand res = new EquipWand();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
