package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class OnEnterEvent extends EventObject
{
	List<PlayerServer> playersInside;
	boolean playerClientInside = false;
	
	List<EntityServer> enemiesInside;
	
	public OnEnterEvent(Realm realm, Rectangle rectangle, MapProperties properties)
	{
		super(realm, rectangle, properties);
		if(realm != null)
		{
			playersInside = new ArrayList<PlayerServer>();
			enemiesInside = new ArrayList<EntityServer>();
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SERVER - Players >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public void update(float delta, ConcurrentHashMap<Integer, PlayerServer> players)
	{
		for(Entry<Integer, PlayerServer> e : players.entrySet())
		{
			PlayerServer player = e.getValue();
			if(player.getRectangle().collidesWith(rectangle))
			{
				if(!playersInside.contains(player))
				{
					playersInside.add(player);
					onEnter(player);
				}
			}
			else
			{
				if(playersInside.contains(player))
				{
					playersInside.remove(player);
					onLeft(player);
				}
			}
		}
	}
	
	private void onEnter(PlayerServer player)
	{
		realm.handleOnEnterEvent(player, properties);
	}
	
	private void onLeft(PlayerServer player)
	{
		realm.handleOnLeftEvent(player, properties);
	}
	
//	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SERVER - Enemies >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public void updateEnemies(float delta, ConcurrentHashMap<Integer, EntityServer> enemies)
	{
		for(Entry<Integer, EntityServer> e : enemies.entrySet())
		{
			EntityServer enemy = e.getValue();
			if(enemy.getRectangle().collidesWith(rectangle))
			{
				if(!enemiesInside.contains(enemy))
				{
					enemiesInside.add(enemy);
					onEnter(enemy);
				}
			}
			else
			{
				if(enemiesInside.contains(enemy))
				{
					enemiesInside.remove(enemy);
					onLeft(enemy);
				}
			}
		}
	}
	
	private void onEnter(EntityServer enemy)
	{
		realm.handleOnEnterEvent(enemy, properties);
	}
	
	private void onLeft(EntityServer enemy)
	{
		realm.handleOnLeftEvent(enemy, properties);
	}
	
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public void update(float delta, PlayerClient player)
	{
		if(player.collidesWith(rectangle))
		{
			if(!playerClientInside)
			{
				playerClientInside = true;
				onEnter(player);
			}
		}
		else if(playerClientInside)
		{
			playerClientInside = false;
			onLeft(player);
		}
	}
	
	protected void onEnter(PlayerClient player)
	{
		player.handleOnEnterEvent(player, properties, this);
	}
	
	protected void onLeft(PlayerClient player)
	{
		player.handleOnLeftEvent(player, properties);
	}
}
