package com.vhelium.lotig.scene.connection.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import com.vhelium.lotig.EventResult;
import com.vhelium.lotig.IEventListener;
import com.vhelium.lotig.constants.ByteConverter;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IClientConnectionHandler;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Server;

public class ClientBluetoothHandler implements IClientConnectionHandler, IEventListener
{
	private IServerConnectionCallback serverCallback;
	private IClientConnectionCallback clientCallback;
	
	private Thread connectionThread;
	private SocketStreamWatcherThread streamWatcherThread;
	
	private BluetoothAdapter btAdapter;
	BluetoothSocket serverSocket;
	BluetoothDevice serverDevice;
	
	Activity activity;
	
	boolean isAlive = true;
	
	public ClientBluetoothHandler(Activity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void registerCallback(IServerConnectionCallback serverCallback, IClientConnectionCallback clientCallback)
	{
		this.serverCallback = serverCallback;
		this.clientCallback = clientCallback;
		//register listener:
		GameHelper.getPlatformResolver().addEventListener(this);
	}
	
	@Override
	public boolean start() throws IOException
	{
		try
		{
			btAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		catch (Exception e)
		{
			clientCallback.returnToMainMenu(2);
		}
		
		if(btAdapter == null)
		{
			Log.e("ClBTHandler.out", "no bluetooth adapter xD");
			return false;
		}
		else
		{
			if(serverCallback != null)// host doesn't need to connect anything, he has the callbacks
			{
				serverCallback.connectionEstablished(1);
				clientCallback.connectionEstablished();
			}
			else
			{
				checkBluetoothStateAndShowServerList();
			}
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
	
	private boolean connect(final String hostDevice) throws IOException, InterruptedException
	{
		connectionThread = new Thread()
		{
			@Override
			public void run()
			{
				Log.w("ClBTHandler.out", "try connecting to: " + hostDevice);
				serverDevice = btAdapter.getRemoteDevice(hostDevice);
				GameHelper.$.setIPinfo("Host: " + hostDevice);
				
				//TODO: check for pairing
				
				for(int i = 2; i <= Server.MAX_SUPPORTED/* Player 2 - 4 */&& serverSocket == null; i++)
				{
					Log.w("ClBTHandler.out", "try connect on socketNr: " + i);
					for(int j = 0; j < 2/* attempts */&& serverSocket == null; j++)
					{
						Log.w("ClBTHandler.out", "SocketNr " + i + ", attempt: " + j);
						
						serverSocket = getConnectedSocket(ServerBluetoothHandler.getUUID(i));
						
						if(serverSocket == null)
						{
							try
							{
								Thread.sleep(200);
							}
							catch (InterruptedException e)
							{
								Log.e("ClBTHandler.out", "error waiting to try sleep..");
							}
						}
						else
							return;
					}
				}
			}
		};
		connectionThread.start();
		connectionThread.join(20000);
		
		if(serverSocket == null)
		{
			Log.e("ClBTHandler.out", "serverSocket == null");
			clientCallback.connectionLost();
			return false;
		}
		Log.i("ClBTHandler.out", "serverSocket != null");
		
		streamWatcherThread = new SocketStreamWatcherThread();
		streamWatcherThread.start();
		
		clientCallback.connectionEstablished();
		
		return true;
	}
	
	private void checkBluetoothStateAndShowServerList()
	{
		if(!btAdapter.isEnabled())// ensure bluetooth is turned on!
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBtIntent, EventResult.REQUEST_ENABLE_BT);
		}
		else
		{
			showServerList(); // gets result in to connect to a remote server after the client has chosen a server "connect()" will be called
		}
	}
	
	private void showServerList()
	{
		Intent serverListIntent = new Intent(activity, ServerListBTActivity.class);
		activity.startActivityForResult(serverListIntent, EventResult.SERVER_LIST_BT_RESULT_CODE);
	}
	
	private BluetoothSocket getConnectedSocket(UUID uuidToTry)
	{
		BluetoothSocket myBSock;
		try
		{
			myBSock = serverDevice.createRfcommSocketToServiceRecord(uuidToTry);
			myBSock.connect();
			return myBSock;
		}
		catch (IOException e)
		{
			Log.w("ClBTHandler.out", "socket is occuppied : " + uuidToTry);
		}
		return null;
	}
	
	@Override
	public void eventResultReceived(EventResult event)
	{
		switch(event.getRequestCode())
		{
			case EventResult.SERVER_LIST_BT_RESULT_CODE:
				if(event.getResultCode() == Activity.RESULT_OK)
				{
					String device = event.getDataExtra("device", String.class);
					Log.i("ClBTHandler.out", "host chosen: " + device);
					try
					{
						connect(device);
					}
					catch (IOException e)
					{
						Log.e("ClBTHandler.out", "error connecting to this server..");
					}
					catch (InterruptedException e)
					{
						Log.e("ClBTHandler.out", "error connecting to this server..");
					}
				}
				else
					clientCallback.returnToMainMenu();
				break;
			
			case EventResult.REQUEST_ENABLE_BT:
				checkBluetoothStateAndShowServerList();
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
				Log.e("ClBTHandler.out", "error streamWatching client..");
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
