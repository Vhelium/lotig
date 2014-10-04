package com.vhelium.lotig;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;

public class DesktopResolver implements PlatformResolver
{
	private final List<IEventListener> eventListeners = new ArrayList<IEventListener>();
	
	@Override
	public void addEventListener(IEventListener listener)
	{
		if(!eventListeners.contains(listener))
			eventListeners.add(listener);
	}
	
	@Override
	public void init()
	{
		
	}
	
	@Override
	public int getVersion()
	{
		return 0;
	}
	
	@Override
	public InputStream getInputStream(String path)
	{
		return Gdx.files.local(path).read();
	}
	
	@Override
	public File getLocalFile(String path)
	{
		return Gdx.files.local(path).file();
	}
	
	@Override
	public OutputStream getOutputStream(String path)
	{
		return Gdx.files.local(path).write(false);
	}
	
	@Override
	public void kill()
	{
		Gdx.app.exit();
	}
	
	@Override
	public String[] setupWlanGetIPs()
	{
		try
		{
			InetAddress[] allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
			String[] ips = new String[allByName.length];
			for(int i = 0; i < allByName.length; i++)
				ips[i] = allByName[i].getHostAddress();
			return ips;
		}
		catch (UnknownHostException e)
		{
			Log.e("DesktopResolver", e.getMessage());
		}
		return new String[] { "0.0.0.0" };
	}
	
	@Override
	public void showServerListWlan()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isSupportingBluetooth()
	{
		return false;
	}
	
	@Override
	public IServerConnectionHandler getNewBluetoothServerHandler()
	{
		return null;
	}
	
	@Override
	public IClientConnectionHandler getNewBluetoothClientHandler()
	{
		return null;
	}
	
	@Override
	public void makeBluetoothVisible()
	{
		return;
	}
	
	@Override
	public boolean isSupportingGemStore()
	{
		return false;
	}
	
	@Override
	public IABManager getIABManager()
	{
		return null;
	}
}