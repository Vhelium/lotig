package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellMonsterMassAttackUp extends SpellEnemy
{
	public static String name = "Mass Attack Up";
	public static String effectAsset = "Absorb Buff";
	
	public static float cooldownBase = 30000;
	public static float cooldownPerLevel = 0;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int dmgBase = 20;
	public static int dmgPerLevel = 3;
	
	public static int durationBase = 6000;
	public static int durationPerLevel = 200;
	
	public static int aoeRadiusBase = 240;
	public static int aoeRadiusPerLevel = 4;
	
	public SpellMonsterMassAttackUp(int priority, HashMap<Integer, Float> conditions)
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
		realm.requestCondition(shooterId, name, "DMG", getDMG(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		realm.playSound(SoundFile.spell_attack_up, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
		
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				realm.requestCondition(entity.getKey(), name, "DMG", getDMG(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
			}
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getDMG(int level)
	{
		return dmgBase + dmgPerLevel * (level - 1);
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
