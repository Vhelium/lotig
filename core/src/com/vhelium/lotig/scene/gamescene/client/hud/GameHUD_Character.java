package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.HashMap;
import java.util.Map.Entry;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GameHUD_Character extends GameHUDMenu
{
	private Sprite spriteBackground;
	private GameSubHUD_Status hud_Status;
	private GameSubHUD_Inventory hud_Inventory;
	private GameSubHUD_Spells hud_Spells;
	private GameSubHUD_Quests hud_Quests;
	private Label txtPlayer;
	private boolean hasNewQuest;
	
	private final HashMap<String, TextButton> menuButtons;
	
	public GameHUD_Character(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		menuButtons = new HashMap<String, TextButton>();
		this.setTouchable(Touchable.childrenOnly);
	}
	
	@Override
	public void showUp()
	{
		if(!hasNewQuest)
		{
			highlightButton("status");
			hudCallback.setSub(GameHUD_Character.this, hud_Status);
		}
		else
		{
			highlightButton("quests");
			hudCallback.setSub(GameHUD_Character.this, hud_Quests);
		}
		
		if(hudCallback.getCurrentOnScreenInfo() != null)
		{
			if(hudCallback.getCurrentOnScreenInfo().id == 3/*Potion*/)
				hudCallback.showOSI(7/*Inventory*/);
			else if(hudCallback.getCurrentOnScreenInfo().id == 4/*Spell*/)
				hudCallback.showOSI(10/*Spells*/);
		}
	}
	
	@Override
	public void setParam(final Integer i)
	{
		if(i == 0)
		{
			hasNewQuest = false;
			menuButtons.get("quests").getLabel().clearActions();
			menuButtons.get("quests").getLabel().setColor(new Color(210 / 255f, 210 / 255f, 210 / 255f, 1f));
		}
		else if(i == 1)
		{
			hasNewQuest = true;
			menuButtons.get("quests").getLabel().clearActions();
			menuButtons.get("quests").getLabel().addAction(Actions.forever(Actions.sequence(Actions.color(new Color(0, 1, 0, 1), 0.5f), Actions.color(new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), 0.5f))));
		}
	}
	
	@Override
	public void hidden()
	{
		if(!hasNewQuest)
		{
			highlightButton("status");
			hudCallback.setSub(GameHUD_Character.this, hud_Status);
		}
		else
		{
			highlightButton("quests");
			hudCallback.setSub(GameHUD_Character.this, hud_Quests);
		}
	}
	
	@Override
	public void loadResources(Main activity)
	{
		TextureRegion textureRegionBackground = GameHelper.$.getGuiAsset("background");
		TextureRegion textureRegionCmdStatus = GameHelper.$.getGuiAsset("cmdCharMenuItem");
		
		spriteBackground = new Sprite(0, 0, textureRegionBackground);
		spriteBackground.setTouchable(Touchable.childrenOnly);
		this.addActor(spriteBackground);
		
		txtPlayer = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		txtPlayer.setAlignment(Align.top | Align.left);
		txtPlayer.setPosition(32, 14);
		this.addActor(txtPlayer);
		
		TextButton cmdStatus = new TextButton(19, 89, textureRegionCmdStatus, GameHelper.getInstance().getMainFont(FontCategory.InGame, 24), "Status", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				highlightButton("status");
				hudCallback.setSub(GameHUD_Character.this, hud_Status);
			}
		});
		this.addActor(cmdStatus);
		menuButtons.put("status", cmdStatus);
		
		TextButton cmdInventory = new TextButton(19, 133, textureRegionCmdStatus, GameHelper.getInstance().getMainFont(FontCategory.InGame, 24), "Inventory", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				highlightButton("inventory");
				hudCallback.setSub(GameHUD_Character.this, hud_Inventory);
			}
		});
		this.addActor(cmdInventory);
		menuButtons.put("inventory", cmdInventory);
		
		TextButton cmdSpells = new TextButton(19, 177, textureRegionCmdStatus, GameHelper.getInstance().getMainFont(FontCategory.InGame, 24), "Spells", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				highlightButton("spells");
				hudCallback.setSub(GameHUD_Character.this, hud_Spells);
			}
		});
		this.addActor(cmdSpells);
		menuButtons.put("spells", cmdSpells);
		
		TextButton cmdQuests = new TextButton(19, 221, textureRegionCmdStatus, GameHelper.getInstance().getMainFont(FontCategory.InGame, 24), "Quests", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				highlightButton("quests");
				hudCallback.setSub(GameHUD_Character.this, hud_Quests);
			}
		});
		this.addActor(cmdQuests);
		menuButtons.put("quests", cmdQuests);
		
		hud_Status = new GameSubHUD_Status(hudCallback);
		hud_Status.loadResources(activity);
		
		hud_Inventory = new GameSubHUD_Inventory(hudCallback);
		hud_Inventory.loadResources(activity);
		
		hud_Spells = new GameSubHUD_Spells(hudCallback);
		hud_Spells.loadResources(activity);
		
		hud_Quests = new GameSubHUD_Quests(hudCallback);
		hud_Quests.loadResources(activity);
	}
	
	public void highlightButton(String button)
	{
		for(Entry<String, TextButton> e : menuButtons.entrySet())
		{
			e.getValue().setHighlight(false);
		}
		menuButtons.get(button).getLabel().clearActions();
		menuButtons.get(button).setHighlight(true);
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		super.initHUD(player);
		hud_Status.initHUD(player);
		hud_Inventory.initHUD(player);
		hud_Spells.initHUD(player);
		hud_Quests.initHUD(player);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public GameSubHUD_Spells getSpellHUD()
	{
		return hud_Spells;
	}
	
	public GameSubHUD_Inventory getInventoryHUD()
	{
		return hud_Inventory;
	}
	
	public GameSubHUD_Quests getQuestHUD()
	{
		return hud_Quests;
	}
	
	public void updatePlayerText()
	{
		txtPlayer.setText("Class: " + player.getPlayerClass() + "\nTitle: " + player.getTitle() + "\nEquip level: " + player.getInventory().getEquipLevel());
		update(0);
	}
}
