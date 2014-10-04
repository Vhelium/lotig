package com.vhelium.lotig.scene.gamescene.spells.deathknight;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellInnerBeast extends Spell
{
	public static String name = "Inner Beast";
	public static String effectAsset = "Inner Beast";
	
	public static float cooldownBase = 30000;
	public static float cooldownPerLevel = 1500;
	
	public static float manaCostBase = 180;
	public static float manaCostPerLevel = 10;
	
	public static int strengthBase = 50;
	public static int strengthPerLevel = 10;
	
	public static int durationBase = 6000;
	public static int durationPerLevel = 1000;
	
	public SpellInnerBeast()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "Increased strength for a short duration.\n\nStrength bonus: " + getStrength(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + "seconds.";
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
		realm.requestCondition(shooterId, name, "STR", getStrength(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		realm.playSound(SoundFile.spell_attack_up, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getStrength(int level)
	{
		return strengthBase + strengthPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
