package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.IConditionListener;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellLivingBomb extends Spell
{
	public static String name = "Living Bomb";
	public static String debuffAsset = "Living Bomb Debuff";
	public static String effectAsset = "Living Bomb";
	public static String bulletAsset = "Living Bomb";
	
	public static float cooldownBase = 8000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 400;
	public static float manaCostPerLevel = 30;
	
	public static float damageBase = 120;
	public static float damagePerLevel = 40;
	
	public static float damageBonusBase = 100;
	public static float damageBonusPerLevel = 30;
	
	public static int aoeRadiusBase = 50;
	public static int aoeRadiusPerLevel = 5;
	
	public static int rangeBase = 300;
	public static int rangePerLevel = 20;
	
	public static float durationBase = 80000;
	public static float durationPerLevel = 0;
	
	public SpellLivingBomb()
	{
		instantCast = false;
	}
	
	//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{
		return "You shoot a magical projectile that infects the enemy struck. When the infected target dies it explodes and damages nearby enemies.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nRange: " + getRange(getLevel());
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
	public DataPacket generateRequest(float x, float y, float rotation, float dirX, float dirY)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_SPELL_REQUEST);
		dp.setString(name);
		dp.setInt(getLevel());
		dp.setFloat(x);
		dp.setFloat(y);
		dp.setFloat(rotation);
		dp.setFloat(dirX);
		dp.setFloat(dirY);
		
		return dp;
	}
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(final int shooterId, int spellLevel, DataPacket dp, final Realm realm)
	{
		final int level = spellLevel;
		final float x = dp.getFloat();
		final float y = dp.getFloat();
		float rotation = dp.getFloat();
		float dirX = dp.getFloat();
		float dirY = dp.getFloat();
		
		BulletOnHitEffect ohe = new BulletOnHitEffect(1, 0, new Condition(name, (int) getDuration(spellLevel), debuffAsset, 0, new IConditionListener()
		{
			@Override
			public void onDied(EntityServerMixin target)
			{
				BulletOnHitEffect ohe = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getAoeRadius(level), effectAsset, 0);
				ohe.setSoundEffect(SoundFile.fire_burst);
				realm.doAoeDamage(realm.getEntity(shooterId), target.getOriginX(), target.getOriginY(), ohe, DamageType.Fire);
			}
			
			@Override
			public void onApplied(EntityServerMixin target)
			{
				
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
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
}
