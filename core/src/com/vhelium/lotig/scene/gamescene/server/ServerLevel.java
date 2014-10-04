package com.vhelium.lotig.scene.gamescene.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.quest.Quest;
import com.vhelium.lotig.scene.gamescene.runnable.RealmRunnable;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_LairPortal;

public class ServerLevel implements IServerLevelCallback
{
	public static int realmIdTown = 1;
	public static int realmIdFight = 2;
	public static int realmIdPvP = 3;
	public static int realmIdPvM = 4;
	public static int realmIdTD = 5;
	
	Main activity;
	private final SceneManager sceneManager;
	private IServerConnectionHandler connectionHandler;
	
	private ConcurrentHashMap<Integer, Realm> connectedPlayersRealms;
	
	private ConcurrentHashMap<String, Integer> unlockedLairs;
	private Quest storyQuestHost;
	private final List<Integer> uniqueChestsOpened = new ArrayList<Integer>();
	
	protected int difficulty = Difficulty.Normal;
	
	private Realm fightRealm;
	private Realm townRealm;
	private RealmPvP pvpRealm;
	private RealmPvM pvmRealm;
	private ConcurrentHashMap<String, Realm> tempRealms;
	private int tempRealmCounter = 10;
	
	public ServerLevel(Main activity, SceneManager sceneManager)
	{
		this.activity = activity;
		this.sceneManager = sceneManager;
	}
	
	public void load()
	{
		loadRes();
		applyRes();
	}
	
	private void loadRes()
	{
		
	}
	
	private void applyRes()
	{
		
	}
	
	public void startGame(IServerConnectionHandler connectionHandler, int difficulty, boolean tutorialSeen)
	{
		this.difficulty = difficulty;
		this.connectionHandler = connectionHandler;
		
		connectedPlayersRealms = new ConcurrentHashMap<Integer, Realm>();
		
		townRealm = new Realm(activity, realmIdTown, this, connectionHandler);
		fightRealm = new Realm(activity, realmIdFight, this, connectionHandler);
		pvpRealm = new RealmPvP(activity, realmIdPvP, this, connectionHandler);
		pvmRealm = new RealmPvM(activity, realmIdPvM, this, connectionHandler);
		tempRealms = new ConcurrentHashMap<String, Realm>();
		unlockedLairs = new ConcurrentHashMap<String, Integer>();
		
		if(tutorialSeen/* || Constants.isDebugging*/)
			townRealm.loadMap("town");
		else
			townRealm.loadMap("tutorial");
		fightRealm.loadMap("pretown"); //nothing or just an outer map
	}
	
	public void update(float pSecondsElapsed)
	{
		float delta = pSecondsElapsed * 1000;
		
		if(townRealm.getRealmPlayerCount() > 0 || townRealm.forcedUpdateTimeLeft > 0)
			townRealm.update(delta);
		
		if(fightRealm.getRealmPlayerCount() > 0 || fightRealm.forcedUpdateTimeLeft > 0)
			fightRealm.update(delta);
		
		if(pvpRealm.getMap() != null && (pvpRealm.getRealmPlayerCount() > 0 || pvpRealm.forcedUpdateTimeLeft > 0))
			pvpRealm.update(delta);
		
		if(pvmRealm.getMap() != null && (pvmRealm.getRealmPlayerCount() > 0 || pvmRealm.forcedUpdateTimeLeft > 0))
			pvmRealm.update(delta);
		
		for(Realm realm : tempRealms.values())
			if(realm.getMap() != null && (realm.getRealmPlayerCount() > 0 || realm.forcedUpdateTimeLeft > 0))
				realm.update(delta);
	}
	
	public void playerJoinsRealm(PlayerServer player, Realm realm)
	{
		player.isLoading = true;
		Realm old = connectedPlayersRealms.get(player.Nr);
		if(old == realm)
			Log.w("ServerLevel", "WARNING: PLAYER" + player.Nr + " REJOINED THE SAME REALM!");
		
		if(!(old instanceof EmptyRealm) && old.getPlayers().containsKey(player.Nr))
			old.removePlayer(player.Nr);//Remove player from old realm
			
		connectedPlayersRealms.put(player.Nr, realm);
		try
		{
			realm.addPlayer(player.Nr, player);
		}
		catch (IOException e)
		{
			Log.d("Realm.out", "error realm.addPlayer(" + player.Nr + ", player); ..");
		}
	}
	
