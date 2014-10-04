package com.vhelium.lotig.scene.gamescene.client.items.materials;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class BlueDust extends StackableItem
{
	protected BlueDust()
	{
		NAME = "Blue dust";
		CATEGORY = ItemCategory.Materials;
	}
	
	@Override
	protected void applyIcon()
	{
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon("Bag"));
	}
	
	@Override
	public String getDescription()
	{
		return NAME + "\n\nCrafting material.";
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ":" + count;
	}
	
	public static BlueDust createItemFromStringFormat(StringTokenizer st)
	{
		return getBlueDust(Integer.parseInt(st.nextToken()));
	}
	
	public static BlueDust getBlueDust(int count)
	{
		BlueDust bag = new BlueDust();
		bag.applyIcon();
		bag.count = count;
		bag.price = 100;
		return bag;
	}
	
	@Override
	public StackableItem oneCopy()
	{
		return getBlueDust(1);
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return true;
	}
}
