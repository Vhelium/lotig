package com.vhelium.lotig.scene.gamescene.spells.ranger;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellPoisonousArrow extends Spell
{
	public static String name = "Poisonous Arrow";
	public static String bulletAsset = "Poisonous Arrow";
	
	public static float cooldownBase = 18000;
	public static float cooldownPerLevel = 500;
	
	public static float manaCostBase = 220;
	public static float manaCostPerLevel = 15;
	
	public static float damageBase = 60;
	public static float damagePerLevel = 15;
	
	public static float damageBonusBase = 40;
	public static float damageBonusPerLevel = 10;
	
	public static int rangeBase = 500;
	public static int rangePerLevel = 10;
	
	public static float poisonBase = 160;
	public static float poisonPerLevel = 20;
	
	public static float durationBase = 5000;
	public static float durationPerLevel = 200;
	
	public SpellPoisonousArrow()
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
		return "The next arrow shot will be imbued in poison and will poison the first enemy hit.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nPoison: " + getPoison(getLevel()) + " over " + (getDuration(getLevel()) / 1000) + " seconds\nRange: " + getRange(getLevel());
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
		
		BulletOnHitEffect ohe = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), 0, new Condition(UniqueCondition.Poisoned, UniqueCondition.Poisoned, (int) getPoison(level), (int) getDuration(spellLevel), false, UniqueCondition.Poisoned, 0));
		
		realm.shoot(shooterId, false, DamageType.Poison, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, ohe);
	}
	
	@Override
	public String getCastSound()
	{
		return SoundFile.bow1;
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
	
	public static float getPoison(int level)
	{
		return poisonBase + poisonPerLevel * (level - 1);
	}
	
	public static float getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
