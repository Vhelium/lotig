package com.vhelium.lotig.scene.gamescene.spells.barbarian;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellRage extends Spell
{
	public static String name = "Rage";
	public static String effectAsset = "Rage";
	
	public static float cooldownBase = 60000;
	public static float cooldownPerLevel = 1000;
	
	public static float manaCostBase = 180;
	public static float manaCostPerLevel = 10;
	
	public static int asBase = 220;
	public static int asPerLevel = 5;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 500;
	
	public SpellRage()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "In the heat of battle you enrage yourself and swing your weapon like the wind.\n\nBonus attack speed: " + getAS(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
		realm.requestCondition(shooterId, name, "AS", getAS(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		realm.playSound(SoundFile.shout_rage, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAS(int level)
	{
		return asBase + asPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
