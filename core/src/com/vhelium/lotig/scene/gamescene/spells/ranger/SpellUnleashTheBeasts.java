package com.vhelium.lotig.scene.gamescene.spells.ranger;

import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMinion;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellUnleashTheBeasts extends Spell
{
	public static String name = "Unleash The Beasts";
	public static String minionName = "Summoned Beast";
	public static String minionAsset = "Monster1";
	public static String minionBulletAsset = "Deadly Blow";
	
	public static float cooldownBase = 60000;
	public static float cooldownPerLevel = 1500;
	
	public static float manaCostBase = 350;
	public static float manaCostPerLevel = 25;
	
	public static float beastCountBase = 2;
	public static float beastCountPerLevel = 0.2f;
	
	public static int durationBase = 5000;
	public static int durationPerLevel = 250;
	
	public SpellUnleashTheBeasts()
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
		return "You summon multiple beasts of the nature who fight on your side for a short time.\n\nBeast count: " + getBeastCount(getLevel()) + "\nBeast lifetime: " + getDuration(getLevel());
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
		
		for(int i = 0; i < getBeastCount(level); i++)
		{
			EntityServerMinion minion = new EntityServerMinion(realm, owner);
			minion.setMaxHp(100);
			minion.setHp(minion.getMaxHp());
			
			minion.setLevel(level);
			minion.setName(minionName);
			minion.setAsset(minionAsset);
			minion.setBulletAsset(minionBulletAsset);
			minion.setWidth(32);
			minion.setHeight(28);
			minion.setRectangle(new Rectangle(0, 0, 32, 28));
			
			minion.setMaxHp(1000);
			minion.setHp(minion.getMaxHp());
			minion.setMaxHp(minion.getHp());
			minion.setHp(minion.getMaxHp());
			minion.setMaxMana(30);
			minion.setMana(minion.getMaxMana());
			minion.setDamage(15);
			minion.setDamageBonus(5);
			minion.setArmor(1);
			minion.setFireRes(15);
			minion.setColdRes(15);
			minion.setLightningRes(15);
			minion.setBaseSpeed(0.25f);
			minion.setShootSpeed(400);
			minion.setDetectRange(800);
			minion.setShootRange(200);
			minion.setPiercing(false);
			
			minion.setLifeTime(getDuration(level));
			minion.setX(x);
			minion.setY(y);
			
			realm.addMinion(owner, minion);
			minion.onSummonned();
		}
		
		realm.playSound(SoundFile.spell_beasts, realm.getPlayer(shooterId).getOriginX(), realm.getPlayer(shooterId).getOriginY());
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static int getBeastCount(int level)
	{
		return (int) (beastCountBase + beastCountPerLevel * (level - 1));
	}
}
