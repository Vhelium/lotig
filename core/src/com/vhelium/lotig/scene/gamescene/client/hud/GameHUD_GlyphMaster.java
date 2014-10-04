package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.ScrollList;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Glyph;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GameHUD_GlyphMaster extends GameHUDMenu
{
	private Main activity;
	private Sprite spriteBackground;
	private Label text;
	private Label txtInv;
	private ScrollList scrollListAll;
	private ScrollList scrollListSelected;
	private TextButton cmdAdd;
	private TextButton cmdRemove;
	private TextButton cmdTrade;
	private int selectedItemId = -1;
	private Item selectedItem = null;
	private final List<Item> selectedGlyphs = new ArrayList<Item>();
	
	public GameHUD_GlyphMaster(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		releaseSelectedGlyphs();
		buttonClicked(-1);
		displayAllGlyphs();
		displaySelectedGlyphs();
		updateTradeButton();
	}
	
	@Override
	public void hidden()
	{
		showUp();
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		super.initHUD(player);
		showUp();
	}
	
	private void releaseSelectedGlyphs()
	{
		for(Item item : selectedGlyphs)
			player.getInventory().addItem(item, false, false);
		selectedGlyphs.clear();
		player.getInventory().doUpdate(ItemCategory.Glyph);
	}
	
	@Override
	public void loadResources(final Main activity)
	{
		this.activity = activity;
		
		TextureRegion textureRegionBackground = GameHelper.$.getGuiAsset("backgroundGlyphMaster");//374 x 480
		TextureRegion textureRegionBackgroundListItem = GameHelper.$.getGuiAsset("backgroundListItem");//173 x 38
		TextureRegion textureRegionBackgroundList = GameHelper.$.getGuiAsset("backgroundList");//173 x 354
		TextureRegion textureRegionCmdItems = GameHelper.$.getGuiAsset("cmdItems");//89 x 50
		
		spriteBackground = new Sprite(213, 0, textureRegionBackground);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 16), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(15, 20);
		text.setText("Select 1-4 glyphs to trade.\n\nTrade 1-2 glyphs for a random glyph.\nTrade 3-4 glyphs for a random class glyph.");
		spriteBackground.addActor(text);
		
		txtInv = new Label("Inventory:", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 16), Color.WHITE));
		txtInv.setAlignment(Align.top | Align.left);
		txtInv.setPosition(15, 151 - 26);
		spriteBackground.addActor(txtInv);
		
		this.addActor(spriteBackground);
		
		cmdAdd = new TextButton(394, 151, textureRegionCmdItems, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Add", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_spell_assigned, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(selectedItemId == -1 || selectedGlyphs.size() >= 4)
					return;
				if(selectedItemId >= 4)//ScrollList (4-X)
				{
					if(selectedItem instanceof StackableItem)
					{
						StackableItem stItem = ((StackableItem) selectedItem);
						
						//Remove from inventory:
						if(stItem.getCount() > 1)
							stItem.countMinus();
						else
							player.getInventory().removeItem(stItem, true);
						
						//Add new copy to list:
						StackableItem newGlyph = stItem.oneCopy();
						selectedGlyphs.add(newGlyph);
						
						buttonClicked(-1);
						displayAllGlyphs();
						displaySelectedGlyphs();
						if(selectedGlyphs.size() >= 4)
							cmdAdd.setEnabled(false);
						
						updateTradeButton();
					}
				}
			}
		});
		this.addActor(cmdAdd);
		cmdAdd.setEnabled(false);
		
		cmdRemove = new TextButton(488, 151, textureRegionCmdItems, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Remove", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_spell_removed, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(selectedItemId == -1 || selectedGlyphs.size() == 0)
					return;
				if(selectedItemId < 4)
				{
					selectedGlyphs.remove(selectedItem);
					player.getInventory().addItem(selectedItem, false, true);
					buttonClicked(-1);
					displayAllGlyphs();
					displaySelectedGlyphs();
					
					updateTradeButton();
				}
			}
		});
		this.addActor(cmdRemove);
		cmdRemove.setEnabled(false);
		
		scrollListAll = new ScrollList(activity, 214, 151, 173, 320, textureRegionBackgroundList, textureRegionBackgroundListItem, GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), hudCallback.getSceneManager().getCamera())
		{
			@Override
			public void onClick(int id)
			{
				buttonClicked(id + 4);
			}
		};
		this.addActor(scrollListAll);
		
		scrollListSelected = new ScrollList(activity, cmdAdd.getX(), 151 + cmdAdd.getHeight() + 10, (int) (cmdRemove.getX() + cmdRemove.getWidth() - cmdAdd.getX()), 149, textureRegionBackgroundList, textureRegionBackgroundListItem, GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), hudCallback.getSceneManager().getCamera())
		{
			@Override
			public void onClick(int id)
			{
				buttonClicked(id);
			}
		};
		this.addActor(scrollListSelected);
		
		cmdTrade = new TextButton(cmdAdd.getX(), scrollListSelected.getY() + scrollListSelected.getHeight() + 18, textureRegionCmdItems, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Remove", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_glyphmaster_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				int value = 0;
				if(selectedGlyphs.size() % 2 == 1)
					value = Price.glyphMasterOneOrThree;
				else
					value = Price.glyphMasterTwoOrFour;
				
				if(player.getInventory().getGold() >= value)
				{
					player.getInventory().removeGold(value);
					
					Item glyph;
					if(selectedGlyphs.size() >= 3)
						glyph = Glyph.getGlyph(Glyph.getRandomSpell(player.getPlayerClass()), 1);
					else
						glyph = Glyph.getRandomGlyph(1);
					
					player.getInventory().addItem(glyph, true, true);
					selectedGlyphs.clear();
					
					buttonClicked(-1);
					displayAllGlyphs();
					displaySelectedGlyphs();
					updateTradeButton();
					
					player.getLevel().saveGame();
					
					hudCallback.postMessageLong("Item received: " + glyph.NAME, true);
					
					//play effect on hud
				}
			}
		});
		cmdTrade.setWidth(cmdRemove.getX() + cmdRemove.getWidth() - cmdAdd.getX());
		cmdTrade.setHeight(80);
		this.addActor(cmdTrade);
		cmdTrade.setEnabled(false);
	}
	
	public void buttonClicked(int clickedId)
	{
		if(selectedItemId == clickedId)
			return;
		
		if(clickedId == -1)
		{
			clickedId = -1;
			selectedItem = null;
			cmdAdd.setEnabled(false);
			cmdRemove.setEnabled(false);
		}
		
		final int id = clickedId;
		
		if(selectedItemId != -1)
		{
			if(selectedItemId >= 4)//ScrollList (4-X)
			{
				scrollListAll.unHighlightItem(selectedItemId - 4);
			}
			else
			//Other list (0-3)
			{
				scrollListSelected.unHighlightItem(selectedItemId);
			}
		}
		selectedItemId = id;
		if(id != -1)
		{
			if(id >= 4)//ScrollList (4-X)
			{
				selectedItem = getInvGlyphBySlot(id - 4);
				scrollListAll.highlightItem(id - 4);
				
				if(selectedGlyphs.size() >= 4)
					cmdAdd.setEnabled(false);
				else
					cmdAdd.setEnabled(true);
				cmdRemove.setEnabled(false);
			}
			else
			//Other list (0-3)
			{
				selectedItem = selectedGlyphs.get(id);
				scrollListSelected.highlightItem(id);
				
				cmdAdd.setEnabled(false);
				cmdRemove.setEnabled(true);
			}
		}
	}
	
	private void updateTradeButton()
	{
		if(selectedGlyphs.size() > 0)
		{
			int value = 0;
			if(selectedGlyphs.size() % 2 == 1)
				value = Price.glyphMasterOneOrThree;
			else
				value = Price.glyphMasterTwoOrFour;
			
			cmdTrade.setText("Trade for\n" + value + " G");
			
			if(player.getInventory().getGold() >= value)
				cmdTrade.setEnabled(true);
			else
				cmdTrade.setEnabled(false);
		}
		else
		{
			cmdTrade.setEnabled(false);
			cmdTrade.setText("Trade");
		}
	}
	
	private void displayAllGlyphs()
	{
		ArrayList<String> content = new ArrayList<String>();
		for(Item item : player.getInventory().getItems().get(ItemCategory.Glyph))
		{
			content.add(item.toStringTrimmed(scrollListAll.getFont(), scrollListAll.getElementTextWidth()));
		}
		
		scrollListAll.setContent(content, "empty");
	}
	
	private void displaySelectedGlyphs()
	{
		ArrayList<String> content = new ArrayList<String>();
		for(Item item : selectedGlyphs)
		{
			content.add(item.toStringTrimmed(scrollListSelected.getFont(), scrollListSelected.getElementTextWidth()));
		}
		
		scrollListSelected.setContent(content, "no glyph selected");
	}
	
	private Item getInvGlyphBySlot(int slot)
	{
		return player.getInventory().getItems().get(ItemCategory.Glyph).get(slot);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
