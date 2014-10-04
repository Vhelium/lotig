package com.vhelium.lotig.scene.gamescene.client.items.offhand;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipFocus extends EquipOffHand
{
	protected EquipFocus()
	{
		super();
		NAME = "Focus";
		playerClass = "Sorcerer";
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
		EquipFocus shield = new EquipFocus();
		shield.randomStats(level, modifier);
		shield.applyIcon();
		
		return shield;
	}
	
	public static EquipFocus createItemFromStringFormat(StringTokenizer st)
	{
		EquipFocus res = new EquipFocus();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
