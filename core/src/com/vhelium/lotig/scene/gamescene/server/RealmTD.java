package com.vhelium.lotig.scene.gamescene.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.levelobject.EventObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_DefenseTower;

public class RealmTD extends Realm
{
	//x: 212, y: 74, z: -385
	private int round = 0;
	private int hp = 0;
	private boolean isRunning = false;
	private final float countdown = 5000f;
	private float countdownLeft = 0;
	private final float spawnDelay = 1200f;
	private float spawnDelayLeft = 0f;
	private EventObject monsterSpawn;
	private final ConcurrentHashMap<Integer, EventObject> waypoints = new ConcurrentHashMap<Integer, EventObject>();
	private int enemiesLeft = 0;
	
	private int cash = 0;
	
	private final List<EntityServer> enemiesToAdd = new ArrayList<EntityServer>();
	
	public RealmTD(Main activity, int realmID, ServerLevel level, IServerConnectionHandler connectionHandler)
	{
		super(activity, realmID, level, connectionHandler);
		pvp = false;
		dropsAllowed = false;
	}
	
	@Override
	public void addPlayer(int playerNr, PlayerServer player) throws IOException
	{
		super.addPlayer(playerNr, player);
		
		if(!isRunning)
		{
			start();
		}
		
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_TD_NEXT_ROUND);
		dp.setInt(round);
		dp.setFloat(countdownLeft);
		sendToPlayer(playerNr, dp);
		
		dp = new DataPacket();
		dp.setInt(MessageType.MSG_TD_CASH_UPDATE);
		dp.setInt(cash);
		sendToPlayer(playerNr, dp);
		
