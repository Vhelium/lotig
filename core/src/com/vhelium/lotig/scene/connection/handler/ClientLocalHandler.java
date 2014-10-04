package com.vhelium.lotig.scene.connection.handler;

import java.io.IOException;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;

public class ClientLocalHandler implements IClientConnectionHandler
{
	private IServerConnectionCallback serverCallback;
	private IClientConnectionCallback clientCallback;
	
	@Override
	public void registerCallback(IServerConnectionCallback serverCallback, IClientConnectionCallback clientCallback)
	{
		this.serverCallback = serverCallback;
		this.clientCallback = clientCallback;
	}
	
	@Override
	public boolean start() throws IOException
	{
		clientCallback.connectionEstablished();
		serverCallback.connectionEstablished(1);
		
		return true;
	}
	
	@Override
	public void sendData(byte[] data) throws IOException
	{
		serverCallback.messageReceived(1, data);
	}
	
	@Override
	public void destroy()
	{
		//nothing to do -O.O-
	}
}
