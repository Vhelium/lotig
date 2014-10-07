package com.vhelium.lotig.scene.gamescene.client;

import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipBoots;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipBracer;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipChest;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipGlove;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipHelmet;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipPants;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipShoulder;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.HealthPotion;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.ManaPotion;
import com.vhelium.lotig.scene.gamescene.client.items.jewelry.EquipAmulet;
import com.vhelium.lotig.scene.gamescene.client.items.jewelry.EquipRing;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipCharm;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipFocus;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipInsignia;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipShield;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipTome;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipTrophy;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipBow;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipBattleAxe;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipLongsword;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipRod;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipScepter;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipWand;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public abstract class TestRangeUtility
{
	public static void preparePlayer(PlayerClient player, int rangeNr)
	{
		player.getInventory().useItem(EquipShoulder.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipHelmet.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipAmulet.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipBracer.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipChest.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipRing.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipRing.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipGlove.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipPants.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		player.getInventory().useItem(EquipBoots.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
		
		if(player.getPlayerClass().equalsIgnoreCase("Barbarian"))
		{
			player.getInventory().useItem(EquipBattleAxe.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().useItem(EquipInsignia.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().clearItems();
			player.getInventory().addItem(EquipBattleAxe.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
			player.getInventory().addItem(EquipBattleAxe.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
		}
		else if(player.getPlayerClass().equalsIgnoreCase("Dark Priest"))
		{
			player.getInventory().useItem(EquipScepter.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().useItem(EquipTome.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().clearItems();
			player.getInventory().addItem(EquipScepter.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
			player.getInventory().addItem(EquipScepter.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
		}
		else if(player.getPlayerClass().equalsIgnoreCase("Death Knight"))
		{
			player.getInventory().useItem(EquipLongsword.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().useItem(EquipShield.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().clearItems();
			player.getInventory().addItem(EquipLongsword.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
			player.getInventory().addItem(EquipLongsword.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
		}
		else if(player.getPlayerClass().equalsIgnoreCase("Druid"))
		{
			player.getInventory().useItem(EquipRod.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().useItem(EquipCharm.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().clearItems();
			player.getInventory().addItem(EquipRod.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
			player.getInventory().addItem(EquipRod.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
		}
		else if(player.getPlayerClass().equalsIgnoreCase("Ranger"))
		{
			player.getInventory().useItem(EquipBow.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().useItem(EquipTrophy.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().clearItems();
			player.getInventory().addItem(EquipBow.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
			player.getInventory().addItem(EquipBow.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
		}
		else if(player.getPlayerClass().equalsIgnoreCase("Sorcerer"))
		{
			player.getInventory().useItem(EquipWand.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().useItem(EquipFocus.randomEquip(rangeNr, player.getLevel().getDifficulty()), false);
			player.getInventory().clearItems();
			player.getInventory().addItem(EquipWand.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
			player.getInventory().addItem(EquipWand.randomEquip(rangeNr, player.getLevel().getDifficulty()), true, false);
		}
		else
			player.getInventory().clearItems();
		
		player.getInventory().addItem(HealthPotion.getPotion(rangeNr, 4), true, false);
		player.getInventory().addItem(ManaPotion.getPotion(rangeNr, 4), true, true);
		
		for(Spell spell : player.getSpells().values())
		{
			spell.setLevel(rangeNr * (player.getLevel().getDifficulty() + 1) / 3 + 1);
		}
		player.hud.getCallback().updateSpellLevelText();
	}
}
