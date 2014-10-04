package com.vhelium.lotig.scene.gamescene.client.items.jewelry;

import java.util.StringTokenizer;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class EquipRing extends Equip
{
	protected EquipRing()
	{
		super();
		NAME = "Ring";
		CATEGORY = ItemCategory.Jewelry;
		possibleAttributesWithMax.put("DEX", 50);
		possibleAttributesWithMax.put("SPD", 50);
		possibleAttributesWithMax.put("VIT", 40);
		possibleAttributesWithMax.put("WIS", 40);
		possibleAttributesWithMax.put("INT", 40);
		
		possibleAttributesWithMax.put("MAXHP", 1000);
		possibleAttributesWithMax.put("MAXMANA", 200);
		
		possibleAttributesWithMax.put("FRES", 100);
		possibleAttributesWithMax.put("CRES", 100);
		possibleAttributesWithMax.put("LRES", 100);
		possibleAttributesWithMax.put("PRES", 100);
		
		possibleAttributesWithMax.put("CDR", 5);
	}
	
	public static Equip randomEquip(int level, int modifier)
	{
		EquipRing ring = new EquipRing();
		ring.randomStats(level, modifier);
		ring.applyIcon();
		
		return ring;
	}
	
	public static EquipRing createItemFromStringFormat(StringTokenizer st)
	{
		EquipRing res = new EquipRing();
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
