package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellMonsterMassWeakening extends SpellEnemy
{
	public static String name = "Mass Weakening";
	public static String effectAsset = "Weakening Debuff";
	
	public static float cooldownBase = 20000;
	public static float cooldownPerLevel = 100;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int attrBase = -100;
	public static int attrPerLevel = -20;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 100;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 4;
	
	public SpellMonsterMassWeakening(int priority, HashMap<Integer, Float> conditions)
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
		int spellLevel = getLevel();
		realm.playSound(SoundFile.spell_dispel, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
		
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(entity.getValue().getTeamNr() != realm.getEntity(shooterId).getTeamNr())
			{
				ConcurrentHashMap<String, Integer> buffs = new ConcurrentHashMap<String, Integer>();
				buffs.put("STR", getAttr(spellLevel));
				buffs.put("DEX", getAttr(spellLevel));
				buffs.put("SPD", getAttr(spellLevel));
				buffs.put("VIT", getAttr(spellLevel));
				buffs.put("INT", getAttr(spellLevel));
				buffs.put("WIS", getAttr(spellLevel));
				realm.requestCondition(entity.getKey(), name, buffs, getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
			}
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAttr(int level)
	{
		return attrBase + attrPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
}
