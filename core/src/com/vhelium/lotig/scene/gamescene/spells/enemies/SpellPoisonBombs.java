package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellPoisonBombs extends SpellEnemy
{
	public static String name = "Poison Bombs";
	public static String effectAsset = "Poison Bombs";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = -200f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static float damageBase = 60;
	public static float damagePerLevel = 20;
	
	public static float damageBonusBase = 30;
	public static float damageBonusPerLevel = 5;
	
	public static float bombCountBase = 5;
	public static float bombCountPerLevel = 0.34f;
	
	public static int aoeRadiusBase = 35;
	public static int aoeRadiusPerLevel = 1;
	
	public static float aoeSpreadRangeBase = 180;
	public static float aoeSpreadRangePerLevel = 4;
	
	public static float burningBase = 100;
	public static float burningPerLevel = 25;
	
	public static float durationBase = 4000;
	public static float durationPerLevel = 50;
	
	public SpellPoisonBombs(int priority, HashMap<Integer, Float> conditions)
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
		if(realm.getEntity(shooterId) == null)
			return;
		int level = getLevel();
		
		for(int i = 0; i < getFireCount(level); i++)
		{
			BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getAoeRadius(level), new Condition(UniqueCondition.Poisoned, UniqueCondition.Poisoned, (int) getBurning(level), (int) getDuration(level), false, UniqueCondition.Poisoned, 0), effectAsset, 0);
			effect.setSoundEffect(SoundFile.spell_poison_bomb);
			effect.delay = 290 * i;
			effect.dontProccFirst = true;
			effect.centered = false;
			realm.doAoeDamage(realm.getEntity(shooterId), x - getAoeSpreadRange(level) / 2 + GameHelper.getInstance().getRandom().nextInt(getAoeSpreadRange(level)), y - getAoeSpreadRange(level) / 2 + GameHelper.getInstance().getRandom().nextInt(getAoeSpreadRange(level)), effect, DamageType.Poison);
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
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
	
	public static int getAoeSpreadRange(int level)
	{
		return (int) (aoeSpreadRangeBase + aoeSpreadRangePerLevel * (level - 1));
	}
	
	public static int getFireCount(int level)
	{
		return (int) (bombCountBase + bombCountPerLevel * (level - 1));
	}
	
	public static float getBurning(int level)
	{
		return burningBase + burningPerLevel * (level - 1);
	}
	
	public static float getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
