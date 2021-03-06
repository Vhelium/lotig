package com.vhelium.lotig.scene.connection.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import com.vhelium.lotig.constants.ByteConverter;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IClientConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionCallback;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Server;

public class ServerWlanHandler implements IServerConnectionHandler
{
	public static int SERVER_PORT = 6000;
	private ServerSocket serverSocket;
	private final HashMap<Integer, Socket> clients;
	private final HashMap<Integer, SocketStreamWatcherThread> streamWatcherThreads;
	private AcceptThread acceptThread;
	
	private IServerConnectionCallback serverCallback;
	private IClientConnectionCallback clientCallback;
	
	private boolean isAlive = true;
	
	public ServerWlanHandler()
	{
		streamWatcherThreads = new HashMap<Integer, SocketStreamWatcherThread>();
		clients = new HashMap<Integer, Socket>();
	}
	
	@Override
	public void registerCallback(IServerConnectionCallback serverCallback, IClientConnectionCallback clientCallback)
	{
		this.serverCallback = serverCallback;
		this.clientCallback = clientCallback;
		
		//maybe add ActivityResultListener:
		//((Main) clientCallback.getActivity()).addActivityResultListener(this);
	}
	
	@Override
	public void start()
	{
		final String[] ipString = GameHelper.getPlatformResolver().setupWlanGetIPs();
		String ips = "";
		for(int i = 0; i < ipString.length; i++)
		{
			ips += ipString[i];
			if(i < ipString.length - 1)
				ips += "\n";
		}
		try
		{
			serverSocket = new ServerSocket(SERVER_PORT);
		}
		catch (IOException e)
		{
			Log.e("SeWLANHandler.out", "couldn't open server: " + e);
		}
		
		GameHelper.getInstance().setIPinfo("IP(s): " + ips + "\nPort: " + SERVER_PORT);
		
		acceptThread = new AcceptThread();
		acceptThread.start();
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
		@Override
		public void run()
		{
			boolean maximumReached = false;
			
			int playerNr = -1;
			while(isAlive && !maximumReached)
			{
				Socket socket = null;
				try
				{
					socket = serverSocket.accept();
				}
				catch (IOException e)
				{
					Log.e("SeWlanHandler.out", "failed to accept socket: " + e.getMessage());
				}
				if(socket != null)
				{
					playerNr = -1;
					// client connects to server:
					Log.e("SeBTHandler.out", "accepted a socket!");
					try
					{
						for(int i = 2; i <= Server.MAX_SUPPORTED; i++)
						{
							if(!clients.containsKey(i))
							{
								playerNr = i;
								break;
							}
						}
						
						clients.put(playerNr, socket);
						
						SocketStreamWatcherThread socketStreamWatcherThread = new SocketStreamWatcherThread(playerNr);
						socketStreamWatcherThread.start();
						streamWatcherThreads.put(playerNr, socketStreamWatcherThread);
						
						maximumReached = (getPlayerCount() >= Server.MAX_SUPPORTED);
						
						serverCallback.connectionEstablished(playerNr);
					}
					catch (IOException e)
					{
						Log.e("SeBTHandler.out", "failed to accept socket2");
						if(isAlive)
							playerDisconnected(playerNr);
					}
				}
			}
			
			acceptThread = null;
			
			//serverSocket.close();
		}
	}
	
	private class SocketStreamWatcherThread extends Thread
	{
		private final int playerNr;
		Socket socket;
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
	public void destroy()
	{
		isAlive = false;
		//unregister listener:
		//((Main) clientCallback.getActivity()).removeActivityResultListener(this);
		
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
		
		if(serverSocket != null)
		{
			try
			{
				serverSocket.close();
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
		
		// set up new accept threat to allow other players to join if it was down
		if(acceptThread == null)
		{
			acceptThread = new AcceptThread();
			acceptThread.start();
		}
	}
}
