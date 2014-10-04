package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public abstract class EventObject
{
	protected Rectangle rectangle;
	protected Realm realm;
	public MapProperties properties;
	public boolean entityCheck = false;
	
	public EventObject(Realm realm, Rectangle rectangle, MapProperties properties)
	{
		this.realm = realm;
		this.rectangle = rectangle;
		this.properties = properties;
	}
	
	public abstract void updateEnemies(float delta, ConcurrentHashMap<Integer, EntityServer> enemies);
	
	public abstract void update(float delta, ConcurrentHashMap<Integer, PlayerServer> players);
	
	public abstract void update(float delta, PlayerClient player);
	
	public Rectangle getRectangle()
	{
		return rectangle;
	}
	
	public static EventObject getInstance(Realm realm, Rectangle rectangle, String type, MapProperties tmxProperties)
	{
		EventObject res = null;
		if(type.equals("onenter"))
		{
			res = new OnEnterEvent(realm, rectangle, tmxProperties);
		}
		else if(type.equals("oninside"))
		{
			res = null;
		}
		else if(type.equals("static"))
		{
			res = new StaticEvent(realm, rectangle, tmxProperties);
		}
		else if(type.equals("lairportal"))
		{
			res = new LairPortalEvent(realm, rectangle, tmxProperties);
		}
		
		return res;
	}
	
	public boolean isClientSided()
	{
		return realm == null;
	}
	
	public boolean containsProperty(String name)
	{
		if(properties != null)
			return properties.containsKey(name);
		return false;
	}
}
