package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Trap;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellPoisonTrap extends SpellEnemy
{
	public static String name = "Poison Trap";
	public static String asset = "poison_trap";
	public static String effectAsset = "Poison Trap";
	public static String soundEffect = SoundFile.spell_poison_bomb;
	
	private static boolean animated = false;
	private static int animationTime = 500;
	
	public static float cooldownBase = 30000f;
	public static float cooldownPerLevel = -600f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 100;
	
	public static float damageBase = 100;
	public static float damagePerLevel = 10;
	
	public static float damageBonusBase = 50;
	public static float damageBonusPerLevel = 5;
	
	public static int rangeBase = 100;
	public static int rangePerLevel = 0;
	
	public static final int width = 20;
	public static final int height = 20;
	
	public static final int burnDuration = 4100;
	
	public static float burningBase = 180;
	public static float burningPerLevel = 25;
	
	public SpellPoisonTrap(int priority, HashMap<Integer, Float> conditions)
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
		if(realm.getPlayer(shooterId) == null)
			return;
		int level = getLevel();
		
		realm.addLevelObjectAtRuntime(new LevelObject_Trap(-1, realm, new Rectangle(x - width / 2, y - height / 2, width, height), asset, soundEffect, animated, animationTime, effectAsset, getDuration(level), realm.getEntity(shooterId).getTeamNr(), getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), getRange(level), DamageType.Poison, new Condition(UniqueCondition.Poisoned, UniqueCondition.Poisoned, (int) getBurning(level), getDuration(level), false, UniqueCondition.Poisoned, 0)));
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
	}
	
	public static float getDamage(int level)
	{
		return damageBase + damagePerLevel * (level - 1);
	}
	
	public static float getBonusDamage(int level)
	{
		return damageBonusBase + damageBonusPerLevel * (level - 1);
	}
	
	public static int getRange(int level)
	{
		return rangeBase + rangePerLevel * (level - 1);
	}
	
	public static float getBurning(int level)
	{
		return burningBase + burningPerLevel * (level - 1);
	}
}
