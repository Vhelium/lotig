package com.vhelium.lotig.scene.gamescene.client.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.GemItem;
import com.vhelium.lotig.scene.gamescene.client.items.GoldItem;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.AttributePotion;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.ConsumableItem;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Potion;
import com.vhelium.lotig.scene.gamescene.client.items.jewelry.EquipRing;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipOffHand;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipWeapon;
import com.vhelium.lotig.scene.gamescene.quest.IQuestListener;

public class Inventory
{
	private final PlayerClient player;
	private final IHUD_InventoryCallback hudCallback;
	
	private int gold = 0;
	private int gems = 0;
	private int maxSize = 25;
	
	private int equipLevel = 0;
	
	private final Hashtable<ItemCategory, List<Item>> items;
	private final Hashtable<String, Equip> equips;
	private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Item>> stashes;
	
	public Inventory(PlayerClient player, IHUD_InventoryCallback hudCallback)
	{
		this.player = player;
		this.hudCallback = hudCallback;
		stashes = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Item>>();
		for(int i = 0; i < 4; i++)
			stashes.put(i, new ConcurrentHashMap<Integer, Item>());
		items = new Hashtable<ItemCategory, List<Item>>();
		items.put(ItemCategory.Glyph, new ArrayList<Item>());
		items.put(ItemCategory.Materials, new ArrayList<Item>());
		items.put(ItemCategory.Consumable, new ArrayList<Item>());
		items.put(ItemCategory.Jewelry, new ArrayList<Item>());
		items.put(ItemCategory.OffHand, new ArrayList<Item>());
		items.put(ItemCategory.Armor, new ArrayList<Item>());
		items.put(ItemCategory.Weapon, new ArrayList<Item>());
		equips = new Hashtable<String, Equip>();
		addGems(500);
	}
	
	public void addGold(int count)
	{
		gold += count;
		hudCallback.updateInventoryInfoText();
		
		List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(player.getLevel().getQuestListeners());
		for(IQuestListener listener : toLoopIQuestListeners)
			listener.onGoldChanged(count);
	}
	
	public int removeGold(int count)
	{
		gold -= count;
		hudCallback.updateInventoryInfoText();
		
		List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(player.getLevel().getQuestListeners());
		for(IQuestListener listener : toLoopIQuestListeners)
			listener.onGoldChanged(-count);
		
		return count;
	}
	
	public int getGold()
	{
		return gold;
	}
	
	public int getGems()
	{
		return gems;
	}
	
	public void addGems(int count)
	{
		gems += count;
		
		player.hud.getMenu("gems").setParam(gems);
	}
	
	public void removeGems(int count)
	{
		gems -= count;
		
		player.hud.getMenu("gems").setParam(gems);
	}
	
	public void addItem(Item item, boolean isNew, boolean doUpdate)
	{
		boolean existing = false;
		if(item instanceof GoldItem)
		{
			addGold(((GoldItem) item).getCount());
			return;
		}
		if(item instanceof GemItem)
		{
			addGems(((GemItem) item).getCount());
			player.getLevel().saveGame();
			return;
		}
		else if(item instanceof StackableItem)
		{
			for(Item existingItem : items.get(item.CATEGORY))
				if(existingItem.NAME.equalsIgnoreCase(item.NAME))
				{
					existing = true;
					((StackableItem) existingItem).countPlus(((StackableItem) item).getCount());
					break;
				}
		}
		if(!existing)
		{
			items.get(item.CATEGORY).add(item);
		}
		if(isNew)
		{
			item.setIsNew(true);
		}
		if(doUpdate)
		{
			doUpdate(item.CATEGORY);
		}
	}
	
	public void removeItem(Item item, boolean doUpdate)
	{
		items.get(item.CATEGORY).remove(item);
		if(doUpdate)
		{
			doUpdate(item.CATEGORY);
		}
		if(item instanceof Potion)
			player.onPotionRemoved((Potion) item);
	}
	
	public void unequipItem(int slot)
	{
		if(isFull())
		{
			hudCallback.postMessage("Inventory full!", false);
			return;
		}
		String itemName = getNameBySlot(slot);
		Equip eq = equips.get(itemName);
		
		updateEquipImage(slot, eq, null);
		equips.remove(itemName);
		
		addItem(eq, false, true);
		
		player.onAttributeChanged();
	}
	
