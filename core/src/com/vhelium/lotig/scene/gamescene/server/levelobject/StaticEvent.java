package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class StaticEvent extends EventObject
{
	public StaticEvent(Realm realm, Rectangle rectangle, MapProperties properties)
	{
		super(realm, rectangle, properties);
	}
	
	@Override
	public void update(float delta, ConcurrentHashMap<Integer, PlayerServer> players)
	{
		
	}
	
	@Override
	public void updateEnemies(float delta, ConcurrentHashMap<Integer, EntityServer> players)
	{
		
	}
	
	@Override
	public void update(float delta, PlayerClient player)
	{
		
	}
}
