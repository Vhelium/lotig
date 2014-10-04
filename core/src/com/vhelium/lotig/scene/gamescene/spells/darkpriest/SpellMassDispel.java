package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMassDispel extends Spell
{
	public static String name = "Mass Dispel";
	public static String effectAsset = "Mass Dispel";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 350;
	public static float manaCostPerLevel = 30;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 7;
	
	public static float targetCount = 2;
	public static float targetCountPerLevel = 0.5f;
	
	public static float maliCountBase = 1;
	public static float maliCountPerLevel = 0.4f;
	
	public SpellMassDispel()
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
		return "You use dark magic to ban multiple negative effects from you and nearby allies.\n\nMax. mali count: " + getMaliCount(getLevel()) + "\nRadius: " + getAoeRadius(getLevel()) + "\nMaximum targets: " + getTargetCount(getLevel());
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
		
		//self:
		realm.playEffect(shooterId, effectAsset, 0);
		realm.playSound(SoundFile.spell_dispel, realm.getPlayer(shooterId).getOriginX(), realm.getPlayer(shooterId).getOriginY());
		realm.getEntity(shooterId).removeRandomMali(getMaliCount(spellLevel));
		//others:
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		int dispelled = 1;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(dispelled >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == realm.getEntity(shooterId).getTeamNr())
			{
				dispelled++;
				realm.playEffect(entity.getKey(), effectAsset, 0);
				realm.getEntity(entity.getKey()).removeRandomMali(getMaliCount(spellLevel));
			}
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
	
	public static int getTargetCount(int level)
	{
		return (int) (targetCount + targetCountPerLevel * (level - 1));
	}
	
	public static int getMaliCount(int level)
	{
		return (int) (maliCountBase + maliCountPerLevel * (level - 1));
	}
}
