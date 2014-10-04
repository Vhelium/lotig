package com.vhelium.lotig.scene.gamescene.client.player;

import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellBloodthirst;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCharge;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCleave;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCriticalStrike;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellExtendedBloodthirst;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellExtendedRage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellFrostStrike;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellHaste;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellRage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellRavage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellTerrifyingShout;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellThunderingRoar;

public class PlayerBarbarian extends PlayerClient
{
	public PlayerBarbarian(ClientLevel level, TMXMap tmxMap, DataPacket dpLairs, SceneManager sceneManager)
	{
		super(level, tmxMap, "Barbarian", dpLairs, sceneManager);
		
		bulletAsset = "Barbarian";
		shootSound = "attack1";
		shootRange = 350;
		piercing = false;
		
		spells.put(0, new SpellRage());
		spells.put(1, new SpellExtendedRage());
		spells.put(2, new SpellBloodthirst());
		spells.put(3, new SpellExtendedBloodthirst());
		spells.put(4, new SpellRavage());
		spells.put(5, new SpellTerrifyingShout());
		spells.put(6, new SpellFrostStrike());
		spells.put(7, new SpellHaste());
		spells.put(8, new SpellThunderingRoar());
		spells.put(9, new SpellCleave());
		spells.put(10, new SpellCriticalStrike());
		spells.put(11, new SpellCharge());
		
		baseAttributes.put("BONUSDMG", 5);
		
		baseAttributes.put("STR", 140);
		baseAttributes.put("DEX", 100);
		baseAttributes.put("SPD", 100);
		baseAttributes.put("VIT", 120);
		baseAttributes.put("WIS", 80);
		baseAttributes.put("INT", 60);
		
		baseAttributes.put("MAXHP", 3200);
		baseAttributes.put("MAXMANA", 1000);
		
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
