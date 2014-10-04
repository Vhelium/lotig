package com.vhelium.lotig.scene.gamescene.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.HUD;
import com.vhelium.lotig.scene.MainMenuScene;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Bullet;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.Effect;
import com.vhelium.lotig.scene.gamescene.EffectManager;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GameScene.ConnectionType;
import com.vhelium.lotig.scene.gamescene.GameScene.GameState;
import com.vhelium.lotig.scene.gamescene.GlobalSettings;
import com.vhelium.lotig.scene.gamescene.IClientMapCallback;
import com.vhelium.lotig.scene.gamescene.LootBag;
import com.vhelium.lotig.scene.gamescene.SavedGame;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.hud.ConfirmationDialogueHUD;
import com.vhelium.lotig.scene.gamescene.client.hud.DeathHUD;
import com.vhelium.lotig.scene.gamescene.client.hud.GameHUD;
import com.vhelium.lotig.scene.gamescene.client.hud.GameHUDMenu;
import com.vhelium.lotig.scene.gamescene.client.hud.GameHUD_TDInfo;
import com.vhelium.lotig.scene.gamescene.client.hud.GemStoreHUD;
import com.vhelium.lotig.scene.gamescene.client.items.IPlayerAttributeListener;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerBarbarian;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerDarkPriest;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerDeathKnight;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerDruid;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerRanger;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerSorcerer;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.quest.IQuestListener;
import com.vhelium.lotig.scene.gamescene.quest.Quest;
import com.vhelium.lotig.scene.gamescene.quest.QuestObjectiveKill;
import com.vhelium.lotig.scene.gamescene.quest.QuestType;
import com.vhelium.lotig.scene.gamescene.server.levelobject.EventObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.ILevelObjectCallBack;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Chest;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_DefenseTower;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_NPC;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Tombstone;

public class ClientLevel implements IPlayerAttributeListener, ILevelObjectCallBack, ISettingsCallback
{
	private static final int Z_INDEX_DAMAGENUMBERS = 20000;
	private static final int Z_INDEX_EFFECTS = 30000;
	private static final int Z_INDEX_DIALOGUES = 30002;
	private static final int Z_INDEX_TOPLAYER = 40000;
	public boolean resetDelta = true;
	private boolean isLoading = false;
	
	public final Main activity;
	public final SceneManager sceneManager;
	
	private TMXMap tmxMap;
	private int difficulty;
	
	private IClientConnectionHandler connectionHandler;
	private int myPlayerNr = -1;
	
	private ConcurrentHashMap<Integer, Bullet> bullets;
	private ConcurrentHashMap<Integer, Bullet> bulletsToRemove;
	
	private PlayerClient player;
	private ConcurrentHashMap<Integer, EntityClient> entities;
	private final List<IQuestListener> questListeners;
	private boolean isOnLairPortals;
	private EventObject lairPortals;
	private boolean isLairActive;
	
	private ConcurrentHashMap<Integer, LootBag> lootBags;
	
	private SavedGame savedGame;
	
	public GameHUD hud;
	public DeathHUD deathHUD;
	private GemStoreHUD gemStoreHUD;
	public Touchpad rotationControl;
	public Touchpad movementControl;
	
	private final float inputUpdateIntervall = 180;
	private float inputUpdateTimeElapsed = 0f;
	
	private final float lootBagUpdateIntervall = 80f;
	private float lootBagUpdateTimeElapsed = 0f;
	private LootBag selectedLootBag;
	
	public long timeDifferenceServer = 0L;
	
	private EffectManager effectManager;
	
	public ClientLevel(Main activity, SceneManager sceneManager)
	{
		this.activity = activity;
		this.sceneManager = sceneManager;
		questListeners = new ArrayList<IQuestListener>();
	}
	
	public void load()
	{
		this.effectManager = GameHelper.getInstance().getEffectManager();
		
		TouchpadStyle touchpadStyle = new TouchpadStyle(new TextureRegionDrawable(GameHelper.$.getGuiAsset("onscreen_control_base")), new TextureRegionDrawable(GameHelper.$.getGuiAsset("onscreen_control_knob")));
		
		movementControl = new Touchpad(0, touchpadStyle);
		movementControl.setPosition(20, SceneManager.CAMERA_HEIGHT - movementControl.getHeight() - 20);
		movementControl.setOrigin(movementControl.getWidth() / 2, movementControl.getHeight() / 2);
		movementControl.setScale(1.1f);
//		movementControl.refreshControlKnobPosition();
		
		hud.addActor(movementControl);
		
		rotationControl = new Touchpad(0, touchpadStyle);
		rotationControl.setPosition(SceneManager.CAMERA_WIDTH - rotationControl.getWidth() - 20, SceneManager.CAMERA_HEIGHT - rotationControl.getHeight() - 20);
		rotationControl.setOrigin(rotationControl.getWidth() / 2, rotationControl.getHeight() / 2);
		rotationControl.setScale(1.1f);
//		rotationControl.refreshControlKnobPosition();
		
		hud.addActor(rotationControl);
	}
	
