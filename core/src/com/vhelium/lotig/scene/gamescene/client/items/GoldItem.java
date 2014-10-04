package com.vhelium.lotig.scene.gamescene.client.items;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GoldItem extends StackableItem
{
	protected GoldItem()
	{
		NAME = "Gold";
		CATEGORY = ItemCategory.All;
	}
	
	@Override
	protected void applyIcon()
	{
		NAME = "Gold";
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon("Gold"));
	}
	
	@Override
	public String getDescription()
	{
		return "Gold!";
	}
	
	@Override
	public String toStringFormat()
	{
		return "Gold:" + count;
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	public static GoldItem createItemFromStringFormat(StringTokenizer st)
	{
		return getGold(Integer.parseInt(st.nextToken()));
	}
	
	public static GoldItem getGold(int count)
	{
		GoldItem gold = new GoldItem();
		gold.applyIcon();
		gold.count = count;
		gold.price = 1;
		return gold;
	}
	
	@Override
	public GoldItem oneCopy()
	{
		return getGold(this.count);
	}
	
	public static Item randomGold(int level)
	{
		level++;
		return getGold(GameHelper.$.getRandom().nextInt(com.vhelium.lotig.constants.Price.maxGoldDrop / com.vhelium.lotig.constants.Constants.ItemMaxLevel * level) + 10);
	}
}
