package com.vhelium.lotig.scene.gamescene.client.player;

import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellBlink;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFireball;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFirestorm;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFrostBomb;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFrostNova;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellLivingBomb;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMagicTrick;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellManaBurn;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMeteorStrike;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMoltenShield;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellThunderbolt;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellTimeWarp;

public class PlayerSorcerer extends PlayerClient
{
	public PlayerSorcerer(ClientLevel level, TMXMap tmxMap, DataPacket dpLairs, SceneManager sceneManager)
	{
		super(level, tmxMap, "Sorcerer", dpLairs, sceneManager);
		
		bulletAsset = "Sorcerer";
		shootSound = "attack2";
		shootRange = 380;
		piercing = false;
		
		spells.put(0, new SpellBlink());
		spells.put(1, new SpellFireball());
		spells.put(2, new SpellFrostNova());
		spells.put(3, new SpellMeteorStrike());
		spells.put(4, new SpellFrostBomb());
		spells.put(5, new SpellTimeWarp());
		spells.put(6, new SpellLivingBomb());
		spells.put(7, new SpellMoltenShield());
		spells.put(8, new SpellFirestorm());
		spells.put(9, new SpellManaBurn());
		spells.put(10, new SpellThunderbolt());
		spells.put(11, new SpellMagicTrick());
		
		baseAttributes.put("BONUSDMG", 5);
		
		baseAttributes.put("STR", 60);
		baseAttributes.put("DEX", 80);
		baseAttributes.put("SPD", 100);
		baseAttributes.put("VIT", 100);
		baseAttributes.put("WIS", 140);
		baseAttributes.put("INT", 120);
		
		baseAttributes.put("MAXHP", 2600);
		baseAttributes.put("MAXMANA", 1500);
		
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