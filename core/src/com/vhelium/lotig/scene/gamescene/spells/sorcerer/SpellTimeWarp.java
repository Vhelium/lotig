package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellTimeWarp extends Spell
{
	public static String name = "Time Warp";
	public static String effectAsset = "Time Warp";
	
	public static float cooldownBase = 150000f;
	public static float cooldownPerLevel = 2000f;
	
	public static float manaCostBase = 400;
	public static float manaCostPerLevel = 25;
	
	public static int reductionBase = 10000;
	public static int reductionPerLevel = 500;
	
	public SpellTimeWarp()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "Use your magic powers to warp the time and lower all your spell cooldowns.\n\nCooldown decrease: " + (getReduction(getLevel()) / 1000f) + " seconds.";
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
		DataPacket dpPlayer = new DataPacket();
		dpPlayer.setInt(MessageType.MSG_PLAYER_COOLDOWN_LOWERING);
		dpPlayer.setInt(getReduction(spellLevel));
		realm.sendToPlayer(shooterId, dpPlayer);
		
		realm.playSound(shooterId, SoundFile.spell_slow_down);
		realm.playEffect(shooterId, effectAsset, 0);
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getReduction(int level)
	{
		return reductionBase + reductionPerLevel * (level - 1);
	}
}
