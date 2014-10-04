package com.vhelium.lotig.scene.gamescene.server;

import com.vhelium.lotig.scene.connection.DataPacket;

public interface IServerLevelCallback
{
	public void sendDataPacketToPlayer(int playerNr, DataPacket dp);
	
	public void sendDataPacketToAllPlayersInThisRealm(int playerNr, DataPacket dp);
	
	public void sendDataPacketToAllConnectedPlayers(DataPacket dp);
	
	public int getDifficulty();
	
	public void sendDataPacketToAllPlayersInThisRealmExcept(int playerNr, DataPacket dp);
}
