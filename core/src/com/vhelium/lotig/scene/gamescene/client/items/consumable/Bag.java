package com.vhelium.lotig.scene.gamescene.client.items.consumable;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class Bag extends ConsumableItem
{
	int size;
	
	protected Bag(int size)
	{
		this.size = size;
		NAME = size + "-Slot Bag";
		CATEGORY = ItemCategory.Consumable;
	}
	
	@Override
	public boolean use(PlayerClient player)
	{
		player.getInventory().setMaxSize(player.getInventory().getMaxSize() + size);
		return true;
	}
	
	@Override
	protected void applyIcon()
	{
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon("Bag"));
	}
	
	@Override
	public String getDescription()
	{
		return NAME + ":\n\nIncreases your inventory\nspace by " + size + ".";
	}
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	@Override
	public String toStringFormat()
	{
		return "Bag:" + size + ":" + count;
	}
	
	public static Bag createItemFromStringFormat(StringTokenizer st)
	{
		return getBag(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
	}
	
	public static Bag getBag(int size, int count)
	{
		Bag bag = new Bag(size);
		bag.applyIcon();
		bag.count = count;
		bag.price = Price.BagSlot * size;
		return bag;
	}
	
	@Override
	public StackableItem oneCopy()
	{
		return getBag(size, 1);
	}
}
