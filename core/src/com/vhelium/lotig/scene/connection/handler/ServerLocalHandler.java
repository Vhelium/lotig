package com.vhelium.lotig.scene.connection.handler;

import java.io.IOException;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;

public class ServerLocalHandler implements IServerConnectionHandler
{
	//private IServerConnectionCallback serverCallback;
	private IClientConnectionCallback clientCallback;
	
	@Override
	public void registerCallback(IServerConnectionCallback serverCallback, IClientConnectionCallback clientCallback)
	{
		//this.serverCallback = serverCallback;
		this.clientCallback = clientCallback;
	}
	
	@Override
	public void start()
	{
		// nothing?
	}
	
	@Override
	public void sendData(int destinationPlayerNr, byte[] data) throws IOException
	{
		clientCallback.messageReceived(data);
	}
	
	@Override
	public int getPlayerCount()
	{
		return 1;
	}
	
	@Override
	public void destroy()
	{
		// nothing to do :)
	}
	
	@Override
	public void playerDisconnected(int playerNr)
	{
		
	}
}
