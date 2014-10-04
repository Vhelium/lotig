package com.vhelium.lotig.scene.gamescene.client.items.jewelry;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class EquipAmulet extends Equip
{
	protected EquipAmulet()
	{
		super();
		NAME = "Amulet";
		CATEGORY = ItemCategory.Jewelry;
		possibleAttributesWithMax.put("SPD", 80);
		possibleAttributesWithMax.put("VIT", 100);
		possibleAttributesWithMax.put("WIS", 100);
		possibleAttributesWithMax.put("INT", 100);
		
		possibleAttributesWithMax.put("MAXHP", 1000);
		possibleAttributesWithMax.put("MAXMANA", 750);
		
		possibleAttributesWithMax.put("FRES", 100);
		possibleAttributesWithMax.put("CRES", 100);
		possibleAttributesWithMax.put("LRES", 100);
		possibleAttributesWithMax.put("PRES", 100);
		
		possibleAttributesWithMax.put("CDR", 5);
	}
	
	public static Equip randomEquip(int level, int modifier)//level: 0 - 10
	{
		EquipAmulet amulet = new EquipAmulet();
		amulet.randomStats(level, modifier);
		amulet.applyIcon();
		return amulet;
	}
	
	public static EquipAmulet createItemFromStringFormat(StringTokenizer st)
	{
		EquipAmulet res = new EquipAmulet();
		Equip.applyStatsFromStringFormat(st, res);
		res.applyIcon();
		return res;
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return true;
	}
}
