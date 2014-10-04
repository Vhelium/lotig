package com.vhelium.lotig.scene.gamescene.spells.barbarian;

import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellRavage extends Spell
{
	public static String name = "Ravage";
	public static String effectAsset = "Ravage";
	
	public static float cooldownBase = 60000;
	public static float cooldownPerLevel = 1200;
	
	public static float manaCostBase = 220;
	public static float manaCostPerLevel = 15;
	
	public static int dmgBase = 25;
	public static int dmgPerLevel = 12;
	
	public static int armorBase = -50;
	public static int armorPerLevel = -30;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 500;
	
	public SpellRavage()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "Possessed by the art of war you perform more powerful swings but give up your part of your defensive stance.\n\nDamage bonus: " + getDMG(getLevel()) + "\nArmor malus: " + getArmor(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
		ConcurrentHashMap<String, Integer> buffs = new ConcurrentHashMap<String, Integer>();
		buffs.put("DMG", getDMG(spellLevel));
		buffs.put("ARMOR", getArmor(spellLevel));
		realm.requestCondition(shooterId, name, buffs, getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		
		realm.playSound(SoundFile.spell_attack_up, realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getDMG(int level)
	{
		return dmgBase + dmgPerLevel * (level - 1);
	}
	
	public static int getArmor(int level)
	{
		return armorBase + armorPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
}
