package com.vhelium.lotig;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.badlogic.gdx.Gdx;
import com.vhelium.lotig.constants.Log;

public class IOSResolver implements PlatformResolver
{
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
}
