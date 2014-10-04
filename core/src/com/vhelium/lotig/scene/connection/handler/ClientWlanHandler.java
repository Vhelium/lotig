package com.vhelium.lotig.scene.connection.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import com.vhelium.lotig.EventResult;
import com.vhelium.lotig.IEventListener;
import com.vhelium.lotig.constants.ByteConverter;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class ClientWlanHandler implements IClientConnectionHandler, IEventListener
{
	private IServerConnectionCallback serverCallback;
	private IClientConnectionCallback clientCallback;
	
	private Thread connectionThread;
	private SocketStreamWatcherThread streamWatcherThread;
	
	private Socket serverSocket;
	
	boolean isAlive = true;
	
	@Override
	public void registerCallback(IServerConnectionCallback serverCallback, IClientConnectionCallback clientCallback)
	{
		this.serverCallback = serverCallback;
		this.clientCallback = clientCallback;
		
		GameHelper.getPlatformResolver().addEventListener(this);
	}
	
	@Override
	public boolean start() throws IOException
	{
		if(serverCallback != null)// host doesn't need to connect anything, he has the callbacks
		{
			serverCallback.connectionEstablished(1);
			clientCallback.connectionEstablished();
		}
		else
		{
			checkWlanStateAndShowServerList();
		}
		
		return true;
	}
	
	@Override
	public void sendData(byte[] data) throws IOException
	{
		if(serverCallback != null)// == HOST
		{
			serverCallback.messageReceived(1, data);
		}
		else
		{
			streamWatcherThread.write(data);
		}
	}
	
	private boolean connect(final String hostDevice, final String port) throws IOException
	{
		connectionThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					InetAddress serverAddr = InetAddress.getByName(hostDevice);
					serverSocket = new Socket(serverAddr, Integer.parseInt(port));
					GameHelper.$.setIPinfo("IP: " + serverAddr.getHostAddress() + "\nPort: " + port);
				}
				catch (UnknownHostException e)
				{
					Log.e("ClientWlanHandler.out", "Couldnt connect to selecetd server.1");
					clientCallback.connectionLost();
				}
				catch (IOException e)
				{
					Log.e("ClientWlanHandler.out", "Couldnt connect to selecetd server.2");
					clientCallback.connectionLost();
				}
			}
		};
		connectionThread.start();
		try
		{
			connectionThread.join();
		}
		catch (InterruptedException e)
		{
			Log.e("ClientWlanHandler.out", "Couldnt join the thread..");
		}
		
		if(serverSocket != null)
		{
			Log.e("ClientWlanHandler.out", "SUCCESFULLY CONNECTED TO SERVER!!!");
			streamWatcherThread = new SocketStreamWatcherThread();
			streamWatcherThread.start();
			
			clientCallback.connectionEstablished();
			return true;
		}
		else
		{
			Log.e("ClientWlanHandler.out", "Couldnt connect to selected server.3");
			clientCallback.connectionLost();
			return false;
		}
	}
	
	private void checkWlanStateAndShowServerList()
	{
		GameHelper.getPlatformResolver().setupWlanGetIPs();
		
		showServerList();
	}
	
	private void showServerList()
	{
		GameHelper.getPlatformResolver().showServerListWlan();
	}
	
	public void serverListResultReceived(String device, String port)
	{
		if(device == null || port == null)
		{
			try
			{
				Log.e("ClientWlanHandler.out", "host chosen: " + device + ", port: " + port);
				connect(device, port);
			}
			catch (IOException e)
			{
				Log.e("ClientWlanHandler.out", "error connecting to this server..");
			}
			
		}
		else
		{
			clientCallback.returnToMainMenu();
		}
	}
	
	@Override
	public void eventResultReceived(EventResult event)
	{
		switch(event.getRequestCode())
		{
			case EventResult.SERVER_LIST_WLAN_RESULT_CODE:
				if(event.getResultCode() == EventResult.RESULT_OK)
				{
					String ip = event.getDataExtra("ip", String.class);
					String port = event.getDataExtra("port", String.class);
					Log.e("ClientWlanHandler.out", "host chosen: " + ip + ", port: " + port);
					
					try
					{
						connect(ip, port);
					}
					catch (IOException e)
					{
						Log.e("ClientWlanHandler.out", "error connecting to this server..");
					}
				}
				else
					clientCallback.returnToMainMenu();
				break;
		}
	}
	
	private class SocketStreamWatcherThread extends Thread
	{
		byte[] restBuffer = null;
		
		public SocketStreamWatcherThread()
		{
			
		}
		
		@Override
		public void run()
		{
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(DataPacket.PACKET_SIZE);
			byte[] buffer = new byte[DataPacket.PACKET_SIZE];
			
			try
			{
				InputStream instream = serverSocket.getInputStream();
				
				while(isAlive)
				{
					int bytesRead = -1;
					do
					{
						bytesRead = instream.read(buffer);
//						Log.d("TEST_TO_DETECT_WLHANDLER", "bytesRead: " + bytesRead + " buffer.lenght: " + buffer.length);
						//TODO: v BufferOverflow: Buffer.checkPutBounds()
						byteBuffer.put(buffer);
						
						if(bytesRead > 0)
						{
							byteBuffer.flip();
							byte[] data = new byte[bytesRead];
							byteBuffer.get(data);
							
							byteBuffer.clear();
							
							processData(data);
						}
					} while(bytesRead > 0);
				}
			}
			catch (IOException e)
			{
				Log.e("ClientWlanHandler.out", "error streamWatching client..");
			}
			
			if(isAlive)
				clientCallback.connectionLost();
		}
		
		public void processData(byte[] data)
		{
			Boolean weiter = false;
			
			do
			{
				try
				{
					weiter = false;
					int gesamtDatenLaenge = 0;
					if(restBuffer != null)
					{
						gesamtDatenLaenge = restBuffer.length;
					}
					gesamtDatenLaenge += data.length;
					byte[] gesamtDaten = new byte[gesamtDatenLaenge];
					int restPufferLenght = 0;
					if(restBuffer != null)
					{
						restPufferLenght = restBuffer.length;
						System.arraycopy(restBuffer, 0, gesamtDaten, 0, restBuffer.length);
						restBuffer = null;
					}
					System.arraycopy(data, 0, gesamtDaten, restPufferLenght, data.length);
					//int PacketSize = Array.getInt(gesamtDaten, 0);
					byte[] ia = new byte[Integer.SIZE / Byte.SIZE];
					System.arraycopy(gesamtDaten, 0, ia, 0, ia.length);
					int PacketSize = ByteConverter.toInt(ia);
					if(PacketSize < gesamtDaten.length)
					{
						byte[] packet = new byte[PacketSize];
						System.arraycopy(gesamtDaten, 0, packet, 0, PacketSize);
						clientCallback.messageReceived(packet);
						restBuffer = new byte[gesamtDaten.length - packet.length];
						System.arraycopy(gesamtDaten, packet.length, restBuffer, 0, gesamtDaten.length - packet.length);
						data = new byte[0];
						weiter = true;
					}
					else
					{
						if(PacketSize == gesamtDaten.length)
						{
							clientCallback.messageReceived(gesamtDaten);
							weiter = false;
						}
						else
						{
							restBuffer = gesamtDaten;
						}
					}
				}
				catch (IOException e)
				{
					weiter = true;//???
				}
			} while(weiter);
		}
		
		public void write(byte[] data) throws IOException
		{
			OutputStream outStream = serverSocket.getOutputStream();
			outStream.write(data);
		}
	}
	
	@Override
	public void destroy()
	{
		isAlive = false;
		isListenerDead = true;
		
		if(streamWatcherThread != null)
		{
			try
			{
				serverSocket.getOutputStream().close();
				serverSocket.getInputStream().close();
				serverSocket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		if(connectionThread != null)
		{
			connectionThread.interrupt();
			connectionThread = null;
		}
	}
	
	boolean isListenerDead = false;
	
	@Override
	public boolean isDead()
	{
		return isListenerDead;
	}
}