	public void startGame(IClientConnectionHandler connectionHandler, int myPlayerNr, SavedGame savedGame, int difficulty, DataPacket dpLairsAndQuest)
	{
		this.connectionHandler = connectionHandler;
		this.myPlayerNr = myPlayerNr;
		this.savedGame = savedGame;
		this.difficulty = difficulty;
		
		hud = new GameHUD(activity, this, sceneManager.getSpriteBatch(), sceneManager.getShapeRenderer());
		hud.loadResources();
		
		deathHUD = new DeathHUD(sceneManager.getSpriteBatch(), sceneManager.getShapeRenderer());
		deathHUD.loadResources(activity, this);
		
		gemStoreHUD = new GemStoreHUD(sceneManager.getSpriteBatch(), sceneManager.getShapeRenderer());
		gemStoreHUD.loadResources(activity, this);
		
		load();
		
		tmxMap = new TMXMap(activity, sceneManager.getCamera(), clientMapCallback);
		
		String playerClass = savedGame.getDataValue("playerclass");
		if(playerClass.equals("Barbarian"))
			player = new PlayerBarbarian(this, tmxMap, dpLairsAndQuest, sceneManager);
		else if(playerClass.equals("Dark Priest"))
			player = new PlayerDarkPriest(this, tmxMap, dpLairsAndQuest, sceneManager);
		else if(playerClass.equals("Death Knight"))
			player = new PlayerDeathKnight(this, tmxMap, dpLairsAndQuest, sceneManager);
		else if(playerClass.equals("Druid"))
			player = new PlayerDruid(this, tmxMap, dpLairsAndQuest, sceneManager);
		else if(playerClass.equals("Ranger"))
			player = new PlayerRanger(this, tmxMap, dpLairsAndQuest, sceneManager);
		else if(playerClass.equals("Sorcerer"))
			player = new PlayerSorcerer(this, tmxMap, dpLairsAndQuest, sceneManager);
		
		player.getSprite().setZIndex(100);
		
		entities = new ConcurrentHashMap<Integer, EntityClient>();
		bullets = new ConcurrentHashMap<Integer, Bullet>();
		bulletsToRemove = new ConcurrentHashMap<Integer, Bullet>();
		lootBags = new ConcurrentHashMap<Integer, LootBag>();
		
		hud.initHUD(player);//only assigns player instance..
		
		player.initialize(savedGame);
		player.addAttributeListener(this);
		
		Log.d("ClientLevel.out", "startGame() finished");
		
		hud.getMinimap().setCentered(Boolean.parseBoolean(GlobalSettings.getInstance().getDataValue("Center-Minimap")));
	}
	
	public void loadMapInitiated()
	{
		isLoading = true;
	}
	
	public void loadMap(String mapName)
	{
		isLoading = true;
		String oldMap = getMapName();
		
		bullets.clear();
		bulletsToRemove.clear();
		entities.clear();
		lootBags.clear();
		isOnLairPortals = false;
		
		sceneManager.getGameScene().getStage().clear();
		player.removePvPFlag();//remove pvp flag
		
		tmxMap.loadMap(mapName, sceneManager.getSpriteBatch(), false);
		
		//Music:
		if(tmxMap.getMapProperty("music") != null && !tmxMap.getMapProperty("music").isEmpty())
			SoundManager.switchMusic(tmxMap.getMapProperty("music"));
		else
			SoundManager.switchMusic(tmxMap.getMapProperty("overworld"));
		
		hud.getMinimap().prepare(tmxMap);
		tmxMap.loadEventTiles(null);
		for(LevelObject obj : tmxMap.getLevelObjects().values())
			obj.registerCallback(this);
		
		hud.getMinimap().initialize(activity, tmxMap);
		
		player.manualX = 0;
		player.manualY = 0;
		player.setX(tmxMap.spawn.x);
		player.setY(tmxMap.spawn.y);
		player.setTeamNr(0);
		
		sceneManager.getCamera().setChaseActor(player);
		sceneManager.getCamera().setBounds(0, 0, getMapWidth(), getMapHeight());
		sceneManager.getCamera().setBoundsEnabled(true);
		sceneManager.getCamera().update();
		
		List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
		for(IQuestListener listener : toLoopIQuestListeners)
			listener.onMapLoaded(mapName);
		
		//check for dungeon quest to be removed:
		player.dungeonQuestFinished(oldMap);
		//check for new available dungeon quest:
		Quest newDungeonQuest = tmxMap.getDungeonQuest(this);
		if(newDungeonQuest != null)
		{
			if(player.setDungeonQuest(newDungeonQuest, difficulty))
				hud.getCallback().postMessage("new quest available!", true);
		}
		else
			player.onQuestRemoved(QuestType.Dungeon);
		
		if(mapName.startsWith("testRange"))
			TestRangeUtility.preparePlayer(player, Integer.parseInt(mapName.substring(9, mapName.length())));
		
		saveGame();
		
		Log.d("ClientLevel.out", "loadMap() finished");
	}
	
