package com.vhelium.lotig.scene.gamescene.client.items.offhand;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipInsignia extends EquipOffHand
{
	protected EquipInsignia()
	{
		super();
		NAME = "Insignia";
		playerClass = "Barbarian";
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
		EquipInsignia shield = new EquipInsignia();
		shield.randomStats(level, modifier);
		shield.applyIcon();
		
		return shield;
	}
	
	public static EquipInsignia createItemFromStringFormat(StringTokenizer st)
	{
		EquipInsignia res = new EquipInsignia();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
