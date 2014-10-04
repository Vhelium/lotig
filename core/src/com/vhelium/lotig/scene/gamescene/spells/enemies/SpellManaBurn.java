package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.IConditionListener;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellManaBurn extends SpellEnemy
{
	public static String name = "Mana Burn";
	public static String effectAsset = "Mana Burn";
	public static String bulletAsset = "Mana Burn";
	
	public static float cooldownBase = 5000f;
	public static float cooldownPerLevel = 0f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static float manaburnBase = 400;
	public static float manaburnPerLevel = 50;
	
	public static int oneDamagePerXmana = 2;
	
	public static int rangeBase = 400;
	public static int rangePerLevel = 4;
	
	public SpellManaBurn(int priority, HashMap<Integer, Float> conditions)
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
	public void activate(final Realm realm, int shooterId, float x, float y, float rotation, float dirX, float dirY)
	{
		final int level = getLevel();
		
		BulletOnHitEffect ohe = new BulletOnHitEffect(1, 0, new Condition(name, 500, effectAsset, 0, new IConditionListener()
		{
			@Override
			public void onDied(EntityServerMixin target)
			{
				
			}
			
			@Override
			public void onApplied(EntityServerMixin target)
			{
				float manaToBurn = Math.max(Math.min(getManaBurn(level), target.getMana()), 0);
				target.setMana(target.getMana() - manaToBurn);
				float damage = manaToBurn / oneDamagePerXmana;
				target.DoDamage(damage, DamageType.Absolute, realm.isPvP());
				
				realm.sendDamageNumberToAllPlayers(target.Nr, -manaToBurn, DamageType.Mana);
				realm.sendDamageNumberToAllPlayers(target.Nr, damage, DamageType.Absolute);
				
				realm.playSound(SoundFile.firestorm, target.getOriginX(), target.getOriginY());
			}
		}));
		
		realm.shoot(shooterId, false, DamageType.Physical, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, ohe);
	}
	
	@Override
	public String getCastSound()
	{
		return SoundFile.spell_missile;
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static float getManaBurn(int level)
	{
		return manaburnBase + manaburnPerLevel * (level - 1);
	}
	
	public static int getRange(int level)
	{
		return rangeBase + rangePerLevel * (level - 1);
	}
}
