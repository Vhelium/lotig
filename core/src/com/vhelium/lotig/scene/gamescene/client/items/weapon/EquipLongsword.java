package com.vhelium.lotig.scene.gamescene.client.items.weapon;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipLongsword extends EquipWeapon
{
	protected EquipLongsword()
	{
		super();
		NAME = "Longsword";
		featuredAttribute = "DMG";
		playerClass = "Death Knight";
		CATEGORY = ItemCategory.Weapon;
		
		maxDamagePossible = 150;
		possibleAttributesWithMax.put("STR", 100);
		possibleAttributesWithMax.put("DEX", 100);
		
		possibleAttributesWithMax.put("LPH", 60);
		possibleAttributesWithMax.put("MPH", 20);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipLongsword claymore = new EquipLongsword();
		claymore.randomStats(level, modifier);
		claymore.randomDamage(level);
		claymore.applyIcon();
		
		return claymore;
	}
	
	public static EquipLongsword createItemFromStringFormat(StringTokenizer st)
	{
		EquipLongsword res = new EquipLongsword();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
