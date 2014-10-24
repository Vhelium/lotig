package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Lever extends LevelObject
{
	public static final String NAME = "Lvr";
	
	private int doorId = -1;
	
	public LevelObject_Lever(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		this.state = 0;
		
		if(tmxObjectProperties.containsKey("doorId"))
		{
			doorId = Integer.parseInt(tmxObjectProperties.get("doorId", String.class));
		}
	}
	
	public LevelObject_Lever(int id, float x, float y, int w, int h, int state)
	{
		this.id = id;
		asset = "Lever";
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 2, 1));
		((AnimatedSprite) sprite).stopAnimationAt(0);
		bounds = new Rectangle(x, y, w, h);
		
		this.state = state;
		stateChangedClient(state);
		
		MapProperties properties = new MapProperties();
		properties.put("showLever", String.valueOf(id));
		event = new OnEnterEvent(null, bounds, properties);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + state;
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
			((AnimatedSprite) sprite).stopAnimationAt(state);
		}
	}
	
	public int getDoorId()
	{
		return doorId;
	}
}