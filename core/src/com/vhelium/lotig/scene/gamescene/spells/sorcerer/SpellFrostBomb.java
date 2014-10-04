package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Bomb;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellFrostBomb extends Spell
{
	public static String name = "Frost Bomb";
	public static String bombAsset = "Frost Bomb";
	public static String effectAsset = "Frost Bomb";
	
	public static float cooldownBase = 60000f;
	public static float cooldownPerLevel = 1000f;
	
	public static float manaCostBase = 400;
	public static float manaCostPerLevel = 10;
	
	public static int delay = 3000;
	
	public static float damageBase = 220;
	public static float damagePerLevel = 30;
	
	public static float damageBonusBase = 20;
	public static float damageBonusPerLevel = 2f;
	
	public static int aoeRadiusBase = 100;
	public static int aoeRadiusPerLevel = 2;
	
	public static final int width = 20;
	public static final int height = 20;
	
	public SpellFrostBomb()
	{
		instantCast = true;
	}
	
	//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You place a Frost Bomb on the ground which explodes after " + (delay / 1000) + " seconds.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nRadius: " + getAoeRadius(getLevel());
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
		if(realm.getPlayer(shooterId) == null)
			return;
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		
		BulletOnHitEffect ohe = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getAoeRadius(level), effectAsset, 0);
		ohe.setSoundEffect(SoundFile.frost_blast);
		realm.addLevelObjectAtRuntime(new LevelObject_Bomb(-1, realm, x, y, width, height, bombAsset, delay, ohe, DamageType.Cold, shooterId));
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
