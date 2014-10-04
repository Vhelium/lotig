package com.vhelium.lotig.scene.gamescene.spells.druid;

import java.util.List;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMinion;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellBlastSpirit extends Spell
{
	public static String name = "Blast Spirit";
	public static String effectAsset = "Blast Spirit";
	
	public static float cooldownBase = 8000f;
	public static float cooldownPerLevel = 200f;
	
	public static float manaCostBase = 140;
	public static float manaCostPerLevel = 12;
	
	public static float damageBase = 250;
	public static float damagePerLevel = 35;
	
	public static float damageBonusBase = 30;
	public static float damageBonusPerLevel = 10;
	
	public static int aoeRadiusBase = 80;
	public static int aoeRadiusPerLevel = 3;
	
	public SpellBlastSpirit()
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
		return "Let your summoned spirits explode!\n\nDamage: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nRadius: " + getAoeRadius(getLevel());
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
			BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(spellLevel) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(spellLevel) + 1), getAoeRadius(spellLevel), effectAsset, 0);
			effect.setSoundEffect(SoundFile.frost_blast);
			realm.doAoeDamage(realm.getEntity(shooterId), minion.getOriginX(), minion.getOriginY(), effect, DamageType.Fire);
			minion.setHp(-minion.getMaxHp());
		}
		
		if(minions.size() < 1)
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
