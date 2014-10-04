package com.vhelium.lotig.scene.gamescene;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class LootBagToInform
{
	public boolean IsNew;
	public float x;
	public float y;
	public ConcurrentHashMap<Integer, Item> Items;
	
	public LootBagToInform()
	{
		IsNew = false;
		Items = new ConcurrentHashMap<Integer, Item>();
	}
	
	public LootBagToInform(float x, float y, List<Item> drops)
	{
		this.x = x;
		this.y = y;
		IsNew = true;
		Items = new ConcurrentHashMap<Integer, Item>();
		for(int i = 0; i < drops.size(); i++)
			Items.put(i, drops.get(i));
	}
	
	public void addItem(int pos, Item item)
	{
		Items.put(pos, item);
	}
}
