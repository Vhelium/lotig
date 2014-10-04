package com.vhelium.lotig.scene.gamescene.client.player;

import java.util.Comparator;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class ItemScoreComparor implements Comparator<Item>
{
	@Override
	public int compare(Item item1, Item item2)
	{
		return item2.getScore() - item1.getScore();
	}
}