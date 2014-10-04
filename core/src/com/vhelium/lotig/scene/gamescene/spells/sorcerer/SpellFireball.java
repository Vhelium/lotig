package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellFireball extends Spell
{
	public static String name = "Fireball";
	public static String bulletAsset = "Fireball";
	public static String effectAsset = "Fireball";
	public static String soundEffect = SoundFile.fire_burst;
	
	public static float cooldownBase = 15000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 220;
	public static float manaCostPerLevel = 20;
	
	public static float damageBase = 150;
	public static float damagePerLevel = 10;
	
	public static float damageBonusBase = 50;
	public static float damageBonusPerLevel = 20;
	
	public static int rangeBase = 500;
	public static int rangePerLevel = 20;
	
	public static int aoeRadiusBase = 100;
	public static int aoeRadiusPerLevel = 7;
	
	public SpellFireball()
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
		return "The next attack will shoot a Fireball that exlodes on inpact.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nSplash radius: " + getAoeRadius(getLevel());
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
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		float rotation = dp.getFloat();
		float dirX = dp.getFloat();
		float dirY = dp.getFloat();
		
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
