package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.LootBagChest;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Chest extends LevelObject
{
	public static final String NAME = "Chs";
	private ConcurrentHashMap<Integer, String> itemStrings;
	
	private final int fakeFootBagId;
	private LootBagChest fakeLootBag;
	private int uniqueId = -1;
	
	public LevelObject_Chest(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		itemStrings = new ConcurrentHashMap<Integer, String>();
		fakeFootBagId = realm.generateLootBagId();
		
		if(tmxObjectProperties.containsKey("chestitems"))
		{
			StringTokenizer st = new StringTokenizer(tmxObjectProperties.get("chestitems", String.class), ",");
			int i = 0;
			while(st.hasMoreTokens())
			{
				itemStrings.put(i, st.nextToken());
				i++;
			}
		}
		if(tmxObjectProperties.containsKey("uniqueId"))
		{
			uniqueId = Integer.parseInt(tmxObjectProperties.get("uniqueId", String.class));
		}
		
		if(isUnique() && realm.getLevel().getUniqueChestsOpened().contains(uniqueId))
			itemStrings.clear();
	}
	
	public LevelObject_Chest(int id, float x, float y, int w, int h, int fakeFootBagId, String itemString)
	{
		this.id = id;
		this.fakeFootBagId = fakeFootBagId;
		
		sprite = new Sprite(x, y, w, h, GameHelper.getInstance().getGameAsset("Chest"));
		bounds = new Rectangle(x, y, w, h);
		itemStrings = new ConcurrentHashMap<Integer, String>();
		
		StringTokenizer st = new StringTokenizer(itemString, ",");
		int i = 0;
		while(st.hasMoreTokens())
		{
			String item = st.nextToken();
			if(!item.equals(""))
				itemStrings.put(i, item);
			i++;
		}
		
		fakeLootBag = new LootBagChest(fakeFootBagId, this, itemStrings, null);
		itemStrings = null;//not needed anymore
		
		MapProperties properties = new MapProperties();
		properties.put("showChest", String.valueOf(id));
		event = new OnEnterEvent(null, bounds, properties);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		String res = NAME + ";" + fakeFootBagId + ";";
		for(int i = 0; i < 6; i++)
		{
			if(itemStrings.containsKey(i))
				res += itemStrings.get(i);
			if(i < 5)
				res += ",";
		}
		return res;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
	
	public LootBagChest getFakeLootBag()
	{
		return fakeLootBag;
	}
	
	public ConcurrentHashMap<Integer, String> getItemStrings()
	{
		return itemStrings;
	}
	
	public void itemPicked(int slot)
	{
		if(fakeLootBag != null)
			fakeLootBag.removeItem(slot);
		else if(itemStrings != null)
			itemStrings.remove(slot);
	}
	
	public boolean isAvailable(int slot)
	{
		if(fakeLootBag != null)
			return fakeLootBag.getItems().containsKey(slot);
		else if(itemStrings != null)
			return itemStrings.containsKey(slot);
		return false;
	}
	
	public int getUniqueId()
	{
		return uniqueId;
	}
	
	public boolean isUnique()
	{
		return uniqueId != -1;
	}
	
	public void clearItems()
	{
		itemStrings.clear();
	}
}