package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Door extends LevelObject
{
	public static final String NAME = "Door";
	
	private int doorId = -1;
	
	public LevelObject_Door(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		this.state = 0;
		this.collision = state == 0;
		asset = "Door";
		
		if(tmxObjectProperties.containsKey("doorId"))
		{
			doorId = Integer.parseInt(tmxObjectProperties.get("doorId", String.class));
		}
		if(tmxObjectProperties.containsKey("asset"))
		{
			asset = tmxObjectProperties.get("asset", String.class);
		}
		
	}
	
	public LevelObject_Door(int id, float x, float y, int w, int h, String asset, int state)
	{
		this.id = id;
		this.asset = asset;
		this.state = state;
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 2, 1));
		((AnimatedSprite) sprite).stopAnimation(state);
		bounds = new Rectangle(x, y, w, h);
		
		this.collision = state == 0;
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + state;
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
			((AnimatedSprite) sprite).stopAnimation(state);
			SoundManager.playSound(SoundFile.gate_open);
		}
	}
	
	public int getDoorId()
	{
		return doorId;
	}
}