package com.vhelium.lotig.scene.gamescene.client.items.armor;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipPants extends EquipArmor
{
	protected EquipPants()
	{
		super();
		NAME = "Pants";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.Armor;
		
		maxArmor = 400;
		possibleAttributesWithMax.put("STR", 80);
		possibleAttributesWithMax.put("SPD", 100);
		possibleAttributesWithMax.put("WIS", 80);
		possibleAttributesWithMax.put("INT", 80);
		possibleAttributesWithMax.put("VIT", 50);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipPants pants = new EquipPants();
		pants.randomStats(level, modifier);
		pants.applyIcon();
		
		return pants;
	}
	
	public static EquipPants createItemFromStringFormat(StringTokenizer st)
	{
		EquipPants res = new EquipPants();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
