package com.vhelium.lotig.scene.connection;

import java.io.IOException;

public interface IServerConnectionHandler
{
	void registerCallback(IServerConnectionCallback serverCallback, IClientConnectionCallback clientCallback);
	
	void start();
	
	void sendData(int destinationPlayerNr, byte[] data) throws IOException;
	
	int getPlayerCount();
	
	void playerDisconnected(int playerNr);
	
	void destroy();
}
