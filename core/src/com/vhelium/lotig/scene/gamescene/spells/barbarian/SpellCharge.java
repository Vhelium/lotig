package com.vhelium.lotig.scene.gamescene.spells.barbarian;

import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellCharge extends Spell
{
	public static String name = "Charge";
	
	public static float cooldownBase = 40000;
	public static float cooldownPerLevel = 500;
	
	public static float manaCostBase = 150;
	public static float manaCostPerLevel = 10;
	
	public static int spdBase = 1800;
	public static int spdPerLevel = 50;
	
	public static float damageBase = 100;
	public static float damagePerLevel = 10;
	
	public static int durationBase = 1200;
	public static int durationPerLevel = 100;
	
	public static int stunDurationBase = 2000;
	public static int stunDurationPerLevel = 100;
	
	public SpellCharge()
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
		return "You accelerate your pace and charge an enemy. The first enemy hit will be damaged and stunned.\n(Speed bonus does not stack with Haste)\n\nSpeed bonus: " + getSPD(getLevel()) + "\nCharge duration: " + (getDuration(getLevel()) / 1000) + " seconds\nDamage: " + getDamage(getLevel()) + "\nStun duration: " + getStunDuration(getLevel());
	}
	
	@Override
	public DataPacket generateRequest(float x, float y, float rotation, float dirX, float dirY)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_SPELL_REQUEST);
		dp.setString(name);
		dp.setInt(getLevel());
		
		return dp;
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
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		ConcurrentHashMap<String, Integer> buffs = new ConcurrentHashMap<String, Integer>();
		buffs.put("ChargeDmg", getDamage(spellLevel));
		buffs.put("ChargeStun", getStunDuration(spellLevel));
		realm.requestCondition(shooterId, name, buffs, getDuration(spellLevel), false, "", System.currentTimeMillis());
		realm.requestCondition(shooterId, SpellHaste.name, "SPD", getSPD(spellLevel), getDuration(spellLevel), true, "", System.currentTimeMillis());
		realm.playSound(SoundFile.shout_speedup, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getSPD(int level)
	{
		return spdBase + spdPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getDamage(int level)
	{
		return (int) (damageBase + damagePerLevel * (level - 1));
	}
	
	public static int getStunDuration(int level)
	{
		return stunDurationBase + stunDurationPerLevel * (level - 1);
	}
}
