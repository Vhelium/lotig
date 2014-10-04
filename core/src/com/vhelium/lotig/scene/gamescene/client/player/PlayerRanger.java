package com.vhelium.lotig.scene.gamescene.client.player;

import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellBarrage;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellColdTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellDeadlyShot;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellEvasion;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellExplosiveShot;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellFireTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellLightningTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellMultiShot;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellPathfinder;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellPoisonousArrow;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellSpikeTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellUnleashTheBeasts;

public class PlayerRanger extends PlayerClient
{
	public PlayerRanger(ClientLevel level, TMXMap tmxMap, DataPacket dpLairs, SceneManager sceneManager)
	{
		super(level, tmxMap, "Ranger", dpLairs, sceneManager);
		
		bulletAsset = "Ranger";
		shootSound = "attack2";
		shootRange = 450;
		piercing = true;
		
		spells.put(0, new SpellFireTrap());
		spells.put(1, new SpellColdTrap());
		spells.put(2, new SpellLightningTrap());
		spells.put(3, new SpellSpikeTrap());
		spells.put(4, new SpellEvasion());
		spells.put(5, new SpellExplosiveShot());
		spells.put(6, new SpellMultiShot());
		spells.put(7, new SpellDeadlyShot());
		spells.put(8, new SpellPoisonousArrow());
		spells.put(9, new SpellPathfinder());
		spells.put(10, new SpellUnleashTheBeasts());
		spells.put(11, new SpellBarrage());
		
		baseAttributes.put("BONUSDMG", 5);
		
		baseAttributes.put("STR", 120);
		baseAttributes.put("DEX", 140);
		baseAttributes.put("SPD", 100);
		baseAttributes.put("VIT", 80);
		baseAttributes.put("WIS", 80);
		baseAttributes.put("INT", 80);
		
		baseAttributes.put("MAXHP", 3000);
		baseAttributes.put("MAXMANA", 1100);
		
		baseAttributes.put("ARMOR", 0);
		
		baseAttributes.put("FRES", 0);
		baseAttributes.put("CRES", 0);
		baseAttributes.put("LRES", 0);
		baseAttributes.put("PRES", 0);
		
		baseAttributes.put("LPH", 0);
		baseAttributes.put("MHP", 0);
		
		baseAttributes.put("CDR", 0);
	}
}
