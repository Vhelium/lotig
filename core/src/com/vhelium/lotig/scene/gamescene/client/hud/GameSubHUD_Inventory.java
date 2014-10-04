package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.ScrollList;
import com.vhelium.lotig.components.ScrollPane;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipBoots;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipBracer;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipChest;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipGlove;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipHelmet;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipPants;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipShoulder;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.ConsumableItem;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Glyph;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Potion;
import com.vhelium.lotig.scene.gamescene.client.items.jewelry.EquipAmulet;
import com.vhelium.lotig.scene.gamescene.client.items.jewelry.EquipRing;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipOffHand;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipWeapon;
import com.vhelium.lotig.scene.gamescene.client.player.IHUD_InventoryCallback;
import com.vhelium.lotig.scene.gamescene.client.player.Inventory;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GameSubHUD_Inventory extends GameHUDMenu
{
	Inventory playerInventory;
	Main activity;
	private final HashMap<Integer, ButtonSprite> equipbuttons;
	private HashMap<Integer, Sprite> equipPlaceHolders;
	private ScrollList scrollList;
	private ScrollPane scrollPane;
	private int selectedItemId = -1;
	private Item selectedItem = null;
	private Sprite spriteCmdItemHighlighted;
	private ItemCategory currentCategory = ItemCategory.All;
	private int currentCategoryId = 0;
	private final List<ItemCategory> categoryList;
	private TextButton cmdUseItem;
	private TextButton cmdDropItem;
	private ButtonSprite cmdCategoryLeft;
	private ButtonSprite cmdCategoryRight;
	private Label txtItemInfo;
	private Label txtItemInfoMod;
	private Label txtItemInfoRed;
	private Label txtInvInfo;
	private Label txtGoldInfo;
	private Label txtCurrentCategory;
	private Label txtSoulbound;
	
	public GameSubHUD_Inventory(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		equipbuttons = new HashMap<Integer, ButtonSprite>();
		categoryList = new ArrayList<ItemCategory>();
		
		categoryList.add(ItemCategory.All);
		categoryList.add(ItemCategory.Weapon);
		categoryList.add(ItemCategory.Armor);
		categoryList.add(ItemCategory.OffHand);
		categoryList.add(ItemCategory.Jewelry);
		categoryList.add(ItemCategory.Materials);
		categoryList.add(ItemCategory.Consumable);
		categoryList.add(ItemCategory.Glyph);
	}
	
	private final IHUD_InventoryCallback invCallback = new IHUD_InventoryCallback()
	{
		@Override
		public void updateEquipImage(final int slotNr, final Sprite oldSprite, final Sprite newSprite)
		{
			if(oldSprite != null)
				oldSprite.remove();
			else
				equipPlaceHolders.get(slotNr).remove();
			if(newSprite != null)
				equipbuttons.get(slotNr).addActor(newSprite);
			else
				equipbuttons.get(slotNr).addActor(equipPlaceHolders.get(slotNr));
		}
		
		@Override
		public void updateItemList(ItemCategory itemCategory)
		{
			if(currentCategory == ItemCategory.All || currentCategory == itemCategory || itemCategory == ItemCategory.All)
			{
				showCategory(currentCategory);
			}
		}
		
		@Override
		public void updateInventoryInfoText()
		{
			GameSubHUD_Inventory.this.updateInventoryInfoText();
		}
		
		@Override
		public void postMessage(String msg, boolean white)
		{
			hudCallback.postMessage(msg, white);
		}
		
		@Override
		public void updatePlayerText()
		{
			hudCallback.updatePlayerText();
		}
	};
	
	@Override
	public void showUp()
	{
		updateInventoryInfoText();
		buttonClicked(-1);
		currentCategoryId = 0;
		currentCategory = ItemCategory.All;
		showCategory(ItemCategory.All);
		
		if(hudCallback.getCurrentOnScreenInfo() != null && hudCallback.getCurrentOnScreenInfo().id == 7/*Inventory*/)
		{
			hudCallback.showOSI(8/*Item*/);
		}
	}
	
	@Override
	public void hidden()
	{
		showUp();
	}
	
	private void showCategory(ItemCategory itemCategory)
	{
		buttonClicked(-1);
		ArrayList<String> content = new ArrayList<String>();
		if(itemCategory == ItemCategory.All)
		{
			for(int c = 1; c < categoryList.size(); c++)
			{
				for(int i = 0; i < playerInventory.getItems().get(categoryList.get(c)).size(); i++)
				{
					Item item = playerInventory.getItems().get(categoryList.get(c)).get(i);
					content.add((item.isUsable(player) ? "" : "#") + (item.getIsNew() ? "! " : "") + item.toStringTrimmed(scrollList.getFont(), scrollList.getElementTextWidth()));
				}
			}
		}
		else
		{
			for(Item item : playerInventory.getItems().get(itemCategory))
			{
				content.add((item.isUsable(player) ? "" : "#") + (item.getIsNew() ? "! " : "") + item.toStringTrimmed(scrollList.getFont(), scrollList.getElementTextWidth()));
			}
		}
		scrollList.setContent(content, null);
		updateCategoryText();
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		super.initHUD(player);
		player.initInventory(invCallback);
		playerInventory = player.getInventory();
//		playerInventory.addItem(Item.getItemFromStringFormat("Shoulder:5:STR:5:VIT:5:SPD:5:DEX:5:WIS:5:1:1"), false, false);
//		playerInventory.addItem(Item.getItemFromStringFormat("Shoulder:5:DMG:5:ARMOR:5:CDR:5:MAXHP:5:MAXMANA:5:1:1"), false, false);
//		player.getInventory().addGold(65000);
//		playerInventory.clearItems();
//		for(int i = 0; i < 40; i++)
//			playerInventory.addItem(EquipShoulder.randomEquip(25, 3), false, false);
//		playerInventory.addItem(EquipClaymore.randomEquip(25, 3), false, true);
//		playerInventory.addItem(EquipLongsword.randomEquip(10, 1), false, false);
//		playerInventory.addItem(EquipWand.randomEquip(10, 1), false, true);
//		playerInventory.addItem(Glyph.getGlyph("Fire Trap", 2), false, false);
//		playerInventory.addItem(Glyph.getGlyph("Fireball", 2), false, false);
//		playerInventory.addItem(Glyph.getGlyph("Blink", 2), false, false);
//		for(int i = 0; i < 20; i++)
//			playerInventory.addItem(Glyph.getRandomGlyph(1), false, false);
//		playerInventory.addItem(Bag.getBag(5, 2), false, false);
//		playerInventory.addItem(Bag.getBag(10, 2), false, false);
//		playerInventory.addItem(Bag.getBag(15, 10), false, false);
//		playerInventory.addItem(HealthPotion.getPotion(0, 2), false, false);
//		playerInventory.addItem(HealthPotion.getPotion(7, 3), false, false);
//		playerInventory.addItem(ManaPotion.getPotion(0, 2), false, false);
//		playerInventory.addItem(ManaPotion.getPotion(7, 3), false, false);
//		playerInventory.addItem(AttributePotion.getAttributePotion("STR", 20, 7), false, false);
//		playerInventory.addItem(AttributePotion.getAttributePotion("DEX", 200, 7), false, false);
//		playerInventory.addItem(AttributePotion.getAttributePotion("SPD", 50, 7), false, true);
//		playerInventory.addItem(BlueDust.getBlueDust(15), false, false);
//		playerInventory.addItem(UniversalGlyph.getClassGlyph(20), false, true);
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		
		TextureRegion textureRegionBackgroundSorting = GameHelper.$.getGuiAsset("backgroundSorting");//      173 x  41   1
		TextureRegion textureRegionBackgroundListItem = GameHelper.$.getGuiAsset("backgroundListItem");//   173 x  38   2
		TextureRegion textureRegionBackgroundList = GameHelper.$.getGuiAsset("backgroundList");//           173 x 354   3
		TextureRegion textureRegionCmdItems = GameHelper.$.getGuiAsset("cmdItems");//                      89 x  50   4
		TextureRegion textureRegionBackgroundItemInfo = GameHelper.$.getGuiAsset("backgroundItemInfo");//183 x 160   5
		TextureRegion textureRegionCmdItem = GameHelper.$.getGuiAsset("cmdItem");//                         50 x  50   6
		TextureRegion textureRegionCmdItemHighlighted = GameHelper.$.getGuiAsset("cmdItemHighlighted");//   50 x  50   7
//		TextureRegion textureRegionInvBorder = GameHelper.$.getGuiAsset("invBorder.png", 173, 0);//                    192 x 240   8
		TextureRegion textureRegionCmdCatLeft = GameHelper.$.getGuiAsset("cmdCatLeft");//   60 x  41   8.1
		TextureRegion textureRegionCmdCatRight = new TextureRegion(textureRegionCmdCatLeft);
		textureRegionCmdCatRight.flip(true, false);
		TextureRegion textureRegionBackgroundInvInfo = GameHelper.$.getGuiAsset("backgroundInvInfo");//  173 x 107   9
		
		equipPlaceHolders = new HashMap<Integer, Sprite>();
		for(int i = 0; i < 12; i++)
		{
			TextureRegion tr = GameHelper.getInstance().getItemIcon("equip/" + Inventory.getNameBySlot(i) + "_empty");
			equipPlaceHolders.put(i, new Sprite(4, 4, tr.getRegionWidth(), tr.getRegionHeight(), tr));
		}
		
		spriteCmdItemHighlighted = new Sprite(0, 0, textureRegionCmdItemHighlighted);
		
		this.addActor(new Sprite(214, 110, textureRegionBackgroundSorting));//Sorting Background
		this.addActor(new Sprite(394, 11, textureRegionBackgroundItemInfo));//ItemInfo Background
		this.addActor(new Sprite(214, 3, textureRegionBackgroundInvInfo));//InvInfo Background
		
		txtItemInfo = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), Color.WHITE));
		txtItemInfo.setAlignment(Align.top | Align.left);
