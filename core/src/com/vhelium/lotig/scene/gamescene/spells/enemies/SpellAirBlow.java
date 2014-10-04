package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.client.player.MovementModifier;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellAirBlow extends SpellEnemy
{
	public static String name = "Air Blow";
	private String bulletAsset = "Air Blow";
	
	public static float cooldownBase = 8000;
	public static float cooldownPerLevel = 50f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static float damageBase = 120;
	public static float damagePerLevel = 22;
	
	public static float damageBonusBase = 25;
	public static float damageBonusPerLevel = 5;
	
	public static int rangeBase = 280;
	public static int rangePerLevel = 3;
	
	static float force = 2f;
	static float decay = 0.88f;
	
	public SpellAirBlow(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = false;
	}
	
	public SpellAirBlow(int priority, HashMap<Integer, Float> conditions, String bulletAsset)
	{
		super(priority, conditions);
		this.bulletAsset = bulletAsset;
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
		BulletOnHitEffect ohe = new BulletOnHitEffect(getDamage(getLevel()) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(getLevel()) + 1), 0, null, new MovementModifier("Air Blow", dirX, dirY, force, decay));
		realm.shoot(shooterId, false, DamageType.Cold, getRange(getLevel()), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, ohe);
	}
	
	@Override
	public String getCastSound()
	{
		return SoundFile.bow3;
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
