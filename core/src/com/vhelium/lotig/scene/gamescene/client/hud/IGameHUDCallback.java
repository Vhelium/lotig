package com.vhelium.lotig.scene.gamescene.client.hud;

import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.OnScreenInfo;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Potion;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public interface IGameHUDCallback
{
	public void hide(String menu);
	
	public void hideForced(String menu);
	
	public void show(String menu);
	
	public void toggle(String string);
	
	public void townPort();
	
	public void setSub(GameHUDMenu mainMenu, GameHUDMenu subMenu);
	
	public void hideSub(GameHUDMenu mainMenu, GameHUDMenu subMenu);
	
	public void sendDataPacket(DataPacket dp);
	
	public String getConnectionType();
	
	public void requestSpell(int slot);
	
	public void selectedSpellStatusChanged(int nr);
	
	public void hotkeySpellChanged(int slot, Spell spell);
	
	public void setSpellPrepared(int slot, boolean prepared);
	
	public void playSpellOutOfMana(int slot);
	
	public void requestPotion(int slot);
	
	public void hotkeyPotionChanged(int slot, Potion potion);
	
	public void setSelectedPotionStatus(boolean value);
	
	public GameHUDMenu getMenu(String menu);
	
	public Item getSelectedInventoryItem();
	
	public void updateSpellLevelText();
	
	public void postMessage(String string, boolean white);
	
	public void postMessageLong(String string, boolean white);
	
	public void updateQuestText();
	
	public void inventoryItemSelected(boolean isSelected);
	
	public void updateInventorySelectedItemText();
	
	public int getSelectedInventoryItemId();
	
	public void updatePlayerText();
	
	public void unselectAnyInventoryItem();
	
	public OnScreenInfo getCurrentOnScreenInfo();
	
	public void showOSI(int id);
	
	public void hideOSI(int id);
	
	public GameHUD_Character getCharacterMenu();
	
	public GameHUD getHUD();
	
	public SceneManager getSceneManager();
}
