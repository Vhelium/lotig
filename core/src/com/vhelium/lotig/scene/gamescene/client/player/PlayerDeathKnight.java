package com.vhelium.lotig.scene.gamescene.client.player;

import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellBladestorm;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellDeadlyBlow;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellFlameStrike;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellFleshReformation;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellInnerBeast;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellIntimidation;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellMithrilArmor;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellShieldBash;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellSpellShield;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellStunningStrike;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellSunderArmor;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellTaunt;

public class PlayerDeathKnight extends PlayerClient
{
	public PlayerDeathKnight(ClientLevel level, TMXMap tmxMap, DataPacket dpLairs, SceneManager sceneManager)
	{
		super(level, tmxMap, "Death Knight", dpLairs, sceneManager);
		
		bulletAsset = "Death Knight";
		shootSound = "attack1";
		shootRange = 350;
		piercing = false;
		
		spells.put(0, new SpellFleshReformation());
		spells.put(1, new SpellMithrilArmor());
		spells.put(2, new SpellSpellShield());
		spells.put(3, new SpellTaunt());
		spells.put(4, new SpellStunningStrike());
		spells.put(5, new SpellIntimidation());
		spells.put(6, new SpellInnerBeast());
		spells.put(7, new SpellShieldBash());
		spells.put(8, new SpellFlameStrike());
		spells.put(9, new SpellDeadlyBlow());
		spells.put(10, new SpellSunderArmor());
		spells.put(11, new SpellBladestorm());
		
		baseAttributes.put("BONUSDMG", 5);
		
		baseAttributes.put("STR", 120);
		baseAttributes.put("DEX", 100);
		baseAttributes.put("SPD", 100);
		baseAttributes.put("VIT", 140);
		baseAttributes.put("WIS", 60);
		baseAttributes.put("INT", 80);
		
		baseAttributes.put("MAXHP", 3400);
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
