package com.vhelium.lotig.scene.gamescene.spells.deathknight;

import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellSpellShield extends Spell
{
	public static String name = "Spell Shield";
	public static String effectAsset = "Spell Shield";
	
	public static float cooldownBase = 40000f;
	public static float cooldownPerLevel = 600f;
	
	public static float manaCostBase = 200;
	public static float manaCostPerLevel = 14;
	
	public static int shieldAmountBase = 40;
	public static int shieldAmountPerLevel = 8;
	
	public static int durationBase = 5000;
	public static int durationPerLevel = 200;
	
	public SpellSpellShield()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "Increases your spell resistances.\n\nResistance bonus; " + getShieldAmount(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
		EntityServerMixin shooter = realm.getEntity(shooterId);
		if(shooter == null)
			return;
		
		realm.playSound(SoundFile.spell_shieldup, shooter.getOriginX(), shooter.getOriginY());
		
		ConcurrentHashMap<String, Integer> buffs = new ConcurrentHashMap<String, Integer>();
		buffs.put("FRES", getShieldAmount(spellLevel));
		buffs.put("CRES", getShieldAmount(spellLevel));
		buffs.put("LRES", getShieldAmount(spellLevel));
		buffs.put("PRES", getShieldAmount(spellLevel));
		realm.requestCondition(shooterId, name, buffs, getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
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
