package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellShadowCurse extends Spell
{
	public static String name = "Shadow Curse";
	public static String effectAsset = "Shadow Curse";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 180;
	public static float manaCostPerLevel = 10;
	
	public static int aoeRadiusBase = 150;
	public static int aoeRadiusPerLevel = 7;
	
	public static int durationBase = 10000;
	public static int durationPerLevel = 200;
	
	public static float damageBase = 360;
	public static float damagePerLevel = 75;
	
	public SpellShadowCurse()
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
		return "You curse the nearest enemy with a dark curse dealing absolute damage over time.\n\nDamage: " + getDamage(getLevel()) + " over " + (getDuration(getLevel()) / 1000) + " seconds\nRadius: " + getAoeRadius(getLevel());
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
		
		realm.playEffect(x, y, getAoeRadius(level) * 2, getAoeRadius(level) * 2, effectAsset, 0);
		
		EntityServerMixin entity = realm.getNearestHostileEntity(x, y, getAoeRadius(spellLevel), realm.getPlayer(shooterId).getTeamNr());
		
		if(entity != null)
		{
			realm.playSound(shooterId, SoundFile.spell_cursed);
			realm.requestCondition(entity.Nr, name, "Cursed", getDamage(spellLevel), getDuration(spellLevel), false, effectAsset, System.currentTimeMillis());
		}
		else
			realm.playSound(shooterId, SoundFile.spell_failed);
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getDamage(int level)
	{
		return (int) (damageBase + damagePerLevel * (level - 1));
	}
}
