package com.vhelium.lotig.scene.gamescene.client;

import java.io.IOException;
import com.badlogic.gdx.Gdx;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.connection.handler.ClientLocalHandler;
import com.vhelium.lotig.scene.connection.handler.ClientWlanHandler;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GameScene.ConnectionType;
import com.vhelium.lotig.scene.gamescene.GameScene.GameState;
import com.vhelium.lotig.scene.gamescene.LoadMapScene;
import com.vhelium.lotig.scene.gamescene.SavedGame;

public class Client implements ILoadMapCallback
{
	private final Main activity;
	private final SceneManager sceneManager;
	private IClientConnectionHandler connectionHandler;
	private ClientLevel level;
	private int myPlayerNr;
	private SavedGame savedGame;
	private long timePingRequestedAbs = 0L;
	
	public Client(Main activity, SceneManager sceneManager)
	{
		this.activity = activity;
		this.sceneManager = sceneManager;
	}
	
	public boolean start(final ConnectionType connectionType, final IServerConnectionCallback serverCallback, SavedGame savedGame)
	{
		level = new ClientLevel(activity, sceneManager);
		this.savedGame = savedGame;
		
		//RESET EVERYTHING!
		if(connectionHandler != null)
		{
			connectionHandler.destroy();
		}
		
		sceneManager.getGameScene().setGameState(GameState.CONNECTING_TO_SERVER);
		
		switch(connectionType)
		{
			case Singleplayer:
				connectionHandler = new ClientLocalHandler();
				Log.d("Client.out", "connectionHandler = new ClientLocalHandler();");
				break;
			
			case Bluetooth:
				connectionHandler = GameHelper.getPlatformResolver().getNewBluetoothClientHandler();
				Log.d("Client.out", "connectionHandler = new ClientBluetoothHandler();");
				break;
			
			case Wlan:
				connectionHandler = new ClientWlanHandler();
				Log.d("Client.out", "connectionHandler = new ClientWlanHandler();");
				break;
		}
		if(connectionHandler != null)
			connectionHandler.registerCallback(serverCallback, iccb);
		else
			return false;
		
		try
		{
			connectionHandler.start();
		}
		catch (IOException e)
		{
			Log.d("Client.out", "error starting client handler..");
			return false;
		}
		return true;
	}
	
