package com.vhelium.lotig.scene.gamescene.client.player;

import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellBlastSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellDauntingGhost;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEarthquake;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEnrageSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEntangle;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellFlameBreath;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellHealSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellIceDart;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellMeditationAura;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellOvercharge;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellSummonSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellThornArmor;

public class PlayerDruid extends PlayerClient
{
	public PlayerDruid(ClientLevel level, TMXMap tmxMap, DataPacket dpLairs, SceneManager sceneManager)
	{
		super(level, tmxMap, "Druid", dpLairs, sceneManager);
		
		bulletAsset = "Druid";
		shootSound = "attack2";
		shootRange = 450;
		piercing = false;
		
		spells.put(0, new SpellSummonSpirit());
		spells.put(1, new SpellHealSpirit());
		spells.put(2, new SpellEnrageSpirit());
		spells.put(3, new SpellBlastSpirit());
		
		spells.put(4, new SpellEarthquake());
		spells.put(5, new SpellIceDart());
		spells.put(6, new SpellFlameBreath());
		spells.put(7, new SpellEntangle());
		
		spells.put(8, new SpellMeditationAura());
		spells.put(9, new SpellOvercharge());
		spells.put(10, new SpellDauntingGhost());
		spells.put(11, new SpellThornArmor());
		
		baseAttributes.put("BONUSDMG", 5);
		
		baseAttributes.put("STR", 80);
		baseAttributes.put("DEX", 80);
		baseAttributes.put("SPD", 100);
		baseAttributes.put("VIT", 80);
		baseAttributes.put("WIS", 120);
		baseAttributes.put("INT", 140);
		
		baseAttributes.put("MAXHP", 2800);
		baseAttributes.put("MAXMANA", 1200);
		
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