	public void removeEquip(int slot)
	{
		String itemName = getNameBySlot(slot);
		Equip eq = equips.get(itemName);
		
		updateEquipImage(slot, eq, null);
		equips.remove(itemName);
		
		doUpdate(eq.CATEGORY);
		
		player.onAttributeChanged();
	}
	
	public void useItem(Item item, boolean byPlayer)
	{
		if(item instanceof Equip)
		{
			String itemName = item.NAME;
			
			if(item instanceof EquipRing)
				itemName = (equips.containsKey("Ring2") || !equips.containsKey("Ring1")) ? "Ring1" : "Ring2";
			else if(item instanceof EquipWeapon)
				itemName = "Weapon";
			else if(item instanceof EquipOffHand)
				itemName = "OffHand";
			
			if(equips.containsKey(itemName))
				addItem(equips.get(itemName), false, false);
			updateEquipImage(getSlotByName(itemName), equips.get(itemName), item);
			equips.put(itemName, (Equip) item);
			removeItem(item, true);
			
			doUpdate(item.CATEGORY);
			player.onAttributeChanged();
			
			if(byPlayer)
				SoundManager.playSound(SoundFile.item_equipped);
		}
		else if(item instanceof ConsumableItem)
		{
			ConsumableItem usableItem = ((ConsumableItem) item);
			if(usableItem.use(player))
			{
				if(usableItem.getCount() > 1)
				{
					usableItem.countMinus();
					hudCallback.updateItemList(item.CATEGORY);
					hudCallback.updateInventoryInfoText();
				}
				else
				{
					removeItem(item, true);
					for(Entry<Integer, Potion> e : player.getHotkeyPotions().entrySet())
						if(e.getValue() == item)
						{
							player.hud.getCallback().hotkeyPotionChanged(e.getKey(), null);
							player.getHotkeyPotions().remove(e.getKey());
							break;
						}
				}
				
				player.onAttributeChanged();
				
				if(byPlayer)
				{
					if(item instanceof Potion || item instanceof AttributePotion)
						SoundManager.playSound(SoundFile.potion_consume);
					else
						SoundManager.playSound(SoundFile.item_used);
				}
			}
		}
	}
	
	public void sellItem(Item item)
	{
		addGold(item.getSellPrice());
		if(item instanceof StackableItem)
		{
			StackableItem stack = ((StackableItem) item);
			if(stack.getCount() > 1)
			{
				stack.countMinus();
				doUpdate(item.CATEGORY);
			}
			else
			{
				removeItem(item, true);
			}
		}
		else
		{
			removeItem(item, true);
		}
	}
	
	private void updateEquipImage(int slotNr, Item oldEquip, Item newEquip)
	{
		Sprite oldS = null;
		Sprite newS = null;
		
		if(oldEquip != null)
			oldS = oldEquip.getIcon();
		if(newEquip != null)
			newS = newEquip.getIcon();
		
		hudCallback.updateEquipImage(slotNr, oldS, newS);
	}
	
	public void doUpdate(ItemCategory category)
	{
		if(category == ItemCategory.All)
			for(List<Item> cat : items.values())
				Collections.sort(cat, new ItemScoreComparor());
		else if(items.containsKey(category))
			Collections.sort(items.get(category), new ItemScoreComparor());
		hudCallback.updateItemList(category);
		hudCallback.updateInventoryInfoText();
		
		equipLevel = 0;
		for(Equip eq : equips.values())
			equipLevel += eq.getScore();
		
		hudCallback.updatePlayerText();
	}
	
	public int getAttributeValue(String ATTR)
	{
		ATTR = ATTR.toUpperCase();
		int res = 0;
		for(Equip eq : equips.values())
			res += eq.getAttribute(ATTR);
		return res;
	}
	
