package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_TownPortal extends LevelObject
{
	public static final String NAME = "TPo";
	
	public LevelObject_TownPortal(int id, Realm realm, Rectangle rectangle)
	{
		this.id = id;
		this.bounds = rectangle;
	}
	
	public LevelObject_TownPortal(int id, float x, float y, int w, int h)
	{
		this.id = id;
		
		sprite = new Sprite(x, y, w, h, GameHelper.getInstance().getGameAsset("Town Portal"));
		bounds = new Rectangle(x, y, w, h);
		
		MapProperties properties = new MapProperties();
		properties.put("name", "Town");
		properties.put("showPort", "town");
		event = new OnEnterEvent(null, bounds, properties);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
}
