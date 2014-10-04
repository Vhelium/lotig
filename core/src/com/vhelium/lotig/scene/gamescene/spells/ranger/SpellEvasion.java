package com.vhelium.lotig.scene.gamescene.spells.ranger;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellEvasion extends Spell
{
	public static String name = "Evasion";
	
	public static float cooldownBase = 40000;
	public static float cooldownPerLevel = 500;
	
	public static float manaCostBase = 180;
	public static float manaCostPerLevel = 15;
	
	public static int spdBase = 3500;
	public static int spdPerLevel = 25;
	
	public static int durationBase = 220;
	public static int durationPerLevel = 5;
	
	public SpellEvasion()
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
		return "You perform a fast combat maneuver.\n\nSpeed bonus: " + getSPD(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000f) + " seconds";
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
		realm.requestCondition(shooterId, name, "SPD", getSPD(spellLevel), getDuration(spellLevel), true, "", System.currentTimeMillis());
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
}
