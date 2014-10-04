package com.vhelium.lotig.scene.gamescene.client.items.armor;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipHelmet extends EquipArmor
{
	protected EquipHelmet()
	{
		super();
		NAME = "Helmet";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.Armor;
		
		maxArmor = 250;
		possibleAttributesWithMax.put("VIT", 60);
		possibleAttributesWithMax.put("WIS", 60);
		
		possibleAttributesWithMax.put("MAXHP", 750);
		possibleAttributesWithMax.put("MAXMANA", 300);
		
		possibleAttributesWithMax.put("LPH", 30);
		possibleAttributesWithMax.put("MPH", 10);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipHelmet helmet = new EquipHelmet();
		helmet.randomStats(level, modifier);
		helmet.applyIcon();
		return helmet;
	}
	
	public static EquipHelmet createItemFromStringFormat(StringTokenizer st)
	{
		EquipHelmet res = new EquipHelmet();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
