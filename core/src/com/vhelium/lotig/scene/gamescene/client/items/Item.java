package com.vhelium.lotig.scene.gamescene.client.items;

import java.util.StringTokenizer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Constants;
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
import com.vhelium.lotig.scene.gamescene.client.items.consumable.UniversalGlyph;
import com.vhelium.lotig.scene.gamescene.client.items.jewelry.EquipAmulet;
import com.vhelium.lotig.scene.gamescene.client.items.jewelry.EquipRing;
import com.vhelium.lotig.scene.gamescene.client.items.materials.BlueDust;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipCharm;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipFocus;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipInsignia;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipShield;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipTome;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipTrophy;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipBow;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipClaymore;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipLongsword;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipRod;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipScepter;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipWand;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public abstract class Item
{
	public String NAME;
	public ItemCategory CATEGORY;
	protected Sprite icon;
	protected int price = 0;
	protected int score = 0;
	protected boolean isNew;
	
	public Sprite getIcon()
	{
		return icon;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public int getSellPrice()
	{
		return (int) (price * Constants.SellItemPriceFactor);
	}
	
	protected abstract void applyIcon();
	
	public abstract String getDescription();
	
	public abstract String toStringFormat();
	
	public static Item getItemFromStringFormat(String stringFormat)
	{
		StringTokenizer st = new StringTokenizer(stringFormat, ":");
		String type = st.nextToken();
		
		if(type.equals("Amulet"))
			return EquipAmulet.createItemFromStringFormat(st);
		
		else if(type.equals("Boots"))
			return EquipBoots.createItemFromStringFormat(st);
		
		else if(type.equals("Bracer"))
			return EquipBracer.createItemFromStringFormat(st);
		
		else if(type.equals("Chest"))
			return EquipChest.createItemFromStringFormat(st);
		
		else if(type.equals("Glove"))
			return EquipGlove.createItemFromStringFormat(st);
		
		else if(type.equals("Helmet"))
			return EquipHelmet.createItemFromStringFormat(st);
		
		else if(type.equals("Pants"))
			return EquipPants.createItemFromStringFormat(st);
		
		else if(type.equals("Ring"))
			return EquipRing.createItemFromStringFormat(st);
		
		else if(type.equals("Shoulder"))
			return EquipShoulder.createItemFromStringFormat(st);
		
		else if(type.equals("Claymore"))
			return EquipClaymore.createItemFromStringFormat(st);
		
		else if(type.equals("Longsword"))
			return EquipLongsword.createItemFromStringFormat(st);
		
		else if(type.equals("Rod"))
			return EquipRod.createItemFromStringFormat(st);
		
		else if(type.equals("Bow"))
			return EquipBow.createItemFromStringFormat(st);
		
		else if(type.equals("Wand"))
			return EquipWand.createItemFromStringFormat(st);
		
		else if(type.equals("Scepter"))
			return EquipScepter.createItemFromStringFormat(st);
		
		else if(type.equals("Shield"))
			return EquipShield.createItemFromStringFormat(st);
		
		else if(type.equals("Insignia"))
			return EquipInsignia.createItemFromStringFormat(st);
		
		else if(type.equals("Tome"))
			return EquipTome.createItemFromStringFormat(st);
		
		else if(type.equals("Charm"))
			return EquipCharm.createItemFromStringFormat(st);
		
		else if(type.equals("Trophy"))
			return EquipTrophy.createItemFromStringFormat(st);
		
		else if(type.equals("Focus"))
			return EquipFocus.createItemFromStringFormat(st);
		
		else if(type.equals("HP Potion"))
			return HealthPotion.createItemFromStringFormat(st);
		
		else if(type.equals("Mana Potion"))
			return ManaPotion.createItemFromStringFormat(st);
		
		else if(type.equals("Glyph"))
			return Glyph.createItemFromStringFormat(st);
		
		else if(type.equals("Bag"))
			return Bag.createItemFromStringFormat(st);
		
		else if(type.equals("AtPt"))
			return AttributePotion.createItemFromStringFormat(st);
		
		else if(type.equals("UniGlyph"))
			return UniversalGlyph.createItemFromStringFormat(st);
		
		else if(type.equals("Blue dust"))
			return BlueDust.createItemFromStringFormat(st);
		
		else if(type.equals("Gold"))
			return GoldItem.createItemFromStringFormat(st);
		
		else if(type.equals("Gem"))
			return GemItem.createItemFromStringFormat(st);
		
		else
			return null;
		
	}
	
	@Override
	public String toString()
	{
		return "null";
	}
	
	public String toStringTrimmed(BitmapFont font, int width)
	{
		return toString();
	}
	
	public void setIsNew(boolean isNew)
	{
		this.isNew = isNew;
	}
	
	public boolean getIsNew()
	{
		return isNew;
	}
	
	public abstract boolean isUsable(PlayerClient player);
}
