package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.runnable.PlayerRunnable;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class AreaUpdateEvent extends EventObject
{
	boolean playerClientInside = false;
	PlayerRunnable runnable;
	float updateDelay = 0;
	
	public AreaUpdateEvent(Realm realm, Rectangle rectangle, PlayerRunnable runnable)
	{
		super(realm, rectangle, null);
		this.runnable = runnable;
	}
	
	public AreaUpdateEvent(Realm realm, Rectangle rectangle, PlayerRunnable runnable, float updateDelay)
	{
		super(realm, rectangle, null);
		this.runnable = runnable;
		this.updateDelay = updateDelay;
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SERVER - Players >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	float udPlayerServerLeft;
	
	@Override
	public void update(float delta, ConcurrentHashMap<Integer, PlayerServer> players)
	{
		if(updateDelay > 0)
			udPlayerServerLeft -= delta;
		if(updateDelay == 0 || udPlayerServerLeft <= 0)
		{
			udPlayerServerLeft = updateDelay;
			if(runnable != null)
				for(Entry<Integer, PlayerServer> e : players.entrySet())
				{
					PlayerServer player = e.getValue();
					if(player.getRectangle().collidesWith(rectangle))
						runnable.updatePlayer(delta, player);
				}
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SERVER - Enemies >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	float udEnemiesServerLeft;
	
	@Override
	public void updateEnemies(float delta, ConcurrentHashMap<Integer, EntityServer> enemies)
	{
		if(updateDelay > 0)
			udEnemiesServerLeft -= delta;
		if(updateDelay == 0 || udEnemiesServerLeft <= 0)
		{
			udEnemiesServerLeft = updateDelay;
			if(runnable != null)
				for(Entry<Integer, EntityServer> e : enemies.entrySet())
				{
					EntityServer enemy = e.getValue();
					if(enemy.getRectangle().collidesWith(rectangle))
						runnable.updateEnemy(delta, enemy);
				}
		}
	}
	
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	float udClientLeft;
	
	@Override
	public void update(float delta, PlayerClient player)
	{
		if(updateDelay > 0)
			udClientLeft -= delta;
		if(updateDelay == 0 || udClientLeft <= 0)
		{
			udClientLeft = updateDelay;
			if(runnable != null)
				if(player.collidesWith(rectangle))
					runnable.updateClient(delta, player);
		}
	}
}
