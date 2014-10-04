package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellEquilibrium extends Spell
{
	public static String name = "Equilibrium";
	public static String effectAsset = "Equilibrium";
	
	public static float cooldownBase = 1000;
	public static float cooldownPerLevel = 400;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int hpCostBase = 300;
	public static int hpCostPerLevel = 40;
	
	public static int manaBase = 100;
	public static int manaPerLevel = 10;
	
	public SpellEquilibrium()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You exchange part of your health for mana.\n\nHealth cost: " + getHpCost(getLevel()) + "\nMana gain: " + getMana(getLevel());
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
		if(realm.getPlayer(shooterId) == null)
			return;
		
		realm.getPlayer(shooterId).DoDamage(getHpCost(spellLevel), DamageType.Absolute, false);
		realm.getPlayer(shooterId).setMana(realm.getPlayer(shooterId).getMana() + getMana(spellLevel));
		
		realm.sendDamageNumberToAllPlayers(shooterId, getMana(spellLevel), DamageType.Mana);
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getHpCost(int level)
	{
		return hpCostBase + hpCostPerLevel * (level - 1);
	}
	
	public static int getMana(int level)
	{
		return manaBase + manaPerLevel * (level - 1);
	}
}
