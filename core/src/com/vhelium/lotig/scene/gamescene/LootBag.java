package com.vhelium.lotig.scene.gamescene;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.client.items.GemItem;
import com.vhelium.lotig.scene.gamescene.client.items.GoldItem;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;

public class LootBag
{
	private Sprite sprite;
	private float x;
	private float y;
	private final ConcurrentHashMap<Integer, Item> items;
	public int id;
	public static final int distributeRadius = 75;
	public static final int lootRadius = 80;
	public static final int maxSize = 6;
	
	public LootBag(int id, float x, float y, ConcurrentHashMap<Integer, String> drops, TextureRegion texture)//client
	{
		this.id = id;
		this.x = x;
		this.y = y;
		if(texture != null)
		{
			sprite = new Sprite(x, y, texture);
			sprite.setX(x);
			sprite.setY(y);
		}
		items = new ConcurrentHashMap<Integer, Item>();
		for(Entry<Integer, String> drop : drops.entrySet())
			items.put(drop.getKey(), Item.getItemFromStringFormat(drop.getValue()));
	}
	
	public LootBag(int id, float x, float y, List<Item> drops)//server
	{
		this.id = id;
		this.x = x;
		this.y = y;
		items = new ConcurrentHashMap<Integer, Item>();
		for(int i = 0; i < drops.size(); i++)
			items.put(i, drops.get(i));
	}
	
	public float getX()
	{
		return x;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public Sprite getSprite()
	{
		return sprite;
	}
	
	public int getSize()
	{
		return items.size();
	}
	
	public boolean isFull()
	{
		return getSize() >= maxSize;
	}
	
	public int addNewItem(Item item)//server
	{
		//Check for merging
		if(item instanceof StackableItem)
			for(Entry<Integer, Item> i2 : items.entrySet())
				if(i2.getValue() instanceof StackableItem && i2.getValue().NAME.equalsIgnoreCase(item.NAME))//item1 and item2 the same
				{
					((StackableItem) i2.getValue()).countPlus(((StackableItem) item).getCount());
					return i2.getKey();
				}
		
		for(int i = 0; i < maxSize; i++)
			if(!items.containsKey(i))
			{
				items.put(i, item);
				return i;
			}
		return -1;
	}
	
	public void addItem(int pos, Item item)//client
	{
		items.put(pos, item);
	}
	
	public void removeItem(int pos)
	{
		items.remove(pos);
	}
	
	public ConcurrentHashMap<Integer, Item> getItems()
	{
		return items;
	}
	
	public int getHardItemCount()
	{
		int res = 0;
		for(Item item : items.values())
			if(!(item instanceof GoldItem) && !(item instanceof GemItem))
				res++;
		return res;
	}
}
