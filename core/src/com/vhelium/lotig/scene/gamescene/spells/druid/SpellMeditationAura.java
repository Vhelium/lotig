package com.vhelium.lotig.scene.gamescene.spells.druid;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMeditationAura extends Spell
{
	public static String name = "Meditation Aura";
	public static String effectAsset = "Meditation";
	
	public static float cooldownBase = 60000f;
	public static float cooldownPerLevel = 1000f;
	
	public static float manaCostBase = 250;
	public static float manaCostPerLevel = 15;
	
	public static int aoeRadiusBase = 170;
	public static int aoeRadiusPerLevel = 7;
	
	public static float wisdomBase = 150;
	public static float wisdomPerLevel = 15;
	
	public static float targetCount = 3;
	public static float targetCountPerLevel = 0.34f;
	
	public static float durationBase = 6000;
	public static float durationPerLevel = 200;
	
	public SpellMeditationAura()
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
		return "An aura of meditation surrounds you which lets you and nearby allies gain a bonus on wisdom and therefore increases the mana regeneration.\n\nBonus wisdom: " + getWisdom(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds\nRadius: " + getAoeRadius(getLevel()) + "\nMaximum targets: " + getTargetCount(getLevel());
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
		realm.playSound(SoundFile.spell_bling, dp.getFloat(), dp.getFloat());
		realm.requestCondition(shooterId, name, "WIS", (int) getWisdom(spellLevel), (int) getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		int buffed = 1;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(buffed >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				buffed++;
				realm.requestCondition(entity.getKey(), name, "WIS", (int) getWisdom(spellLevel), (int) getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
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
	
	public static float getWisdom(int level)
	{
		return wisdomBase + wisdomPerLevel * (level - 1);
	}
	
	public static float getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
