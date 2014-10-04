package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.HashMap;
import java.util.Map.Entry;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.ShapeRendererBatch;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.HUD;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GameScene.ConnectionType;
import com.vhelium.lotig.scene.gamescene.LootBag;
import com.vhelium.lotig.scene.gamescene.OnScreenInfo;
import com.vhelium.lotig.scene.gamescene.QuestLog;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Potion;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.maps.Minimap;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class GameHUD extends HUD
{
	private final Main activity;
	
	private final ClientLevel level;
	
	private GameHUD_Status hud_Status;
	private GameHUD_SpellButtons hud_SpellButtons;
	private GameHUD_PotionButtons hud_PotionButtons;
	private GameHUD_Tools hud_Tools;
	private GameHUD_Character hud_Character;
	private GameHUD_Connection hud_Connection;
	private GameHUD_PvPScores hud_PvPScores;
	private GameHUD_ArenaRounds hud_ArenaRounds;
	private GameHUD_Boss hud_Boss;
	private GameHUD_Fame hud_Fame;
	private GameHUD_Gems hud_Gems;
	private GameHUD_GlyphMaster hud_GlyphMaster;
	private GameHUD_Reward hud_QuestReward;
	private GameHUD_SkinSelection hud_SkinSelection;
	private GameHUD_DefenseTower hud_DefenseTower;
	private GameHUD_TDInfo hud_TDInfo;
	
	private GameHUD_Loot hud_Loot;
	private GameHUD_Action hud_Action;
	private GameHUD_Selling hud_Selling;
	private GameHUD_Enchanting hud_Enchanting;
	private GameHUD_Stash hud_Stash;
	private GameHUD_Menu hud_Menu;
	private GameHUD_Tipps hud_Tipps;
	private GameHUD_Settings hud_Settings;
	
	private Minimap minimap;
	private Sprite minimapButton;
	private QuestLog questLog;
	private Sprite questLogButton;
	
	private Label postText;
	private Sprite postBG;
	
	private final HashMap<String, GameHUDMenu> allMenus = new HashMap<String, GameHUDMenu>();
	
	private final HashMap<String, GameHUDMenu> staticMenus = new HashMap<String, GameHUDMenu>();
	private final HashMap<String, GameHUDMenu> rightMenus = new HashMap<String, GameHUDMenu>();
	private final HashMap<String, GameHUDMenu> centerMenus = new HashMap<String, GameHUDMenu>();
	
	public GameHUD(Main activity, ClientLevel level, SpriteBatch batch, ShapeRendererBatch shape)
	{
		super(batch, shape);
		this.activity = activity;
		this.level = level;
	}
	
	private final IGameHUDCallback hudCallback = new IGameHUDCallback()
	{
		@Override
		public void hide(final String menu)
		{
			hideMenu(menu);
		}
		
		@Override
		public void hideForced(String menu)
		{
			hideMenuForced(menu);
		}
		
		@Override
		public void show(final String menu)
		{
			showMenu(menu);
		}
		
		@Override
		public void toggle(final String menu)
		{
			toggleMenu(menu);
		}
		
		@Override
		public void townPort()
		{
			level.townPort();
		}
		
		@Override
		public void setSub(final GameHUDMenu mainMenu, final GameHUDMenu subMenu)
		{
			setSubMenu(mainMenu, subMenu);
		}
		
		@Override
		public void hideSub(final GameHUDMenu mainMenu, final GameHUDMenu subMenu)
		{
			hideSubMenu(mainMenu, subMenu);
		}
		
		@Override
		public String getConnectionType()
		{
			if(level.getConnectionType() == ConnectionType.Singleplayer)
				return "Singleplayer";
			else if(level.getConnectionType() == ConnectionType.Bluetooth)
			{
				if(level.isHost())
					return "Bluetooth Server";
				else
					return "Bluetooth Client";
			}
			else if(level.getConnectionType() == ConnectionType.Wlan)
			{
				if(level.isHost())
					return "Wlan Server";
				else
					return "Wlan Client";
			}
			else
				return "NONE";
		}
		
		@Override
		public void sendDataPacket(DataPacket dp)
		{
			level.sendDataPacket(dp);
		}
		
		@Override
		public void requestSpell(int slot)
		{
			if(hud_Character.getSpellHUD().isActive)
			{
				hud_Character.getSpellHUD().spellRequestedWhileActive(slot);
			}
			else
				level.getPlayer().requestSpell(slot);
		}
		
		@Override
		public void selectedSpellStatusChanged(int nr)
		{
			hud_SpellButtons.selectedSpellStatusChanged(nr);
		}
		
		@Override
		public void hotkeySpellChanged(int slot, Spell spell)
		{
			hud_SpellButtons.hotkeySpellChanged(slot, spell);
		}
		
		@Override
		public void setSpellPrepared(int slot, boolean prepared)
		{
			hud_SpellButtons.setSpellPrepared(slot, prepared);
		}
		
		@Override
		public void playSpellOutOfMana(int slot)
		{
			hud_SpellButtons.playSpellOutOfMana(slot);
		}
		
		@Override
		public void requestPotion(int slot)
		{
			if(hud_Character.getInventoryHUD().isActive && hud_Character.getInventoryHUD().getSelectedItem() instanceof Potion)
			{
				hud_Character.getInventoryHUD().potionRequestedWhileActive(slot);
			}
			else if(!hud_PotionButtons.isOnCD(slot))
				level.getPlayer().requestPotion(slot);
		}
		
		@Override
		public void hotkeyPotionChanged(int slot, Potion potion)
		{
			hud_PotionButtons.hotkeyPotionChanged(slot, potion);
		}
		
		@Override
		public void setSelectedPotionStatus(boolean value)
		{
			hud_PotionButtons.setSelectedPotionStatus(value);
		}
		
		@Override
		public GameHUDMenu getMenu(String menu)
		{
			return allMenus.get(menu);
		}
		
		@Override
		public Item getSelectedInventoryItem()
		{
			return hud_Character.getInventoryHUD().getSelectedInventoryItem();
		}
		
		@Override
		public int getSelectedInventoryItemId()
		{
			return hud_Character.getInventoryHUD().getSelectedInventoryItemId();
		}
		
		@Override
		public void updateSpellLevelText()
		{
			hud_Character.getSpellHUD().updateText();
		}
		
		@Override
		public void postMessageLong(String text, boolean white)
		{
			GameHUD.this.postMessage(text, white, true);
		}
		
		@Override
		public void postMessage(String text, boolean white)
		{
			GameHUD.this.postMessage(text, white, false);
		}
		
		@Override
		public void updateQuestText()
		{
			if(hud_Character.getQuestHUD().isActive)
				hud_Character.getQuestHUD().updateText();
			if(questLog.isActive)
				questLog.updateText(level.getPlayer());
		}
		
		@Override
		public void inventoryItemSelected(boolean isSelected)
		{
			if(hud_Selling.isActive)
				hud_Selling.setItemSelected(isSelected);
			if(hud_Enchanting.isActive)
				hud_Enchanting.setItemSelected(isSelected);
			if(hud_Stash.isActive)
				hud_Stash.setItemSelected(isSelected);
		}
		
		@Override
		public void updateInventorySelectedItemText()
		{
			hud_Character.getInventoryHUD().updateSelectedItemText();
		}
		
		@Override
		public void updatePlayerText()
		{
			hud_Character.updatePlayerText();
		}
		
		@Override
		public void unselectAnyInventoryItem()
		{
			if(hud_Character.getInventoryHUD().isActive)
				hud_Character.getInventoryHUD().buttonClicked(-1);
		}
		
		@Override
		public OnScreenInfo getCurrentOnScreenInfo()
		{
			return currentOSI;
		}
		
		@Override
		public void showOSI(int id)
		{
			showOnScreenInfo(id);
		}
		
		@Override
		public void hideOSI(int id)
		{
			hideOnScreenInfo(id);
		}
		
		@Override
		public GameHUD_Character getCharacterMenu()
		{
			return hud_Character;
		}
		
		@Override
		public GameHUD getHUD()
		{
			return GameHUD.this;
		}
		
		@Override
		public SceneManager getSceneManager()
		{
			return level.sceneManager;
		}
	};
	
	private void postMessage(String text, boolean white, boolean isLong)
	{
		postText.setColor(white ? new Color(1, 1, 1, 1) : new Color(1, 0, 0, 1));
		postText.setText(text);
		postText.pack();
		postText.setX(SceneManager.CAMERA_WIDTH / 2 - postText.getWidth() / 2);
		postText.getColor().a = 0;
		
		postText.clearActions();
		if(isLong)
			postText.addAction(Actions.sequence(Actions.alpha(1f, 0.3f), Actions.delay(2.5f), Actions.alpha(0f, 0.20f)));
		else
			postText.addAction(Actions.sequence(Actions.alpha(1f, 0.3f), Actions.delay(1f), Actions.alpha(0f, 0.20f)));
		
		postText.setZIndex(20);
		
		postBG.setWidth(postText.getWidth() + 8);
		postBG.setY(postText.getY() + postText.getHeight() / 2 - postBG.getHeight() / 2);
		postBG.setX(SceneManager.CAMERA_WIDTH / 2 - postBG.getWidth() / 2);
		postBG.setAlpha(0);
		
		postBG.clearActions();
		if(isLong)
			postBG.addAction(Actions.sequence(Actions.alpha(1f, 0.25f), Actions.delay(2.6f), Actions.alpha(0f, 0.25f)));
		else
			postBG.addAction(Actions.sequence(Actions.alpha(1f, 0.25f), Actions.delay(1.1f), Actions.alpha(0f, 0.25f)));
		
		postBG.setZIndex(20);
		
		postBG.remove();
		postText.remove();
		GameHUD.this.addActor(postBG);
		GameHUD.this.addActor(postText);
	}
	
	public void playUnableToCast(int slot)
	{
		hud_SpellButtons.playUnableToCast(slot);
	}
	
	public void playPotionCD(int slot)
	{
		hud_PotionButtons.playCD(slot);
	}
	
	public void loadResources()
	{
		hud_Tools = new GameHUD_Tools(hudCallback);
		hud_Tools.loadResources(activity);
		staticMenus.put("tools", hud_Tools);
		allMenus.put("tools", hud_Tools);
		
		hud_Status = new GameHUD_Status(hudCallback);
		hud_Status.loadResources(activity);
		staticMenus.put("status", hud_Status);
		allMenus.put("status", hud_Status);
		
		hud_SpellButtons = new GameHUD_SpellButtons(hudCallback);
		hud_SpellButtons.loadResources(activity);
		staticMenus.put("spellbuttons", hud_SpellButtons);
		allMenus.put("spellbuttons", hud_SpellButtons);
		
		hud_PotionButtons = new GameHUD_PotionButtons(hudCallback);
		hud_PotionButtons.loadResources(activity);
		staticMenus.put("potionbuttons", hud_PotionButtons);
		allMenus.put("potionbuttons", hud_PotionButtons);
		
		hud_Loot = new GameHUD_Loot(hudCallback);
		hud_Loot.loadResources(activity);
		rightMenus.put("loot", hud_Loot);
		allMenus.put("loot", hud_Loot);
		
		hud_Action = new GameHUD_Action(hudCallback);
		hud_Action.loadResources(activity);
		rightMenus.put("action", hud_Action);
		allMenus.put("action", hud_Action);
		
		hud_Selling = new GameHUD_Selling(hudCallback);
		hud_Selling.loadResources(activity);
		rightMenus.put("selling", hud_Selling);
		allMenus.put("selling", hud_Selling);
		
		hud_Enchanting = new GameHUD_Enchanting(hudCallback);
		hud_Enchanting.loadResources(activity);
		rightMenus.put("enchanting", hud_Enchanting);
		allMenus.put("enchanting", hud_Enchanting);
		
		hud_Stash = new GameHUD_Stash(hudCallback);
		hud_Stash.loadResources(activity);
		rightMenus.put("stash", hud_Stash);
		allMenus.put("stash", hud_Stash);
		
		hud_Menu = new GameHUD_Menu(hudCallback);
		hud_Menu.loadResources(activity);
		rightMenus.put("menu", hud_Menu);
		allMenus.put("menu", hud_Menu);
		
		hud_Character = new GameHUD_Character(hudCallback);
		hud_Character.loadResources(activity);
		centerMenus.put("character", hud_Character);
		allMenus.put("character", hud_Character);
		
		hud_Connection = new GameHUD_Connection(hudCallback);
		hud_Connection.loadResources(activity);
		centerMenus.put("connection", hud_Connection);
		allMenus.put("connection", hud_Connection);
		
		hud_PvPScores = new GameHUD_PvPScores(hudCallback);
		hud_PvPScores.loadResources(activity);
		allMenus.put("pvpscores", hud_PvPScores);
		
		hud_TDInfo = new GameHUD_TDInfo(hudCallback);
		hud_TDInfo.loadResources(activity);
		allMenus.put("tdInfo", hud_TDInfo);
		
		hud_ArenaRounds = new GameHUD_ArenaRounds(hudCallback);
		hud_ArenaRounds.loadResources(activity);
		allMenus.put("arenarounds", hud_ArenaRounds);
		
		hud_Boss = new GameHUD_Boss(hudCallback);
		hud_Boss.loadResources(activity);
		allMenus.put("boss", hud_Boss);
		
		hud_Fame = new GameHUD_Fame(hudCallback);
		hud_Fame.loadResources(activity);
		allMenus.put("fame", hud_Fame);
		
		hud_Gems = new GameHUD_Gems(hudCallback);
		hud_Gems.loadResources(activity);
		allMenus.put("gems", hud_Gems);
		
		hud_QuestReward = new GameHUD_Reward(hudCallback);
		hud_QuestReward.loadResources(activity);
		allMenus.put("questreward", hud_QuestReward);
		
		hud_Settings = new GameHUD_Settings(hudCallback);
		hud_Settings.loadResources(activity);
		centerMenus.put("settings", hud_Settings);
		allMenus.put("settings", hud_Settings);
		
		hud_Tipps = new GameHUD_Tipps(hudCallback);
		hud_Tipps.loadResources(activity);
		centerMenus.put("tipps", hud_Tipps);
		allMenus.put("tipps", hud_Tipps);
		
		hud_GlyphMaster = new GameHUD_GlyphMaster(hudCallback);
		hud_GlyphMaster.loadResources(activity);
		centerMenus.put("glyphmaster", hud_GlyphMaster);
		allMenus.put("glyphmaster", hud_GlyphMaster);
		
		hud_SkinSelection = new GameHUD_SkinSelection(hudCallback);
		hud_SkinSelection.loadResources(activity);
		rightMenus.put("skinSelection", hud_SkinSelection);
		allMenus.put("skinSelection", hud_SkinSelection);
		
		hud_DefenseTower = new GameHUD_DefenseTower(hudCallback);
		hud_DefenseTower.loadResources(activity);
		rightMenus.put("defenseTower", hud_DefenseTower);
		allMenus.put("defenseTower", hud_DefenseTower);
		
		postText = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 16), Color.WHITE));
		postText.setAlignment(Align.top | Align.left);
		postText.setPosition(0, 60);
		postText.setColor(new Color(1, 0, 0, 0));
		this.addActor(postText);
		
		postBG = new Sprite(0, 60, GameHelper.getInstance().getGuiAsset("backgroundPost"));
		postBG.setAlpha(0);
		this.addActor(postBG);
		
		showMenu("tools");
		showMenu("status");
		showMenu("spellbuttons");
		showMenu("potionbuttons");
		showMenu("fame");
		showMenu("gems");
		showMenu("questreward");
		
		questLog = new QuestLog(activity);
		questLog.initialize();
		
		TextureRegion txtQL = GameHelper.getInstance().getGuiAsset("questLogButton");//TODO:get from GUI Texture Pack
		questLogButton = new Sprite(SceneManager.CAMERA_WIDTH - txtQL.getRegionWidth() - 5, 172, txtQL);
		questLogButton.addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				questLogButton.getColor().a = 0.9f;
				questLog.updateText(level.getPlayer());
				GameHUD.this.toggleQuestLog(true);
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				questLogButton.getColor().a = 0.6f;
				GameHUD.this.toggleQuestLog(false);
			}
		});
		questLogButton.setAlpha(0.6f);
		this.addActor(questLogButton);
		
		minimap = new Minimap(activity, shape);
		
		TextureRegion txtMM = GameHelper.getInstance().getGuiAsset("minimapButton");//TODO:get from GUI Texture Pack
		minimapButton = new Sprite(SceneManager.CAMERA_WIDTH - txtMM.getRegionWidth() - 5, 222, txtMM);
		minimapButton.addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				minimapButton.setAlpha(0.9f);
				GameHUD.this.toggleMinimap(true);
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				minimapButton.setAlpha(0.6f);
				GameHUD.this.toggleMinimap(false);
			}
		});
		minimapButton.setAlpha(0.5f);
		this.addActor(minimapButton);
		
		//TODO: ONLY IF PC!
		this.addListener(new InputListener()
		{
			@Override
			public boolean keyDown(InputEvent event, int keycode)
			{
				if(keycode == Keys.A)
				{
					level.getPlayer().manualX = -1;
					return true;
				}
				else if(keycode == Keys.D)
				{
					level.getPlayer().manualX = 1;
					return true;
				}
				
				if(keycode == Keys.W)
				{
					level.getPlayer().manualY = -1;
					return true;
				}
				else if(keycode == Keys.S)
				{
					level.getPlayer().manualY = 1;
					return true;
				}
				
				return false;
			}
			
			@Override
			public boolean keyUp(InputEvent event, int keycode)
			{
				if(keycode == Keys.A && level.getPlayer().manualX == -1 || keycode == Keys.D && level.getPlayer().manualX == 1)
				{
					level.getPlayer().manualX = 0;
					return true;
				}
				
				if(keycode == Keys.W && level.getPlayer().manualY == -1 || keycode == Keys.S && level.getPlayer().manualY == 1)
				{
					level.getPlayer().manualY = 0;
					return true;
				}
				
				return false;
			}
		});
	}
	
	public void initHUD(PlayerClient player)
	{
		player.hud = this;
		for(Entry<String, GameHUDMenu> hud : allMenus.entrySet())
			hud.getValue().initHUD(player);
	}
	
	public void update(float delta)
	{
		for(Entry<String, GameHUDMenu> hud : allMenus.entrySet())
			if(hud.getValue().isActive)
			{
				hud.getValue().update(delta);
				if(hud.getValue().subMenu != null)
					hud.getValue().subMenu.update(delta);
			}
	}
	
	public void showMenu(final String menu)
	{
		GameHUDMenu hud = allMenus.get(menu);
		if(hud.isActive || hud.getParent() != null/*already has parent!*/)
			return;
		
		if(rightMenus.containsKey(menu))
		{
			for(String hudMenu : rightMenus.keySet())
				if(rightMenus.get(hudMenu).isActive)
					hideMenu(hudMenu);
		}
		else if(centerMenus.containsKey(menu))
		{
			for(String hudMenu : centerMenus.keySet())
				if(centerMenus.get(hudMenu).isActive)
					hideMenu(hudMenu);
		}
		
		hud.isActive = true;
		hud.showUp();
		if(hud.subMenu != null)
		{
			hud.subMenu.isActive = true;
			hud.subMenu.showUp();
		}
		this.addActor(hud);
//		sortChildren();
	}
	
	public boolean closeMiddleMenues()
	{
		boolean cl = false;
		for(String hudMenu : centerMenus.keySet())
			if(centerMenus.get(hudMenu).isActive)
			{
				hideMenu(hudMenu);
				cl = true;
			}
		return cl;
	}
	
	public boolean closeRightMenues()
	{
		boolean cl = false;
		for(String hudMenu : rightMenus.keySet())
			if(rightMenus.get(hudMenu).isActive)
			{
				hideMenu(hudMenu);
				cl = true;
			}
		return cl;
	}
	
	public void hideMenu(final String menu)
	{
		final GameHUDMenu hud = allMenus.get(menu);
		if(!hud.isActive)
			return;
		
		hud.isActive = false;
		hud.hidden();
		
		if(hud.subMenu != null)
		{
			hud.subMenu.isActive = false;
			hud.subMenu.hidden();
		}
		
		hud.remove();
	}
	
	public void hideMenuForced(final String menu)
	{
		GameHUDMenu hud = allMenus.get(menu);
		if(!hud.isActive)
			return;
		
		hud.isActive = false;
		hud.hidden();
		
		if(hud.subMenu != null)
		{
			hud.subMenu.isActive = false;
			hud.subMenu.hidden();
		}
		
		hud.remove();
	}
	
	public void setSubMenu(final GameHUDMenu mainMenu, final GameHUDMenu subMenu)
	{
		if(subMenu == null || mainMenu.subMenu == subMenu)
			return;
		
		if(mainMenu.subMenu != null)
		{
			mainMenu.subMenu.isActive = false;
			mainMenu.subMenu.hidden();
			mainMenu.subMenu.remove();
		}
		
		subMenu.isActive = true;
		subMenu.showUp();
		mainMenu.subMenu = subMenu;
		mainMenu.addActor(subMenu);
//		GameHUD.this.sortChildren();
	}
	
	public void hideSubMenu(final GameHUDMenu mainMenu, final GameHUDMenu subMenu)
	{
		if(subMenu == null)
			return;
		
		subMenu.isActive = false;
		subMenu.hidden();
		subMenu.remove();
	}
	
	public void toggleMenu(String menu)
	{
		if(allMenus.get(menu).isActive)
			hideMenu(menu);
		else
			showMenu(menu);
	}
	
	public void lootBagItemAdded(LootBag bag, int pos)
	{
		hud_Loot.lootBagItemAdded(bag, pos);
	}
	
	public void lootBagItemRemoved(LootBag bag, int pos)
	{
		hud_Loot.lootBagItemRemoved(bag, pos);
	}
	
	public void setLootBagInRange(LootBag bag)
	{
		hud_Loot.setLootBagInRange(bag);
	}
	
	public void fakeLootBBagLeft()
	{
		hud_Loot.fakeLootBagLeft();
	}
	
	public void lootBagRemoved(LootBag bag)
	{
		hud_Loot.lootBagRemoved(bag);
	}
	
	public IGameHUDCallback getCallback()
	{
		return hudCallback;
	}
	
	public GameHUDMenu getMenu(String menu)
	{
		return allMenus.get(menu);
	}
	
	private OnScreenInfo currentOSI;
	
	public void showOnScreenInfo(final int id)
	{
		
		if(currentOSI != null)
			currentOSI.remove();
		OnScreenInfo osi = GameHelper.getInstance().getOnScreenInfo(id);
		if(osi != null)
		{
			osi.setZIndex(9001);
			this.addActor(osi);
		}
		currentOSI = osi;
	}
	
	public void hideOnScreenInfo(int id)
	{
		if(currentOSI != null && currentOSI.id == id)
		
		{
			currentOSI.remove();
			currentOSI = null;
		}
	}
	
	public Minimap getMinimap()
	{
		return minimap;
	}
	
	public void toggleMinimap(final boolean visible)
	{
		minimap.remove();
		if(visible)
			GameHUD.this.addActor(minimap);
		else
			minimap.remove();
	}
	
	public QuestLog getQuestLog()
	{
		return questLog;
	}
	
	public void toggleQuestLog(final boolean visible)
	{
		questLog.toggled(visible);
		questLog.remove();
		if(visible)
			GameHUD.this.addActor(questLog);
	}
	
	public void displayReward(String title, int gold, int fame, Item... items)
	{
		hud_QuestReward.displayReward(title, gold, fame, items);
	}
}
