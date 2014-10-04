package com.vhelium.lotig.scene.connection.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import com.vhelium.lotig.EventResult;
import com.vhelium.lotig.IEventListener;
import com.vhelium.lotig.constants.ByteConverter;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.gamescene.GameHelper;

@SuppressLint("UseSparseArrays")
public class ServerBluetoothHandler implements IServerConnectionHandler, IEventListener
{
	private static final int REQUEST_TURN_ON_DISCOVERABLE = 3;
	
	private BluetoothAdapter bluetoothAdapter;
	
	private final HashMap<Integer, BluetoothSocket> clients;
	private final HashMap<Integer, SocketStreamWatcherThread> streamWatcherThreads;
	private final HashMap<Integer, AcceptThread> acceptThreads;
	private static HashMap<Integer, UUID> uuids;
	
	private IServerConnectionCallback serverCallback;
	private IClientConnectionCallback clientCallback;
	
	Activity activity;
	
	private boolean isAlive = true;
	
	public ServerBluetoothHandler(Activity activity)
	{
		this.activity = activity;
		streamWatcherThreads = new HashMap<Integer, SocketStreamWatcherThread>();
		acceptThreads = new HashMap<Integer, AcceptThread>();
		clients = new HashMap<Integer, BluetoothSocket>();
	}
	
