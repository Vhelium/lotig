package com.vhelium.lotig.scene.gamescene.spells.barbarian;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellExtendedBloodthirst extends Spell
{
	public static String name = "Extended Bloodthirst";
	public static String effectAsset = "Bloodthirst";
	
	public static float cooldownBase = 90000;
	public static float cooldownPerLevel = 1500;
	
	public static float manaCostBase = 280;
	public static float manaCostPerLevel = 20;
	
	public static int lphBase = 150;
	public static int lphPerLevel = 12;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 200;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 5;
	
	public static float targetCount = 2;
	public static float targetCountPerLevel = 0.34f;
	
	public SpellExtendedBloodthirst()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You and nearby allies fall into the blood thirst state lusting after fresh blood.\n\nLife stolen per hit: " + getLPH(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds\nRadius: " + getAoeRadius(getLevel()) + "\nMaximum targets: " + getTargetCount(getLevel());
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
		realm.requestCondition(shooterId, "Bloodthirst", "LPH", getLPH(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		realm.playSound(SoundFile.shout_bloodthirst, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
		
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		int buffed = 1;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(buffed >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				buffed++;
				realm.requestCondition(entity.getKey(), "Bloodthirst", "LPH", getLPH(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
			}
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getLPH(int level)
	{
		return lphBase + lphPerLevel * (level - 1);
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
