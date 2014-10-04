package com.vhelium.lotig.scene.gamescene.spells.druid;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellThornArmor extends Spell
{
	public static String name = "Thorn Armor";
	public static String effectAsset = "Thorn Armor";
	
	public static float cooldownBase = 60000f;
	public static float cooldownPerLevel = 1000f;
	
	public static float manaCostBase = 200;
	public static float manaCostPerLevel = 15;
	
	public static int reflectionDamageBase = 40;
	public static int reflectionDamagePerLevel = 5;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 500;
	
	public SpellThornArmor()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "Your armor grows thorns which damages all attackers.\n\nThorn damage: " + getReflectionDamage(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
		realm.playSound(shooterId, SoundFile.spell_root);
		realm.requestCondition(shooterId, name, "THORNS", getReflectionDamage(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
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
