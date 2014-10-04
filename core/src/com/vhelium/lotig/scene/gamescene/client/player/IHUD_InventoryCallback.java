package com.vhelium.lotig.scene.gamescene.client.player;

import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;

public interface IHUD_InventoryCallback
{
	public void updateEquipImage(int slotNr, Sprite oldSprite, Sprite newSprite);
	
	public void updateItemList(ItemCategory itemCategory);
	
	public void updateInventoryInfoText();
	
	public void postMessage(String string, boolean white);
	
	public void updatePlayerText();
}
