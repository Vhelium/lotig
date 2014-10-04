package com.vhelium.lotig.scene.gamescene.spells.ranger;

import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Trap;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellLightningTrap extends Spell
{
	public static String name = "Lightning Trap";
	public static String asset = "lightning_trap";
	public static String effectAsset = "Lightning Trap";
	public static String soundEffect = SoundFile.electric;
	
	private static boolean animated = false;
	private static int animationTime = 500;
	
	public static float cooldownBase = 30000f;
	public static float cooldownPerLevel = 0f;
	
	public static float manaCostBase = 250;
	public static float manaCostPerLevel = 20;
	
	public static int durationBase = 20000;
	public static int durationPerLevel = 500;
	
	public static float damageBase = 80;
	public static float damagePerLevel = 20;
	
	public static float damageBonusBase = 20;
	public static float damageBonusPerLevel = 8;
	
	public static int rangeBase = 100;
	public static int rangePerLevel = 15;
	
	public static int stunDurationBase = 4000;
	public static int stunDurationPerLevel = 200;
	
	public static final int width = 20;
	public static final int height = 20;
	
	public SpellLightningTrap()
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
		return "You place a lightning trap on the ground. If an enemy steps on it it explodes damaging and stunning nearby enemies.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nRange: " + getRange(getLevel()) + "\nStun duration: " + getStunDuration(getLevel()) + "\nTrap duration: " + getDuration(getLevel());
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
		
		realm.addLevelObjectAtRuntime(new LevelObject_Trap(-1, realm, new Rectangle(x - width / 2, y - height / 2, width, height), asset, soundEffect, animated, animationTime, effectAsset, getDuration(level), realm.getEntity(shooterId).getTeamNr(), getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getRange(level), DamageType.Lightning, new Condition(UniqueCondition.Stunned, UniqueCondition.Stunned, 0, getStunDuration(spellLevel), false, UniqueCondition.Stunned, 0)));
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getStunDuration(int level)
	{
		return stunDurationBase + stunDurationPerLevel * (level - 1);
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
