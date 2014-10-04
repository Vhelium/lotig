package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellSlowAoE extends SpellEnemy
{
	public static String name = "Slow AoE";
	public static String effectAsset = "Slow AoE";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = -120f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 2;
	
	public static int durationBase = 4000;
	public static int durationPerLevel = 125;
	
	public static int slowBase = -300;
	public static int slowPerLevel = -20;
	
	public SpellSlowAoE(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = true;
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
	public void activate(Realm realm, int shooterId, float x, float y, float rotation, float dirX, float dirY)
	{
		if(realm.getPlayer(shooterId) == null)
			return;
		int level = getLevel();
		
		realm.playEffect(x, y, getAoeRadius(level) * 2, getAoeRadius(level) * 2, effectAsset, 0);
		realm.playSound(SoundFile.spell_slow, x, y);
		
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(level));
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(entity.getValue().getTeamNr() != realm.getEntity(shooterId).getTeamNr())
			{
				realm.requestCondition(entity.getKey(), name, "SPD", getSlow(level), getDuration(level), true, "", System.currentTimeMillis());
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
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getSlow(int level)
	{
		return slowBase + slowPerLevel * (level - 1);
	}
}
