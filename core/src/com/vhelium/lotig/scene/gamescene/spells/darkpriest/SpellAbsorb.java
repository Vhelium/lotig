package com.vhelium.lotig.scene.gamescene.spells.darkpriest;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellAbsorb extends Spell
{
	public static String name = "Absorb";
	public static String effectAsset = "Absorb";
	public static String buffAsset = "Absorb Buff";
	public static String debuffAsset = "Absorb Debuff";
	
	public static float cooldownBase = 6000f;
	public static float cooldownPerLevel = 1000f;
	
	public static float manaCostBase = 450;
	public static float manaCostPerLevel = 25;
	
	public static int aoeRadiusBase = 200;
	public static int aoeRadiusPerLevel = 7;
	
	public static float dmgMalusBase = -30;
	public static float dmgMalusPerLevel = -10;
	
	public static float dmgPerHitBase = 25;
	public static float dmgPerHitPerLevel = 12;
	
	public static int durationBase = 3500;
	public static int durationPerLevel = 250;
	
	public SpellAbsorb()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You drain the strength from nearby enemies and reduce their attack power. You gain increased damage for each enemy hit.\n\nRadius: " + getAoeRadius(getLevel()) + "\nDamage malus: " + getDmgMalus(getLevel()) + "\nDamage bonus per hit: " + getDmgPerHit(getLevel()) + "\nDuration: " + (getDuration(getLevel()) / 1000) + " seconds";
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
		dp.setFloat(x);
		dp.setFloat(y);
		
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
		
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		
		long millis = System.currentTimeMillis();
		
		realm.playEffect(x, y, getAoeRadius(level) * 2, getAoeRadius(level) * 2, effectAsset, 0);
		realm.playSound(SoundFile.spell_attack_up, x, y);
		
		int hitten = 0;
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(realm.getEntity(shooterId).getOriginX(), realm.getEntity(shooterId).getOriginY(), getAoeRadius(spellLevel));
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() != realm.getEntity(shooterId).getTeamNr())
			{
				realm.requestCondition(entity.getKey(), name + "Debuff", "DMG", getDmgMalus(spellLevel), getDuration(spellLevel), true, debuffAsset, millis);
				hitten++;
			}
		}
		
		if(hitten > 0)
			for(Entry<Integer, PlayerServer> player : realm.getPlayers().entrySet())
			{
				if(player.getKey() == shooterId || player.getValue().getTeamNr() == realm.getPlayer(shooterId).getTeamNr())
				{
					realm.requestCondition(player.getKey(), name + "Buff", "DMG", getDmgPerHit(spellLevel) * hitten, getDuration(spellLevel), true, buffAsset, millis);
				}
			}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
	
	public static int getDmgMalus(int level)
	{
		return (int) (dmgMalusBase + dmgMalusPerLevel * (level - 1));
	}
	
	public static int getDmgPerHit(int level)
	{
		return (int) (dmgPerHitBase + dmgPerHitPerLevel * (level - 1));
	}
}
