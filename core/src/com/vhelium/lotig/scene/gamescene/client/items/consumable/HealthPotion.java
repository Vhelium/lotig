package com.vhelium.lotig.scene.gamescene.client.items.consumable;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class HealthPotion extends Potion
{
	private int initialLevel = 0;
	private int restoreValue;
	
	protected HealthPotion()
	{
		NAME = "HP Potion";
		CATEGORY = ItemCategory.Consumable;
	}
	
	@Override
	public boolean use(PlayerClient player)
	{
		player.useHpPotion(this);
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
		return NAME + ":\n\nRestores " + restoreValue + " health.";
	}
	
	@Override
	public String toStringFormat()
	{
		return "HP Potion:" + initialLevel + ":" + count;
	}
	
	public static HealthPotion createItemFromStringFormat(StringTokenizer st)
	{
		return getPotion(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
	}
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	public static HealthPotion getPotion(int level, int count)
	{
		HealthPotion potion = new HealthPotion();
		potion.initialLevel = level;
		if(level < 3)
		{
			potion.restoreValue = 1000;
			potion.price = Price.HealthPotionS;
			potion.score = 104;
			potion.NAME += " S";
		}
		else if(level < 6)
		{
			potion.restoreValue = 1500;
			potion.price = Price.HealthPotionM;
			potion.score = 103;
			potion.NAME += " M";
		}
		else if(level < 9)
		{
			potion.restoreValue = 2200;
			potion.price = Price.HealthPotionL;
			potion.score = 102;
			potion.NAME += " L";
		}
		else
		{
			potion.restoreValue = 3500;
			potion.price = Price.HealthPotionXL;
			potion.score = 101;
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
