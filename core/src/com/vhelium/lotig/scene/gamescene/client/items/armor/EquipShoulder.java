package com.vhelium.lotig.scene.gamescene.client.items.armor;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipShoulder extends EquipArmor
{
	protected EquipShoulder()
	{
		super();
		NAME = "Shoulder";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.Armor;
		
		maxArmor = 350;
		possibleAttributesWithMax.put("STR", 80);
		possibleAttributesWithMax.put("VIT", 50);
		possibleAttributesWithMax.put("WIS", 50);
		
		possibleAttributesWithMax.put("MAXHP", 1000);
		possibleAttributesWithMax.put("MAXMANA", 300);
		
		possibleAttributesWithMax.put("FRES", 50);
		possibleAttributesWithMax.put("CRES", 50);
		possibleAttributesWithMax.put("LRES", 50);
		possibleAttributesWithMax.put("PRES", 50);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipShoulder shoulder = new EquipShoulder();
		shoulder.randomStats(level, modifier);
		shoulder.applyIcon();
		
		return shoulder;
	}
	
	public static EquipShoulder createItemFromStringFormat(StringTokenizer st)
	{
		EquipShoulder res = new EquipShoulder();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
