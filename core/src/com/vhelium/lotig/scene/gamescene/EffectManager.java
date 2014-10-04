package com.vhelium.lotig.scene.gamescene;

import java.util.HashMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellBloodthirst;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCleave;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellFrostStrike;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellRage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellRavage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellTerrifyingShout;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellThunderingRoar;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellAbsorb;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellArcaneExplosions;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellBondOfDebt;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellClutchOfDeath;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellHellFire;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellMassDispel;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellMassHeal;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellShadowCurse;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellBladestorm;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellFleshReformation;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellInnerBeast;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellIntimidation;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellMithrilArmor;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellSpellShield;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellTaunt;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellBlastSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEarthquake;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEntangle;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellMeditationAura;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellOvercharge;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellThornArmor;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellEarthBall;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellEnhancedDefense;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellFlameShield;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellFlameSlam;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellGroundSlam;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellMonsterMassWeakening;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellPoisonBombs;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellPoisonTrap;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellSlowAoE;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellColdTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellFireTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellLightningTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellPathfinder;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellSpikeTrap;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFireball;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFirestorm;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFrostBomb;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFrostNova;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellLivingBomb;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMagicTrick;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellManaBurn;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMeteorStrike;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMoltenShield;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellTimeWarp;

public class EffectManager
{
	Main activity;
	private final HashMap<String, TiledTextureRegion> effectAssets;
	private final HashMap<String, Long> effectAnimationTimes;
	
	public EffectManager(Main activity)
	{
		this.activity = activity;
		effectAssets = new HashMap<String, TiledTextureRegion>();
		effectAnimationTimes = new HashMap<String, Long>();
	}
	
