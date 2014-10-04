package com.vhelium.lotig.scene.connection;

import java.io.IOException;

public interface IServerConnectionCallback
{
	void connectionEstablished(int playerNr) throws IOException;
	
	void messageReceived(int playerNr, byte[] data) throws IOException;
	
	void connectionLost(int playerNr);
}
