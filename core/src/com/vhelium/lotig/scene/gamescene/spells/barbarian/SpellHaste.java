package com.vhelium.lotig.scene.gamescene.spells.barbarian;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellHaste extends Spell
{
	public static String name = "Haste";
	
	public static float cooldownBase = 100000;
	public static float cooldownPerLevel = 1500;
	
	public static float manaCostBase = 280;
	public static float manaCostPerLevel = 25;
	
	public static int spdBase = 600;
	public static int spdPerLevel = 25;
	
	public static int durationBase = 10000;
	public static int durationPerLevel = 750;
	
	public SpellHaste()
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
		return "You strengthen your stamina and run with increased movement speed for a short period.\n\nSpeed bonus: " + getSPD(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
}
