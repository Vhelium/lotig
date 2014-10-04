package com.vhelium.lotig.scene.gamescene.client.items.weapon;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipScepter extends EquipWeapon
{
	protected EquipScepter()
	{
		super();
		NAME = "Scepter";
		featuredAttribute = "DMG";
		playerClass = "Dark Priest";
		CATEGORY = ItemCategory.Weapon;
		
		maxDamagePossible = 150;
		possibleAttributesWithMax.put("STR", 100);
		possibleAttributesWithMax.put("DEX", 100);
		
		possibleAttributesWithMax.put("LPH", 60);
		possibleAttributesWithMax.put("MPH", 20);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipScepter claymore = new EquipScepter();
		claymore.randomStats(level, modifier);
		claymore.randomDamage(level);
		claymore.applyIcon();
		
		return claymore;
	}
	
	public static EquipScepter createItemFromStringFormat(StringTokenizer st)
	{
		EquipScepter res = new EquipScepter();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
