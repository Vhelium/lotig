package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellRootShot extends SpellEnemy
{
	public static String name = "Root Shot";
	public static String bulletAsset = "Entangle";
	public static String effectAsset = "Entangle";
	
	public static float cooldownBase = 15000f;
	public static float cooldownPerLevel = -100f;
	
	public static float manaCostBase = 220;
	public static float manaCostPerLevel = 14;
	
	public static float damageBase = 70;
	public static float damagePerLevel = 15;
	
	public static float damageBonusBase = 20;
	public static float damageBonusPerLevel = 4f;
	
	public static int rangeBase = 320;
	public static int rangePerLevel = 5;
	
	public static float durationBase = 3000;
	public static float durationPerLevel = 150;
	
	public SpellRootShot(int priority, HashMap<Integer, Float> conditions)
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
		BulletOnHitEffect ohe = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), 0, new Condition(name, "Root", 0, (int) getDuration(level), false, effectAsset, 0));
		ohe.setSoundEffect(SoundFile.spell_root);
		realm.shoot(shooterId, false, DamageType.Physical, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, ohe);
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
	
	public static float getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
