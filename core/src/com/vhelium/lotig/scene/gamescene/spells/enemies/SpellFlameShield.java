package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellFlameShield extends SpellEnemy
{
	public static String name = "Flame Shield";
	public static String effectAsset = "Flame Shield";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 0f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int reflectionDamageBase = 100;
	public static int reflectionDamagePerLevel = 10;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 0;
	
	public SpellFlameShield(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = true;
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
	
	@Override
	public void activate(Realm realm, int shooterId, float x, float y, float rotation, float dirX, float dirY)
	{
		realm.playSound(SoundFile.burning_up, x, y);
		realm.requestCondition(shooterId, name, "THORNS", getReflectionDamage(getLevel()), getDuration(getLevel()), true, effectAsset, System.currentTimeMillis());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getReflectionDamage(int level)
	{
		return reflectionDamageBase + reflectionDamagePerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
