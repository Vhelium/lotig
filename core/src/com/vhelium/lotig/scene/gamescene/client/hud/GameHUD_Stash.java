package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.vhelium.lotig.components.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GameHUD_Stash extends GameHUDMenu
{
	private Main activity;
	private Sprite spriteRight;
	private Label text;
	private Sprite spriteCmdItemHighlighted;
	private final ConcurrentHashMap<Integer, ButtonSprite> buttons;
	private int selectedItemId = -1;
	private TextButton cmdTake;
	private TextButton cmdPut;
	private int currentNr = -1;
	
	public GameHUD_Stash(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		buttons = new ConcurrentHashMap<Integer, ButtonSprite>();
	}
	
	@Override
	public void showUp()
	{
		selectedItemId = -1;
		unHighlightLootItem();
		cmdTake.setEnabled(false);
		
		if(hudCallback.getSelectedInventoryItem() != null && !(hudCallback.getSelectedInventoryItem() instanceof Equip) && player.getInventory().getStash(currentNr).size() < Constants.StashSize)
			cmdPut.setEnabled(true);
		else
			cmdPut.setEnabled(false);
	}
	
	@Override
	public void hidden()
	{
		unHighlightLootItem();
		for(int i = 0; i < buttons.size(); i++)
			buttons.get(i).clearChildren();
	}
	
	@Override
	public void setParam(Integer nr)
	{
		currentNr = nr;
		
		unHighlightLootItem();
		for(int i = 0; i < buttons.size(); i++)
			buttons.get(i).clearChildren();
		for(Entry<Integer, Item> e : player.getInventory().getStash(currentNr).entrySet())
			itemAdded(e.getKey());
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
		
		cmdTake = new TextButton(17, 171, textureRegionCmdLoot, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Take", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.item_picked, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(selectedItemId != -1 && player.getInventory().getStash(currentNr).containsKey(selectedItemId))
				{
					player.getInventory().addItem(player.getInventory().getStash(currentNr).get(selectedItemId), false, true);
					player.getInventory().getStash(currentNr).remove(selectedItemId);
					itemRemoved(selectedItemId);
					player.getLevel().saveStash();
					cmdTake.setEnabled(false);
				}
			}
		});
		spriteRight.addActor(cmdTake);
		
		cmdPut = new TextButton(111, 171, textureRegionCmdLoot, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Put", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.item_put, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(player.getInventory().getStash(currentNr).size() >= Constants.StashSize)
					hudCallback.postMessage("Stash full!", false);
				else
				{
					Item selectedItem = ((GameHUD_Character) hudCallback.getMenu("character")).getInventoryHUD().getSelectedItem();
					if(selectedItem != null && !(selectedItem instanceof Equip))
					{
						int newPos = getEmptyStashPos();
						if(newPos != -1)
						{
							player.getInventory().removeItem(selectedItem, true);
							player.getInventory().getStash(currentNr).put(newPos, selectedItem);
							itemAdded(newPos);
							player.getLevel().saveStash();
							cmdPut.setEnabled(false);
						}
						else
							hudCallback.postMessage("Couldn't add item!", false);
					}
				}
			}
		});
		spriteRight.addActor(cmdPut);
		
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
		cmdTake.setEnabled(false);
		cmdPut.setEnabled(false);
	}
	
	private void buttonClicked(int clickedId)
	{
		if(selectedItemId == clickedId)
			return;
		
		if(clickedId == -1 || !player.getInventory().getStash(currentNr).containsKey(clickedId) || !buttons.containsKey(clickedId))
		{
			clickedId = -1;
			cmdTake.setEnabled(false);
			cmdPut.setEnabled(false);
			text.setText("");
		}
		
		if(selectedItemId != -1)
		{
			unHighlightLootItem();
		}
		
		selectedItemId = clickedId;
		if(clickedId != -1)
		{
			highlightLootItem(clickedId);
			text.setText(player.getInventory().getStash(currentNr).get(selectedItemId).toString());
			cmdTake.setEnabled(true);
			cmdPut.setEnabled(false);
			
			hudCallback.unselectAnyInventoryItem();
		}
	}
	
	private int getEmptyStashPos()
	{
		for(int i = 0; i < Constants.StashSize; i++)
			if(!player.getInventory().getStash(currentNr).containsKey(i))
				return i;
		return -1;
	}
	
	private void highlightLootItem(int id)
	{
		buttons.get(id).addActor(spriteCmdItemHighlighted); //isHighlighted = true;
	}
	
	private void unHighlightLootItem()
	{
		spriteCmdItemHighlighted.remove(); //isHighlighted = false;
		selectedItemId = -1;
		text.setText("");
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	private void itemAdded(int pos)
	{
		buttons.get(pos).addActor(player.getInventory().getStash(currentNr).get(pos).getIcon());
	}
	
	private void itemRemoved(final int pos)
	{
		buttons.get(pos).clearChildren();
		if(pos == selectedItemId)
			unHighlightLootItem();
	}
	
	public void setItemSelected(boolean b)
	{
		selectedItemId = -2;
		Item selectedItem = hudCallback.getSelectedInventoryItem();
		if(b && selectedItem != null)
		{
			buttonClicked(-1);
			if(!(selectedItem instanceof Equip) && player.getInventory().getStash(currentNr).size() < Constants.StashSize)
			{
				cmdPut.setEnabled(true);
				cmdTake.setEnabled(false);
			}
			else
			{
				cmdPut.setEnabled(false);
				cmdTake.setEnabled(false);
			}
		}
		else
		{
			cmdPut.setEnabled(false);
		}
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		super.initHUD(player);
	}
}
