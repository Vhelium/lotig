package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMeteorStrike extends Spell
{
	public static String name = "Meteor Strike";
	public static String effectAsset = "Meteor Strike";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 400;
	public static float manaCostPerLevel = 20;
	
	public static float damageBase = 200;
	public static float damagePerLevel = 30;
	
	public static float damageBonusBase = 50;
	public static float damageBonusPerLevel = 5f;
	
	public static int aoeRadiusBase = 220;
	public static int aoeRadiusPerLevel = 8;
	
	public SpellMeteorStrike()
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
		return "You summon a meteor on the nearest enemy.\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nRadius: " + getAoeRadius(getLevel());
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
		
		EntityServerMixin entity = realm.getNearestHostileEntity(x, y, getAoeRadius(spellLevel), realm.getPlayer(shooterId).getTeamNr());
		
		if(entity != null)
		{
			realm.playEffect(entity.getOriginX(), entity.getOriginY(), -1, -1, effectAsset, 0);
			float dmg = entity.DoDamage(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), DamageType.Fire, realm.isPvP());
			realm.sendDamageNumberToAllPlayers(entity.Nr, -dmg, DamageType.Fire);
			realm.playSound(SoundFile.spell_explosion_short, x, y);
		}
		else
			realm.playSound(shooterId, SoundFile.spell_failed);
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static float getDamage(int level)
	{
		return damageBase + damagePerLevel * (level - 1);
	}
	
	public static float getBonusDamage(int level)
	{
		return damageBonusBase + damageBonusPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
}
