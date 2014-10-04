package com.vhelium.lotig.scene.gamescene.spells.druid;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMinion;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellEnrageSpirit extends Spell
{
	public static String name = "Enrage Spirit";
	public static String effectAsset = "Rage";
	
	public static float cooldownBase = 25000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 120;
	public static float manaCostPerLevel = 12;
	
	public static float dmgBase = 10;
	public static float dmgPerLevel = 2;
	
	public static int asBase = 200;
	public static int asPerLevel = 12;
	
	public static int durationBase = 6000;
	public static int durationPerLevel = 300;
	
	public SpellEnrageSpirit()
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
		return "Enrage your spirits and give them a temporary attack speed and damage buff.\n\nBonus attack speed: " + getAS(getLevel()) + "\nBonus damage: " + getDMG(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
		
		return dp;
	}
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		if(realm.getPlayer(shooterId) == null)
			return;
		
		List<EntityServerMinion> minions = realm.getPlayer(shooterId).getMinions(SpellSummonSpirit.minionName);
		
		ConcurrentHashMap<String, Integer> buffs = new ConcurrentHashMap<String, Integer>();
		buffs.put("AS", getAS(spellLevel));
		buffs.put("DMG", getDMG(spellLevel));
		
		for(EntityServerMinion minion : minions)
		{
			realm.requestCondition(minion.Nr, "Enraged", buffs, getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		}
		
		if(minions.size() < 1)
			realm.playSound(shooterId, SoundFile.spell_failed);
		else
			realm.playSound(SoundFile.spell_attack_up, realm.getPlayer(shooterId).getOriginX(), realm.getPlayer(shooterId).getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAS(int level)
	{
		return asBase + asPerLevel * (level - 1);
	}
	
	public static int getDMG(int level)
	{
		return (int) (dmgBase + dmgPerLevel * (level - 1));
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
