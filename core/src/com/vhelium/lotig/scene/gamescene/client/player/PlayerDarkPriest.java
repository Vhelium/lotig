package com.vhelium.lotig.scene.gamescene.client.player;

import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellAbsorb;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellArcaneExplosions;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellBondOfDebt;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellClutchOfDeath;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellEquilibrium;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellHellFire;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellLightwell;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellMassDispel;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellMassHeal;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellShadowCurse;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellSilence;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellVampiricTouch;

public class PlayerDarkPriest extends PlayerClient
{
	public PlayerDarkPriest(ClientLevel level, TMXMap tmxMap, DataPacket dpLairs, SceneManager sceneManager)
	{
		super(level, tmxMap, "Dark Priest", dpLairs, sceneManager);
		
		bulletAsset = "Dark Priest";
		shootSound = "attack2";
		shootRange = 400;
		piercing = false;
		
		spells.put(0, new SpellAbsorb());
		spells.put(1, new SpellMassHeal());
		spells.put(2, new SpellVampiricTouch());
		spells.put(3, new SpellShadowCurse());
		spells.put(4, new SpellEquilibrium());
		spells.put(5, new SpellArcaneExplosions());
		spells.put(6, new SpellMassDispel());
		spells.put(7, new SpellSilence());
		spells.put(8, new SpellClutchOfDeath());
		spells.put(9, new SpellBondOfDebt());
		spells.put(10, new SpellHellFire());
		spells.put(11, new SpellLightwell());
		
		baseAttributes.put("BONUSDMG", 5);
		
		baseAttributes.put("STR", 80);
		baseAttributes.put("DEX", 80);
		baseAttributes.put("SPD", 100);
		baseAttributes.put("VIT", 80);
		baseAttributes.put("WIS", 140);
		baseAttributes.put("INT", 120);
		
		baseAttributes.put("MAXHP", 2800);
		baseAttributes.put("MAXMANA", 1300);
		
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
