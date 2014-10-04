package com.vhelium.lotig.scene.gamescene.spells.druid;

import java.util.List;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMinion;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellHealSpirit extends Spell
{
	public static String name = "Heal Spirit";
	public static String effectAsset = "Healed";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 140;
	public static float manaCostPerLevel = 15;
	
	public static float healBase = 300;
	public static float healPerLevel = 50;
	
	public SpellHealSpirit()
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
		return "You heal up your spirits with the energies\nof nature.\n\nHeal: " + getHealCount(getLevel());
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
		
		for(EntityServerMinion minion : minions)
		{
			minion.doHeal(getHealCount(spellLevel));
			realm.sendDamageNumberToAllPlayers(minion.Nr, getHealCount(spellLevel), DamageType.Heal);
			realm.playEffect(minion.Nr, effectAsset, 0);
		}
		
		if(minions.size() < 1)
			realm.playSound(SoundFile.spell_failed);//failed
		else
			realm.playSound(SoundFile.spell_bling, realm.getPlayer(shooterId).getOriginX(), realm.getPlayer(shooterId).getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static float getHealCount(int level)
	{
		return healBase + healPerLevel * (level - 1);
	}
}
