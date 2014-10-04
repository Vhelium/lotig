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

public class SpellFlameWall extends SpellEnemy
{
	public static String name = "Flame Wall";
	public static String bulletAsset = "Flame Breath";
	
	public static float cooldownBase = 15000f;
	public static float cooldownPerLevel = -50f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static float damageBase = 120;
	public static float damagePerLevel = 15;
	
	public static float damageBonusBase = 50;
	public static float damageBonusPerLevel = 1;
	
	public static int rangeBase = 300;
	public static int rangePerLevel = 5;
	
	public static float burningBase = 40;
	public static float burningPerLevel = 12;
	
	public static float durationBase = 6100;
	public static float durationPerLevel = 0;
	
	public SpellFlameWall(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = false;
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
		int level = getLevel();
		BulletOnHitEffect ohe = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), 0, new Condition(UniqueCondition.Burning, UniqueCondition.Burning, (int) getBurning(level), (int) getDuration(level), false, UniqueCondition.Burning, 0));
		realm.shoot(shooterId, true, DamageType.Fire, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, ohe, false);
	}
	
	@Override
	public String getCastSound()
	{
		return SoundFile.spell_fire_cast;
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
	
	public static int getRange(int level)
	{
		return rangeBase + rangePerLevel * (level - 1);
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
