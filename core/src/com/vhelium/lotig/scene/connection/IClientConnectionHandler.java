package com.vhelium.lotig.scene.connection;

import java.io.IOException;

public interface IClientConnectionHandler
{
	void registerCallback(IServerConnectionCallback serverCallback, IClientConnectionCallback clientCallback);
	
	boolean start() throws IOException;
	
	void sendData(byte[] data) throws IOException;
	
	void destroy();
}
