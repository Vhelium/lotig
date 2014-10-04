package com.vhelium.lotig.scene.gamescene.client.items.armor;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipChest extends EquipArmor
{
	protected EquipChest()
	{
		super();
		NAME = "Chest";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.Armor;
		
		maxArmor = 600;
		possibleAttributesWithMax.put("STR", 50);
		possibleAttributesWithMax.put("VIT", 180);
		
		possibleAttributesWithMax.put("MAXHP", 3500);
		possibleAttributesWithMax.put("MAXMANA", 300);
		
		possibleAttributesWithMax.put("FRES", 150);
		possibleAttributesWithMax.put("CRES", 150);
		possibleAttributesWithMax.put("LRES", 150);
		possibleAttributesWithMax.put("PRES", 150);
		
		possibleAttributesWithMax.put("THORNS", 130);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipChest chest = new EquipChest();
		chest.randomStats(level, modifier);
		chest.applyIcon();
		return chest;
	}
	
	public static EquipChest createItemFromStringFormat(StringTokenizer st)
	{
		EquipChest res = new EquipChest();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
