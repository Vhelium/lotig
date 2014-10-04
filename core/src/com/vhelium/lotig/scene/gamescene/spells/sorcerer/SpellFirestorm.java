package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

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

public class SpellFirestorm extends Spell
{
	public static String name = "Firestorm";
	public static String effectAsset = "Firestorm";
	
	public static float cooldownBase = 50000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 420;
	public static float manaCostPerLevel = 15;
	
	public static float repeatBase = 4;
	public static float repeatPerLevel = 0.34f;
	
	public static float damageBase = 40;
	public static float damagePerLevel = 20;
	
	public static float damageBonusBase = 20;
	public static float damageBonusPerLevel = 10f;
	
	public static int aoeRadiusBase = 100;
	public static int aoeRadiusPerLevel = 2;
	
	public static float burningBase = 60;
	public static float burningPerLevel = 10;
	
	public static float durationBase = 4000;
	public static float durationPerLevel = 0;
	
	public SpellFirestorm()
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
		return "You summon the elements of fire and create a firestorm around you damaging nearby enemies periodically.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nBurning: " + getBurning(getLevel()) + " over " + (getDuration(getLevel()) / 1000) + " seconds\nRadius: " + getAoeRadius(getLevel()) + "\nStorm ticks: " + getRepeat(getLevel());
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
		
		BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getAoeRadius(level), new Condition(UniqueCondition.Burning, UniqueCondition.Burning, (int) getBurning(level), (int) getDuration(spellLevel), false, UniqueCondition.Burning, 0), effectAsset, 0);
		effect.setSoundEffect(SoundFile.firestorm);
		effect.repeat = getRepeat(level);
		effect.delay = 500f;
		effect.centered = true;
		effect.effectAttached = true;
		realm.doAoeDamage(realm.getEntity(shooterId), x, y, effect, DamageType.Fire);
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
	
	public static int getRepeat(int level)
	{
		return (int) (repeatBase + repeatPerLevel * (level - 1));
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
