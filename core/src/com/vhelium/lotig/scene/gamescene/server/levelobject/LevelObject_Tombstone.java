package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Tombstone extends LevelObject
{
	static final String NAME = "tbst";
	private float displayTimeLeft;
	private final float displayTime = 20000;
	
	public LevelObject_Tombstone(int id, Realm realm, float originX, float originY)
	{
		this.id = id;
		asset = "tombstone";
		bounds = new Rectangle(originX - 24, originY - 24, 48, 48);
		displayTimeLeft = displayTime;
	}
	
	public LevelObject_Tombstone(int id, float x, float y, int w, int h)
	{
		this.id = id;
		asset = "tombstone";
		bounds = new Rectangle(x, y, w, h);
		sprite = new Sprite(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), GameHelper.getInstance().getGameAsset(asset));
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		if(displayTimeLeft > 0)
		{
			displayTimeLeft -= delta;
			if(displayTimeLeft <= 0)
				realm.removeLevelObject(this);
		}
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME;
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
}
