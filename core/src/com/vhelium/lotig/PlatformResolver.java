package com.vhelium.lotig;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;

public interface PlatformResolver
{
	void init();
	
	public int getVersion();
	
	public InputStream getInputStream(String path);
	
	public File getLocalFile(String path);
	
	public OutputStream getOutputStream(String path);
	
	public void kill();
	
	public void addEventListener(IEventListener listener);
	
	public String[] setupWlanGetIPs();
	
	public void showServerListWlan();
	
	public boolean isSupportingBluetooth();
	
	public IServerConnectionHandler getNewBluetoothServerHandler();
	
	public IClientConnectionHandler getNewBluetoothClientHandler();
	
	public void makeBluetoothVisible();
	
	public boolean isSupportingGemStore();
	
	public IABManager getIABManager();
}
