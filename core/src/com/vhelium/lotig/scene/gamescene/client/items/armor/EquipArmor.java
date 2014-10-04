package com.vhelium.lotig.scene.gamescene.client.items.armor;

import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ArmorModification;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public abstract class EquipArmor extends Equip
{
	protected int maxArmor;
	
	@Override
	protected void randomStats(int level, int modifier)
	{
		bonusAttributes.put("ARMOR", getRandomAttributeValue(maxArmor, level));
		int additionalPrice = bonusAttributes.get("ARMOR") * GameHelper.getInstance().getPricePerAttributePoint("ARMOR");
		price += additionalPrice;
		score += additionalPrice / Constants.ScoreToPriceFactor;
		super.randomStats(level, modifier);
		randomMod(level);
	}
	
	protected void randomMod(int level)
	{
		if(GameHelper.getInstance().getRandom().nextInt(100) < Constants.modChanceArmor)
		{
			modification = ArmorModification.getRandomMod();
			((ArmorModification) modification).apply(this);
			Equip.recalculateScoreAndPrice(this);
		}
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return true;
	}
}

//possibleAttributesWithMax.put("STR", 100);
//possibleAttributesWithMax.put("DEX", 100);
//possibleAttributesWithMax.put("SPD", 100);
//possibleAttributesWithMax.put("VIT", 100);
//possibleAttributesWithMax.put("WIS", 100);
//possibleAttributesWithMax.put("INT", 100);
//
//possibleAttributesWithMax.put("MAXHP", 1000);
//possibleAttributesWithMax.put("MAXMANA", 750);
//
//possibleAttributesWithMax.put("FRES", 100);
//possibleAttributesWithMax.put("CRES", 100);
//possibleAttributesWithMax.put("LRES", 100);
//possibleAttributesWithMax.put("PRES", 100);
//
//possibleAttributesWithMax.put("LPH", 60);
//possibleAttributesWithMax.put("MPH", 50);
//
//possibleAttributesWithMax.put("CDR", 5);
//
//possibleAttributesWithMax.put("BONUSDMG", 25);
