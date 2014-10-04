package com.vhelium.lotig.scene.gamescene.spells.deathknight;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellIntimidation extends Spell
{
	public static String name = "Intimidation";
	public static String effectAsset = "Intimidation";
	
	public static float cooldownBase = 30000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 250;
	public static float manaCostPerLevel = 18;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 7;
	
	public static float targetCount = 3;
	public static float targetCountPerLevel = 0.5f;
	
	public static int durationBase = 5000;
	public static int durationPerLevel = 300;
	
	public static int slowBase = -400;
	public static int slowPerLevel = -20;
	
	public SpellIntimidation()
	{
		instantCast = true;
	}
	
	//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You intimidate nearby enemy targets reducing their movement speed.\n\nRadius: " + getAoeRadius(getLevel()) + "\nMaximal targets: " + getTargetCount(getLevel()) + "\nSpeed malus: " + getSlow(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
	}
	
	@Override
	public String getName()
	{
		return name;
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
		if(realm.getPlayer(shooterId) == null)
			return;
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		
		realm.playEffect(x, y, getAoeRadius(level) * 2, getAoeRadius(level) * 2, effectAsset, 0);
		realm.playSound(SoundFile.shout_damage, x, y);
		int slowedTargets = 0;
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() != realm.getEntity(shooterId).getTeamNr())
			{
				realm.requestCondition(entity.getKey(), name, "SPD", getSlow(spellLevel), getDuration(spellLevel), true, "", System.currentTimeMillis());
				slowedTargets++;
			}
			if(slowedTargets > getTargetCount(level))
				break;
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
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getSlow(int level)
	{
		return slowBase + slowPerLevel * (level - 1);
	}
}
