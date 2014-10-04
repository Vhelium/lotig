package com.vhelium.lotig.scene.gamescene.spells.ranger;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellPathfinder extends Spell
{
	public static String name = "Pathfinder";
	public static String effectAsset = "Pathfinder";
	
	public static float cooldownBase = 90000;
	public static float cooldownPerLevel = -1000;
	
	public static float manaCostBase = 300;
	public static float manaCostPerLevel = 20;
	
	public static int spdBase = 350;
	public static int spdPerLevel = 20;
	
	public static int armorBase = 400;
	public static int armorPerLevel = 30;
	
	public static int durationBase = 10000;
	public static int durationPerLevel = 500;
	
	public static int aoeRadiusBase = 250;
	public static int aoeRadiusPerLevel = 10;
	
	public static float targetCount = 2;
	public static float targetCountPerLevel = 0.5f;
	
	public SpellPathfinder()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You grant allies speed and armor bonus.\n\nBonus speed: " + getSPD(getLevel()) + "\nBonus armor: " + getArmor(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds\nRadius: " + getAoeRadius(getLevel()) + "\nMaximum targets: " + getTargetCount(getLevel());
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
		dp.setFloat(x);
		dp.setFloat(y);
		
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
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		int buffed = 0;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(buffed >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				buffed++;
				ConcurrentHashMap<String, Integer> buffs = new ConcurrentHashMap<String, Integer>();
				buffs.put("SPD", getSPD(spellLevel));
				buffs.put("ARMOR", getArmor(spellLevel));
				realm.requestCondition(entity.getKey(), name, buffs, getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
			}
		}
		if(buffed > 0)
			realm.playSound(SoundFile.spell_bling, dp.getFloat(), dp.getFloat());
		else
			realm.playSound(shooterId, SoundFile.spell_failed);
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getSPD(int level)
	{
		return spdBase + spdPerLevel * (level - 1);
	}
	
	public static int getArmor(int level)
	{
		return armorBase + armorPerLevel * (level - 1);
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
