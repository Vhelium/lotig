package com.vhelium.lotig.scene.gamescene.client.items.armor;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipBracer extends EquipArmor
{
	protected EquipBracer()
	{
		super();
		NAME = "Bracer";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.Armor;
		
		maxArmor = 250;
		possibleAttributesWithMax.put("DEX", 40);
		possibleAttributesWithMax.put("WIS", 60);
		possibleAttributesWithMax.put("INT", 80);
		
		possibleAttributesWithMax.put("MAXMANA", 300);
		
		possibleAttributesWithMax.put("FRES", 100);
		possibleAttributesWithMax.put("CRES", 100);
		possibleAttributesWithMax.put("LRES", 100);
		possibleAttributesWithMax.put("PRES", 100);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipBracer bracer = new EquipBracer();
		bracer.randomStats(level, modifier);
		bracer.applyIcon();
		return bracer;
	}
	
	public static EquipBracer createItemFromStringFormat(StringTokenizer st)
	{
		EquipBracer res = new EquipBracer();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
