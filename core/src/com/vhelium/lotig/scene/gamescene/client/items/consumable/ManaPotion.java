package com.vhelium.lotig.scene.gamescene.client.items.consumable;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class ManaPotion extends Potion
{
	private int initialLevel = 0;
	private int restoreValue;
	
	protected ManaPotion()
	{
		NAME = "Mana Potion";
		CATEGORY = ItemCategory.Consumable;
	}
	
	@Override
	public boolean use(PlayerClient player)
	{
		player.useManaPotion(this);
		return true;
	}
	
	@Override
	protected void applyIcon()
	{
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon(NAME));
	}
	
	@Override
	public String getDescription()
	{
		return NAME + ":\n\nRestores " + restoreValue + " mana.";
	}
	
	@Override
	public String toStringFormat()
	{
		return "Mana Potion:" + initialLevel + ":" + count;
	}
	
	public static ManaPotion createItemFromStringFormat(StringTokenizer st)
	{
		return getPotion(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
	}
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	public static ManaPotion getPotion(int level, int count)
	{
		ManaPotion potion = new ManaPotion();
		potion.initialLevel = level;
		if(level < 3)
		{
			potion.restoreValue = 400;
			potion.price = Price.ManaPotionS;
			potion.score = 99;
			potion.NAME += " S";
		}
		else if(level < 6)
		{
			potion.restoreValue = 600;
			potion.price = Price.ManaPotionM;
			potion.score = 98;
			potion.NAME += " M";
		}
		else if(level < 9)
		{
			potion.restoreValue = 900;
			potion.price = Price.ManaPotionL;
			potion.score = 97;
			potion.NAME += " L";
		}
		else
		{
			potion.restoreValue = 1400;
			potion.price = Price.ManaPotionXL;
			potion.score = 96;
			potion.NAME += " XL";
		}
		potion.applyIcon();
		potion.count = count;
		return potion;
	}
	
	public int getRestoreValue()
	{
		return restoreValue;
	}
	
	@Override
	public StackableItem oneCopy()
	{
		return getPotion(initialLevel, 1);
	}
}