		dp = new DataPacket();
		dp.setInt(MessageType.MSG_TD_HP_UPDATE);
		dp.setInt(hp);
		sendToPlayer(playerNr, dp);
	}
	
	private void start()
	{
		reset();
		isRunning = true;
		countdownLeft = countdown;
		List<EventObject> tws = tmxMap.getStaticEvents("Tower");
		for(EventObject tw : tws)
			addLevelObjectAtRuntime(new LevelObject_DefenseTower(-1, this, new Rectangle(tw.getRectangle().getX(), tw.getRectangle().getY(), 48, 48)));
	}
	
	private void reset()
	{
		isRunning = false;
		
		round = 1;
		cash = 400;
		hp = 20;
		enemiesLeft = 0;
		countdownLeft = 0;
		spawnDelayLeft = 0;
		enemiesToAdd.clear();
		
		this.removeAllEnemies();
		
		List<LevelObject> toRemove = new ArrayList<LevelObject>();
		for(LevelObject obj : tmxMap.getLevelObjects().values())
			if(obj instanceof LevelObject_DefenseTower)
				toRemove.add(obj);
		
		for(LevelObject tR : toRemove)
			this.removeLevelObject(tR);
		toRemove.clear();
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		
		if(countdownLeft > 0)
		{
			countdownLeft -= delta;
			
			if(countdownLeft <= 0)
				nextRound();
		}
		if(spawnDelayLeft > 0)
		{
			spawnDelayLeft -= delta;
			
			if(spawnDelayLeft <= 0)
			{
				spawnDelayLeft = spawnDelay;
				spawnNextEntity();
			}
		}
	}
	
	private void spawnNextEntity()
	{
		if(enemiesToAdd.size() <= 0)
			return;
		
		EntityServer entity = enemiesToAdd.get(0);
		entity.setRotation(-90);
		entity.setNewPosition(monsterSpawn.getRectangle().getX() + monsterSpawn.getRectangle().getWidth() / 2 - entity.getWidth() / 2, monsterSpawn.getRectangle().getY() + monsterSpawn.getRectangle().getHeight() / 2 - entity.getHeight() / 2);
		entity.spawned();
		entity.roaming = false;
		
		List<Vector2> gotos = new ArrayList<Vector2>();
		for(int i = 1; i <= waypoints.size(); i++)
			if(waypoints.containsKey(i))
			{
				EventObject wp = waypoints.get(i);
				gotos.add(new Vector2((int) (wp.getRectangle().getX() + wp.getRectangle().getWidth() / 2), (int) (wp.getRectangle().getY() + wp.getRectangle().getHeight() / 2)));
			}
		entity.goToQueue(gotos);
		enemiesToAdd.remove(entity);
		
		enemyCounter++;
		addEntity(enemyCounter, entity);
	}
	
	private void nextRound()
	{
		enemiesToAdd.clear();
		if(round > 1 && round % 10 == 0)
			for(int i = 0; i < 2; i++)
			{
				EntityServer ent = GameHelper.getInstance().getEnemyModel("td_boss").createEntity(0, round - 1, 1, this);
				enemiesToAdd.add(ent);
			}
		else
			for(int i = 0; i < 5 + round / 3; i++)
			{
				EntityServer ent = GameHelper.getInstance().getEnemyModel("td_normal").createEntity(0, round - 1, 1, this);
				enemiesToAdd.add(ent);
			}
		enemiesLeft = enemiesToAdd.size();
		spawnDelayLeft = spawnDelay;
	}
	
	private void gameOver()
	{
		start();
		enemiesLeft = 0;
		countdownLeft = 10000f;
		informCash();
		informHP();
		informNextRound();
	}
	
	@Override
	protected void enemyDeath(int enemyNr)
	{
		super.enemyDeath(enemyNr);
		enemiesLeft--;
		
		if(round % 10 == 0)
			cash += (round / 10) * 100;
		else
			cash += 8 + round / 2;
		
		informCash();
		
		if(enemiesLeft < 1)
			roundFinished();
	}
	
	private void roundFinished()
	{
		enemiesLeft = 0;
		countdownLeft = countdown;
		round++;
		informNextRound();
		
		cash += round * 2;
		informCash();
	}
	
	private void informNextRound()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_TD_NEXT_ROUND);
		dp.setInt(round);
		dp.setFloat(countdownLeft);
		sendToAllActivePlayers(dp);
	}
	
	private void informCash()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_TD_CASH_UPDATE);
		dp.setInt(cash);
		sendToAllActivePlayers(dp);
	}
	
	private void informHP()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_TD_HP_UPDATE);
		dp.setInt(hp);
		sendToAllActivePlayers(dp);
	}
	
	@Override
	public void handleMapLoadCompleted(int playerNr) throws IOException//Player loaded the map. Now send entity infos and inform others
	{
		super.handleMapLoadCompleted(playerNr);
	}
	
	@Override
	public void handleOnEnterEvent(EntityServer enemy, MapProperties properties)
	{
		super.handleOnEnterEvent(enemy, properties);
		if(properties.containsKey("tdFinish"))
		{
			super.enemyDeath(enemy.Nr);
			enemiesLeft--;
			hp--;
			informHP();
			
			if(hp <= 0)
				gameOver();
			else if(enemiesLeft < 1)
				roundFinished();
		}
	}
	
	@Override
	public void handleUnhandledPacket(int packetType, int playerNr, DataPacket dp)
	{
		switch(packetType)
		{
			case MessageType.MSG_UPGRADE_DEFENSE_TOWER:
				handleUpgradeDefenseTower(dp.getInt(), dp.getInt());
				break;
		}
	}
	
	private void handleUpgradeDefenseTower(int id, int path)
	{
		LevelObject_DefenseTower tower = (LevelObject_DefenseTower) getMap().getLevelObjects().get(id);
		if(tower != null)
		{
			int upgradeCosts = LevelObject_DefenseTower.getUpgradeCostToState(path);
			if(cash >= upgradeCosts)
			{
				tower.upgradeTower(this, path);
				cash -= upgradeCosts;
				informCash();
			}
		}
	}
	
	@Override
	public void loadMap(String mapName)
	{
		super.loadMap(mapName);
		monsterSpawn = tmxMap.getStaticEvent("MonsterSpawn");
		List<EventObject> rawWaypoints = tmxMap.getStaticEvents("Waypoint");
		for(EventObject obj : rawWaypoints)
			waypoints.put(Integer.parseInt(obj.properties.getValues().next().toString()), obj);
	}
	
	@Override
	public void removePlayer(int playerNr)
	{
		super.removePlayer(playerNr);
		if(players.size() < 1)
		{
			forcedUpdateTimeLeft = 0f;//instant freeze
		}
	}
	
//	@Override
//	public void requestTownPort(int playerNr)
//	{
//		//Town Port disabled..
//	}
}
