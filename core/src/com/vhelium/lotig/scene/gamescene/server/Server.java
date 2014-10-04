package com.vhelium.lotig.scene.gamescene.server;

import java.io.IOException;
import java.util.Map.Entry;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.connection.handler.ServerLocalHandler;
import com.vhelium.lotig.scene.connection.handler.ServerWlanHandler;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GameScene.ConnectionType;
import com.vhelium.lotig.scene.gamescene.GameScene.GameState;

public class Server
{
	public static final int MAX_SUPPORTED = 4;
	private IServerConnectionHandler connectionHandler;
	private Thread connectionThread;
	private final SceneManager sceneManager;
	private ServerLevel level;
	private final Main activity;
	
	public Server(Main activity, SceneManager sceneManager)
	{
		this.activity = activity;
		this.sceneManager = sceneManager;
	}
	
	public boolean start(ConnectionType connectionType, int difficulty, IClientConnectionCallback clientCallback, boolean tutorialSeen)
	{
		level = new ServerLevel(activity, sceneManager);
		
		//RESET EVERYTHING!
		if(connectionHandler != null)
		{
			connectionHandler.destroy();
		}
		
		sceneManager.getGameScene().setGameState(GameState.STARTING_SERVER);
		
		switch(connectionType)
		{
			case Singleplayer:
				connectionHandler = new ServerLocalHandler();
				break;
			
			case Bluetooth:
				connectionHandler = GameHelper.getPlatformResolver().getNewBluetoothServerHandler();
				break;
			
			case Wlan:
				connectionHandler = new ServerWlanHandler();
				break;
		}
		
		if(connectionHandler != null)
			connectionHandler.registerCallback(iccb, clientCallback);
		else
			return false;
		
		connectionThread = new Thread()
		{
			@Override
			public void run()
			{
				connectionHandler.start();
			}
		};
		connectionThread.start();
		
		level.startGame(connectionHandler, difficulty, tutorialSeen);
		return true;
	}
	
	public IServerConnectionCallback iccb = new IServerConnectionCallback()
	{
		@Override
		public void connectionEstablished(int playerNr) throws IOException
		{
			Log.d("Server.out", "new connected client");
			
			//send MSG_VERSION_CHECK to player:
			DataPacket dpAnswer = new DataPacket();
			dpAnswer.setInt(MessageType.MSG_VERSION_CHECK);
			dpAnswer.setInt(GameHelper.getPlatformResolver().getVersion());
			connectionHandler.sendData(playerNr, dpAnswer.finish());
		}
		
		@Override
		public void messageReceived(int playerNr, byte[] data) throws IOException
		{
			processPacket(playerNr, data);
		}
		
		@Override
		public void connectionLost(int playerNr)
		{
			if(level.getConnectedPlayers().containsKey(playerNr))
				level.removePlayer(playerNr);
		}
	};
	
	public void update(float delta)
	{
		if(level != null)
			level.update(delta);
	}
	
