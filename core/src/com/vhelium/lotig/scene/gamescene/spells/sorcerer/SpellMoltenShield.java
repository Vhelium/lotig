package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMoltenShield extends Spell
{
	public static String name = "Molten Shield";
	public static String effectAsset = "Molten Shield";
	
	public static float cooldownBase = 60000;
	public static float cooldownPerLevel = 1000;
	
	public static float manaCostBase = 180;
	public static float manaCostPerLevel = 10;
	
	public static int fresBase = 140;
	public static int fresPerLevel = 10;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 250;
	
	public static int reflectionDamageBase = 25;
	public static int reflectionDamagePerLevel = 5;
	
	public SpellMoltenShield()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "With magical energy you create a molten shield around you that increases your fire resistance and damages attackers.\n\nFire resistance: " + getFRES(getLevel()) + "\nDamage: " + getReflectionDamage(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
	}
	
	@Override
	public String getName()
	{
		return name;
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
		buffs.put("THORNS", getReflectionDamage(spellLevel));
		buffs.put("FRES", getFRES(spellLevel));
		realm.requestCondition(shooterId, name, buffs, getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		realm.playSound(shooterId, SoundFile.burning_up);
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getFRES(int level)
	{
		return fresBase + fresPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getReflectionDamage(int level)
	{
		return reflectionDamageBase + reflectionDamagePerLevel * (level - 1);
	}
}