	public void loadEffects()
	{
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("gfx/packs/effects.atlas"));
		loadEffect(atlas, "Frozen", 2, 1, 120L);
		loadEffect(atlas, UniqueCondition.Stunned, 2, 1, 120L);
		loadEffect(atlas, UniqueCondition.Burning, 2, 1, 110L);
		loadEffect(atlas, "Healed", 3, 1, 120L);
		loadEffect(atlas, UniqueCondition.Poisoned, 2, 1, 110L);
		loadEffect(atlas, "Hp Pot", 2, 1, 150L);
		loadEffect(atlas, "Mana Pot", 2, 1, 150L);
		loadEffect(atlas, UniqueCondition.Silenced, 2, 1, 150L);
		loadEffect(atlas, SpellFireball.effectAsset, 4, 1, 120L);
		loadEffect(atlas, SpellAbsorb.effectAsset, 4, 1, 120L);
		loadEffect(atlas, SpellAbsorb.buffAsset, 2, 1, 150L);
		loadEffect(atlas, SpellAbsorb.debuffAsset, 2, 1, 150L);
		loadEffect(atlas, SpellSpellShield.effectAsset, 2, 1, 150L);
		loadEffect(atlas, SpellMithrilArmor.effectAsset, 2, 1, 150L);
		loadEffect(atlas, SpellTaunt.effectAsset, 4, 1, 100L);
		loadEffect(atlas, SpellIntimidation.effectAsset, 4, 1, 50L);
		loadEffect(atlas, SpellInnerBeast.effectAsset, 2, 1, 120L);
		loadEffect(atlas, SpellFleshReformation.effectAsset, 3, 1, 110L);
		loadEffect(atlas, SpellRage.effectAsset, 2, 1, 80L);
		loadEffect(atlas, SpellBloodthirst.effectAsset, 2, 1, 100L);
		loadEffect(atlas, SpellRavage.effectAsset, 2, 1, 90L);
		loadEffect(atlas, SpellTerrifyingShout.effectAsset, 4, 1, 50L);
		loadEffect(atlas, SpellFrostStrike.effectAsset, 4, 1, 100L);
		loadEffect(atlas, SpellThunderingRoar.effectAsset, 4, 1, 60L);
		loadEffect(atlas, SpellCleave.effectAsset, 5, 1, 55L);
		loadEffect(atlas, SpellMassHeal.effectAsset, 4, 1, 120L);
		loadEffect(atlas, SpellEarthquake.effectAsset, 4, 1, 130L);
		loadEffect(atlas, SpellEntangle.effectAsset, 3, 1, 110L);
		loadEffect(atlas, SpellMeditationAura.effectAsset, 4, 1, 100L);
		loadEffect(atlas, SpellThornArmor.effectAsset, 2, 1, 120L);
		loadEffect(atlas, SpellOvercharge.effectAsset, 4, 1, 70L);
		loadEffect(atlas, SpellBlastSpirit.effectAsset, 4, 1, 90L);
		loadEffect(atlas, SpellShadowCurse.effectAsset, 2, 1, 80L);
		loadEffect(atlas, SpellArcaneExplosions.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellMassDispel.effectAsset, 3, 1, 100L);
		loadEffect(atlas, SpellClutchOfDeath.debuffAsset, 3, 1, 100L);
		loadEffect(atlas, SpellClutchOfDeath.effectAsset, 4, 1, 100L);
		loadEffect(atlas, SpellHellFire.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellBondOfDebt.debuffAsset, 2, 1, 150L);
		loadEffect(atlas, SpellBondOfDebt.effectAsset, 2, 1, 150L);
		loadEffect(atlas, SpellBladestorm.effectAsset, 5, 1, 60L);
		loadEffect(atlas, SpellFireTrap.effectAsset, 4, 1, 120L);
		loadEffect(atlas, SpellLightningTrap.effectAsset, 4, 1, 120L);
		loadEffect(atlas, SpellSpikeTrap.effectAsset, 4, 1, 120L);
		loadEffect(atlas, SpellColdTrap.effectAsset, 4, 1, 120L);
		loadEffect(atlas, SpellPathfinder.effectAsset, 2, 1, 150L);
		loadEffect(atlas, SpellFrostNova.effectAsset, 4, 1, 100L);
		loadEffect(atlas, SpellMeteorStrike.effectAsset, 5, 1, 60L);
		loadEffect(atlas, SpellFrostBomb.effectAsset, 4, 1, 100L);
		loadEffect(atlas, SpellTimeWarp.effectAsset, 4, 1, 70L);
		loadEffect(atlas, SpellLivingBomb.debuffAsset, 2, 1, 80L);
		loadEffect(atlas, SpellLivingBomb.effectAsset, 4, 1, 100L);
		loadEffect(atlas, SpellMoltenShield.effectAsset, 2, 1, 150L);
		loadEffect(atlas, SpellFirestorm.effectAsset, 4, 1, 125L);
		loadEffect(atlas, SpellManaBurn.effectAsset, 2, 1, 110L);
		loadEffect(atlas, SpellMagicTrick.effectAsset, 4, 1, 80L);
		loadEffect(atlas, SpellEarthBall.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellFlameSlam.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellGroundSlam.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellPoisonBombs.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellPoisonTrap.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellEnhancedDefense.effectAsset, 4, 1, 110L);
		loadEffect(atlas, SpellFlameShield.effectAsset, 2, 1, 150L);
		loadEffect(atlas, SpellSlowAoE.effectAsset, 4, 1, 650L);
		loadEffect(atlas, SpellMonsterMassWeakening.effectAsset, 2, 1, 150L);
		loadEffect(atlas, "Spike Ring", 4, 1, 80L);
		loadEffect(atlas, "Spike Ring Warning", 2, 1, 200L);
		loadEffect(atlas, "shrine_atkspd", 2, 1, 330L);
		loadEffect(atlas, "shrine_spd", 2, 1, 330L);
		loadEffect(atlas, "shrine_def", 2, 1, 330L);
		loadEffect(atlas, "shrine_atk", 2, 1, 330L);
		loadEffect(atlas, "shrine_manareg", 2, 1, 330L);
		loadEffect(atlas, "shrine_hpreg", 2, 1, 330L);
		loadEffect(atlas, "AoeAreaWarning", 4, 1, 100L);
		loadEffect(atlas, "Spike Trap small", 6, 1, 65L);
		loadEffect(atlas, "da_blood", 6, 1, 80L);
		loadEffect(atlas, "da_player", 6, 1, 80L);
	}
	
	private void loadEffect(TextureAtlas atlas, String name, int x, int y, long animTime)
	{
		AtlasRegion reg = atlas.findRegion(name);
		if(reg == null)
			Log.e("EffectManager", "Effect not found: " + name);
		effectAssets.put(name, new TiledTextureRegion(reg, x, y, true));
		effectAnimationTimes.put(name, animTime);
	}
	
	public TiledTextureRegion getEffectAsset(String name)
	{
		return effectAssets.get(name);
	}
	
	public long getAnimationTime(String name)
	{
		return effectAnimationTimes.get(name);
	}
}