	public void playerPortTown(int playerNr)
	{
		if(connectedPlayersRealms.get(playerNr).getPlayer(playerNr).realmID != getRealmByName("town").realmID)
			playerJoinsRealm(connectedPlayersRealms.get(playerNr).getPlayer(playerNr), townRealm);
	}
	
	public void changeRealmMap(Realm realm, String newMapName)
	{
		for(Entry<Integer, PlayerServer> player : realm.getPlayers().entrySet())
		{
			player.getValue().isLoading = true;
		}
		
		String oldMapName = realm.getCurrentMapName();
		realm.loadMap(newMapName);
		
		//send MSG_LOAD_MAP_INFO to players:
		for(Entry<Integer, PlayerServer> player : realm.getPlayers().entrySet())
		{
			DataPacket dpAnswer = new DataPacket();
			dpAnswer.setInt(MessageType.MSG_LOAD_MAP_INFO);
			dpAnswer.setString(realm.getCurrentMapName());
			Log.d("Realm.out", "sending map to load..");
			try
			{
				connectionHandler.sendData(player.getKey(), dpAnswer.finish());
			}
			catch (IOException e1)
			{
				Log.e("Realm.out", "error sending packet2");
			}
		}
		if(getTotalPlayerCount() > realm.getRealmPlayerCount())//players outside of this realm?
		{
			//send map name to other players (for quests)
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_CLIENT_PLAYER_JOINED_OTHER_MAP);
			dpOthers.setString(realm.getCurrentMapName());
			sendDataPacketToAllConnectedPlayersExceptInRealm(realm, dpOthers);
		}
		
