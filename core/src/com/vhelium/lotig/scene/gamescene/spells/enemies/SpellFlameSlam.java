package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellFlameSlam extends SpellEnemy
{
	public static String name = "Flame Slam";
	public static String effectAsset = "Flame Slam";
	
	public static float cooldownBase = 4500;
	public static float cooldownPerLevel = -20f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static float damageBase = 150;
	public static float damagePerLevel = 15;
	
	public static float damageBonusBase = 30;
	public static float damageBonusPerLevel = 3;
	
	public static int aoeRadiusBase = 80;
	public static int aoeRadiusPerLevel = 3;
	
	public SpellFlameSlam(int priority, HashMap<Integer, Float> conditions)
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
		BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(getLevel()) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(getLevel()) + 1), getAoeRadius(getLevel()), "", 0);
		effect.setSoundEffect(SoundFile.spell_tremble_short);
		realm.playEffect(shooterId, getAoeRadius(getLevel()) * 2, getAoeRadius(getLevel()) * 2, effectAsset, 0);
		realm.doAoeDamage(realm.getEntity(shooterId), x, y, effect, DamageType.Physical);
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
}
