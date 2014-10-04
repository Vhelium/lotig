package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;

public class SpellLightningSurroundShot extends SpellSurroundShot
{
	public static String name = "Lightning Surround Shot";
	
	public SpellLightningSurroundShot(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		bulletAsset = "Lightning Shot";
		castSound = SoundFile.spell_bubbles;
		
		cooldownBase = 5000;
		cooldownPerLevel = 0;
		
		manaCostBase = 0;
		manaCostPerLevel = 0;
		
		damageBase = 200;
		damagePerLevel = 15;
		
		damageBonusBase = 50;
		damageBonusPerLevel = 10;
		
		rangeBase = 140;
		rangePerLevel = 2;
		
		shotCount = 9;
		
		damageType = DamageType.Lightning;
		piercing = false;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
}
