package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

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

public class SpellHellFire extends Spell
{
	public static String name = "Hell Fire";
	public static String effectAsset = "Hell Fire";
	
	public static float cooldownBase = 60000f;
	public static float cooldownPerLevel = 100f;
	
	public static float manaCostBase = 450;
	public static float manaCostPerLevel = 30;
	
	public static float damageBase = 65;
	public static float damagePerLevel = 35;
	
	public static float damageBonusBase = 30;
	public static float damageBonusPerLevel = 5;
	
	public static float fireCountBase = 8;
	public static float fireCountPerLevel = 0.4f;
	
	public static int aoeRadiusBase = 35;
	public static int aoeRadiusPerLevel = 1;
	
	public static float aoeSpreadRangeBase = 350;
	public static float aoeSpreadRangePerLevel = 3;
	
	public static float burningBase = 20;
	public static float burningPerLevel = 10;
	
	public static float durationBase = 3000;
	public static float durationPerLevel = 100;
	
	public SpellHellFire()
	{
		instantCast = true;
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
		return "You summon the fire of hell causing multiple conflagrations on random spots around you. Enemies hit will burn in the hell fire.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nFire count: " + getFireCount(getLevel()) + "\nFire diameter: " + getAoeRadius(getLevel()) + "\nBurning damage: " + getBurning(getLevel()) + " over " + (getDuration(getLevel()) / 1000) + " seconds";
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
		
		return dp;
	}
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		if(realm.getEntity(shooterId) == null)
			return;
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		
		for(int i = 0; i < getFireCount(level); i++)
		{
			BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getAoeRadius(level), new Condition(UniqueCondition.Burning, UniqueCondition.Burning, (int) getBurning(level), (int) getDuration(spellLevel), false, UniqueCondition.Burning, 0), effectAsset, 0);
			effect.setSoundEffect(SoundFile.fire_burst);
			effect.delay = GameHelper.getInstance().getRandom().nextInt(1200);
			effect.centered = false;
			effect.dontProccFirst = true;
			realm.doAoeDamage(realm.getEntity(shooterId), x - getAoeSpreadRange(level) / 2 + GameHelper.getInstance().getRandom().nextInt(getAoeSpreadRange(level)), y - getAoeSpreadRange(level) / 2 + GameHelper.getInstance().getRandom().nextInt(getAoeSpreadRange(level)), effect, DamageType.Fire);
		}
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
	
	public static int getAoeSpreadRange(int level)
	{
		return (int) (aoeSpreadRangeBase + aoeSpreadRangePerLevel * (level - 1));
	}
	
	public static int getFireCount(int level)
	{
		return (int) (fireCountBase + fireCountPerLevel * (level - 1));
	}
	
	public static float getBurning(int level)
	{
		return burningBase + burningPerLevel * (level - 1);
	}
	
	public static float getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
