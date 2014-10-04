package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.LootBag;
import com.vhelium.lotig.scene.gamescene.LootBagChest;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class GameHUD_Loot extends GameHUDMenu
{
	private Main activity;
	private Sprite spriteRight;
	private LootBag selectedLootBag;
	private Label text;
	private Sprite spriteCmdItemHighlighted;
	private final ConcurrentHashMap<Integer, ButtonSprite> buttons;
	private int selectedItemId = -1;
	private TextButton cmdLootTake;
	private TextButton cmdLootTakeAll;
	
	public GameHUD_Loot(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		buttons = new ConcurrentHashMap<Integer, ButtonSprite>();
	}
	
	@Override
	public void showUp()
	{
		selectedItemId = -1;
		unHighlightLootItem();
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("right");
		final TextureRegion textureRegionCmdItem = GameHelper.$.getGuiAsset("cmdItem");
		final TextureRegion textureRegionCmdItemHighlighted = GameHelper.$.getGuiAsset("cmdItemHighlighted");
		final TextureRegion textureRegionCmdLoot = GameHelper.$.getGuiAsset("cmdLoot");
		
		spriteCmdItemHighlighted = new Sprite(0, 0, 50, 50, textureRegionCmdItemHighlighted);
		spriteRight = new Sprite(587, 48, textureRegionRight);
		
		cmdLootTake = new TextButton(17, 171, textureRegionCmdLoot, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Take", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.item_picked, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(-1 != selectedItemId)
				{
					Item existingConsumable = player.getInventory().containsConsumableItem(selectedLootBag.getItems().get(selectedItemId));
					if(!player.getInventory().isFull() || existingConsumable != null)
					{
						if(selectedLootBag instanceof LootBagChest)
						{
							DataPacket dp = new DataPacket();
							dp.setInt(MessageType.MSG_REQUEST_CHEST_ITEM);
							dp.setInt(((LootBagChest) selectedLootBag).chest.getId());
							dp.setInt(selectedItemId);
							hudCallback.sendDataPacket(dp);
						}
						else
						{
							DataPacket dp = new DataPacket();
							dp.setInt(MessageType.MSG_REQUEST_LOOT_ITEM);
							dp.setInt(selectedLootBag.id);
							dp.setInt(selectedItemId);
							hudCallback.sendDataPacket(dp);
						}
					}
					else
					{
						hudCallback.postMessage("Inventory full!", false);
					}
				}
			}
		});
		spriteRight.addActor(cmdLootTake);
		
		cmdLootTakeAll = new TextButton(111, 171, textureRegionCmdLoot, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Take All", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.item_picked, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(null != selectedLootBag && 0 < selectedLootBag.getSize())
				{
					if(player.getInventory().hasSpaceFor(selectedLootBag.getHardItemCount()))
					{
						if(selectedLootBag instanceof LootBagChest)
						{
							DataPacket dp = new DataPacket();
							dp.setInt(MessageType.MSG_REQUEST_CHEST_ITEM);
							dp.setInt(((LootBagChest) selectedLootBag).chest.getId());
							dp.setInt(-1);//=all
							hudCallback.sendDataPacket(dp);
						}
						else
						{
							DataPacket dp = new DataPacket();
							dp.setInt(MessageType.MSG_REQUEST_LOOT_ITEM);
							dp.setInt(selectedLootBag.id);
							dp.setInt(-1);//=all
							hudCallback.sendDataPacket(dp);
						}
					}
					else
					{
						//Take as many items as possible
						int space = player.getInventory().getMaxSize() - player.getInventory().size();
						List<Integer> requestedItems = new ArrayList<Integer>();
						for(Entry<Integer, Item> e : selectedLootBag.getItems().entrySet())
						{
							Item existingConsumable = player.getInventory().containsConsumableItem(e.getValue());
							if(existingConsumable != null)
							{
								requestedItems.add(e.getKey());
							}
							else if(space > 0)
							{
								requestedItems.add(e.getKey());
								space--;
							}
						}
						//send DP with request for multiple specific items
						if(selectedLootBag instanceof LootBagChest)
						{
							DataPacket dp = new DataPacket();
							dp.setInt(MessageType.MSG_REQUEST_CHEST_ITEM);
							dp.setInt(((LootBagChest) selectedLootBag).chest.getId());
							dp.setInt(-2);//=multiple specific
							dp.setInt(requestedItems.size());
							for(Integer i : requestedItems)
								dp.setInt(i);
							hudCallback.sendDataPacket(dp);
						}
						else
						{
							DataPacket dp = new DataPacket();
							dp.setInt(MessageType.MSG_REQUEST_LOOT_ITEM);
							dp.setInt(selectedLootBag.id);
							dp.setInt(-2);//=multiple specific
							dp.setInt(requestedItems.size());
							for(Integer i : requestedItems)
								dp.setInt(i);
							hudCallback.sendDataPacket(dp);
						}
						//post Inv full because it cannot hold all items!
						hudCallback.postMessage("Inventory full!", false);
					}
				}
			}
		});
		spriteRight.addActor(cmdLootTakeAll);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(17, 136);
		spriteRight.addActor(text);
		
		float posX = 17;
		float posY = 12;
		for(int y = 0; y < 2; y++)
		{
			for(int x = 0; x < 3; x++)
			{
				final int nr = x + y * 3;
				ButtonSprite cmdLoot = new ButtonSprite(posX, posY, textureRegionCmdItem, new OnMyClickListener()
				{
					@Override
					public void onClick(ChangeEvent event, Actor actor)
					{
						buttonClicked(nr);
					}
				});
				cmdLoot.setWidth(50);
				cmdLoot.setHeight(50);
				spriteRight.addActor(cmdLoot);
				buttons.put(nr, cmdLoot);
				posX += 64;
			}
			posX = 17;
			posY += 59;
		}
		
		this.addActor(spriteRight);
		cmdLootTake.setEnabled(false);
		cmdLootTakeAll.setEnabled(false);
	}
	
	private void buttonClicked(int clickedId)
	{
		if(selectedItemId == clickedId)
			return;
		
		if(clickedId == -1 || selectedLootBag == null || !selectedLootBag.getItems().containsKey(clickedId) || !buttons.containsKey(clickedId))
		{
			clickedId = -1;
			cmdLootTake.setEnabled(false);
			if(null != selectedLootBag && 0 < selectedLootBag.getSize())
			{
				cmdLootTakeAll.setEnabled(true);
			}
			text.setText("");
		}
		final int id = clickedId;
		
		if(selectedItemId != -1)
		{
			unHighlightLootItem();
		}
		
		selectedItemId = id;
		if(id != -1)
		{
			highlightLootItem(id);
			text.setText(selectedLootBag.getItems().get(selectedItemId).toString());
			cmdLootTake.setEnabled(true);
			cmdLootTakeAll.setEnabled(true);
		}
	}
	
	private void highlightLootItem(int id)
	{
		buttons.get(id).addActor(spriteCmdItemHighlighted); //isHighlighted = true;
		cmdLootTake.setEnabled(true);
	}
	
	private void unHighlightLootItem()
	{
		spriteCmdItemHighlighted.remove(); //isHighlighted = false;
		selectedItemId = -1;
		text.setText("");
		cmdLootTake.setEnabled(false);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public void setLootBagInRange(LootBag bag)
	{
		if(selectedLootBag == bag)
			return;
		
		if(selectedLootBag instanceof LootBagChest)//bag wants to override fakeBag even though fakeBag is nearer
		{
			if(bag == null && !canRemoveFakeBag)
				return;
			if(bag != null)
			{
				double distBag = Math.sqrt(Math.pow(bag.getX() - player.getOriginX(), 2) + Math.pow(bag.getY() - player.getOriginY(), 2));
				double distFake = Math.sqrt(Math.pow(selectedLootBag.getX() - player.getOriginX(), 2) + Math.pow(selectedLootBag.getY() - player.getOriginY(), 2));
				if(distBag > distFake)
					return;
			}
		}
		selectedLootBag = bag;
		
		if(selectedLootBag == null && this.isActive)
			hudCallback.hideForced("loot");
		else if(selectedLootBag != null && !this.isActive)
		{
			hudCallback.hideForced("action");
			hudCallback.show("loot");
		}
		unHighlightLootItem();
		for(ButtonSprite cmd : buttons.values())
		{
			cmd.clearChildren();
		}
		
		cmdLootTake.setEnabled(false);
		cmdLootTakeAll.setEnabled(true);
		
		if(selectedLootBag != null)
		{
			for(Entry<Integer, Item> e : selectedLootBag.getItems().entrySet())
				buttons.get(e.getKey()).addActor(e.getValue().getIcon());
		}
	}
	
	boolean canRemoveFakeBag = false;
	
	public void fakeLootBagLeft()
	{
		canRemoveFakeBag = true;
		setLootBagInRange(null);
		canRemoveFakeBag = false;
	}
	
	public void lootBagItemAdded(LootBag bag, final int pos)
	{
		if(bag == selectedLootBag)
		{
			selectedLootBag.getItems().get(pos).getIcon().remove();
			buttons.get(pos).addActor(selectedLootBag.getItems().get(pos).getIcon());
		}
	}
	
	public void lootBagItemRemoved(final LootBag bag, final int pos)
	{
		if(bag == selectedLootBag)
		{
			buttons.get(pos).clearChildren();
			if(pos == selectedItemId)
				unHighlightLootItem();
		}
	}
	
	public void lootBagRemoved(LootBag bag)
	{
		if(bag == selectedLootBag)
			setLootBagInRange(null);
	}
}