//		txtItemInfo.setPosition(405, 21);
//		this.addActor(txtItemInfo);
		
		txtItemInfoRed = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), Color.WHITE));
		txtItemInfoRed.setAlignment(Align.top | Align.left);
//		txtItemInfoRed.setPosition(405, 0);
		txtItemInfoRed.setColor(new Color(255 / 255f, 150 / 255f, 130 / 255f, 1));
//		this.addActor(txtItemInfoRed);
		
		txtItemInfoMod = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), Color.WHITE));
		txtItemInfoMod.setAlignment(Align.top | Align.left);
//		txtItemInfoMod.setPosition(405, 0);
		txtItemInfoMod.setColor(new Color(204 / 255f, 51 / 255f, 255 / 255f, 1));
//		this.addActor(txtItemInfoMod);
		
		scrollPane = new com.vhelium.lotig.components.ScrollPane(405, 21, 170, 125);
		scrollPane.setScrollX(false);
		scrollPane.setScrollY(true);
		scrollPane.setFade(GameHelper.$.getGuiAsset("fade"));
		scrollPane.addItemVertical(txtItemInfo);
		scrollPane.addItemVertical(txtItemInfoMod);
		scrollPane.addItemVertical(txtItemInfoRed);
		this.addActor(scrollPane);
		
		txtSoulbound = new Label("Soulbound", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), Color.WHITE));
		txtSoulbound.setAlignment(Align.top | Align.left);
		txtSoulbound.setPosition(405, 150);
		txtSoulbound.setColor(30 / 255f, 225 / 255f, 243 / 255f, 1);
		txtSoulbound.setVisible(false);
		this.addActor(txtSoulbound);
		
		txtInvInfo = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		txtInvInfo.setAlignment(Align.top | Align.center);
		txtInvInfo.setPosition(0, 0);
		this.addActor(txtInvInfo);
		
		txtGoldInfo = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		txtGoldInfo.setAlignment(Align.top | Align.center);
		txtGoldInfo.setPosition(0, 0);
		this.addActor(txtGoldInfo);
		
		txtCurrentCategory = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		txtCurrentCategory.setAlignment(Align.top | Align.center);
		txtCurrentCategory.setPosition(0, 0);
		this.addActor(txtCurrentCategory);
		
		float posX = 402;
		float posY = 240;
		for(int y = 0; y < 4; y++)
		{
			for(int x = 0; x < 3; x++)
			{
				final int nr = x + y * 3;
				ButtonSprite cmdEquip = new ButtonSprite(posX, posY, textureRegionCmdItem, new OnMyClickListener()
				{
					@Override
					public void onClick(ChangeEvent event, Actor actor)
					{
						buttonClicked(nr);
					}
				});
				cmdEquip.addActor(equipPlaceHolders.get(nr));
				this.addActor(cmdEquip);
				equipbuttons.put(nr, cmdEquip);
				posX += 58;
			}
			posX = 402;
			posY += 58;
		}
		
		cmdUseItem = new TextButton(394, 176, textureRegionCmdItems, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Use", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(selectedItemId == -1)
					return;
				if(selectedItemId >= 12)//ScrollList (12-X)
				{
					playerInventory.useItem(getInvItemBySlot(selectedItemId - 12), true);
				}
				else
				//Equip (0-11)
				{
					playerInventory.unequipItem(selectedItemId);
				}
			}
		});
		this.addActor(cmdUseItem);
		cmdUseItem.setEnabled(false);
		cmdUseItem.setText("Use");
		
		cmdDropItem = new TextButton(488, 176, textureRegionCmdItems, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Drop", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(selectedItem instanceof Equip && selectedItemId >= 12)
				{
					SoundManager.playSound(SoundFile.item_destroyed);
					playerInventory.removeItem(getInvItemBySlot(selectedItemId - 12), true);
				}
				else
					dropSelectedItem();
			}
		});
		this.addActor(cmdDropItem);
		cmdDropItem.setEnabled(false);
		
		scrollList = new ScrollList(activity, 214, 151, 173, 326, textureRegionBackgroundList, textureRegionBackgroundListItem, GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), hudCallback.getSceneManager().getCamera())
		{
			@Override
			public void onClick(int id)
			{
				buttonClicked(id + 12);
			}
		};
		this.addActor(scrollList);
		
		cmdCategoryLeft = new ButtonSprite(214, 110, textureRegionCmdCatLeft, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				currentCategoryId--;
				if(currentCategoryId < 0)
					currentCategoryId = categoryList.size() - 1;
				currentCategory = categoryList.get(currentCategoryId);
				updateCategoryText();
				showCategory(currentCategory);
				scrollList.resetPosition();
			}
		});
		this.addActor(cmdCategoryLeft);
		
		cmdCategoryRight = new ButtonSprite(327, 110, textureRegionCmdCatRight, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				currentCategoryId++;
				currentCategoryId = currentCategoryId % categoryList.size();
				currentCategory = categoryList.get(currentCategoryId);
				updateCategoryText();
				showCategory(currentCategory);
				scrollList.resetPosition();
			}
		});
		this.addActor(cmdCategoryRight);
	}
	
	public void buttonClicked(int clickedId)
	{
		if(selectedItemId == clickedId)
			return;
		
		if(clickedId == -1 || (clickedId < 12 && playerInventory.getEquipBySlot(clickedId) == null))
		{
			clickedId = -1;
			selectedItem = null;
			txtItemInfo.setText("");
			txtItemInfoMod.setText("");
			txtItemInfoRed.setText("");
			cmdUseItem.setEnabled(false);
			cmdUseItem.setText("Use");
			cmdDropItem.setEnabled(false);
			cmdDropItem.setText("Drop");
			txtSoulbound.setVisible(false);
			hudCallback.setSelectedPotionStatus(false);
			
			hudCallback.inventoryItemSelected(false);
		}
		else
			SoundManager.playSound(SoundFile.menu_item_selected);
		
		final int id = clickedId;
		
		if(selectedItemId != -1)
		{
			if(selectedItemId >= 12)//ScrollList (12-X)
			{
				scrollList.unHighlightItem(selectedItemId - 12);
			}
			else
			//Equip (0-11)
			{
				unHighlightEquip(selectedItemId);
			}
		}
		selectedItemId = id;
		if(id != -1)
		{
			if(id >= 12)//ScrollList (12-X)
			{
				scrollList.highlightItem(id - 12);
				selectedItem = getInvItemBySlot(id - 12);
				setItemInfoText(false);
				
				if(selectedItem != null && selectedItem.getIsNew())
				{
					selectedItem.setIsNew(false);
					changeItemContent(selectedItemId - 12, selectedItem.toString());
				}
				
				if(selectedItem instanceof EquipWeapon)
				{
					EquipWeapon weapon = (EquipWeapon) selectedItem;
					cmdUseItem.setEnabled(weapon.playerClass.equalsIgnoreCase(player.getPlayerClass()));
					cmdUseItem.setText("Equip");
					cmdDropItem.setEnabled(true);
					cmdDropItem.setText("Destroy");
					txtSoulbound.setVisible(true);
					hudCallback.setSelectedPotionStatus(false);
				}
				else if(selectedItem instanceof EquipOffHand)
				{
					EquipOffHand offhand = (EquipOffHand) selectedItem;
					cmdUseItem.setEnabled(offhand.playerClass.equalsIgnoreCase(player.getPlayerClass()));
					cmdUseItem.setText("Equip");
					cmdDropItem.setEnabled(true);
					cmdDropItem.setText("Destroy");
					txtSoulbound.setVisible(true);
					hudCallback.setSelectedPotionStatus(false);
				}
				else if(selectedItem instanceof Equip)
				{
					cmdUseItem.setEnabled(true);
					cmdUseItem.setText("Equip");
					cmdDropItem.setEnabled(true);
					cmdDropItem.setText("Destroy");
					txtSoulbound.setVisible(true);
					hudCallback.setSelectedPotionStatus(false);
				}
				else if(selectedItem instanceof Potion)
				{
					cmdUseItem.setEnabled(true);
					cmdUseItem.setText("Use");
					cmdDropItem.setEnabled(true);
					cmdDropItem.setText("Drop");
					txtSoulbound.setVisible(false);
					hudCallback.setSelectedPotionStatus(true);
					if(hudCallback.getCurrentOnScreenInfo() != null && hudCallback.getCurrentOnScreenInfo().id == 8/*Item*/)
					{
						cmdUseItem.setEnabled(false);
						cmdDropItem.setEnabled(false);
						cmdDropItem.setText("Drop");
						hudCallback.showOSI(9/*Potion Hotkey*/);
					}
				}
				else if(selectedItem instanceof Glyph)
				{
					cmdUseItem.setText("Use");
					hudCallback.setSelectedPotionStatus(false);
					cmdDropItem.setEnabled(true);
					cmdDropItem.setText("Drop");
					txtSoulbound.setVisible(false);
					if(player.containsSpell(((Glyph) selectedItem).getSpellName()))
						cmdUseItem.setEnabled(true);
					else
						cmdUseItem.setEnabled(false);
				}
				else if(selectedItem instanceof ConsumableItem)
				{
					cmdUseItem.setEnabled(true);
					cmdUseItem.setText("Use");
					cmdDropItem.setEnabled(true);
					cmdDropItem.setText("Drop");
					txtSoulbound.setVisible(false);
					hudCallback.setSelectedPotionStatus(false);
				}
				else
				{
					cmdUseItem.setEnabled(false);
					cmdUseItem.setText("Use");
					cmdDropItem.setEnabled(true);
					cmdDropItem.setText("Drop");
					txtSoulbound.setVisible(false);
					hudCallback.setSelectedPotionStatus(false);
				}
				
				hudCallback.inventoryItemSelected(true);
			}
			else
			//Equip (0-11)
			{
				highlightEquip(id);
				selectedItem = playerInventory.getEquipBySlot(id);
				setItemInfoText(true);// selectedItem.getDescription());
				if(selectedItem instanceof Equip)
				{
					cmdUseItem.setEnabled(true);
					cmdUseItem.setText("Unequip");
					cmdDropItem.setEnabled(false);
					cmdDropItem.setText("Destroy");
					txtSoulbound.setVisible(true);
					hudCallback.setSelectedPotionStatus(false);
				}
				//else if Potion, etc
				else
				{
					cmdUseItem.setEnabled(false);
					cmdUseItem.setText("Use");
					cmdDropItem.setEnabled(false);
					cmdDropItem.setText("Destroy");
					txtSoulbound.setVisible(false);
					hudCallback.setSelectedPotionStatus(false);
				}
				
				hudCallback.inventoryItemSelected(true);
			}
		}
	}
	
	private void dropSelectedItem()
	{
		if(selectedItemId <= -1)
			return;
		
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_DROP_PLAYER_ITEM);
		dp.setString(selectedItem.toStringFormat());
		dp.setFloat(player.getOriginX());
		dp.setFloat(player.getOriginY());
		hudCallback.sendDataPacket(dp);
		
		if(selectedItemId >= 12)//ScrollList (12-X)
		{
			playerInventory.removeItem(getInvItemBySlot(selectedItemId - 12), true);
		}
		else
		//Equip (0-11)
		{
			playerInventory.removeEquip(selectedItemId);
		}
	}
	
	private void highlightEquip(int id)
	{
		equipbuttons.get(id).addActor(spriteCmdItemHighlighted); //isHighlighted = true;
	}
	
	private Item getInvItemBySlot(int slot)
	{
		if(currentCategory == ItemCategory.All)
		{
			int currentSize = 0;
			int prevSize = 0;
			for(int c = 1; c < categoryList.size(); c++)
			{
				currentSize += playerInventory.getItems().get(categoryList.get(c)).size();
				if(currentSize > slot)
				{
					return playerInventory.getItems().get(categoryList.get(c)).get(slot - prevSize);
				}
				prevSize = currentSize;
			}
			return null;
		}
		else
		{
			return playerInventory.getItems().get(currentCategory).get(slot);
		}
	}
	
	private void unHighlightEquip(int id)
	{
		spriteCmdItemHighlighted.remove(); //isHighlighted = false;
	}
	
	private void updateInventoryInfoText()
	{
		txtInvInfo.setText("Inventory:\n" + playerInventory.size() + " / " + playerInventory.getMaxSize());
		txtInvInfo.setColor(1, playerInventory.isFull() ? 0 : 1, playerInventory.isFull() ? 0 : 1, 1);
		txtItemInfo.pack();
		txtInvInfo.setX(216 + 84 - txtInvInfo.getWidth() / 2);
		txtInvInfo.setY(13);
		
		txtGoldInfo.setText("Gold:\n" + playerInventory.getGold());
		txtGoldInfo.pack();
		txtGoldInfo.setX(216 + 84 - txtGoldInfo.getWidth() / 2);
		txtGoldInfo.setY(62);
	}
	
	private void updateCategoryText()
	{
		txtCurrentCategory.setText(currentCategory.toString());
		txtCurrentCategory.setX(248 + 52 - txtCurrentCategory.getWidth() / 2);
		txtCurrentCategory.setY(125);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public Item getSelectedItem()
	{
		return selectedItem;
	}
	
	public Item getSelectedInventoryItem()
	{
		return selectedItem;
	}
	
	public int getSelectedInventoryItemId()
	{
		return selectedItemId;
	}
	
	public void changeItemContent(int id, String content)
	{
		scrollList.changeItemContent(id, content);
	}
	
	public void potionRequestedWhileActive(int slot)
	{
		//It is ensured that the selected item is a potion!
		player.getHotkeyPotions().put(slot, (Potion) selectedItem);
		hudCallback.hotkeyPotionChanged(slot, (Potion) selectedItem);
		buttonClicked(-1);
		
		if(hudCallback.getCurrentOnScreenInfo() != null && hudCallback.getCurrentOnScreenInfo().id == 9/*Potion Hotkey*/)
			hudCallback.showOSI(13/*Close Menu*/);
	}
	
	private void setItemInfoText(boolean isEquiped)
	{
		String[] res = new String[2];
		if(!isEquiped && selectedItem instanceof Equip)
		{
			Equip equipedItem = null;
			if(selectedItem instanceof EquipShoulder && playerInventory.getEquipBySlot(Inventory.SLOT_ID_SHOULDER) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_SHOULDER);
			else if(selectedItem instanceof EquipHelmet && playerInventory.getEquipBySlot(Inventory.SLOT_ID_HELMET) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_HELMET);
			else if(selectedItem instanceof EquipAmulet && playerInventory.getEquipBySlot(Inventory.SLOT_ID_AMULET) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_AMULET);
			else if(selectedItem instanceof EquipBracer && playerInventory.getEquipBySlot(Inventory.SLOT_ID_BRACER) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_BRACER);
			else if(selectedItem instanceof EquipChest && playerInventory.getEquipBySlot(Inventory.SLOT_ID_CHEST) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_CHEST);
			else if(selectedItem instanceof EquipGlove && playerInventory.getEquipBySlot(Inventory.SLOT_ID_GLOVE) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_GLOVE);
			else if(selectedItem instanceof EquipPants && playerInventory.getEquipBySlot(Inventory.SLOT_ID_PANTS) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_PANTS);
			else if(selectedItem instanceof EquipOffHand && playerInventory.getEquipBySlot(Inventory.SLOT_ID_OFFHAND) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_OFFHAND);
			else if(selectedItem instanceof EquipBoots && playerInventory.getEquipBySlot(Inventory.SLOT_ID_BOOTS) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_BOOTS);
			else if(selectedItem instanceof EquipWeapon && playerInventory.getEquipBySlot(Inventory.SLOT_ID_WEAPON) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_WEAPON);
			else if(selectedItem instanceof EquipRing && playerInventory.getEquipBySlot(Inventory.SLOT_ID_RING1) != null && playerInventory.getEquipBySlot(Inventory.SLOT_ID_RING2) != null)
				equipedItem = playerInventory.getEquipBySlot(Inventory.SLOT_ID_RING1);
			
			if(equipedItem != null)
			{
				res = ((Equip) selectedItem).getCompareDescription(equipedItem);
			}
			else
			{
				res[0] = selectedItem.getDescription();
			}
		}
		else
		{
			res[0] = selectedItem.getDescription();
		}
		txtItemInfo.setText(res[0]);
		txtItemInfo.pack();
		
		boolean mod = false;
		if(selectedItem instanceof Equip && ((Equip) selectedItem).modification != null)
		{
			mod = true;
			txtItemInfoMod.setY(txtItemInfo.getY() + txtItemInfo.getHeight() + 2);
			txtItemInfoMod.setText(((Equip) selectedItem).modification.getName());
		}
		else
			txtItemInfoMod.setText("");
		txtItemInfoMod.pack();
		
		if(res[1] != null && !res[1].equals(""))
		{
			if(!mod)
				txtItemInfoRed.setY(txtItemInfo.getY() + txtItemInfo.getHeight() + 2);
			else
				txtItemInfoRed.setY(txtItemInfoMod.getY() + txtItemInfoMod.getHeight() + 2);
			txtItemInfoRed.setText(res[1]);
		}
		else
			txtItemInfoRed.setText("");
		txtItemInfoRed.pack();
		
		scrollPane.updateItems();
	}
	
	public void updateSelectedItemText()
	{
		if(selectedItemId >= 12)
			setItemInfoText(false);
		else if(selectedItemId != -1)
			setItemInfoText(true);
	}
}
