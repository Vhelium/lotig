package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMassHeal extends Spell
{
	public static String name = "Mass Heal";
	public static String effectAsset = "Mass Heal";
	
	public static float cooldownBase = 22000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 200;
	public static float manaCostPerLevel = 20;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 20;
	
	public static float healBase = 500;
	public static float healPerLevel = 100;
	
	public static float targetCount = 3;
	public static float targetCountPerLevel = 0.34f;
	
	public SpellMassHeal()
	{
		instantCast = true;
	}
	
	//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{
		return "With the use of dark energy you restore health to yourself and nearby allies and let them last longer.\n\nHealth restored: " + getHealCount(getLevel()) + "\nRadius: " + getAoeRadius(getLevel()) + "\nMaximum targets: " + getTargetCount(getLevel());
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
	
	@Override
	public DataPacket generateRequest(float x, float y, float rotation, float dirX, float dirY)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_SPELL_REQUEST);
		dp.setString(name);
		dp.setInt(getLevel());
		dp.setFloat(x);
		dp.setFloat(y);
		
		return dp;
	}
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		if(realm.getEntity(shooterId) == null)
			return;
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		
		realm.playEffect(x, y, getAoeRadius(level) * 2, getAoeRadius(level) * 2, effectAsset, 0);
		realm.playSound(SoundFile.spell_bling, x, y);
		//self:
		realm.getEntity(shooterId).doHeal(getHealCount(spellLevel));
		realm.sendDamageNumberToAllPlayers(shooterId, getHealCount(spellLevel), DamageType.Heal);
		//others:
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		int healed = 1;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(healed >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				healed++;
				entity.getValue().doHeal(getHealCount(spellLevel));
				realm.sendDamageNumberToAllPlayers(entity.getKey(), getHealCount(spellLevel), DamageType.Heal);
			}
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
	
	public static int getTargetCount(int level)
	{
		return (int) (targetCount + targetCountPerLevel * (level - 1));
	}
	
	public static float getHealCount(int level)
	{
		return healBase + healPerLevel * (level - 1);
	}
}
