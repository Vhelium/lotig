package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_LairPortal extends LevelObject
{
	public static final String NAME = "LrPt";
	
	private String name, lair;
	
	public LevelObject_LairPortal(int id, Realm realm, Rectangle rectangle, MapProperties props)
	{
		this.id = id;
		this.bounds = rectangle;
		state = 0;
		
		if(props.containsKey("name"))
		{
			name = props.get("name", String.class);
		}
		if(props.containsKey("showLairPort"))
		{
			lair = props.get("showLairPort", String.class);
		}
		if(props.containsKey("asset"))
		{
			asset = props.get("asset", String.class);
		}
		else
			asset = "Lair Portal";
	}
	
	public LevelObject_LairPortal(int id, float x, float y, int w, int h, String name, String asset, String lair, int state)
	{
		this.id = id;
		this.asset = asset;
		
		TiledTextureRegion tr = GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 6, 1);
		sprite = new AnimatedSprite(x + w / 2 - tr.getTileWidth() / 2, y + h - tr.getTileHeight(), tr.getTileWidth(), tr.getTileHeight(), tr);
		bounds = new Rectangle(x, y, w, h);
		
		this.state = state;
		stateChangedClient(state);
		
		MapProperties properties = new MapProperties();
		properties.put("name", name);
		properties.put("showLairPort", lair);
		event = new OnEnterEvent(null, bounds, properties);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + name + ";" + asset + ";" + lair + ";" + state;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		if(sprite != null)
		{
			if(state == 1)
				((AnimatedSprite) sprite).animate(200, 0, 2, true);
			else
				((AnimatedSprite) sprite).animate(200, 3, 5, true);
		}
	}
	
	public String getLair()
	{
		return lair;
	}
}
