package com.vhelium.lotig.scene.gamescene.client.items.weapon;

import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.WeaponModification;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public abstract class EquipWeapon extends Equip
{
	public int minDamage = 100;
	protected int maxDamagePossible;
	public String playerClass;
	
	protected void randomDamage(int level)
	{
		bonusAttributes.put("DMG", minDamage + getRandomAttributeValue(maxDamagePossible, level));
		int additionalPrice = (bonusAttributes.get("DMG") - minDamage) * GameHelper.getInstance().getPricePerAttributePoint("DMG");
		price += additionalPrice;
		score += additionalPrice / Constants.ScoreToPriceFactor;
		randomMod(level);
	}
	
	protected void randomMod(int level)
	{
		if(GameHelper.getInstance().getRandom().nextInt(100) < Constants.modChanceWeapon)
		{
			modification = WeaponModification.getRandomMod();
			
			if(bonusAttributes.containsKey("DMG"))
				bonusAttributes.put("DMG", (int) (bonusAttributes.get("DMG") * ((WeaponModification) modification).getDmgFactor()));
			
			Equip.recalculateScoreAndPrice(this);
		}
	}
	
	@Override
	public String getDescription()
	{
		return NAME + " (" + score + "):\nClass: " + playerClass + "\n\n" + statsToString();
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return player.getPlayerClass().equalsIgnoreCase(playerClass);
	}
}