	public static UUID getUUID(int i)
	{
		if(uuids == null)
		{
			uuids = new HashMap<Integer, UUID>();
			
			uuids.put(2, UUID.fromString("83c2a1a0-db30-11e2-a28f-0800200c9a66"));
			uuids.put(3, UUID.fromString("89ed9c60-db30-11e2-a28f-0800200c9a66"));
			uuids.put(4, UUID.fromString("8ead2040-db30-11e2-a28f-0800200c9a66"));
		}
		
		return uuids.get(i);
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
	public void start()
	{
		try
		{
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		catch (Exception e)
		{
			clientCallback.returnToMainMenu(2);
		}
		
		GameHelper.$.setIPinfo("My device: " + bluetoothAdapter.getName());
		
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, EventResult.DISCOVERABLE_DURATION);
		activity.startActivityForResult(discoverableIntent, REQUEST_TURN_ON_DISCOVERABLE);
	}
	
	private void enableAccepts()
	{
		AcceptThread acceptThread2 = new AcceptThread(2);
		acceptThread2.start();
		acceptThreads.put(2, acceptThread2);
		
		AcceptThread acceptThread3 = new AcceptThread(3);
		acceptThread3.start();
		acceptThreads.put(3, acceptThread3);
		
		AcceptThread acceptThread4 = new AcceptThread(4);
		acceptThread4.start();
		acceptThreads.put(4, acceptThread4);
	}
	
	@Override
	public void sendData(int destinationPlayerNr, byte[] data) throws IOException
	{
		if(destinationPlayerNr == 1) // local client
		{
			clientCallback.messageReceived(data);
		}
		else
		{
			streamWatcherThreads.get(destinationPlayerNr).write(data);
		}
	}
	
	private class AcceptThread extends Thread
	{
		public BluetoothServerSocket serverSocket;
		private final int playerNr;
		
		public AcceptThread(int playerNr)
		{
			this.playerNr = playerNr;
			
			BluetoothServerSocket tmp = null;
			try
			{
				tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("AERPG", getUUID(playerNr));
			}
			catch (IOException e)
			{
				
			}
			serverSocket = tmp;
		}
		
		@Override
		public void run()
		{
			BluetoothSocket socket = null;
			while(isAlive)
			{
				try
				{
					socket = serverSocket.accept();
				}
				catch (IOException e)
				{
					break;
				}
				if(socket != null)
				{
					// client connects to server:
					try
					{
						// Close the socket now that the connection has been
						// made.
						serverSocket.close();
						Log.i("SeBTHandler.out", "client connected on socket: " + playerNr);
						
						clients.put(playerNr, socket);
						
						SocketStreamWatcherThread socketStreamWatcherThread = new SocketStreamWatcherThread(playerNr);
						socketStreamWatcherThread.start();
						streamWatcherThreads.put(playerNr, socketStreamWatcherThread);
						
						acceptThreads.remove(playerNr);
						
						serverCallback.connectionEstablished(playerNr);
					}
					catch (IOException e)
					{
						Log.e("SeBTHandler.out", "failed to accept socket");
						if(isAlive)
							playerDisconnected(playerNr);
					}
					break;
				}
			}
		}
	}
	
	private class SocketStreamWatcherThread extends Thread
	{
		private final int playerNr;
		public BluetoothSocket socket;
		byte[] restBuffer = null;
		
		public SocketStreamWatcherThread(int playerNr)
		{
			this.playerNr = playerNr;
			socket = clients.get(playerNr);
		}
		
		@Override
		public void run()
		{
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(DataPacket.PACKET_SIZE);
			byte[] buffer = new byte[DataPacket.PACKET_SIZE];
			
			try
			{
				InputStream instream = socket.getInputStream();
				
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
				Log.e("SeBTHandler.out", "error streamWatching client..");
			}
			
			// Getting out of the while loop means the connection is dead.
			if(isAlive)
				playerDisconnected(playerNr);
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
						serverCallback.messageReceived(/* from */playerNr, packet);
						restBuffer = new byte[gesamtDaten.length - packet.length];
						System.arraycopy(gesamtDaten, packet.length, restBuffer, 0, gesamtDaten.length - packet.length);
						data = new byte[0];
						weiter = true;
					}
					else
					{
						if(PacketSize == gesamtDaten.length)
						{
							serverCallback.messageReceived(/* from */playerNr, gesamtDaten);
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
			OutputStream outStream = socket.getOutputStream();
			outStream.write(data);
		}
	}
	
	@Override
	public int getPlayerCount()
	{
		return clients.size() + 1;
	}
	
	@Override
	public void eventResultReceived(EventResult event)
	{
		Log.i("servbt", "ResultCode: " + event.getResultCode() + ", RequestCode: " + event.getRequestCode());
		if(event.getRequestCode() == REQUEST_TURN_ON_DISCOVERABLE)
		{
			if(event.getResultCode() == EventResult.DISCOVERABLE_DURATION)
			{
				Log.i("servbt", "turn on");
				GameHelper.getInstance().setBluetoothVisibleNow();
				enableAccepts();
			}
			else if(event.getResultCode() == Activity.RESULT_CANCELED)
			{
				Log.w("SeBTHandler.out", "declined to set bt to visible..");
				clientCallback.returnToMainMenu();
			}
		}
	}
	
	@Override
	public void destroy()
	{
		isAlive = false;
		//unregister listener:
		isListenerDead = true;
		
		for(Map.Entry<Integer, SocketStreamWatcherThread> e : streamWatcherThreads.entrySet())
		{
			try
			{
				e.getValue().socket.getOutputStream().close();
				e.getValue().socket.getInputStream().close();
				e.getValue().socket.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		for(Map.Entry<Integer, AcceptThread> e : acceptThreads.entrySet())
		{
			try
			{
				e.getValue().serverSocket.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void playerDisconnected(int playerNr)
	{
		if(clients.containsKey(playerNr))
			clients.remove(playerNr);
		
		serverCallback.connectionLost(playerNr);
		
		if(streamWatcherThreads.containsKey(playerNr))
			streamWatcherThreads.remove(playerNr);
		
		// set up new accept threat to allow other players to join
		if(!acceptThreads.containsKey(playerNr))
		{
			AcceptThread acceptThread = new AcceptThread(playerNr);
			acceptThread.start();
			acceptThreads.put(playerNr, acceptThread);
		}
	}
	
	boolean isListenerDead = false;
	
	@Override
	public boolean isDead()
	{
		return isListenerDead;
	}
}
