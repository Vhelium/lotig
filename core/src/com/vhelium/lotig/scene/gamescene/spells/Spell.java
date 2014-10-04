package com.vhelium.lotig.scene.gamescene.spells;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
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

public abstract class Spell
{
	public boolean instantCast;
	public float cooldownLeft;
	private int level = 0;
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public void levelUp()
	{
		level++;
	}
	
	public abstract String getDescription();
	
	public abstract String getName();
	
	public abstract DataPacket generateRequest(float x, float y, float rotation, float dirX, float dirY);
	
	public abstract float getManaCost();
	
	public abstract float getCooldown();
	
	public String getCastSound()
	{
		return null;
	}
	
	public static void activateSpell(String name, int spellLevel, EntityServerMixin entity, int entityNr, DataPacket dp, Realm realm)
	{
		if(name.equalsIgnoreCase(SpellFireball.name))
		{
			entity.setMana(entity.getMana() - SpellFireball.getManaCostStatic(spellLevel));
			SpellFireball.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellAbsorb.name))
		{
			entity.setMana(entity.getMana() - SpellAbsorb.getManaCostStatic(spellLevel));
			SpellAbsorb.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellBlink.name))
		{
			entity.setMana(entity.getMana() - SpellBlink.getManaCostStatic(spellLevel));
			SpellBlink.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMithrilArmor.name))
		{
			entity.setMana(entity.getMana() - SpellMithrilArmor.getManaCostStatic(spellLevel));
			SpellMithrilArmor.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellSpellShield.name))
		{
			entity.setMana(entity.getMana() - SpellSpellShield.getManaCostStatic(spellLevel));
			SpellSpellShield.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellTaunt.name))
		{
			entity.setMana(entity.getMana() - SpellTaunt.getManaCostStatic(spellLevel));
			SpellTaunt.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellStunningStrike.name))
		{
			entity.setMana(entity.getMana() - SpellStunningStrike.getManaCostStatic(spellLevel));
			SpellStunningStrike.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellIntimidation.name))
		{
			entity.setMana(entity.getMana() - SpellIntimidation.getManaCostStatic(spellLevel));
			SpellIntimidation.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellInnerBeast.name))
		{
			entity.setMana(entity.getMana() - SpellInnerBeast.getManaCostStatic(spellLevel));
			SpellInnerBeast.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellShieldBash.name))
		{
			entity.setMana(entity.getMana() - SpellShieldBash.getManaCostStatic(spellLevel));
			SpellShieldBash.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFlameStrike.name))
		{
			entity.setMana(entity.getMana() - SpellFlameStrike.getManaCostStatic(spellLevel));
			SpellFlameStrike.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellDeadlyBlow.name))
		{
			entity.setMana(entity.getMana() - SpellDeadlyBlow.getManaCostStatic(spellLevel));
			SpellDeadlyBlow.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellSunderArmor.name))
		{
			entity.setMana(entity.getMana() - SpellSunderArmor.getManaCostStatic(spellLevel));
			SpellSunderArmor.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFleshReformation.name))
		{
			entity.setMana(entity.getMana() - SpellFleshReformation.getManaCostStatic(spellLevel));
			SpellFleshReformation.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellRage.name))
		{
			entity.setMana(entity.getMana() - SpellRage.getManaCostStatic(spellLevel));
			SpellRage.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellExtendedRage.name))
		{
			entity.setMana(entity.getMana() - SpellExtendedRage.getManaCostStatic(spellLevel));
			SpellExtendedRage.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellBloodthirst.name))
		{
			entity.setMana(entity.getMana() - SpellBloodthirst.getManaCostStatic(spellLevel));
			SpellBloodthirst.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellExtendedBloodthirst.name))
		{
			entity.setMana(entity.getMana() - SpellExtendedBloodthirst.getManaCostStatic(spellLevel));
			SpellExtendedBloodthirst.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellRavage.name))
		{
			entity.setMana(entity.getMana() - SpellRavage.getManaCostStatic(spellLevel));
			SpellRavage.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellTerrifyingShout.name))
		{
			entity.setMana(entity.getMana() - SpellTerrifyingShout.getManaCostStatic(spellLevel));
			SpellTerrifyingShout.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFrostStrike.name))
		{
			entity.setMana(entity.getMana() - SpellFrostStrike.getManaCostStatic(spellLevel));
			SpellFrostStrike.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellHaste.name))
		{
			entity.setMana(entity.getMana() - SpellHaste.getManaCostStatic(spellLevel));
			SpellHaste.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellThunderingRoar.name))
		{
			entity.setMana(entity.getMana() - SpellThunderingRoar.getManaCostStatic(spellLevel));
			SpellThunderingRoar.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellCleave.name))
		{
			entity.setMana(entity.getMana() - SpellCleave.getManaCostStatic(spellLevel));
			SpellCleave.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellCriticalStrike.name))
		{
			entity.setMana(entity.getMana() - SpellCriticalStrike.getManaCostStatic(spellLevel));
			SpellCriticalStrike.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMassHeal.name))
		{
			entity.setMana(entity.getMana() - SpellMassHeal.getManaCostStatic(spellLevel));
			SpellMassHeal.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellSummonSpirit.name))
		{
			entity.setMana(entity.getMana() - SpellSummonSpirit.getManaCostStatic(spellLevel));
			SpellSummonSpirit.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellHealSpirit.name))
		{
			entity.setMana(entity.getMana() - SpellHealSpirit.getManaCostStatic(spellLevel));
			SpellHealSpirit.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellEnrageSpirit.name))
		{
			entity.setMana(entity.getMana() - SpellEnrageSpirit.getManaCostStatic(spellLevel));
			SpellEnrageSpirit.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellEarthquake.name))
		{
			entity.setMana(entity.getMana() - SpellEarthquake.getManaCostStatic(spellLevel));
			SpellEarthquake.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellIceDart.name))
		{
			entity.setMana(entity.getMana() - SpellIceDart.getManaCostStatic(spellLevel));
			SpellIceDart.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFlameBreath.name))
		{
			entity.setMana(entity.getMana() - SpellFlameBreath.getManaCostStatic(spellLevel));
			SpellFlameBreath.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellEntangle.name))
		{
			entity.setMana(entity.getMana() - SpellEntangle.getManaCostStatic(spellLevel));
			SpellEntangle.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMeditationAura.name))
		{
			entity.setMana(entity.getMana() - SpellMeditationAura.getManaCostStatic(spellLevel));
			SpellMeditationAura.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellDauntingGhost.name))
		{
			entity.setMana(entity.getMana() - SpellDauntingGhost.getManaCostStatic(spellLevel));
			SpellDauntingGhost.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellOvercharge.name))
		{
			entity.setMana(entity.getMana() - SpellOvercharge.getManaCostStatic(spellLevel));
			SpellOvercharge.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellThornArmor.name))
		{
			entity.setMana(entity.getMana() - SpellThornArmor.getManaCostStatic(spellLevel));
			SpellThornArmor.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellBlastSpirit.name))
		{
			entity.setMana(entity.getMana() - SpellBlastSpirit.getManaCostStatic(spellLevel));
			SpellBlastSpirit.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellCharge.name))
		{
			entity.setMana(entity.getMana() - SpellCharge.getManaCostStatic(spellLevel));
			SpellCharge.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellVampiricTouch.name))
		{
			entity.setMana(entity.getMana() - SpellVampiricTouch.getManaCostStatic(spellLevel));
			SpellVampiricTouch.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellShadowCurse.name))
		{
			entity.setMana(entity.getMana() - SpellShadowCurse.getManaCostStatic(spellLevel));
			SpellShadowCurse.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellEquilibrium.name))
		{
			entity.setMana(entity.getMana() - SpellEquilibrium.getManaCostStatic(spellLevel));
			SpellEquilibrium.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellArcaneExplosions.name))
		{
			entity.setMana(entity.getMana() - SpellArcaneExplosions.getManaCostStatic(spellLevel));
			SpellArcaneExplosions.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMassDispel.name))
		{
			entity.setMana(entity.getMana() - SpellMassDispel.getManaCostStatic(spellLevel));
			SpellMassDispel.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellSilence.name))
		{
			entity.setMana(entity.getMana() - SpellSilence.getManaCostStatic(spellLevel));
			SpellSilence.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellClutchOfDeath.name))
		{
			entity.setMana(entity.getMana() - SpellClutchOfDeath.getManaCostStatic(spellLevel));
			SpellClutchOfDeath.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellBondOfDebt.name))
		{
			entity.setMana(entity.getMana() - SpellBondOfDebt.getManaCostStatic(spellLevel));
			SpellBondOfDebt.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellHellFire.name))
		{
			entity.setMana(entity.getMana() - SpellHellFire.getManaCostStatic(spellLevel));
			SpellHellFire.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellLightwell.name))
		{
			entity.setMana(entity.getMana() - SpellLightwell.getManaCostStatic(spellLevel));
			SpellLightwell.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellBladestorm.name))
		{
			entity.setMana(entity.getMana() - SpellBladestorm.getManaCostStatic(spellLevel));
			SpellBladestorm.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellExplosiveShot.name))
		{
			entity.setMana(entity.getMana() - SpellExplosiveShot.getManaCostStatic(spellLevel));
			SpellExplosiveShot.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMultiShot.name))
		{
			entity.setMana(entity.getMana() - SpellMultiShot.getManaCostStatic(spellLevel));
			SpellMultiShot.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFireTrap.name))
		{
			entity.setMana(entity.getMana() - SpellFireTrap.getManaCostStatic(spellLevel));
			SpellFireTrap.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellColdTrap.name))
		{
			entity.setMana(entity.getMana() - SpellColdTrap.getManaCostStatic(spellLevel));
			SpellColdTrap.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellLightningTrap.name))
		{
			entity.setMana(entity.getMana() - SpellLightningTrap.getManaCostStatic(spellLevel));
			SpellLightningTrap.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellSpikeTrap.name))
		{
			entity.setMana(entity.getMana() - SpellSpikeTrap.getManaCostStatic(spellLevel));
			SpellSpikeTrap.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellEvasion.name))
		{
			entity.setMana(entity.getMana() - SpellEvasion.getManaCostStatic(spellLevel));
			SpellEvasion.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellDeadlyShot.name))
		{
			entity.setMana(entity.getMana() - SpellDeadlyShot.getManaCostStatic(spellLevel));
			SpellDeadlyShot.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellPoisonousArrow.name))
		{
			entity.setMana(entity.getMana() - SpellPoisonousArrow.getManaCostStatic(spellLevel));
			SpellPoisonousArrow.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellPathfinder.name))
		{
			entity.setMana(entity.getMana() - SpellPathfinder.getManaCostStatic(spellLevel));
			SpellPathfinder.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellUnleashTheBeasts.name))
		{
			entity.setMana(entity.getMana() - SpellUnleashTheBeasts.getManaCostStatic(spellLevel));
			SpellUnleashTheBeasts.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellBarrage.name))
		{
			entity.setMana(entity.getMana() - SpellBarrage.getManaCostStatic(spellLevel));
			SpellBarrage.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFrostNova.name))
		{
			entity.setMana(entity.getMana() - SpellFrostNova.getManaCostStatic(spellLevel));
			SpellFrostNova.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMeteorStrike.name))
		{
			entity.setMana(entity.getMana() - SpellMeteorStrike.getManaCostStatic(spellLevel));
			SpellMeteorStrike.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFrostBomb.name))
		{
			entity.setMana(entity.getMana() - SpellFrostBomb.getManaCostStatic(spellLevel));
			SpellFrostBomb.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellTimeWarp.name))
		{
			entity.setMana(entity.getMana() - SpellTimeWarp.getManaCostStatic(spellLevel));
			SpellTimeWarp.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellLivingBomb.name))
		{
			entity.setMana(entity.getMana() - SpellLivingBomb.getManaCostStatic(spellLevel));
			SpellLivingBomb.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMoltenShield.name))
		{
			entity.setMana(entity.getMana() - SpellMoltenShield.getManaCostStatic(spellLevel));
			SpellMoltenShield.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellFirestorm.name))
		{
			entity.setMana(entity.getMana() - SpellFirestorm.getManaCostStatic(spellLevel));
			SpellFirestorm.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellManaBurn.name))
		{
			entity.setMana(entity.getMana() - SpellManaBurn.getManaCostStatic(spellLevel));
			SpellManaBurn.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellThunderbolt.name))
		{
			entity.setMana(entity.getMana() - SpellThunderbolt.getManaCostStatic(spellLevel));
			SpellThunderbolt.activate(entityNr, spellLevel, dp, realm);
		}
		else if(name.equalsIgnoreCase(SpellMagicTrick.name))
		{
			entity.setMana(entity.getMana() - SpellMagicTrick.getManaCostStatic(spellLevel));
			SpellMagicTrick.activate(entityNr, spellLevel, dp, realm);
		}
	}
}