		//check for town exit
		if(realm.realmID == fightRealm.realmID && townRealm.getMap().getNextLevel()/*TownExit*/!= null)//fight realm changed the map
		{
			if(oldMapName.equals("pretown") && !newMapName.equals("pretown"))
				townRealm.getMap().getNextLevel().setStateAndInform(1, townRealm);
			else if(newMapName.equals("pretown"))
				townRealm.getMap().getNextLevel().setStateAndInform(0, townRealm);
		}
	}
	
	public void informPlayersOtherMap(String currentMapName)
	{
		for(Entry<Integer, Realm> realm : connectedPlayersRealms.entrySet())
		{
			DataPacket dp = new DataPacket(MessageType.MSG_OTHER_MAP_HAS_BEEN_LOADED);
			dp.setString(currentMapName);
			realm.getValue().sendToPlayer(realm.getKey(), dp);
		}
	}
	
	public void changeRealmMap(String realmName, String mapName)
	{
		changeRealmMap(getRealmByName(realmName), mapName);
	}
	
	public void changeTempRealmMap(Realm realm, String mapName)
	{
		changeRealmMap(realm, mapName);
	}
	
	public Realm getRealmByName(String name)
	{
		if(name.equalsIgnoreCase("town"))
			return townRealm;
		else if(name.equalsIgnoreCase("fight"))
			return fightRealm;
		else if(name.equalsIgnoreCase("pvp"))
			return pvpRealm;
		else if(name.equalsIgnoreCase("pvm"))
			return pvmRealm;
		else
			return null;
	}
	
	public void addPlayer(int playerNr, DataPacket dp)
	{
		if(playerNr == 1)
		{
			int lairCount = dp.getInt();
			for(int i = 0; i < lairCount; i++)
				unlockedLairsPut(dp.getString(), dp.getInt());
			String sQuest = dp.getString();
			if(!sQuest.equals(""))
				storyQuestHost = new Quest(sQuest, null);
			else
				storyQuestHost = null;
			
			if(fightRealm.getMap().getNextLevel() != null)
				fightRealm.getMap().getNextLevel().refresh(fightRealm);
			
			int uniqueChestCount = dp.getInt();
			for(int i = 0; i < uniqueChestCount; i++)
				uniqueChestsOpened.add(dp.getInt());
			
			townRealm.getMap().refreshUniqueChests(uniqueChestsOpened);
			townRealm.getMap().checkDependingLevelObjects(townRealm);
			
			fightRealm.getMap().refreshUniqueChests(uniqueChestsOpened);
			fightRealm.getMap().checkDependingLevelObjects(fightRealm);
			
			pvmRealm.getMap().refreshUniqueChests(uniqueChestsOpened);
			pvmRealm.getMap().checkDependingLevelObjects(pvmRealm);
			
			pvpRealm.getMap().refreshUniqueChests(uniqueChestsOpened);
			pvpRealm.getMap().checkDependingLevelObjects(pvpRealm);
		}
		PlayerServer player = new PlayerServer(this, playerNr, dp);
		
		Log.d("ServerLevel.out", "Add Player: " + playerNr);
		
		connectedPlayersRealms.put(playerNr, Realm.getEmptyRealm());
		playerJoinsRealm(player, townRealm);
	}
	
	public void removePlayer(int playerNr)
	{
		connectedPlayersRealms.get(playerNr).removePlayer(playerNr);
		connectedPlayersRealms.remove(playerNr);
	}
	
	public void destroy()
	{
		
	}
	
	public int getTotalPlayerCount()
	{
		return connectedPlayersRealms.size();
	}
	
	@Override
	public void sendDataPacketToPlayer(int playerNr, DataPacket dp)
	{
		connectedPlayersRealms.get(playerNr).sendToPlayer(playerNr, dp);
	}
	
	@Override
	public void sendDataPacketToAllPlayersInThisRealm(int playerNr, DataPacket dp)
	{
		connectedPlayersRealms.get(playerNr).sendToAllActivePlayers(dp);
	}
	
	@Override
	public void sendDataPacketToAllConnectedPlayers(DataPacket dp)
	{
		for(Entry<Integer, Realm> e : connectedPlayersRealms.entrySet())
		{
			e.getValue().sendToPlayer(e.getKey(), dp);
		}
	}
	
	@Override
	public void sendDataPacketToAllPlayersInThisRealmExcept(int nr, DataPacket dp)
	{
		connectedPlayersRealms.get(nr).sendToAllActivePlayersExcept(nr, dp);
	}
	
	public void sendDataPacketToAllConnectedPlayersExcept(int except, DataPacket dp)
	{
		for(Entry<Integer, Realm> e : connectedPlayersRealms.entrySet())
		{
			if(e.getKey() != except)
				e.getValue().sendToPlayer(e.getKey(), dp);
		}
	}
	
	public void sendDataPacketToAllConnectedPlayersExceptInRealm(Realm realm, DataPacket dpOthers)
	{
		for(Entry<Integer, Realm> e : connectedPlayersRealms.entrySet())
		{
			if(e.getValue().realmID != realm.realmID)
				e.getValue().sendToPlayer(e.getKey(), dpOthers);
		}
	}
	
	@Override
	public int getDifficulty()
	{
		return difficulty;
	}
	
	public ConcurrentHashMap<Integer, Realm> getConnectedPlayersRealms()
	{
		return connectedPlayersRealms;
	}
	
	public ConcurrentHashMap<Integer, PlayerServer> getConnectedPlayers()
	{
		ConcurrentHashMap<Integer, PlayerServer> res = new ConcurrentHashMap<Integer, PlayerServer>();
		for(Entry<Integer, Realm> pr : connectedPlayersRealms.entrySet())
		{
			res.put(pr.getKey(), pr.getValue().getPlayer(pr.getKey()));
		}
		return res;
	}
	
	public Realm getTempRealm(String mapName)
	{
		if(!tempRealms.containsKey(mapName))
		{
			Realm realm;
			if(mapName.startsWith("td"))
			{
				realm = new RealmTD(activity, tempRealmCounter, this, connectionHandler);
			}
			else if(mapName.startsWith("pvp"))
			{
				realm = new RealmPvP(activity, tempRealmCounter, this, connectionHandler);
			}
			else if(mapName.startsWith("pvm"))
			{
				realm = new RealmPvM(activity, tempRealmCounter, this, connectionHandler);
			}
			else
				realm = new Realm(activity, tempRealmCounter, this, connectionHandler);
			tempRealmCounter++;
			realm.loadMap(mapName);
			tempRealms.put(mapName, realm);
		}
		return tempRealms.get(mapName);
	}
	
	public ConcurrentHashMap<String, Integer> getUnlockedLairs()
	{
		return unlockedLairs;
	}
	
	public void handleNextLair(int playerNr, DataPacket dp)
	{
		String lair = dp.getString();
		int nr = dp.getInt();
		unlockedLairsPut(lair, nr);
		if(getTotalPlayerCount() > 1)
		{
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_NEXT_LAIR);
			dpOthers.setString(lair);
			dpOthers.setInt(nr);
			sendDataPacketToAllConnectedPlayersExcept(playerNr, dpOthers);
		}
	}
	
	public void unlockedLairsPut(String lair, int nr)
	{
		unlockedLairs.put(lair, nr);
		
		if(nr > 0)
			for(LevelObject obj : townRealm.getMap().getLevelObjects().values())
				if(obj instanceof LevelObject_LairPortal && ((LevelObject_LairPortal) obj).getLair().equalsIgnoreCase(lair))
				{
					obj.setStateAndInform(1, townRealm);//turn lever
					break;
				}
	}
	
	public void handleQuestUpdateHost(int playerNr, DataPacket dp)
	{
		int step = dp.getInt();
		int progress = dp.getInt();
		
		if(storyQuestHost != null && storyQuestHost.getStep() == step)
			storyQuestHost.setProgress(progress);
		else
		{
			String sQuest = GameHelper.getInstance().getStoryQuest(step);
			if(sQuest != null)
			{
				storyQuestHost = new Quest(sQuest, null);
				storyQuestHost.setProgress(progress);
			}
			else
				storyQuestHost = null;//no new quest
				
			if(fightRealm.getMap().getNextLevel() != null)
				fightRealm.getMap().getNextLevel().refresh(fightRealm);
		}
		
		processOnAllRealms(new RealmRunnable()
		{
			@Override
			public void run(Realm realm)
			{
				if(realm != null && realm.getMap() != null)
					realm.getMap().checkDependingLevelObjects(realm);
			}
		});
		
		if(getTotalPlayerCount() > 1)
		{
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_QUEST_UPDATE_CLIENT);
			dpOthers.setInt(step);
			dpOthers.setInt(progress);
			sendDataPacketToAllConnectedPlayersExcept(playerNr, dpOthers);
		}
	}
	
	private void processOnAllRealms(RealmRunnable runnable)
	{
		runnable.run(fightRealm);
		runnable.run(townRealm);
		runnable.run(pvpRealm);
		runnable.run(pvmRealm);
		for(Realm temp : tempRealms.values())
			runnable.run(temp);
	}
	
	public String getStoryQuestHostString()
	{
		if(storyQuestHost != null)
			return storyQuestHost.getStringFormat();
		else
			return "";
	}
	
	public Quest getStoryQuestHost()
	{
		return storyQuestHost;
	}
	
	public List<Integer> getUniqueChestsOpened()
	{
		return uniqueChestsOpened;
	}
	
	public void handlePlayerChangeSkin(int playerNr, DataPacket dp)
	{
		String skin = dp.getString();
		connectedPlayersRealms.get(playerNr).getPlayer(playerNr).setAsset(skin);
		
		if(connectedPlayersRealms.get(playerNr).getPlayers().size() > 1)
		{
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_ENTITY_SKIN_CHANGED);
			dpOthers.setInt(playerNr);
			dpOthers.setString(skin);
			connectedPlayersRealms.get(playerNr).sendToAllActivePlayersExcept(playerNr, dpOthers);
		}
	}
}
