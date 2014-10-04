package com.vhelium.lotig;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.badlogic.gdx.Gdx;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.connection.handler.ClientBluetoothHandler;
import com.vhelium.lotig.scene.connection.handler.ServerBluetoothHandler;
import com.vhelium.lotig.scene.connection.handler.ServerListBTActivity;
import com.vhelium.lotig.scene.connection.handler.ServerListWlanActivity;

public class AndroidResolver implements PlatformResolver
{
	private final AndroidLauncher activity;
	private AndroidIABManager iabManager;
	private final List<IEventListener> eventListeners = new ArrayList<IEventListener>();
	
	public AndroidResolver(AndroidLauncher activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void init()
	{
		iabManager = new AndroidIABManager(activity);
	}
	
	@Override
	public void addEventListener(IEventListener listener)
	{
		if(!eventListeners.contains(listener))
			eventListeners.add(listener);
	}
	
	@Override
	public int getVersion()
	{
		// TODO Auto-generated method stub
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
		activity.finish();
	}
	
	@Override
	public String[] setupWlanGetIPs()
	{
		WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		
		if(!wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(true);
		
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		
		return new String[] { String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff)) };
	}
	
	@Override
	public void showServerListWlan()
	{
		Intent serverListIntent = new Intent(activity, ServerListWlanActivity.class);
		activity.startActivityForResult(serverListIntent, EventResult.SERVER_LIST_WLAN_RESULT_CODE);
	}
	
	public void activityResultReceived(int requestCode, int resultCode, Intent data)
	{
		EventResult result = new EventResult(requestCode, resultCode, data);
		switch(requestCode)
		{
			case EventResult.SERVER_LIST_WLAN_RESULT_CODE:
				if(resultCode == Activity.RESULT_OK)
				{
					result.putDataExtra("ip", data.getStringExtra(ServerListWlanActivity.EXTRA_SELECTED_IP));
					result.putDataExtra("port", data.getStringExtra(ServerListWlanActivity.EXTRA_SELECTED_PORT));
					break;
				}
				else
					return;
				
			case EventResult.SERVER_LIST_BT_RESULT_CODE:
				if(resultCode == Activity.RESULT_OK)
				{
					result.putDataExtra("device", data.getStringExtra(ServerListBTActivity.EXTRA_SELECTED_DEVICE));
					break;
				}
				else
					return;
				
			case EventResult.REQUEST_TURN_ON_DISCOVERABLE:
				if(resultCode == EventResult.DISCOVERABLE_DURATION)
				{
					result.putDataExtra("duration", EventResult.DISCOVERABLE_DURATION);
					break;
				}
				else
					return;
				
			default:
				break;
		
		}
		
		Iterator<IEventListener> listeners = eventListeners.iterator();
		while(listeners.hasNext())
		{
			IEventListener lis = listeners.next();
			if(lis.isDead())
			{
				listeners.remove();
				continue;
			}
			
			lis.eventResultReceived(result);
			
			if(lis.isDead())
			{
				listeners.remove();
			}
		}
	}
	
	@Override
	public void makeBluetoothVisible()
	{
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, EventResult.DISCOVERABLE_DURATION);
		activity.startActivityForResult(discoverableIntent, EventResult.REQUEST_TURN_ON_DISCOVERABLE);
	}
	
	@Override
	public boolean isSupportingBluetooth()
	{
		return true;
	}
	
	@Override
	public IServerConnectionHandler getNewBluetoothServerHandler()
	{
		return new ServerBluetoothHandler(activity);
	}
	
	@Override
	public IClientConnectionHandler getNewBluetoothClientHandler()
	{
		return new ClientBluetoothHandler(activity);
	}
	
	@Override
	public boolean isSupportingGemStore()
	{
		return true;
	}
	
	@Override
	public IABManager getIABManager()
	{
		return iabManager;
	}
	
}
