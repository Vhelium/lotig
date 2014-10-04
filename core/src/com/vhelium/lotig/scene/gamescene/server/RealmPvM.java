package com.vhelium.lotig.scene.gamescene.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.levelobject.EventObject;

public class RealmPvM extends Realm
{
	private int round = 0;
	private boolean isRunning = false;
	private final float countdown = 5000;
	private float countdownLeft = 0;
	private final int arenaDifficulty = 0;
	private List<EventObject> monsterSpawns;
	private static final float bonusFamePerRound = 0.34f;
	private float finishedDelayLeft = 0;
	
	private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, EntityServer>> enemyRounds;
	
	public RealmPvM(Main activity, int realmID, ServerLevel level, IServerConnectionHandler connectionHandler)
	{
		super(activity, realmID, level, connectionHandler);
		enemyRounds = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, EntityServer>>();
		pvp = false;
		dropsAllowed = false;
	}
	
	@Override
	public void addPlayer(int playerNr, PlayerServer player) throws IOException
	{
		super.addPlayer(playerNr, player);
		
		if(!isRunning)
		{
			startArena();
		}
	}
	
	private void startArena()
	{
		resetArena();
		isRunning = true;
		countdownLeft = countdown;
		
		DataPacket dp = new DataPacket(MessageType.MSG_ARENA_NEXT_ROUND);
		dp.setFloat(countdown);
		sendToAllActivePlayers(dp);
		
		HashMap<Integer, List<String>> enemiesToAdd = GameHelper.getInstance().getArenaRounds(getCurrentMapName());
		for(Entry<Integer, List<String>> e : enemiesToAdd.entrySet())//rounds
		{
			enemyRounds.put(e.getKey(), new ConcurrentHashMap<Integer, EntityServer>());
			int spawnPoint = 0;
			for(String enemy : enemiesToAdd.get(e.getKey()))
			{
				String[] name = enemy.split("-");
				EntityServer ent = GameHelper.getInstance().getEnemyModel(name[0]).createEntity(arenaDifficulty, Integer.parseInt(name[1]), level.getTotalPlayerCount(), this);
				EventObject spawn = monsterSpawns.get(spawnPoint % monsterSpawns.size());
				ent.setNewPosition(spawn.getRectangle().getX(), spawn.getRectangle().getY());
				ent.roaming = true;
				ent.roamingRange = (tmxMap.getMapHeight() + tmxMap.getMapWidth()) / 4;
				ent.spawned();
				spawnPoint++;
				
				enemyCounter++;
				enemyRounds.get(e.getKey()).put(enemyCounter, ent);
			}
		}
	}
	
	private void onArenaFinished()
	{
		finishedDelayLeft = 200f;
	}
	
	private void arenaFinished()
	{
		for(Integer playerNr : players.keySet())
		{
			PlayerServer player = players.get(playerNr);
			player.setHp(player.getMaxHp());
			player.setMana(player.getMaxMana());
			player.setRotation(180);
			players.get(playerNr).removeAllConditions();
			level.playerPortTown(playerNr);
		}
		
		Log.w("pvm", "finished after round: " + round);
		if(round > 3)
		{
			int fame = 0;
			float bonus = 0f;
			for(int r = 3; r < round; r++)
			{
				fame += 1 + (int) bonus;
				bonus += bonusFamePerRound;
			}
			
			DataPacket dp = new DataPacket(MessageType.MSG_ARENA_FAME_REWARD);
			dp.setInt(fame);
			level.sendDataPacketToAllConnectedPlayers(dp);
			
			Log.w("pvm", "sent to player");
		}
		
		resetArena();
	}
	
	private void nextRound()
	{
		round++;
		
		enemies.clear();
		bossEntity = null;
		
		if(enemyRounds.get(round) == null)
		{
			onArenaFinished();
		}
		else
		{
			addEnemies(enemyRounds.get(round));
			informRound();
		}
	}
	