	public void handleMapLoadCompleted(DataPacket dp)
	{
		player.setDead(false);
		
		for(int i = 0; i < tmxMap.getLayers().size(); i++)
		{
			if(tmxMap.getLayer(i).getName().endsWith("Top"))
				tmxMap.getLayer(i).setZIndex(Z_INDEX_TOPLAYER);
			sceneManager.getGameScene().getStage().addActor(tmxMap.getLayer(i));
		}
		
		if(tmxMap.getAnimatedLayer() != null)
			sceneManager.getGameScene().getStage().addActor(tmxMap.getAnimatedLayer());
		
		//add all entities to the world
		int entities = dp.getInt();
		for(int i = 0; i < entities; i++)
		{
			addEntity(dp.getInt(), dp.getString(), dp.getString(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getInt(), dp.getInt(), dp.getFloat(), dp.getBoolean());
		}
		
		int lootBagCount = dp.getInt();
		for(int i = 0; i < lootBagCount; i++)
		{
			final int bagId = dp.getInt();
			final float x = dp.getFloat();
			final float y = dp.getFloat();
			final ConcurrentHashMap<Integer, String> drops = new ConcurrentHashMap<Integer, String>();
			int itemCount = dp.getInt();
			for(int it = 0; it < itemCount; it++)
			{
				int id = dp.getInt();
				drops.put(id, dp.getString());
			}
			
			lootBags.put(bagId, new LootBag(bagId, x, y, drops, GameHelper.getInstance().getGameAsset("lootBag")));
			sceneManager.getGameScene().getStage().addActor(lootBags.get(bagId).getSprite());
		}
		
		int levelObjCount = dp.getInt();
		for(int i = 0; i < levelObjCount; i++)
		{
			handleAddLevelObject(dp);
		}
		
		isLairActive = dp.getBoolean();
		
		if(tmxMap.getCurrentMapName().equals("town"))
		{
			lairPortals = null;
			for(EventObject ev : tmxMap.getEventObjectsClient())
				if(ev.containsProperty("lairportals"))
					lairPortals = ev;
			
			showDirectionArrow();
		}
		else
			player.hideDirectionArrow();
		
		for(Quest q : player.getQuests().values())
			if(q.getQuestObjective() instanceof QuestObjectiveKill)
				onKillQuestReceived(q);
		
		isLoading = false;
	}
	
	public void handleClientPlayerJoinedOtherMap(String mapName)
	{
		List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
		for(IQuestListener listener : toLoopIQuestListeners)
			listener.onMapLoaded(mapName);
	}
	
	public void handlePlayerAreaEntered(String areaName)
	{
		List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
		for(IQuestListener listener : toLoopIQuestListeners)
			listener.onAreaEntered(areaName);
	}
	
	private final IClientMapCallback clientMapCallback = new IClientMapCallback()
	{
		@Override
		public void onNextLevelAdded(Rectangle rectangle)
		{
			hud.getMinimap().onNextLevelAdded(rectangle);
		}
		
		@Override
		public void onCollisionRemoved(int key)
		{
			hud.getMinimap().onCollisionRemoved(key);
		}
		
		@Override
		public void onCollisionAdded(int key, Rectangle bounds)
		{
			hud.getMinimap().onCollisionAdded(key, bounds);
		}
	};
	
	public void handleConfirmRealmRequest(final DataPacket in)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				ConfirmationDialogueHUD dialogue = new ConfirmationDialogueHUD(sceneManager.getSpriteBatch(), sceneManager.getShapeRenderer())
				{
					@Override
					public void onYes()
					{
						DataPacket dp = new DataPacket();
						dp.setInt(MessageType.MSG_REQUEST_REALM_WITH_LAIR_NR);
						dp.setString(in.getString());
						dp.setInt(in.getInt());
						dp.setBoolean(true);
						sendDataPacket(dp);
						if(sceneManager.getCurrentScene().equals(sceneManager.getGameScene()))
							setHUD(hud);
						else
							setHUD(null);
					}
					
					@Override
					public void onNo()
					{
						setHUD(hud);
					}
				};
				dialogue.loadResources(activity, ClientLevel.this);
				setHUD(dialogue);
			}
		});
	}
	
	public void handleArenaNextRound(DataPacket dp)
	{
		hud.getCallback().postMessage("Next round in " + Math.round(dp.getFloat() / 1000) + " seconds!", true);
		
		List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(getQuestListeners());
		for(IQuestListener listener : toLoopIQuestListeners)
			listener.onArenaRoundFinished();
	}
	
	public void handleArenaUpdateRound(DataPacket dp)
	{
		hud.showMenu("arenarounds");
		hud.getMenu("arenarounds").setParam(String.valueOf(dp.getInt()));
	}
	
	public void update(float pSecondsElapsed)
	{
		long millis = System.currentTimeMillis() + timeDifferenceServer;
		float delta = pSecondsElapsed * 1000;
		
		if(resetDelta)
		{
			delta = 0;
			resetDelta = false;
			player.manualX = 0;
			player.manualY = 0;
			player.resetVelocity();
		}
		
		if(!isLoading)
		{
			if(!player.isDead())
				player.update(delta, millis, rotationControl, movementControl);
			player.getSprite().setZIndex((int) ((player.getY() + player.getHeight()) / tmxMap.getMapHeight() * 1000));
			
			for(Entry<Integer, EntityClient> e : entities.entrySet())
			{
				e.getValue().update(delta, millis);
				e.getValue().getSprite().setZIndex((int) ((e.getValue().getY() + e.getValue().getHeight()) / tmxMap.getMapHeight() * 1000));
			}
			
			inputUpdateTimeElapsed += delta;
			
			if(inputUpdateTimeElapsed >= inputUpdateIntervall)
			{
				inputUpdateTimeElapsed -= inputUpdateIntervall;
				try
				{
					DataPacket dp = new DataPacket();
					dp.setInt(MessageType.MSG_SEND_PLAYER_POS);
					dp.setFloat(player.getX());
					dp.setFloat(player.getY());
					dp.setFloat(player.getRotation());
					connectionHandler.sendData(dp.finish());
				}
				catch (IOException e)
				{
					Log.e("ClientLevel", "error sending pos upd dp");
				}
			}
			
			float offsetX = 0f, offsetY = 0f;
			
			if(player.getOriginX() < SceneManager.CAMERA_WIDTH / 2)
				offsetX = player.getOriginX() - SceneManager.CAMERA_WIDTH / 2;
			else if(player.getOriginX() > tmxMap.getMapWidth() - SceneManager.CAMERA_WIDTH / 2)
				offsetX = player.getOriginX() - (tmxMap.getMapWidth() - SceneManager.CAMERA_WIDTH / 2);
			
			if(player.getOriginY() < SceneManager.CAMERA_HEIGHT / 2)
				offsetY = player.getOriginY() - SceneManager.CAMERA_HEIGHT / 2;
			else if(player.getOriginY() > tmxMap.getMapHeight() - SceneManager.CAMERA_HEIGHT / 2)
				offsetY = player.getOriginY() - (tmxMap.getMapHeight() - SceneManager.CAMERA_HEIGHT / 2);
			
			hud.getMinimap().updatePlayer(player.getOriginX(), player.getOriginY(), offsetX, offsetY);
			hud.getQuestLog().update(delta, player);
			
			bulletsToRemove.clear();
			for(Entry<Integer, Bullet> e : bullets.entrySet())
			{
				Bullet b = e.getValue();
				b.update(delta);
				if(b.getIsDead())
					bulletsToRemove.put(e.getKey(), b);
			}
			
			for(Entry<Integer, Bullet> e : bulletsToRemove.entrySet())
			{
				e.getValue().getSprite().remove();
				if(bullets.containsKey(e.getKey()))
					bullets.remove(e.getKey());
			}
			
			if(lootBags.size() > 0)
			{
				lootBagUpdateTimeElapsed += delta;
				
				if(lootBagUpdateTimeElapsed >= lootBagUpdateIntervall)
				{
					lootBagUpdateTimeElapsed -= lootBagUpdateIntervall;
					
					selectedLootBag = getNearestLootBag(player.getOriginX(), player.getOriginY());
					hud.setLootBagInRange(selectedLootBag);
				}
			}
			tmxMap.updateEventObjectsClient(delta, player);
			
			hud.update(delta);
			if(deathHUD.isActive)
				deathHUD.update(delta);
			
//			sceneManager.getGameScene().sortChildren(true);
			
			if(tmxMap.getLevelObjects().size() > 0)
			{
				for(LevelObject obj : tmxMap.getLevelObjects().values())
					obj.updateClient(delta, player);
			}
			tmxMap.updateLevelObjectsToRemove();
		}
	}
	
	@Override
	public void playerAttributeChanged()
	{
		try
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_PLAYER_STATUS_UPDATE);
			player.setServerSidedStats(dp);
			connectionHandler.sendData(dp.finish());
		}
		catch (IOException e)
		{
			Log.e("ClientLevel.out", "Error playerAttributeChanged()");
		}
	}
	
	public void handleEntityPosUpdate(DataPacket dp)
	{
		int entityCount = dp.getInt();
		
		for(int i = 0; i < entityCount; i++)
		{
			int entityNr = dp.getInt();
			float x = dp.getFloat();
			float y = dp.getFloat();
			float rot = dp.getFloat();
			
			if(entityNr != myPlayerNr)
			{
				EntityClient entity = entities.get(entityNr);
				if(entity != null)
					entity.updatePositionSmooth(x, y, rot);
			}
		}
	}
	
	public void shareBulletShot(boolean piercing, int damageType, float maxRange, float x, float y, float rotation, float directionX, float directionY, String bulletType)
	{
		try
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_REQUEST_SHOOTBULLET);
			dp.setBoolean(piercing);
			dp.setInt(damageType);
			dp.setFloat(maxRange);
			dp.setFloat(x);
			dp.setFloat(y);
			dp.setFloat(rotation);
			dp.setFloat(directionX);
			dp.setFloat(directionY);
			dp.setString(bulletType);
			connectionHandler.sendData(dp.finish());
		}
		catch (IOException e)
		{
			Log.e("ClientLevel.out", "Error shooting Bullet");
		}
	}
	
	public void handleBulletShot(DataPacket dp)
	{
		int id = dp.getInt();
		int targetId = dp.getInt();
		Bullet bullet;
		if(targetId == -1)
			bullet = new Bullet(tmxMap, dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getString(), dp.getBoolean());
		else
			bullet = new Bullet(tmxMap, getEntity(targetId), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getString());
		bullets.put(id, bullet);
		this.sceneManager.getGameScene().getStage().addActor(bullet.getSprite());
		int shooterID = dp.getInt();
		if(shooterID != myPlayerNr && entities.containsKey(shooterID))
			entities.get(shooterID).shoot();
	}
	
	public void handleRemoveBullets(final DataPacket dp)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				int count = dp.getInt();
				for(int i = 0; i < count; i++)
				{
					int id = dp.getInt();
					if(bullets.get(id) != null)
					{
						bullets.get(id).getSprite().remove();
						bullets.remove(id);
					}
				}
			}
		});
	}
	
	public void handleDamageNumber(DataPacket dp)
	{
		int count = dp.getInt();
		for(int i = 0; i < count; i++)
		{
			int id = dp.getInt();
			int value = dp.getInt();
			int dt = dp.getInt();
			if(id != -1)
				showDamageNumber(id, value, dt);
			else
				showDamageNumber(value, dt, dp.getInt(), dp.getInt());
		}
	}
	
	public void handleAddLevelObject(DataPacket dp)
	{
		final int objId = dp.getInt();
		final LevelObject obj = LevelObject.getLevelObjectFromStringFormat(objId, dp.getFloat(), dp.getFloat(), dp.getInt(), dp.getInt(), dp.getString());
		if(obj != null)
		{
			Gdx.app.postRunnable(new Runnable()
			{
				@Override
				public void run()
				{
					if(obj.getSprite() != null)
					{
						obj.getSprite().setZIndex((int) ((obj.getBounds().getY() + obj.getBounds().getHeight()) / tmxMap.getMapHeight() * 1000));
						sceneManager.getGameScene().getStage().addActor(obj.getSprite());
					}
					tmxMap.addLevelObject(objId, obj, null);
					obj.registerCallback(ClientLevel.this);
				}
			});
		}
	}
	
	public void handleRemoveLevelObjects(final DataPacket dp)
	{
		final int count = dp.getInt();
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				for(int i = 0; i < count; i++)
				{
					int key = dp.getInt();
					if(tmxMap.getLevelObjects().containsKey(key))
					{
						if(tmxMap.getLevelObjects().get(key).getSprite() != null)
							tmxMap.getLevelObjects().get(key).getSprite().remove();
						tmxMap.removeLevelObject(key, null);
					}
				}
			}
		});
	}
	
	public void handleLevelObjectStateChanged(DataPacket dp)
	{
		int id = dp.getInt();
		if(tmxMap.getLevelObjects().containsKey(id))
		{
			int state = dp.getInt();
			tmxMap.getLevelObjects().get(id).setState(state);
			tmxMap.getLevelObjects().get(id).stateChangedClient(state);
		}
	}
	
	public void handleLevelObjectRemoveCollision(int id)
	{
		if(tmxMap.getLevelObjects().containsKey(id))
		{
			tmxMap.removeCollision(tmxMap.getLevelObjects().get(id));
		}
	}
	
	@Override
	public void lowerHealth(LevelObject object)
	{
		DamageNumber nr = new DamageNumber((object.getBounds().getX() + object.getBounds().getWidth() / 2) * 2, object.getBounds().getY() + object.getBounds().getHeight() / 3, -1, DamageType.Physical, activity);
		nr.setZIndex(Z_INDEX_DAMAGENUMBERS);
		sceneManager.getGameScene().getStage().addActor(nr);
	}
	
	@Override
	public void stateChanged(LevelObject object, int state)
	{
		if(object instanceof LevelObject_DefenseTower && hud.getMenu("defenseTower").isActive)
			hud.getCallback().getMenu("defenseTower").showUp();
	}
	
	public void handlePlayEffeft(DataPacket dp)
	{
		int size = dp.getInt();
		for(int i = 0; i < size; i++)
		{
			Effect effect = null;
			String effectName;
			boolean centered = dp.getBoolean();
			if(centered)//Centered on entity
			{
				int targetId = dp.getInt();
				boolean hook = dp.getBoolean();
				int width = dp.getInt();
				int height = dp.getInt();
				EntityClientMixin entity = null;
				if(targetId == myPlayerNr)
					entity = player;
				else
					entity = entities.get(targetId);
				
				effectName = dp.getString();
				
				//TODO: Check duration, 0=infinity
				if(entity != null)//TODO: Check for hook, if false attach to world (unnecessary?^^)
				{
					if(width > 0 && height > 0)
						effect = new Effect(entity, width, height, effectManager.getEffectAsset(effectName));
					else
						effect = new Effect(entity, effectManager.getEffectAsset(effectName));
					effect.setZIndex(Z_INDEX_EFFECTS);
					entity.getSprite().addActor(effect);
				}
			}
			else
			//in real world
			{
				float x = dp.getFloat();
				float y = dp.getFloat();
				int width = dp.getInt();
				int height = dp.getInt();
				effectName = dp.getString();
				
				if(width != -1 && height != -1)
				{
					effect = new Effect(x - width / 2, y - height / 2, width, height, effectManager.getEffectAsset(effectName));
					effect.setZIndex(Z_INDEX_EFFECTS);
					sceneManager.getGameScene().getStage().addActor(effect);
				}
				else
				{
					TiledTextureRegion texture = effectManager.getEffectAsset(effectName);
					effect = new Effect(x - texture.getTileWidth() / 2, y - texture.getTileHeight() / 2, texture);
					effect.setZIndex(Z_INDEX_EFFECTS);
					sceneManager.getGameScene().getStage().addActor(effect);
				}
			}
			if(effect != null)
			{
				int duration = dp.getInt();
				boolean loop = dp.getBoolean();
				if(duration == 0)
					effect.animate(effectManager.getAnimationTime(effectName), false, Effect.getAnimationListener());
				else
				{
					effect.animate(effectManager.getAnimationTime(effectName), loop);
					effect.setAutoRemove(null, "", duration, System.currentTimeMillis(), 0);
				}
			}
		}
	}
	
	public void handlePlaySound(DataPacket dp)
	{
		SoundManager.playSound(dp.getString());
	}
	
	public void handlePlayerHealthUpdate(DataPacket dp)
	{
		player.setHp(dp.getFloat());
		player.setMana(dp.getFloat());
	}
	
	public void handleOtherPlayerShootSpeedChanged(DataPacket dp)
	{
		entities.get(dp.getInt()).setShootSpeed(dp.getFloat());
	}
	
	public void townPort()
	{
		Log.d("Client level", "requesting town port. current map: " + tmxMap.getCurrentMapName());
		if(tmxMap.getCurrentMapName().toLowerCase().equals("town"))
			return;
		
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REQUEST_TOWN_PORT);
		try
		{
			connectionHandler.sendData(dp.finish());
		}
		catch (IOException e)
		{
			Log.e("ClientLevel.out", "Error requesting town port");
		}
	}
	
	public void shareRequestRespawn()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REQUEST_RESPAWN);
		try
		{
			connectionHandler.sendData(dp.finish());
		}
		catch (IOException e)
		{
			Log.e("ClientLevel.out", "Error requesting respawn");
		}
	}
	
	public void addEntity(final int entityNr, final String name, final String asset, final float x, final float y, final float rot, final int width, final int height, final float shootSpeed, final boolean isDead)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				if(entityNr == myPlayerNr)
				{
					player.setX(x);
					player.setY(y);
					player.setRotation(rot);
					player.setDead(isDead);
					sceneManager.getGameScene().getStage().addActor(player.getSprite());
				}
				else
				{
					EntityClient entity = new EntityClient(ClientLevel.this, tmxMap, name, width, height, asset);
					entity.setNewPos(x, y);
					entity.setRotation(rot);
					entity.setShootSpeed(shootSpeed);
					entity.setDead(isDead);
					sceneManager.getGameScene().getStage().addActor(entity.getSprite());
					entities.put(entityNr, entity);
				}
