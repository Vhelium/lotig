package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class GameHUD_Enchanting extends GameHUDMenu
{
	private TextButton cmdEnchant;
	private Label text;
	String stringFormat;
	Sprite spriteCmdItem;
	Item item;
	
	public GameHUD_Enchanting(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		Item selectedItem = hudCallback.getSelectedInventoryItem();
		if(selectedItem != null && selectedItem instanceof Equip)
		{
			if(player.getInventory().getGold() >= getCost(selectedItem.getScore()))
			{
				text.setText(selectedItem.getDescription());
				cmdEnchant.setText("Enchant (" + getCost(selectedItem.getScore()) + " G)");
				cmdEnchant.setEnabled(true);
			}
			else
			{
				text.setText("Not enough gold!");
				cmdEnchant.setText("Enchant (" + getCost(selectedItem.getScore()) + " G)");
				cmdEnchant.setEnabled(false);
			}
		}
		else
		{
			text.setText("Select an equippable\nitem in the inventory\nto enchant.");
			cmdEnchant.setText("Enchant");
			cmdEnchant.setEnabled(false);
		}
	}
	
	@Override
	public void loadResources(Main activity)
	{
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("right");
		final TextureRegion textureRegionCmdLoot = GameHelper.$.getGuiAsset("cmdEnchanting");
		
		Sprite spriteRight = new Sprite(587, 48, textureRegionRight);
		
		cmdEnchant = new TextButton(17, 171, textureRegionCmdLoot, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Enchant", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_enchanting_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				Item selectedItem = hudCallback.getSelectedInventoryItem();
				int selectedItemId = hudCallback.getSelectedInventoryItemId();
				if(selectedItem != null && selectedItem instanceof Equip)
				{
					Equip eq = ((Equip) selectedItem);
					for(String attr : eq.getAttributesNames())
					{
						eq.increaseAttribute(attr, 10);
						text.setText(selectedItem.getDescription());
						hudCallback.updateInventorySelectedItemText();
						if(selectedItemId >= 12)//is in inventory
							((GameHUD_Character) hudCallback.getMenu("character")).getInventoryHUD().changeItemContent(selectedItemId - 12, eq.toString());
						player.onAttributeChanged();
					}
					
					player.getInventory().removeGold(getCost(selectedItem.getScore()));
					
					setItemSelected(true);
				}
			}
		});
		spriteRight.addActor(cmdEnchant);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(17, 17);
		spriteRight.addActor(text);
		
		this.addActor(spriteRight);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public void setItemSelected(boolean b)
	{
		Item selectedItem = hudCallback.getSelectedInventoryItem();
		if(b && selectedItem != null && selectedItem instanceof Equip)
		{
			if(player.getInventory().getGold() >= getCost(selectedItem.getScore()))
			{
				text.setText(selectedItem.getDescription());
				cmdEnchant.setText("Enchant (" + getCost(selectedItem.getScore()) + " G)");
				cmdEnchant.setEnabled(true);
			}
			else
			{
				text.setText("Not enough gold!");
				cmdEnchant.setText("Enchant (" + getCost(selectedItem.getScore()) + " G)");
				cmdEnchant.setEnabled(false);
			}
		}
		else
		{
			text.setText("Select an equippable\nitem in the inventory\nto enchant.");
			cmdEnchant.setText("Enchant");
			cmdEnchant.setEnabled(false);
		}
	}
	
	private static int getCost(int ilvl)
	{
		return Price.EnchantementBase + ilvl * Price.EnchantementPerItemLevel;
	}
}
