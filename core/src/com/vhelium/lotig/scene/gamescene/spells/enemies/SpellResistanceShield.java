package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellResistanceShield extends SpellEnemy
{
	public static String name = "Resistance Shield";
	public static String effectAsset = "Spell Shield";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 0;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int shieldAmountBase = 200;
	public static int shieldAmountPerLevel = 20;
	
	public static int durationBase = 6000;
	public static int durationPerLevel = 200;
	
	public SpellResistanceShield(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = true;
	}
	
	@Override
	public String getName()
	{
		return name;
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
	public void activate(Realm realm, int shooterId, float x, float y, float rotation, float dirX, float dirY)
	{
		int spellLevel = getLevel();
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
