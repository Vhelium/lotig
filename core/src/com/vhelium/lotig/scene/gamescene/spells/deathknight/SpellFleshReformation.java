package com.vhelium.lotig.scene.gamescene.spells.deathknight;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellFleshReformation extends Spell
{
	public static String name = "Flesh Reformation";
	public static String effectAsset = "Flesh Reformation";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 180;
	public static float manaCostPerLevel = 12;
	
	public static float maliCountBase = 1;
	public static float maliCountPerLevel = 0.34f;
	
	public SpellFleshReformation()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "Remove multiple negative effects.\n\nMaximal mali: " + getMaliCount(getLevel());
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
		realm.playEffect(shooterId, effectAsset, 0);
		realm.playSound(shooterId, SoundFile.spell_dispel);
		
		realm.getEntity(shooterId).removeRandomMali(getMaliCount(spellLevel));
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getMaliCount(int level)
	{
		return (int) (maliCountBase + maliCountPerLevel * (level - 1));
	}
}
