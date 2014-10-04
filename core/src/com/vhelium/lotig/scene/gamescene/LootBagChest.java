package com.vhelium.lotig.scene.gamescene;

import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Chest;

public class LootBagChest extends LootBag
{
	public LevelObject_Chest chest;
	
	public LootBagChest(int fakeFootBagId, LevelObject_Chest chest, ConcurrentHashMap<Integer, String> itemStrings, Object object)
	{
		super(fakeFootBagId, -1, -1, itemStrings, null);
		this.chest = chest;
	}
}