	private void processPacket(int playerNr, byte[] data) throws IOException
	{
		DataPacket dp = new DataPacket(data);
		int packetType = dp.getInt();
		
		switch(packetType)
		{
			case MessageType.MSG_VERSION_CHECK:
				handleVersionCheck(playerNr, dp);
				break;
			
			case MessageType.MSG_SEND_CLIENT_INFO:
				handleClientInfo(playerNr, dp);
				break;
			
			case MessageType.MSG_SEND_PLAYER_POS:
				level.getConnectedPlayersRealms().get(playerNr).handlePlayerPosUpdate(playerNr, dp);
				break;
			
			case MessageType.MSG_REQUEST_SHOOTBULLET:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestShootBullet(playerNr, dp);
				break;
			
			case MessageType.MSG_MAP_LOAD_COMPLETED:
				level.getConnectedPlayersRealms().get(playerNr).handleMapLoadCompleted(playerNr);//Player loaded the map. Now send entity infos and inform others
				break;
			
			case MessageType.MSG_REQUEST_TOWN_PORT:
				level.getConnectedPlayersRealms().get(playerNr).requestTownPort(playerNr);
				break;
			
			case MessageType.MSG_PLAYER_STATUS_UPDATE:
				level.getConnectedPlayersRealms().get(playerNr).handlePlayerStatusUpdate(playerNr, dp);
				break;
			
			case MessageType.MSG_REQUEST_LOOT_ITEM:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestLootItem(playerNr, dp);
				break;
			
			case MessageType.MSG_REQUEST_CHEST_ITEM:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestChestItem(playerNr, dp);
				break;
			
			case MessageType.MSG_SPELL_REQUEST:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestSpell(playerNr, dp);
				break;
			
			case MessageType.MSG_REQUEST_REALM:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestRealm(playerNr, dp);
				break;
			
			case MessageType.MSG_REQUEST_TEMP_REALM:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestTempRealm(playerNr, dp.getString());
				break;
			
			case MessageType.MSG_REQUEST_REALM_WITH_LAIR_NR:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestRealmWithLairNr(playerNr, dp);
				break;
			
			case MessageType.MSG_JOIN_TEAM:
				level.getConnectedPlayersRealms().get(playerNr).handleJoinTeam(playerNr, dp);
				break;
			
			case MessageType.MSG_REQUEST_CONDITION:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestCondition(playerNr, dp);
				break;
			
			case MessageType.MSG_DROP_PLAYER_ITEM:
				level.getConnectedPlayersRealms().get(playerNr).handleDropPlayerItem(playerNr, dp);
				break;
			
			case MessageType.MSG_REQUEST_RESPAWN:
				level.getConnectedPlayersRealms().get(playerNr).handleRequestRespawn(playerNr, dp);
				break;
			
			case MessageType.MSG_NPC_SPOKEN:
				level.getConnectedPlayersRealms().get(playerNr).handleNpcSpoken(playerNr, dp);
				break;
			
			case MessageType.MSG_NEXT_LAIR:
				level.handleNextLair(playerNr, dp);
				break;
			
			case MessageType.MSG_QUEST_UPDATE_HOST:
				level.handleQuestUpdateHost(playerNr, dp);
				break;
			
			case MessageType.MSG_CHANGE_SKIN:
				level.handlePlayerChangeSkin(playerNr, dp);
				break;
			
			case MessageType.MSG_LEVER_PULLED:
				level.getConnectedPlayersRealms().get(playerNr).handleLevelPulled(dp.getInt());
				break;
			
			case MessageType.MSG_TIME_PING:
				DataPacket dpTimePing = new DataPacket(MessageType.MSG_TIME_PING);
				dpTimePing.setLong(System.currentTimeMillis());
				try
				{
					connectionHandler.sendData(playerNr, dpTimePing.finish());
				}
				catch (IOException e)
				{
					Log.e("Realm.out", "error sending packet51");
				}
				break;
			
			case MessageType.MSG_ENTITY_SET_NEW_POS:
				level.getConnectedPlayersRealms().get(playerNr).handlePlayerSetNewPosition(playerNr, dp);
				break;
			
			case MessageType.MSG_I_WANNA_DIE:
				level.getConnectedPlayersRealms().get(playerNr).handleIWannaDie(playerNr);
				break;
			
			case MessageType.MSG_REQUEST_RECOVER_HEAL:
				level.getConnectedPlayersRealms().get(playerNr).handleRecoverHeal(playerNr, dp.getFloat());
				break;
			
			case MessageType.MSG_REQUEST_ENEMY_ON_FIRE:
				level.getConnectedPlayersRealms().get(playerNr).handleEnemyOnFire(playerNr, dp.getInt());
				break;
			
			case MessageType.MSG_REQUEST_RANDOM_BLINK:
				level.getConnectedPlayersRealms().get(playerNr).handleRandomBlink(playerNr);
				break;
			
			default:
				level.getConnectedPlayersRealms().get(playerNr).handleUnhandledPacket(packetType, playerNr, dp);
				break;
		}
	}
	
	private void handleVersionCheck(int playerNr, DataPacket dp) throws IOException
	{
		//Client has same version number
		//Send MSG_PLAYER_NR to player:
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_PLAYER_NR);
		dpAnswer.setInt(playerNr);
		dpAnswer.setInt(level.getDifficulty());
		if(playerNr != 1)
		{
			dpAnswer.setInt(level.getUnlockedLairs().size());
			for(Entry<String, Integer> lair : level.getUnlockedLairs().entrySet())
			{
				dpAnswer.setString(lair.getKey());
				dpAnswer.setInt(lair.getValue());
			}
			dpAnswer.setString(level.getStoryQuestHostString());
		}
		connectionHandler.sendData(playerNr, dpAnswer.finish());
	}
	
	private void handleClientInfo(int playerNr, DataPacket dp) throws IOException
	{
		Log.d("Server.out", "Player" + playerNr + " connected as: " + dp.getString());
		
		//add player object to level:
		level.addPlayer(playerNr, dp);
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
		
		if(connectionThread != null && connectionThread.isAlive())
		{
			connectionThread.interrupt();
			connectionThread = null;
		}
	}
	
	public static boolean isPlayer(int entityId)
	{
		return entityId >= 1 && entityId <= MAX_SUPPORTED;
	}
}