	public IClientConnectionCallback iccb = new IClientConnectionCallback()
	{
		@Override
		public void connectionEstablished() throws IOException
		{
			Log.d("Client.out", "connectionEstablished..");
			sceneManager.getGameScene().setGameState(GameState.VERSION_CHECK);
		}
		
		@Override
		public void messageReceived(byte[] dp)
		{
			processPacket(dp);
		}
		
		@Override
		public void connectionLost()
		{
			try
			{
				level.saveGame();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			returnToMainMenu();
		}
		
		@Override
		public Main getActivity()
		{
			return activity;
		}
		
		@Override
		public void returnToMainMenu()
		{
			level.returnToMainMenu();
		}
		
		@Override
		public void returnToMainMenu(int code)
		{
			level.returnToMainMenu(code);
		}
	};
	
	public void update(float delta)
	{
		level.update(delta);
	}
	
	private void processPacket(byte[] data)
	{
		DataPacket dp = new DataPacket(data);
		int packetType = dp.getInt();
		
		switch(packetType)
		{
			case MessageType.MSG_VERSION_CHECK:
				handleVersionCheck(dp);
				break;
			
			case MessageType.MSG_PLAYER_NR:
				handlePlayerNr(dp);
				break;
			
			case MessageType.MSG_PLAYER_HEALTH_UPDATE:
				level.handlePlayerHealthUpdate(dp);
				break;
			
			case MessageType.MSG_ITEMS_DROPPED:
				level.handleItemsDropped(dp);
				break;
			
			case MessageType.MSG_REMOVE_BULLETS:
				level.handleRemoveBullets(dp);
				break;
			
			case MessageType.MSG_ENEMY_DEATH:
				level.handleEnemyDeath(dp);
				break;
			
			case MessageType.MSG_BOSS_DEATH:
				level.handleBossDeath(dp);
				break;
			
			case MessageType.MSG_LOAD_MAP_INFO:
				handleLoadMapInfo(dp);
				break;
			
			case MessageType.MSG_OTHER_MAP_HAS_BEEN_LOADED:
				level.handleOtherMapLoaded(dp);
				break;
			
			case MessageType.MSG_ENTITY_POS_UPDATE:
				level.handleEntityPosUpdate(dp);
				break;
			
			case MessageType.MSG_BULLET_SHOT:
				level.handleBulletShot(dp);
				break;
			
			case MessageType.MSG_MAP_LOAD_COMPLETED:
				handleMapLoadCompleted(dp);
				break;
			
			case MessageType.MSG_ADD_ENTITY:
				handleAddEntity(dp);
				break;
			
			case MessageType.MSG_ADD_ENTITIES:
				handleAddEntities(dp);
				break;
			
			case MessageType.MSG_REMOVE_ENTITY:
				handleRemoveEntity(dp);
				break;
			
			case MessageType.MSG_REMOVE_ENTITIES:
				handleRemoveEntities(dp);
				break;
			
			case MessageType.MSG_OTHER_PLAYER_SP_CHANGED:
				level.handleOtherPlayerShootSpeedChanged(dp);
				break;
			
			case MessageType.MSG_LOOT_ITEM_PICKED:
				level.handleLootItemPicked(dp);
				break;
			
			case MessageType.MSG_CHEST_ITEM_PICKED:
				level.handleChestItemPicked(dp);
				break;
			
			case MessageType.MSG_NEXT_LAIR:
				level.getPlayer().handleNextLair(dp);
				break;
			
			case MessageType.MGS_DAMAGE_NUMBER:
				level.handleDamageNumber(dp);
				break;
			
			case MessageType.MSG_ADD_CONDITION:
				level.handleAddCondition(dp);
				break;
			
			case MessageType.MSG_REMOVE_CONDITION:
				level.handleRemoveCondition(dp);
				break;
			
			case MessageType.MSG_PLAY_EFFECT:
				level.handlePlayEffeft(dp);
				break;
			
			case MessageType.MSG_PLAY_SOUND:
				level.handlePlaySound(dp);
				break;
			
			case MessageType.MSG_ADD_CONDITION_EFFECT_OTHERS:
				level.handleAddConditionEffectOthers(dp);
				break;
			
			case MessageType.MSG_TIME_PING:
				handleTimePing(dp);
				break;
			
			case MessageType.MSG_ENTITY_SET_NEW_POS:
				level.newEntityPos(dp);
				break;
			
			case MessageType.MSG_ADD_LEVEL_OBJECT:
				level.handleAddLevelObject(dp);
				break;
			
			case MessageType.MSG_REMOVE_LEVEL_OBJECTS:
				level.handleRemoveLevelObjects(dp);
				break;
			
			case MessageType.MSG_LEVEL_OBJECT_STATE_CHANGED:
				level.handleLevelObjectStateChanged(dp);
				break;
			
			case MessageType.MSG_PLAYER_DIED:
				level.handlePlayerDied(dp);
				break;
			
			case MessageType.MSG_PLAYER_TEMP_ATTR_CHANGE:
				level.getPlayer().handleTempAttributeChange(dp);
				break;
			
			case MessageType.MSG_UPDATE_PVP_SCORES:
				level.handleUpdatePvPScores(dp);
				break;
			
			case MessageType.MSG_PLAYER_TEAM_RESPAWN:
				level.handlePlayerTeamRespawn(dp);
				break;
			
			case MessageType.MSG_PLAYER_STUNNED:
				level.getPlayer().setStunned(dp.getBoolean());
				break;
			
			case MessageType.MSG_PLAYER_ROOTED:
				level.getPlayer().setRooted(dp.getBoolean());
				break;
			
			case MessageType.MSG_PLAYER_SILENCED:
				level.getPlayer().setSilenced(dp.getBoolean());
				break;
			
			case MessageType.MSG_BOSS_HP_UPDATE:
				level.handleBossHpUpdate(dp);
				break;
			
			case MessageType.MSG_ARENA_NEXT_ROUND:
				level.handleArenaNextRound(dp);
				break;
			
			case MessageType.MSG_UPDATE_ARENA_ROUND:
				level.handleArenaUpdateRound(dp);
				break;
			
			case MessageType.MSG_ARENA_FAME_REWARD:
				level.getPlayer().arenaFameReward(dp.getInt());
				break;
			
			case MessageType.MSG_POST_MESSAGE:
				level.hud.getCallback().postMessage(dp.getString(), dp.getBoolean());
				break;
			
			case MessageType.MSG_CONFIRM_REALM_REQUEST:
				level.handleConfirmRealmRequest(dp);
				break;
			
			case MessageType.MSG_PLAYER_TEAM_JOINED:
				level.handlePlayerTeamJoined(dp);
				break;
			
			case MessageType.MSG_PLAYER_COOLDOWN_LOWERING:
				level.getPlayer().lowerCooldowns(dp.getInt());
				break;
			
			case MessageType.MSG_QUEST_UPDATE_CLIENT:
				level.getPlayer().updateStoryQuestHost(dp.getInt(), dp.getInt());
				break;
			
			case MessageType.MSG_CLIENT_PLAYER_JOINED_OTHER_MAP:
				level.handleClientPlayerJoinedOtherMap(dp.getString());
				break;
			
			case MessageType.MSG_UNIQUE_CHEST_OPENED:
				level.getPlayer().handleUniqueChestOpened(dp.getInt());
				break;
			
			case MessageType.MSG_NPC_SPOKEN:
				level.handleNpcSpoken(dp.getInt());
				break;
			
			case MessageType.MSG_ENTITY_SKIN_CHANGED:
				level.handleEntitySkinChanged(dp.getInt(), dp.getString());
				break;
			
			case MessageType.MSG_LEVEL_OBJECT_REMOVE_COLLISION:
				level.handleLevelObjectRemoveCollision(dp.getInt());
				break;
			
			case MessageType.MSG_AREA_ENTERED:
				level.handlePlayerAreaEntered(dp.getString());
				break;
			
			case MessageType.MSG_TD_NEXT_ROUND:
				level.handleTDNextRound(dp);
				break;
			
			case MessageType.MSG_TD_CASH_UPDATE:
				level.handleTDCashUpdate(dp);
				break;
			
			case MessageType.MSG_TD_HP_UPDATE:
				level.handleTDHPUpdate(dp);
				break;
			
			case MessageType.MSG_ADD_MOVEMENT_MODIFIER:
				level.getPlayer().addMovementModifier(dp.getString(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat());
				break;
			
			case MessageType.MSG_PLAYER_YOU_GOT_HIT:
				level.getPlayer().handleYouGotHit(dp.getInt());
				break;
		}
	}
	
	private void handleVersionCheck(DataPacket dp)
	{
		try
		{
			int version = dp.getInt();
			int myVersion = GameHelper.getPlatformResolver().getVersion();
			if(version == myVersion)
			{
				DataPacket dpAnswer = new DataPacket();
				dpAnswer.setInt(MessageType.MSG_VERSION_CHECK);
				connectionHandler.sendData(dpAnswer.finish());
				sceneManager.getGameScene().setGameState(GameState.SEND_CLIENT_INFO);
			}
			else
				level.returnToMainMenu(3);
		}
		catch (IOException e)
		{
			Log.e("Client.out", "handleVersionCheck: failed");
		}
	}
	
	private void handlePlayerNr(final DataPacket dp)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				myPlayerNr = dp.getInt();
				Log.d("Client.out", "starting level as player " + myPlayerNr + "..");
				level.startGame(connectionHandler, myPlayerNr, savedGame, dp.getInt(), dp);
				Log.d("Client.out", "level started..");
				
				try
				{
					if(getLevel().isMultiplayerClient())
						Thread.sleep(1500);
				}
				catch (InterruptedException e1)
				{
					Log.e("Client.out", "handlePlayerNr: failed");
				}
				
				try
				{
					//SEND CLIENT INFO TO SERVER
					DataPacket dpInfo = new DataPacket();
					dpInfo.setInt(MessageType.MSG_SEND_CLIENT_INFO);
					dpInfo.setString("Player");
					dpInfo = level.getPlayer().collectDataForServer(dpInfo);
					
					connectionHandler.sendData(dpInfo.finish());
				}
				catch (IOException e)
				{
					Log.e("Client.out", "could not send client info!!");
				}
			}
		});
	}
	
	private void handleLoadMapInfo(final DataPacket dp)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				Log.d("Client.out", "handleLoadMapInfo");
				sceneManager.getGameScene().setGameState(GameState.LOADING_MAP);
				
				if(!(sceneManager.getCurrentScene() instanceof LoadMapScene))
				{
					sceneManager.setScene(GameHelper.getInstance().getLoadMapScene());
//					level.movementControl.resetKnob();
//					level.rotationControl.resetKnob();
					level.hud.hideMenu("pvpscores");
					level.hud.hideMenu("tdInfo");
					level.hud.hideMenu("arenarounds");
					level.hud.hideMenu("boss");
					level.hud.hideMenu("loot");
					level.hud.hideMenu("stash");
					level.hud.hideMenu("action");
					level.hud.hideMenu("enchanting");
					level.hud.toggleMinimap(false);
					level.hud.toggleQuestLog(false);
					level.hud.closeMiddleMenues();
					level.hud.closeRightMenues();
				}
				
				String mapName = dp.getString();
				GameHelper.getInstance().getLoadMapScene().executeLoadMap(Client.this, mapName);
			}
		});
	}
	
	@Override
	public void loadMapExecuted()
	{
		//Send info to server, that map is loaded and wait for infos:
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_MAP_LOAD_COMPLETED);
		
		try
		{
			connectionHandler.sendData(dpAnswer.finish());
		}
		catch (IOException e)
		{
			Log.e("Client.out", "nope 1");
		}
	}
	
	private void handleMapLoadCompleted(final DataPacket dp)
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				level.handleMapLoadCompleted(dp);
				level.resetDelta = true;
				
				timePingRequestedAbs = System.currentTimeMillis();
				try
				{
					connectionHandler.sendData(new DataPacket(MessageType.MSG_TIME_PING).finish());
				}
				catch (IOException e1)
				{
					Log.e("Client.out", "could not send timePingRequestedAbs!!");
				}
				
				sceneManager.setScene(sceneManager.getGameScene());
				sceneManager.getGameScene().setGameState(GameState.RUNNING);
			}
		});
	}
	
	public void handleTimePing(DataPacket dp)
	{
		long serverAbsTime = dp.getLong();
		long curr = System.currentTimeMillis();
		long ping2 = (curr - timePingRequestedAbs) / 2;
		level.timeDifferenceServer = serverAbsTime + ping2 - curr;
	}
	
	private void handleAddEntity(DataPacket dp)
	{
		level.addEntity(dp.getInt(), dp.getString(), dp.getString(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getInt(), dp.getInt(), dp.getFloat(), dp.getBoolean());
	}
	
	private void handleAddEntities(DataPacket dp)
	{
		int size = dp.getInt();
		for(int i = 0; i < size; i++)
		{
			level.addEntity(dp.getInt(), dp.getString(), dp.getString(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getInt(), dp.getInt(), dp.getFloat(), dp.getBoolean());
		}
	}
	
	private void handleRemoveEntity(DataPacket dp)
	{
		level.removeEntity(dp.getInt());
	}
	
	private void handleRemoveEntities(DataPacket dp)
	{
		int count = dp.getInt();
		for(int i = 0; i < count; i++)
			level.removeEntity(dp.getInt());
	}
	
	public ClientLevel getLevel()
	{
		return level;
	}
	
	public void dispose()
	{
		if(level != null)
			level.destroy();
		
		if(connectionHandler != null)
		{
			connectionHandler.destroy();
			connectionHandler = null;
		}
	}
}
