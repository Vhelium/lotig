package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellFireball extends SpellEnemy
{
	public static String name = "Fireball";
	private static String bulletAsset = "Fireball";
	private static String effectAsset = "Fireball";
	private static String soundEffect = SoundFile.fire_burst;
	
	private static float cooldownBase = 8000;
	private static float cooldownPerLevel = -80f;
	
	private static float manaCostBase = 0;
	private static float manaCostPerLevel = 0;
	
	private static float damageBase = 120;
	private static float damagePerLevel = 10;
	
	private static float damageBonusBase = 50;
	private static float damageBonusPerLevel = 20;
	
	private static int rangeBase = 500;
	private static int rangePerLevel = 0;
	
	private static int aoeRadiusBase = 100;
	private static int aoeRadiusPerLevel = 5;
	
	public SpellFireball(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = false;
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
		int level = getLevel();
		BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getAoeRadius(level), effectAsset, 0);
		effect.setSoundEffect(soundEffect);
		realm.shoot(shooterId, false, DamageType.Fire, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, effect);
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
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
}
