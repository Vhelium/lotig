package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellMonsterMassHeal extends SpellEnemy
{
	public static String name = "Mass Heal";
	public static String effectAsset = "Mass Heal";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 0f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int aoeRadiusBase = 220;
	public static int aoeRadiusPerLevel = 10;
	
	public static float healBase = 300;
	public static float healPerLevel = 90;
	
	public SpellMonsterMassHeal(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = true;
	}
	
	//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
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
		if(realm.getEntity(shooterId) == null)
			return;
		
		realm.playEffect(x, y, getAoeRadius(getLevel()) * 2, getAoeRadius(getLevel()) * 2, effectAsset, 0);
		realm.playSound(SoundFile.spell_bling, x, y);
		//self:
		realm.getEntity(shooterId).doHeal(getHealCount(getLevel()));
		realm.sendDamageNumberToAllPlayers(shooterId, getHealCount(getLevel()), DamageType.Heal);
		//others:
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(getLevel()));
		
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				entity.getValue().doHeal(getHealCount(getLevel()));
				realm.sendDamageNumberToAllPlayers(entity.getKey(), getHealCount(getLevel()), DamageType.Heal);
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
	
	public static float getHealCount(int level)
	{
		return healBase + healPerLevel * (level - 1);
	}
}
