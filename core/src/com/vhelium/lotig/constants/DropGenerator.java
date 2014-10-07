package com.vhelium.lotig.constants;

import java.util.ArrayList;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.GemItem;
import com.vhelium.lotig.scene.gamescene.client.items.GoldItem;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipBoots;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipBracer;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipChest;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipGlove;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipHelmet;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipPants;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipShoulder;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.AttributePotion;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Bag;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Glyph;
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

public abstract class DropGenerator
{
	public static ArrayList<Item> generateDrop(int level, int modifier, int playerCount)
	{
		ArrayList<Item> drops = new ArrayList<Item>();
		int r;
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceEquip1)
			drops.add(generateEquipDrop(level, modifier));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceEquip2)
			drops.add(generateEquipDrop(level, modifier));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceEquipTrash)
			drops.add(generateEquipDrop(Math.max(level - 5, 0), modifier));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceEquipElite)
			drops.add(generateEquipDrop(Math.min(level + 4, Constants.ItemMaxLevel), modifier));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceGlyph)
			drops.add(generateGlyphDrop(level));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceMiscellaneous)
			drops.add(generateMiscellaneous(level));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChancePotion)
			drops.add(generatePotionDrop(level));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceAttributePotion)
			drops.add(generateAttributePotionDrop(level));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceGold)
			drops.add(GoldItem.randomGold(level));
		
		r = nextInt(100) + 1;
		if(r <= Constants.dropChanceGem)
			drops.add(GemItem.randomGem(level));
		
		return drops;
	}
	
	private static Item generateEquipDrop(int level, int modifier)
	{
		int r = nextInt(21);
		switch(r)
		{
			case 0:
				return EquipBoots.randomEquip(level, modifier);
			case 1:
				return EquipBracer.randomEquip(level, modifier);
			case 2:
				return EquipChest.randomEquip(level, modifier);
			case 3:
				return EquipGlove.randomEquip(level, modifier);
			case 4:
				return EquipHelmet.randomEquip(level, modifier);
			case 5:
				return EquipPants.randomEquip(level, modifier);
			case 6:
				return EquipShoulder.randomEquip(level, modifier);
			case 7:
				return EquipAmulet.randomEquip(level, modifier);
			case 8:
				return EquipRing.randomEquip(level, modifier);
			case 9:
				return EquipBow.randomEquip(level, modifier);
			case 10:
				return EquipBattleAxe.randomEquip(level, modifier);
			case 11:
				return EquipLongsword.randomEquip(level, modifier);
			case 12:
				return EquipRod.randomEquip(level, modifier);
			case 13:
				return EquipScepter.randomEquip(level, modifier);
			case 14:
				return EquipWand.randomEquip(level, modifier);
			case 15:
				return EquipCharm.randomEquip(level, modifier);
			case 16:
				return EquipInsignia.randomEquip(level, modifier);
			case 17:
				return EquipShield.randomEquip(level, modifier);
			case 18:
				return EquipTome.randomEquip(level, modifier);
			case 19:
				return EquipTrophy.randomEquip(level, modifier);
			case 20:
				return EquipFocus.randomEquip(level, modifier);
			default:
				return null;
		}
	}
	
	private static Item generateGlyphDrop(int level)
	{
		return Glyph.getRandomGlyph(1);
	}
	
	private static Item generatePotionDrop(int level)
	{
		int r = nextInt(2);
		switch(r)
		{
			case 0:
				return HealthPotion.getPotion(level, 1);
			case 1:
				return ManaPotion.getPotion(level, 1);
			default:
				return null;
		}
	}
	
	private static Item generateAttributePotionDrop(int level)
	{
		return AttributePotion.getRandomAttributePotion(1);
	}
	
	private static Item generateMiscellaneous(int level)
	{
		int r = nextInt(1);
		switch(r)
		{
			case 0:
				return Bag.getBag(level + 1, 1);//TODO: Random size!
			default:
				return null;
		}
	}
	
	private static int nextInt(int upperExcluded)
	{
		return GameHelper.getInstance().getRandom().nextInt(upperExcluded);
	}
	
	public static Item dropWeaponOrOffHand(int level, int modifier)
	{
		int r = nextInt(12);
		switch(r)
		{
			case 0:
				return EquipBow.randomEquip(level, modifier);
			case 1:
				return EquipBattleAxe.randomEquip(level, modifier);
			case 2:
				return EquipLongsword.randomEquip(level, modifier);
			case 3:
				return EquipRod.randomEquip(level, modifier);
			case 4:
				return EquipScepter.randomEquip(level, modifier);
			case 5:
				return EquipWand.randomEquip(level, modifier);
			case 6:
				return EquipCharm.randomEquip(level, modifier);
			case 7:
				return EquipInsignia.randomEquip(level, modifier);
			case 8:
				return EquipShield.randomEquip(level, modifier);
			case 9:
				return EquipTome.randomEquip(level, modifier);
			case 10:
				return EquipTrophy.randomEquip(level, modifier);
			case 11:
				return EquipFocus.randomEquip(level, modifier);
			default:
				return null;
		}
	}
	
	public static Item dropArmor(int level, int modifier)
	{
		int r = nextInt(9);
		switch(r)
		{
			case 0:
				return EquipBoots.randomEquip(level, modifier);
			case 1:
				return EquipBracer.randomEquip(level, modifier);
			case 2:
				return EquipChest.randomEquip(level, modifier);
			case 3:
				return EquipGlove.randomEquip(level, modifier);
			case 4:
				return EquipHelmet.randomEquip(level, modifier);
			case 5:
				return EquipPants.randomEquip(level, modifier);
			case 6:
				return EquipShoulder.randomEquip(level, modifier);
			case 7:
				return EquipAmulet.randomEquip(level, modifier);
			case 8:
				return EquipRing.randomEquip(level, modifier);
			default:
				return null;
		}
	}
}