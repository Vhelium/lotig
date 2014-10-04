package com.vhelium.lotig.scene.gamescene.client.items;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GemItem extends StackableItem
{
	protected GemItem()
	{
		NAME = "Gem";
		CATEGORY = ItemCategory.All;
	}
	
	@Override
	protected void applyIcon()
	{
		NAME = "Gem";
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon("Gem"));
	}
	
	@Override
	public String getDescription()
	{
		return "Gem!";
	}
	
	@Override
	public String toStringFormat()
	{
		return "Gem:" + count;
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
	
	public static GemItem createItemFromStringFormat(StringTokenizer st)
	{
		return getGem(Integer.parseInt(st.nextToken()));
	}
	
	public static GemItem getGem(int count)
	{
		GemItem gem = new GemItem();
		gem.applyIcon();
		gem.count = count;
		gem.price = 1;
		return gem;
	}
	
	@Override
	public GemItem oneCopy()
	{
		return getGem(this.count);
	}
	
	public static Item randomGem(int level)
	{
		return getGem(GameHelper.$.getRandom().nextInt(3) + 1);
	}
}