	private int getSlotByName(String itemName)
	{
		if(itemName.equals("Shoulder"))
			return SLOT_ID_SHOULDER;
		
		else if(itemName.equals("Helmet"))
			return SLOT_ID_HELMET;
		
		else if(itemName.equals("Amulet"))
			return SLOT_ID_AMULET;
		
		else if(itemName.equals("Bracer"))
			return SLOT_ID_BRACER;
		
		else if(itemName.equals("Chest"))
			return SLOT_ID_CHEST;
		
		else if(itemName.equals("Ring1"))
			return SLOT_ID_RING1;
		
		else if(itemName.equals("Glove"))
			return SLOT_ID_GLOVE;
		
		else if(itemName.equals("Pants"))
			return SLOT_ID_PANTS;
		
		else if(itemName.equals("Ring2"))
			return SLOT_ID_RING2;
		
		else if(itemName.equals("OffHand"))
			return SLOT_ID_OFFHAND;
		
		else if(itemName.equals("Boots"))
			return SLOT_ID_BOOTS;
		
		else if(itemName.equals("Weapon"))
			return SLOT_ID_WEAPON;
		
		else
			return -1;
		
	}
	
	public static String getNameBySlot(int slot)
	{
		switch(slot)
		{
			case SLOT_ID_SHOULDER:
				return "Shoulder";
			case SLOT_ID_HELMET:
				return "Helmet";
			case SLOT_ID_AMULET:
				return "Amulet";
			case SLOT_ID_BRACER:
				return "Bracer";
			case SLOT_ID_CHEST:
				return "Chest";
			case SLOT_ID_RING1:
				return "Ring1";
			case SLOT_ID_GLOVE:
				return "Glove";
			case SLOT_ID_PANTS:
				return "Pants";
			case SLOT_ID_RING2:
				return "Ring2";
			case SLOT_ID_OFFHAND:
				return "OffHand";
			case SLOT_ID_BOOTS:
				return "Boots";
			case SLOT_ID_WEAPON:
				return "Weapon";
			default:
				return null;
		}
	}
	
	public int size()
	{
		int res = 0;
		for(List<Item> cat : items.values())
			res += cat.size();
		return res;
	}
	
	public Equip getEquipBySlot(int slot)
	{
		return equips.get(getNameBySlot(slot));
	}
	
	public Hashtable<ItemCategory, List<Item>> getItems()
	{
		return items;
	}
	
	public Equip getEquip(String name)
	{
		return equips.get(name);
	}
	
	public Collection<Equip> getEquipList()
	{
		return equips.values();
	}
	
	public int getEquipLevel()
	{
		return equipLevel;
	}
	
	public int getMaxSize()
	{
		return maxSize;
	}
	
	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}
	
	public boolean isFull()
	{
		return size() >= maxSize;
	}
	
	public boolean hasSpaceFor(int count)
	{
		return size() + count <= maxSize;
	}
	
	public static final int SLOT_ID_SHOULDER = 0;
	public static final int SLOT_ID_HELMET = 1;
	public static final int SLOT_ID_AMULET = 2;
	public static final int SLOT_ID_BRACER = 3;
	public static final int SLOT_ID_CHEST = 4;
	public static final int SLOT_ID_RING1 = 5;
	public static final int SLOT_ID_GLOVE = 6;
	public static final int SLOT_ID_PANTS = 7;
	public static final int SLOT_ID_RING2 = 8;
	public static final int SLOT_ID_OFFHAND = 9;
	public static final int SLOT_ID_BOOTS = 10;
	public static final int SLOT_ID_WEAPON = 11;
	
	public Item containsConsumableItem(Item item)
	{
		if(item instanceof ConsumableItem)
		{
			for(Item i : items.get(ItemCategory.Consumable))
			{
				if(i.NAME.equalsIgnoreCase(item.NAME))
					return i;
			}
		}
		else if(item instanceof GoldItem || item instanceof GemItem)
			return item;
		
		return null;
	}
	
	public ConcurrentHashMap<Integer, Item> getStash(int nr)
	{
		if(!stashes.containsKey(nr))
			stashes.put(nr, new ConcurrentHashMap<Integer, Item>());
		return stashes.get(nr);
	}
	
	public void clearItems()
	{
		List<Item> allItems = new ArrayList<Item>();
		for(List<Item> cat : items.values())
			for(Item item : cat)
				allItems.add(item);
		for(int i = 0; i < allItems.size(); i++)
			removeItem(allItems.get(i), false);
		doUpdate(ItemCategory.All);
	}
}
