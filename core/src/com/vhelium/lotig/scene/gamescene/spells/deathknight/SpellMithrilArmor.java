package com.vhelium.lotig.scene.gamescene.spells.deathknight;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMithrilArmor extends Spell
{
	public static String name = "Mithril Armor";
	public static String effectAsset = "Mithril Armor";
	
	public static float cooldownBase = 40000;
	public static float cooldownPerLevel = 1500;
	
	public static float manaCostBase = 200;
	public static float manaCostPerLevel = 12;
	
	public static int shieldAmountBase = 80;
	public static int shieldAmountPerLevel = 15;
	
	public static int durationBase = 6000;
	public static int durationPerLevel = 750;
	
	public SpellMithrilArmor()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You improve your armor by transorming it into Mithril for a short duration.\n\nArmor bonus: " + getShieldAmount(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
		realm.playSound(shooterId, SoundFile.spell_shieldup_mithril);
		realm.requestCondition(shooterId, name, "ARMOR", getShieldAmount(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getShieldAmount(int level)
	{
		return shieldAmountBase + shieldAmountPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
