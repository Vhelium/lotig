package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellPiercingBullet extends SpellEnemy
{
	public static String name = "Piercing Bullet";
	private String bulletAsset = "null";
	
	public static float cooldownBase = 10000f;
	public static float cooldownPerLevel = 0;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static float damageBase = 250;
	public static float damagePerLevel = 20;
	
	public static float damageBonusBase = 30;
	public static float damageBonusPerLevel = 2;
	
	public static int rangeBase = 400;
	public static int rangePerLevel = 5;
	
	public SpellPiercingBullet(int priority, HashMap<Integer, Float> conditions, String bulletAsset)
	{
		super(priority, conditions);
		this.bulletAsset = bulletAsset;
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
		BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), 0, "", 0);
		realm.shoot(shooterId, true, DamageType.Absolute, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, effect);
	}
	
	@Override
	public String getCastSound()
	{
		return SoundFile.swing2;
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
}
