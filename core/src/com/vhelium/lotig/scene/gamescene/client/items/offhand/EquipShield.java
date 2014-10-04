package com.vhelium.lotig.scene.gamescene.client.items.offhand;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipShield extends EquipOffHand
{
	protected EquipShield()
	{
		super();
		NAME = "Shield";
		playerClass = "Death Knight";
		featuredAttribute = "ARMOR";
		CATEGORY = ItemCategory.OffHand;
		
		possibleAttributesWithMax.put("STR", 60);
		possibleAttributesWithMax.put("DEX", 60);
		possibleAttributesWithMax.put("VIT", 60);
		possibleAttributesWithMax.put("WIS", 60);
		possibleAttributesWithMax.put("INT", 60);
		
		possibleAttributesWithMax.put("MAXMANA", 1000);
		possibleAttributesWithMax.put("MAXHP", 1000);
		
		possibleAttributesWithMax.put("CDR", 10);
		
		possibleAttributesWithMax.put("BONUSDMG", 25);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipShield shield = new EquipShield();
		shield.randomStats(level, modifier);
		shield.applyIcon();
		
		return shield;
	}
	
	public static EquipShield createItemFromStringFormat(StringTokenizer st)
	{
		EquipShield res = new EquipShield();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
