package com.vhelium.lotig.scene.gamescene.client.items.armor;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipBoots extends EquipArmor
{
	protected EquipBoots()
	{
		super();
		NAME = "Boots";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.Armor;
		
		maxArmor = 250;
		possibleAttributesWithMax.put("STR", 50);
		possibleAttributesWithMax.put("DEX", 100);
		possibleAttributesWithMax.put("SPD", 200);
		possibleAttributesWithMax.put("BONUSDMG", 20);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipBoots boots = new EquipBoots();
		boots.randomStats(level, modifier);
		boots.applyIcon();
		return boots;
	}
	
	public static EquipBoots createItemFromStringFormat(StringTokenizer st)
	{
		EquipBoots res = new EquipBoots();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
