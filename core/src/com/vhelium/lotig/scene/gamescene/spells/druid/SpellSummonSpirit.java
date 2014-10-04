package com.vhelium.lotig.scene.gamescene.spells.druid;

import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMinion;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellSummonSpirit extends Spell
{
	public static String name = "Summon Spirit";
	public static String minionName = "Summoned Spirit";
	public static String minionAsset = "Monster1";
	public static String minionBulletAsset = "Frost Strike";
	
	public static float cooldownBase = 25000;
	public static float cooldownPerLevel = 1000;
	
	public static float manaCostBase = 400;
	public static float manaCostPerLevel = 30;
	
	public SpellSummonSpirit()
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
		return "You summon a spirit of the nature and let him fight at your side.\n\nSpirit damage: xx" + "\nSpirit health: xx" + "\nDuration: xx" + "";
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
		EntityServerMixin owner = realm.getEntity(shooterId);
		if(owner == null)
			return;
		
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		
		EntityServerMinion minion = new EntityServerMinion(realm, owner);
		
		minion.setLevel(level);
		minion.setName(minionName);
		minion.setAsset(minionAsset);
		minion.setBulletAsset(minionBulletAsset);
		minion.setWidth(32);
		minion.setHeight(28);
		minion.setRectangle(new Rectangle(0, 0, 32, 28));
		
		minion.setMaxHp(600);
		minion.setHp(minion.getMaxHp());
		minion.setMaxHp(minion.getHp());
		minion.setHp(minion.getMaxHp());
		minion.setMaxMana(30);
		minion.setMana(minion.getMaxMana());
		minion.setDamage(50);
		minion.setDamageBonus(5);
		minion.setArmor(50);
		minion.setFireRes(15);
		minion.setColdRes(15);
		minion.setLightningRes(15);
		minion.setBaseSpeed(0.15f);
		minion.setShootSpeed(1000);
		minion.setDetectRange(800);
		minion.setShootRange(400);
		minion.setPiercing(false);
		
		minion.setLifeTime(20000f);
		minion.setX(x);//TODO: randomize
		minion.setY(y);//TODO: randomize
		
		realm.addMinion(owner, minion);
		minion.onSummonned();
		
		realm.playSound(SoundFile.spell_summon, owner.getOriginX(), owner.getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
}
