package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.StringTokenizer;
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
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class GameHUD_Selling extends GameHUDMenu
{
	private TextButton cmdBuy;
	private TextButton cmdSell;
	private Label text;
	String stringFormat;
	Sprite spriteCmdItem;
	Item item;
	int price;
	
	public GameHUD_Selling(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		StringTokenizer st = new StringTokenizer(param.toString(), ";");
		stringFormat = st.nextToken();
		item = Item.getItemFromStringFormat(stringFormat);
		price = Integer.parseInt(st.nextToken());
		text.setText(item.getDescription());
		text.setX(106 - text.getWidth() / 2);
		
		spriteCmdItem.clearChildren();
		spriteCmdItem.addActor(item.getIcon());
		
		cmdBuy.setText("Buy\n" + price + "G");
		
		if(player.getInventory().getGold() >= price)
			cmdBuy.setEnabled(true);
		else
			cmdBuy.setEnabled(false);
		
		if(hudCallback.getSelectedInventoryItem() != null)
		{
			cmdSell.setEnabled(true);
			cmdSell.setText("Sell\n" + hudCallback.getSelectedInventoryItem().getSellPrice() + "G");
		}
		else
		{
			cmdSell.setEnabled(false);
			cmdSell.setText("Sell");
		}
	}
	
	@Override
	public void loadResources(Main activity)
	{
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("right");
		final TextureRegion textureRegionCmdBuy = GameHelper.$.getGuiAsset("cmdBuy");
		final TextureRegion textureRegionCmdSell = GameHelper.$.getGuiAsset("cmdSell");
		final TextureRegion textureRegionCmdItem = GameHelper.$.getGuiAsset("cmdItem");
		
		Sprite spriteRight = new Sprite(587, 48, textureRegionRight);
		spriteCmdItem = new Sprite(textureRegionRight.getRegionWidth() / 2 - textureRegionCmdItem.getRegionWidth() / 2, 11, 50, 50, textureRegionCmdItem);
		spriteRight.addActor(spriteCmdItem);
		
		cmdBuy = new TextButton(17, 161, textureRegionCmdBuy, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Buy\n" + price + "G", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(player.getInventory().getGold() >= price)
				{
					Item existingConsumable = player.getInventory().containsConsumableItem(item);
					if(!player.getInventory().isFull() || existingConsumable != null)
					{
						player.getInventory().removeGold(price);
						player.getInventory().addItem(Item.getItemFromStringFormat(stringFormat), true, true);
						SoundManager.playSound(SoundFile.item_bought);
						
						if(player.getInventory().getGold() >= price)
							cmdBuy.setEnabled(true);
						else
							cmdBuy.setEnabled(false);
					}
					else
						hudCallback.postMessage("Inventory full!", false);
				}
			}
		});
		spriteRight.addActor(cmdBuy);
		
		cmdSell = new TextButton(111, 161, textureRegionCmdSell, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Sell", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				Item selectedItem = ((GameHUD_Character) hudCallback.getMenu("character")).getInventoryHUD().getSelectedItem();
				if(selectedItem != null)
				{
					player.getInventory().sellItem(selectedItem);
					SoundManager.playSound(SoundFile.item_sold);
					
					if(player.getInventory().getGold() >= price)
						cmdBuy.setEnabled(true);
					else
						cmdBuy.setEnabled(false);
				}
			}
		});
		spriteRight.addActor(cmdSell);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), Color.WHITE));
		text.setAlignment(Align.top | Align.center);
		text.setPosition(17, 70);
		spriteRight.addActor(text);
		
		this.addActor(spriteRight);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public void setItemSelected(boolean b)
	{
		if(b && hudCallback.getSelectedInventoryItem() != null)
		{
			cmdSell.setEnabled(true);
			cmdSell.setText("Sell\n" + hudCallback.getSelectedInventoryItem().getSellPrice() + "G");
		}
		else
		{
			cmdSell.setEnabled(false);
			cmdSell.setText("Sell");
		}
	}
}
