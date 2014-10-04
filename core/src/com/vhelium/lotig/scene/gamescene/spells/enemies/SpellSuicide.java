package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.IConditionListener;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellSuicide extends SpellEnemy
{
	public static String name = "Suicide";
	public static String effectAsset = "Fireball";
	
	public static float cooldownBase = 6000;
	public static float cooldownPerLevel = 0;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int durationBase = 30000;
	public static int durationPerLevel = 0;
	
	public static float damageBase = 300;
	public static float damagePerLevel = 30;
	
	public static float damageBonusBase = 100;
	public static float damageBonusPerLevel = 20;
	
	public static int aoeRadiusBase = 60;
	public static int aoeRadiusPerLevel = 3;
	
	public SpellSuicide(int priority, HashMap<Integer, Float> conditions)
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
	public void activate(final Realm realm, final int shooterId, float x, float y, float rotation, float dirX, float dirY)
	{
		realm.requestCondition(shooterId, name, "", 0, getDuration(getLevel()), false, "", System.currentTimeMillis(), null, new IConditionListener()
		{
			@Override
			public void onDied(EntityServerMixin target)
			{
				BulletOnHitEffect ohe = new BulletOnHitEffect(getDamage(getLevel()) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(getLevel()) + 1), getAoeRadius(getLevel()), effectAsset, 0);
				ohe.setSoundEffect(SoundFile.fire_burst);
				realm.doAoeDamage(realm.getEntity(shooterId), target.getOriginX(), target.getOriginY(), ohe, DamageType.Fire);
			}
			
			@Override
			public void onApplied(EntityServerMixin target)
			{
				
			}
		});
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static float getDamage(int level)
	{
		return damageBase + damagePerLevel * (level - 1);
	}
	
	public static float getBonusDamage(int level)
	{
		return damageBonusBase + damageBonusPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
}