//				sceneManager.getGameScene().sortChildren(true);
			}
		});
	}
	
	public void removeEntity(final int entityNr)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				if(entities.get(entityNr) != null)
				{
					entities.get(entityNr).setDead(true);
					entities.get(entityNr).getSprite().remove();
					entities.remove(entityNr);
				}
			}
		});
	}
	
	private EntityClientMixin getEntity(int targetId)
	{
		if(targetId == myPlayerNr)
			return player;
		else if(entities.containsKey(targetId))
			return entities.get(targetId);
		
		return null;
	}
	
	public void newEntityPos(DataPacket dp)
	{
		int id = dp.getInt();
		if(id == myPlayerNr)
		{
			player.setX(dp.getFloat());
			player.setY(dp.getFloat());
		}
		else if(entities.containsKey(id))
		{
			entities.get(id).setNewPos(dp.getFloat(), dp.getFloat());
		}
	}
	
	public void handlePlayerDied(final DataPacket dp)
	{
		final int id = dp.getInt();
		final int tombId = dp.getInt();
		final boolean hasDeathPenalty = dp.getBoolean();
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				if(id == myPlayerNr)
				{
					player.setDead(true);
					if(hasDeathPenalty)
					{
						deathHUD.setPenalty(player.onDeathPenalty());
						deathHUD.setRespawn(com.vhelium.lotig.constants.Constants.RespawnTime);
					}
					else
						deathHUD.setRespawn(com.vhelium.lotig.constants.Constants.RespawnTimeArena);
					
					player.getSprite().setVisible(false);
					
					if(tombId != -1)
					{
						LevelObject_Tombstone tombstone = new LevelObject_Tombstone(id, dp.getFloat(), dp.getFloat(), dp.getInt(), dp.getInt());
						tombstone.getSprite().setZIndex((int) ((tombstone.getBounds().getY() + tombstone.getBounds().getHeight()) / tmxMap.getMapHeight() * 1000));
						sceneManager.getGameScene().getStage().addActor(tombstone.getSprite());
						tmxMap.addLevelObject(tombId, tombstone, null);
					}
					
					saveGame();
					setHUD(deathHUD);
				}
				else if(entities.containsKey(id))//Coop Player
				{
					entities.get(id).setDead(true);
					entities.get(id).getSprite().setVisible(false);
//					entities.remove(id); //NOT needed since we know he is dead and the sprite is removed!
					
					if(tombId != -1)
					{
						LevelObject_Tombstone tombstone = new LevelObject_Tombstone(id, dp.getFloat(), dp.getFloat(), dp.getInt(), dp.getInt());
						tombstone.getSprite().setZIndex((int) ((tombstone.getBounds().getY() + tombstone.getBounds().getHeight()) / tmxMap.getMapHeight() * 1000));
						sceneManager.getGameScene().getStage().addActor(tombstone.getSprite());
						tmxMap.addLevelObject(tombId, tombstone, null);
					}
				}
			}
		});
	}
	
	public void handlePlayerTeamRespawn(final DataPacket dp)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				player.setDead(false);
				addEntity(myPlayerNr, "Player", null, dp.getFloat(), dp.getFloat(), dp.getFloat(), 0, 0, player.getShootSpeed(), false);
				
				setHUD(hud);
			}
		});
	}
	
	public void handlePlayerTeamJoined(DataPacket dp)
	{
		final int playerNr = dp.getInt();
		final int teamNr = dp.getInt();
		
		final EntityClientMixin ent = playerNr == myPlayerNr ? player : (entities.containsKey(playerNr) ? entities.get(playerNr) : null);
//		if(playerNr == myPlayerNr)
//			ent = player;
//		else if(entities.containsKey(playerNr))
//			ent = entities.get(playerNr);
		
		if(ent != null)
		{
			Gdx.app.postRunnable(new Runnable()
			{
				@Override
				public void run()
				{
					ent.removePvPFlag();
					if(teamNr != 0)
					{
						Sprite s = new Sprite(ent.getSprite().getWidth() / 2 - 4, -4, GameHelper.getInstance().getGameAsset("teamFlag" + teamNr));
						ent.setPvPFlag(s);
					}
				}
			});
		}
	}
	
	public void handleEnemyDeath(DataPacket dp)
	{
		int id = dp.getInt();
		if(id != -1 && entities.containsKey(id))
		{
			List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
			for(IQuestListener listener : toLoopIQuestListeners)
				listener.onEntityDied(entities.get(id).getName());
			
			if(hud.getMenu("character").isActive)
				hud.getCallback().updateQuestText();
			removeEntity(id);
		}
		else if(id == -1)
		{
			List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
			for(IQuestListener listener : toLoopIQuestListeners)
			{
				listener.onEntityDied(dp.getString());
			}
		}
	}
	
	public void handleBossDeath(DataPacket dp)
	{
		int id = dp.getInt();
		if(id != -1 && entities.containsKey(id))
		{
			List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
			for(IQuestListener listener : toLoopIQuestListeners)
			{
				listener.onEntityDied(entities.get(id).getName());
				listener.onBossDied();
			}
			if(hud.getMenu("character").isActive)
				hud.getCallback().updateQuestText();
			removeEntity(id);
		}
		else if(id == -1)
		{
			List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
			for(IQuestListener listener : toLoopIQuestListeners)
			{
				listener.onEntityDied(dp.getString());
				listener.onBossDied();
			}
		}
	}
	
	public void handleItemsDropped(DataPacket dp)
	{
		int bagCount = dp.getInt();
		for(int i = 0; i < bagCount; i++)
		{
			final int bagId = dp.getInt();
			if(!lootBags.containsKey(bagId))
			{
				final float x = dp.getFloat();
				final float y = dp.getFloat();
				final ConcurrentHashMap<Integer, String> drops = new ConcurrentHashMap<Integer, String>();
				int itemCount = dp.getInt();
				for(int it = 0; it < itemCount; it++)
				{
					drops.put(dp.getInt(), dp.getString());
				}
				
				Gdx.app.postRunnable(new Runnable()
				{
					@Override
					public void run()
					{
						lootBags.put(bagId, new LootBag(bagId, x, y, drops, GameHelper.getInstance().getGameAsset("lootBag")));
						sceneManager.getGameScene().getStage().addActor(lootBags.get(bagId).getSprite());
					}
				});
			}
			else
			{
				int itSize = dp.getInt();
				for(int it = 0; it < itSize; it++)
				{
					int pos = dp.getInt();
					lootBags.get(bagId).addItem(pos, Item.getItemFromStringFormat(dp.getString()));
					hud.lootBagItemAdded(lootBags.get(bagId), pos);
				}
			}
		}
		SoundManager.playSound(SoundFile.loot_dropped);
	}
	
	//returns a LootBag that is not Full & in Range. If nothing was found, return null
	private LootBag getNearestLootBag(float x, float y)
	{
		LootBag res = null;
		double shortestDist = LootBag.lootRadius + 1;//get the nearest
		for(Entry<Integer, LootBag> e : lootBags.entrySet())
		{
			double dist = Math.sqrt(Math.pow(e.getValue().getX() - x, 2) + Math.pow(e.getValue().getY() - y, 2));//Pytagoras
			if(dist <= LootBag.lootRadius)//in range
			{
				if(dist < shortestDist)
				{
					res = e.getValue();
					shortestDist = dist;
				}
			}
		}
		return res;
	}
	
	public void handleLootItemPicked(DataPacket dp)
	{
		final int playerNr = dp.getInt();
		final int bagId = dp.getInt();
		final int itemCount = dp.getInt();
		
		if(lootBags.containsKey(bagId))
		{
			for(int i = 0; i < itemCount; i++)
			{
				int slotNr = dp.getInt();
				if(lootBags.get(bagId).getItems().containsKey(slotNr))
				{
					if(playerNr == myPlayerNr)
					{
						player.getInventory().addItem(lootBags.get(bagId).getItems().get(slotNr), true, false);
					}
					lootBags.get(bagId).removeItem(slotNr);
					hud.lootBagItemRemoved(lootBags.get(bagId), slotNr);
				}
			}
			player.getInventory().doUpdate(ItemCategory.All);
			if(lootBags.get(bagId).getItems().size() < 1)
			{
				Gdx.app.postRunnable(new Runnable()
				{
					@Override
					public void run()
					{
						lootBags.get(bagId).getSprite().remove();
						hud.lootBagRemoved(lootBags.get(bagId));
						lootBags.remove(bagId);
					}
				});
			}
		}
	}
	
	public void handleChestItemPicked(DataPacket dp)
	{
		final int playerNr = dp.getInt();
		final int chestId = dp.getInt();
		final int itemCount = dp.getInt();
		LevelObject_Chest chest = (LevelObject_Chest) tmxMap.getLevelObjects().get(chestId);
		
		if(chest != null)
		{
			for(int i = 0; i < itemCount; i++)
			{
				int slotNr = dp.getInt();
				if(chest.getFakeLootBag().getItems().containsKey(slotNr))
				{
					if(playerNr == myPlayerNr)
						player.getInventory().addItem(chest.getFakeLootBag().getItems().get(slotNr), true, true);
					chest.getFakeLootBag().removeItem(slotNr);
					
					hud.lootBagItemRemoved(chest.getFakeLootBag(), slotNr);
				}
			}
		}
	}
	
	public void handleAddCondition(final DataPacket dp)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				player.addCondition(dp.getString(), dp.getInt(), dp.getLong(), dp.getString());
			}
		});
	}
	
	public void handleRemoveCondition(final DataPacket dp)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				int id = dp.getInt();
				String condi = dp.getString();
				if(id == myPlayerNr)
				{
					player.removeCondition(condi);
				}
				else
				{
					entities.get(id).removeCondition(condi);
				}
			}
		});
	}
	
	public void handleAddConditionEffectOthers(DataPacket dp)
	{
		final int entityId = dp.getInt();
		final String name = dp.getString();
		final int duration = dp.getInt();
		final long absStartTime = dp.getLong();
		final String effect = dp.getString();
		
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				if(entities.containsKey(entityId))
				{
					if(entities.get(entityId).conditionEffects.containsKey(name))
					{
						entities.get(entityId).conditionEffects.get(name).setAutoRemove(entities.get(entityId), name, duration, absStartTime, timeDifferenceServer);
					}
					else
					{
						float scale = (entities.get(entityId).getWidth() / player.getWidth() + entities.get(entityId).getHeight() / player.getHeight()) / 2;
						
						TiledTextureRegion texture = GameHelper.getInstance().getEffectManager().getEffectAsset(effect);
						Effect e = new Effect(entities.get(entityId), scale, texture);
						e.animate(effectManager.getAnimationTime(effect), true);
						e.setAutoRemove(entities.get(entityId), name, duration, absStartTime, timeDifferenceServer);
						e.setZIndex(Z_INDEX_EFFECTS);
						entities.get(entityId).getSprite().addActor(e);
						entities.get(entityId).conditionEffects.put(name, e);
					}
				}
			}
		});
	}
	
	private void showDamageNumber(int entityId, int value, int damageType)
	{
		if(entityId == myPlayerNr)
			player.addDamageNumberToQueue(new DamageNumber(player.getSprite().getWidth(), value, damageType, activity));
		else
		{
			EntityClient entity = entities.get(entityId);
			if(entity != null)
				entity.addDamageNumberToQueue(new DamageNumber(entity.getSprite().getWidth(), value, damageType, activity));
		}
	}
	
	private void showDamageNumber(int value, int damageType, int x, int y)
	{
		sceneManager.getGameScene().getStage().addActor(new DamageNumber(x * 2, y, value, damageType, activity));
	}
	
	public void handleUpdatePvPScores(DataPacket dp)
	{
		GameHUDMenu pvpHud = hud.getMenu("pvpscores");
		pvpHud.setParam(dp.getInt() + ";" + dp.getInt());
		if(!pvpHud.isActive)
			hud.showMenu("pvpscores");
	}
	
	public void handleBossHpUpdate(DataPacket dp)
	{
		GameHUDMenu bossHud = hud.getMenu("boss");
		bossHud.setParam(dp.getString() + ";" + dp.getFloat());
		if(!bossHud.isActive)
			hud.showMenu("boss");
	}
	
	public int getMapWidth()
	{
		return tmxMap.getMapWidth();
	}
	
	public int getMapHeight()
	{
		return tmxMap.getMapHeight();
	}
	
	public boolean isMultiplayerClient()
	{
		return sceneManager.getGameScene().getConnectionType() != ConnectionType.Singleplayer && !sceneManager.getGameScene().isHost();
	}
	
	public void destroy()
	{
		
	}
	
	public void saveGame()
	{
		if(player != null && savedGame != null)
			savedGame.save(player.getSaveData());
	}
	
	public void saveStash()
	{
		for(int i = 0; i < 4; i++)
			GlobalSettings.update("stash" + i, player.getStashString(i));
		saveGame();
	}
	
	public boolean isHost()
	{
		return sceneManager.getGameScene().isHost();
	}
	
	public ConnectionType getConnectionType()
	{
		return sceneManager.getGameScene().getConnectionType();
	}
	
	public void sendDataPacket(DataPacket dp)
	{
		try
		{
			connectionHandler.sendData(dp.finish());
		}
		catch (IOException e)
		{
			Log.e("ClientLevel", "Error sending a DataPacket");
		}
	}
	
	public PlayerClient getPlayer()
	{
		return player;
	}
	
	public void returnToMainMenu()
	{
		returnToMainMenu(0);
	}
	
	public void returnToMainMenu(int code)
	{
		sceneManager.getGameScene().setGameState(GameState.STOPPED);
		sceneManager.getGameScene().dispose();
		sceneManager.setScene(new MainMenuScene(activity, sceneManager, code));
	}
	
	public void registerQuest(Quest quest)
	{
		questListeners.add(quest.questObjective);
//		Log.w("ClientLevel", "Quest registered: Step: " + quest.getStep() + ", new listener count: " + questListeners.size());
	}
	
	public void unregisterQuest(Quest quest)
	{
		questListeners.remove(quest.questObjective);
//		Log.e("ClientLevel", "Quest UNregistered: Step: " + quest.getStep() + ", new listener count: " + questListeners.size());
	}
	
	public String getMapName()
	{
		return tmxMap.getCurrentMapName();
	}
	
	public TMXMap getMap()
	{
		return tmxMap;
	}
	
	public int getDifficulty()
	{
		return difficulty;
	}
	
	public void handleOtherMapLoaded(DataPacket dp)
	{
		player.handleOtherMapLoaded(dp.getString());
	}
	
	public void onQuestFinished(Quest quest)
	{
		player.onQuestFinished(quest);
	}
	
	private void showDirectionArrow()
	{
		if(isLairActive)
		{
			EventObject townExit = tmxMap.getStaticEvent("TownExit");
			if(townExit != null)
			{
				player.setDirectionArrow(townExit.getRectangle().getX() + townExit.getRectangle().getWidth() / 2, townExit.getRectangle().getY() + townExit.getRectangle().getY() / 2);
			}
		}
		else if(!isOnLairPortals && lairPortals != null)
		{
			player.setDirectionArrow(lairPortals.getRectangle().getX() + lairPortals.getRectangle().getWidth() / 2, lairPortals.getRectangle().getY() + lairPortals.getRectangle().getY() / 2);
		}
		else
		{
			player.hideDirectionArrow();
		}
	}
	
	public void setOnLairPortals(boolean is)
	{
		isOnLairPortals = is;
		showDirectionArrow();
	}
	
	public List<IQuestListener> getQuestListeners()
	{
		return questListeners;
	}
	
	public void onKillQuestReceived(Quest q)
	{
		String monsterName = ((QuestObjectiveKill) q.getQuestObjective()).getTargetName();
		for(EntityClient ent : entities.values())
		{
			if(ent.getName().equalsIgnoreCase(monsterName))
				ent.setQuestMark(true);
		}
		//TODO: Pin the "!" to monsters!
	}
	
	public void onKillQuestFinished(Quest q)
	{
		String monsterName = ((QuestObjectiveKill) q.getQuestObjective()).getTargetName();
		for(EntityClient ent : entities.values())
		{
			if(ent.getName().equalsIgnoreCase(monsterName))
				ent.setQuestMark(false);
		}
	}
	
	@Override
	public void onSettingChanged(String name, boolean value)
	{
		if(name.equals("Aim-Help"))
			player.onAimHelpChanged(value);
		else if(name.equals("Center-Minimap"))
			hud.getMinimap().setCentered(value);
		else if(name.equals("Music"))
			SoundManager.setHasMusic(value);
		else if(name.equals("Sound"))
			SoundManager.setHasSound(value);
	}
	
	public void onBackKeyDown()
	{
		SoundManager.playSound(SoundFile.menu_ingame_selected);
		if(!hud.closeMiddleMenues())
			if(!hud.closeRightMenues())
				hud.toggleMenu("menu");
	}
	
	public void setHUD(HUD hud)
	{
//		movementControl.resetKnob();
//		rotationControl.resetKnob();
		if(sceneManager.getCurrentScene().getHUD() != null)
			sceneManager.getCurrentScene().getHUD().isActive = false;
		if(hud != null)
			hud.isActive = true;
		sceneManager.getCurrentScene().setActiveHUD(hud);
	}
	
	public void showHUD()
	{
		setHUD(hud);
	}
	
	public void hideHUD()
	{
		setHUD(null);
	}
	
	public void onDialogue(float x, float y, int dialogueId, LevelObject_NPC npc)
	{
		String text = GameHelper.getInstance().getDialogue(dialogueId);
		Dialogue dialogue = new Dialogue(this, x, y, text, npc);
		dialogue.setZIndex(Z_INDEX_DIALOGUES);
		sceneManager.getGameScene().getStage().addActor(dialogue);
	}
	
	public void onDialogueFinished(LevelObject_NPC npc)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_NPC_SPOKEN);
		dp.setInt(npc.id);
		dp.setInt(npc.uniqueId);//only as a backup if the server doesn't know the npc!
		sendDataPacket(dp);
		
		if(npc.uniqueId != -1)
		{
			List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
			for(IQuestListener listener : toLoopIQuestListeners)
				listener.onDialogueFinished(npc.uniqueId);
		}
	}
	
	public void handleNpcSpoken(int uniqueId)
	{
		if(uniqueId != -1)
		{
			List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(questListeners);
			for(IQuestListener listener : toLoopIQuestListeners)
				listener.onDialogueFinished(uniqueId);
		}
	}
	
	public void handleEntitySkinChanged(final int id, final String skin)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				if(id == myPlayerNr)
				{
					player.onSkinChanged(skin);
				}
				else
				{
					if(entities.containsKey(id))
						entities.get(id).onSkinChanged(skin);
				}
			}
		});
	}
	
	public void handleTDNextRound(DataPacket dp)
	{
		final int round = dp.getInt();
		final float cdLeft = dp.getFloat();
		
		GameHUD_TDInfo tdHUD = (GameHUD_TDInfo) hud.getMenu("tdInfo");
		tdHUD.updateRound(round);
		if(cdLeft > 0)
			hud.getCallback().postMessage("Next wave in " + Math.round(cdLeft / 1000) + " seconds!", true);
		if(!tdHUD.isActive)
			hud.showMenu("tdInfo");
	}
	
	public void handleTDCashUpdate(DataPacket dp)
	{
		final int cash = dp.getInt();
		
		GameHUD_TDInfo tdHUD = (GameHUD_TDInfo) hud.getMenu("tdInfo");
		tdHUD.updateCash(cash);
		if(!tdHUD.isActive)
			hud.showMenu("tdInfo");
		
		if(hud.getMenu("defenseTower").isActive)
			hud.getMenu("defenseTower").showUp();
	}
	
	public void handleTDHPUpdate(DataPacket dp)
	{
		final int hp = dp.getInt();
		
		GameHUD_TDInfo tdHUD = (GameHUD_TDInfo) hud.getMenu("tdInfo");
		tdHUD.updateHP(hp);
		if(!tdHUD.isActive)
			hud.showMenu("tdInfo");
	}
	
	public void requestOpenGemStore()
	{
		if(GameHelper.getPlatformResolver().isSupportingGemStore() && hud.isActive && !player.isDead())
		{
			setHUD(gemStoreHUD);
			gemStoreHUD.onShowUp();
		}
		else
		{
			hud.getCallback().postMessage("Can't open Gem Store now", false);
		}
	}
	
	public void requestPlayerRecoverHeal(float hp)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REQUEST_RECOVER_HEAL);
		dp.setFloat(hp);
		sendDataPacket(dp);
	}
	
	public void requestEnemyOnFire(int attackerId)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REQUEST_ENEMY_ON_FIRE);
		dp.setInt(attackerId);
		sendDataPacket(dp);
	}
	
	public void requestRandomBlink()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REQUEST_RANDOM_BLINK);
		sendDataPacket(dp);
	}
}