	private void resetArena()
	{
		isRunning = false;
		finishedDelayLeft = 0;
		
		round = 0;
		enemies.clear();
		bossEntity = null;
		enemiesToRemove.clear();
		playerBullets.clear();
		playerBulletsToRemove.clear();
		enemyBullets.clear();
		enemyBulletsToRemove.clear();
		lootbags.clear();
		lootBagsToRemove.clear();
		bagsToInform.clear();
	}
	
	@Override
	public void handleMapLoadCompleted(int playerNr) throws IOException//Player loaded the map. Now send entity infos and inform others
	{
		super.handleMapLoadCompleted(playerNr);
		
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_UPDATE_ARENA_ROUND);
		dpAnswer.setInt(round);
		
		sendToPlayer(playerNr, dpAnswer);
		
		if(countdownLeft > 0)
		{
			DataPacket dpCd = new DataPacket(MessageType.MSG_ARENA_NEXT_ROUND);
			dpCd.setFloat(countdownLeft);
			sendToAllActivePlayers(dpCd);
		}
	}
	
	@Override
	protected void enemyDeath(int enemyNr)
	{
		super.enemyDeath(enemyNr);
		
		if(enemies.size() < 1)
		{
			if(round < enemyRounds.size())
			{
				countdownLeft = countdown;
				DataPacket dpCd = new DataPacket(MessageType.MSG_ARENA_NEXT_ROUND);
				dpCd.setFloat(countdown);
				sendToAllActivePlayers(dpCd);
			}
			else
			{
				onArenaFinished();
			}
		}
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		
		if(finishedDelayLeft > 0)
		{
			finishedDelayLeft -= delta;
			
			if(finishedDelayLeft <= 0)
				arenaFinished();
		}
		else if(countdownLeft > 0)
		{
			countdownLeft -= delta;
			
			if(countdownLeft <= 0)
				nextRound();
		}
	}
	
	@Override
	protected void onPlayerDeath(int id, PlayerServer player)
	{
		for(Condition cond : player.getConditions().values())
			if(cond.getConditionListener() != null)
				cond.getConditionListener().onDied(player);
		
		player.removeAllConditions();
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_PLAYER_DIED);
		dp.setInt(id);
		dp.setInt(-1);
		dp.setBoolean(false);
		
		sendToAllActivePlayers(dp);
		
		boolean playerAlive = false;
		for(PlayerServer pl : players.values())
			if(!pl.isDead())
			{
				playerAlive = true;
				break;
			}
		if(!playerAlive)
			finishedDelayLeft = 2500f;
	}
	
	@Override
	public void handleRequestRespawn(int playerNr, DataPacket dp)
	{
		Log.w("pvm", "player died at round: " + round);
		
		PlayerServer player = players.get(playerNr);
		if(!player.wasDead)
			return;
		
		player.setHp(player.getMaxHp() / 2);
		player.setMana(player.getMaxMana() / 2);
		player.setRotation(180);
		players.get(playerNr).removeAllConditions();
		player.wasDead = false;
		
		level.playerPortTown(playerNr);//Das bewirkt, dass removePlayer aufgerufen wird!
	}
	
	@Override
	public void handleUnhandledPacket(int packetType, int playerNr, DataPacket dp)
	{
		switch(packetType)
		{
//			case MessageType.MGS_SOMETHING:
//				break;
		}
	}
	
	private void informRound()
	{
		DataPacket dpScores = new DataPacket();
		dpScores.setInt(MessageType.MSG_UPDATE_ARENA_ROUND);
		dpScores.setInt(round);
		
		sendToAllActivePlayers(dpScores);
	}
	
	@Override
	public void loadMap(String mapName)
	{
		super.loadMap(mapName);
		monsterSpawns = tmxMap.getStaticEvents("MonsterSpawn");
	}
	
	@Override
	public void removePlayer(int playerNr)
	{
		super.removePlayer(playerNr);
		if(players.size() < 1)
		{
			onArenaFinished();
		}
	}
	
	@Override
	public void requestTownPort(int playerNr)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_POST_MESSAGE);
		dp.setString("Port not available here!");
		dp.setBoolean(false);
		sendToPlayer(playerNr, dp);
	}
}
