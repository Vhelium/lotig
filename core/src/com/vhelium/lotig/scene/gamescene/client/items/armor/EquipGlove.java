package com.vhelium.lotig.scene.gamescene.client.items.armor;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipGlove extends EquipArmor
{
	protected EquipGlove()
	{
		super();
		NAME = "Glove";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.Armor;
		
		maxArmor = 250;
		possibleAttributesWithMax.put("STR", 50);
		possibleAttributesWithMax.put("DEX", 70);
		possibleAttributesWithMax.put("INT", 80);
		
		possibleAttributesWithMax.put("LPH", 30);
		possibleAttributesWithMax.put("MPH", 18);
		
		possibleAttributesWithMax.put("CDR", 12);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipGlove glove = new EquipGlove();
		glove.randomStats(level, modifier);
		glove.applyIcon();
		return glove;
	}
	
	public static EquipGlove createItemFromStringFormat(StringTokenizer st)
	{
		EquipGlove res = new EquipGlove();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
