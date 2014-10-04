package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellGroundSlam extends SpellEnemy
{
	public static String name = "Ground Slam";
	public static String effectAsset = "Ground Slam";
	
	public float cooldownBase = 6000;
	public float cooldownPerLevel = 75f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static float damageBase = 240;
	public static float damagePerLevel = 20;
	
	public static float damageBonusBase = 30;
	public static float damageBonusPerLevel = 3;
	
	public int aoeRadiusBase = 80;
	public int aoeRadiusPerLevel = 3;
	
	public SpellGroundSlam(int priority, HashMap<Integer, Float> conditions)
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
	
	public int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
}
