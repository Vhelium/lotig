package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellEnhancedDefense extends SpellEnemy
{
	public static String name = "Enhanced Defense";
	public static String effectAsset = "Enhanced Defense Up";
	
	public static float cooldownBase = 30000;
	public static float cooldownPerLevel = 100f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int shieldAmountBase = 700;
	public static int shieldAmountPerLevel = 60;
	
	public static int durationBase = 20000;
	public static int durationPerLevel = 100;
	
	public SpellEnhancedDefense(int priority, HashMap<Integer, Float> conditions)
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
		realm.playEffect(shooterId, effectAsset, 0);
		realm.playSound(shooterId, SoundFile.spell_shieldup_defense);
		realm.requestCondition(shooterId, name, "ARMOR", getShieldAmount(getLevel()), getDuration(getLevel()), true, "", System.currentTimeMillis());
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
