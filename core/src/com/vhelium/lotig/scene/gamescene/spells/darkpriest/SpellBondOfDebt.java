package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellBondOfDebt extends Spell
{
	public static String name = "Bond of Debt";
	public static String effectAsset = "Bond of Debt";
	public static String debuffAsset = "Bond of Debt Debuff";
	
	public static float cooldownBase = 50000;
	public static float cooldownPerLevel = 1200;
	
	public static float manaCostBase = 300;
	public static float manaCostPerLevel = 15;
	
	public static int absorbBase = 750;
	public static int absorbPerLevel = 150;
	
	public static int durationBase = 12000;
	public static int durationPerLevel = 2000;
	
	public static int armorBase = -75;
	public static int armorPerLevel = -45;
	
	public static int debuffDurationBase = 5000;
	public static int debuffDurationPerLevel = 250;
	
	public static float targetCount = 3;
	public static float targetCountPerLevel = 0.5f;
	
	public SpellBondOfDebt()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You make use of your dark powers and create an energy shield that absorbs incomming damage. When it breaks you suffer a malus on your armor for a short period.\n\nDamage absorption: " + getAbsorb(getLevel()) + "\nShield duration: " + (getDuration(getLevel()) / 1000) + " seconds\nArmor malus: " + getArmor(getLevel()) + "\nMalus duration: " + (getDebuffDuration(getLevel()) / 1000) + "\nAdditional targets: " + getTargetCount(getLevel());
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public DataPacket generateRequest(float x, float y, float rotation, float dirX, float dirY)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_SPELL_REQUEST);
		dp.setString(name);
		dp.setInt(getLevel());
		
		return dp;
	}
	
	@Override
	public float getManaCost()
	{
		return getManaCostStatic(getLevel());
	}
	
	@Override
	public float getCooldown()
	{
		return cooldownBase + cooldownPerLevel * (getLevel() - 1);
	}
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		EntityServerMixin shooter = realm.getEntity(shooterId);
		if(shooter == null)
			return;
		
		//self
		realm.playSound(SoundFile.spell_shieldup, shooter.getOriginX(), shooter.getOriginY());
		Condition aftereffect = new Condition(name + " Debuff", "ARMOR", getArmor(spellLevel), getDebuffDuration(spellLevel), true, debuffAsset, 0);
		realm.requestCondition(shooterId, name, "ABSORB", getAbsorb(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis(), aftereffect);
		//others:
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(shooter.getOriginX(), shooter.getOriginY(), 500);
		int buffed = 0;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(buffed >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == shooter.getTeamNr())
			{
				buffed++;
				Condition ae = new Condition(name + " Debuff", "ARMOR", getArmor(spellLevel), getDebuffDuration(spellLevel), true, debuffAsset, 0);
				realm.requestCondition(entity.getKey(), name, "ABSORB", getAbsorb(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis(), ae);
			}
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAbsorb(int level)
	{
		return absorbBase + absorbPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getArmor(int level)
	{
		return armorBase + armorPerLevel * (level - 1);
	}
	
	public static int getDebuffDuration(int level)
	{
		return debuffDurationBase + debuffDurationPerLevel * (level - 1);
	}
	
	public static int getTargetCount(int level)
	{
		return (int) (targetCount + targetCountPerLevel * (level - 1));
	}
}
