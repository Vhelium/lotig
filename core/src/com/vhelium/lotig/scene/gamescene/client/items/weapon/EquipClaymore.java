package com.vhelium.lotig.scene.gamescene.client.items.weapon;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipClaymore extends EquipWeapon
{
	protected EquipClaymore()
	{
		super();
		NAME = "Claymore";
		featuredAttribute = "DMG";
		playerClass = "Barbarian";
		CATEGORY = ItemCategory.Weapon;
		
		maxDamagePossible = 150;
		possibleAttributesWithMax.put("STR", 100);
		possibleAttributesWithMax.put("DEX", 100);
		
		possibleAttributesWithMax.put("LPH", 60);
		possibleAttributesWithMax.put("MPH", 20);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipClaymore claymore = new EquipClaymore();
		claymore.randomStats(level, modifier);
		claymore.randomDamage(level);
		claymore.applyIcon();
		
		return claymore;
	}
	
	public static EquipClaymore createItemFromStringFormat(StringTokenizer st)
	{
		EquipClaymore res = new EquipClaymore();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
