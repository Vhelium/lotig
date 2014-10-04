package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Lightwell;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellLightwell extends Spell
{
	public static String name = "Lightwell";
	public static String effectAsset = "Lightwell";
	
	public static float cooldownBase = 90000f;
	public static float cooldownPerLevel = 2000f;
	
	public static float manaCostBase = 400;
	public static float manaCostPerLevel = 10;
	
	public static float healBase = 300;
	public static float healPerLevel = 30;
	
	public static int durationBase = 20000;
	public static int durationPerLevel = 200;
	
	public static int respawnBase = 3500;
	public static int respawnPerLevel = -50;
	
	public static final int width = 48;
	public static final int height = 48;
	
	public SpellLightwell()
	{
		instantCast = true;
	}
	
	//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You place a lightwell on the ground which provides healing orbs to you and your allies.\n\nHeal per orb: " + getHealCount(getLevel()) + "\nRespawn rate: " + (getRespawn(getLevel()) / 1000f) + " seconds\nDuration : " + (getDuration(getLevel()) / 1000) + " seconds";
	}
	
	@Override
	public String getName()
	{
		return name;
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
		
		realm.playSound(SoundFile.spell_summon, x, y);
		realm.addLevelObjectAtRuntime(new LevelObject_Lightwell(-1, realm, new Rectangle(x - width / 2, y - height / 2, width, height), getDuration(level), getRespawn(level), getHealCount(level)));
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getHealCount(int level)
	{
		return (int) (healBase + healPerLevel * (level - 1));
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getRespawn(int level)
	{
		return respawnBase + respawnPerLevel * (level - 1);
	}
}
