package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;

public class SpellWaterSurroundShot extends SpellSurroundShot
{
	public static String name = "Water Surround Shot";
	
	public SpellWaterSurroundShot(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		bulletAsset = "Water Shot";
		castSound = SoundFile.spell_bubbles;
		
		cooldownBase = 5000;
		cooldownPerLevel = 0;
		
		manaCostBase = 0;
		manaCostPerLevel = 0;
		
		damageBase = 100;
		damagePerLevel = 20;
		
		damageBonusBase = 10;
		damageBonusPerLevel = 20;
		
		rangeBase = 180;
		rangePerLevel = 2;
		
		shotCount = 8;
		
		damageType = DamageType.Cold;
		piercing = false;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
}
