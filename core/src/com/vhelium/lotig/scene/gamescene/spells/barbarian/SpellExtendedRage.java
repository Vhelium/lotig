package com.vhelium.lotig.scene.gamescene.spells.barbarian;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellExtendedRage extends Spell
{
	public static String name = "Extended Rage";
	public static String effectAsset = "Rage";
	
	public static float cooldownBase = 90000;
	public static float cooldownPerLevel = 1500;
	
	public static float manaCostBase = 280;
	public static float manaCostPerLevel = 20;
	
	public static int asBase = 240;
	public static int asPerLevel = 2;
	
	public static int durationBase = 10000;
	public static int durationPerLevel = 500;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 5;
	
	public static float targetCount = 2;
	public static float targetCountPerLevel = 0.34f;
	
	public SpellExtendedRage()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You enrage yourself and nearby allies to swing their weapons like the wind.\n\nBonus attack speed: " + getAS(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds\nRadius: " + getAoeRadius(getLevel()) + "\nMaximum targets: " + getTargetCount(getLevel());
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
		realm.requestCondition(shooterId, "Rage", "AS", getAS(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		realm.playSound(SoundFile.shout_rage, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
		
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		int buffed = 1;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(buffed >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				buffed++;
				realm.requestCondition(entity.getKey(), "Rage", "AS", getAS(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
			}
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAS(int level)
	{
		return asBase + asPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
	
	public static int getTargetCount(int level)
	{
		return (int) (targetCount + targetCountPerLevel * (level - 1));
	}
}
