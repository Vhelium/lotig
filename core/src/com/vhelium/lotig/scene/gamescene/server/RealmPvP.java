package com.vhelium.lotig.scene.gamescene.server;

import java.io.IOException;
import java.util.ArrayList;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.server.levelobject.EventObject;

public class RealmPvP extends Realm
{
	public static final int TEAM_RED = 1;
	public static final int TEAM_BLUE = 2;
	
	int scoreRed = 0, scoreBlue = 0;
	
	public RealmPvP(Main activity, int realmID, ServerLevel level, IServerConnectionHandler connectionHandler)
	{
		super(activity, realmID, level, connectionHandler);
		pvp = true;
		dropsAllowed = false;
	}
	
	@Override
	public void handleMapLoadCompleted(int playerNr) throws IOException//Player loaded the map. Now send entity infos and inform others
	{
		super.handleMapLoadCompleted(playerNr);
		
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_UPDATE_PVP_SCORES);
		dpAnswer.setInt(scoreRed);
		dpAnswer.setInt(scoreBlue);
		
		sendToPlayer(playerNr, dpAnswer);
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
		
		if(player.getTeamNr() == TEAM_RED)
			scoreBlue++;
		else if(player.getTeamNr() == TEAM_BLUE)
			scoreRed++;
		
		informScores();
	}
	
	@Override
	public void handleRequestRespawn(int playerNr, DataPacket dp)
	{
		PlayerServer player = players.get(playerNr);
		if(!player.wasDead)
			return;
		
		player.setHp(player.getMaxHp());
		player.setMana(player.getMaxMana());
		player.setRotation(180);
		players.get(playerNr).removeAllConditions();
		player.wasDead = false;
		Log.e("Realmpvp", "isDead: " + player.isDead() + ",   hp: " + player.getHp() + ",   maxhp: " + player.getMaxHp());
		
		ArrayList<EventObject> spawns = tmxMap.getStaticEvents("TeamSpawn");
		for(EventObject ev : spawns)
			if(ev.properties.containsKey("team") && Integer.parseInt(ev.properties.get("team", String.class)) == player.getTeamNr())
				player.setNewPosition(ev.getRectangle().getX(), ev.getRectangle().getY());
		
		DataPacket dpPlayer = new DataPacket(MessageType.MSG_PLAYER_TEAM_RESPAWN);
		dpPlayer.setFloat(player.getX());
		dpPlayer.setFloat(player.getY());
		dpPlayer.setFloat(player.getRotation());
		sendToPlayer(playerNr, dpPlayer);
		
		this.playerHealthUpdate(playerNr);
		
		informOtherPlayersAboutPlayer(playerNr, player, false);
	}
	
	@Override
	public void handleUnhandledPacket(int packetType, int playerNr, DataPacket dp)
	{
		switch(packetType)
		{
			case MessageType.MSG_REQUEST_PVP_SCORE_RESET:
				scoreRed = 0;
				scoreBlue = 0;
				informScores();
				break;
		}
	}
	
	private void informScores()
	{
		
		DataPacket dpScores = new DataPacket();
		dpScores.setInt(MessageType.MSG_UPDATE_PVP_SCORES);
		dpScores.setInt(scoreRed);
		dpScores.setInt(scoreBlue);
		
		sendToAllActivePlayers(dpScores);
	}
}
