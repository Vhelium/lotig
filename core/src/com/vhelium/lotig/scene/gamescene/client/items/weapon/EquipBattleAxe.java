package com.vhelium.lotig.scene.gamescene.client.items.weapon;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public class EquipBattleAxe extends EquipWeapon
{
	protected EquipBattleAxe()
	{
		super();
		NAME = "Battle Axe";
		featuredAttribute = "DMG";
		playerClass = "Barbarian";
		CATEGORY = ItemCategory.Weapon;
		
		maxDamagePossible = 150;
		possibleAttributesWithMax.put("STR", 100);
		possibleAttributesWithMax.put("DEX", 100);
		
		possibleAttributesWithMax.put("LPH", 60);
		possibleAttributesWithMax.put("MPH", 20);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipBattleAxe battleAxe = new EquipBattleAxe();
		battleAxe.randomStats(level, modifier);
		battleAxe.randomDamage(level);
		battleAxe.applyIcon();
		
		return battleAxe;
	}
	
	public static EquipBattleAxe createItemFromStringFormat(StringTokenizer st)
	{
		EquipBattleAxe res = new EquipBattleAxe();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
}
