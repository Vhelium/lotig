package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.Server;

public class LevelObject_LevelExit extends LevelObject
{
	public static final String NAME = "LvEx";
	int direction = 0;
	
	public LevelObject_LevelExit(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		String sDirection = tmxObjectProperties.get("direction", String.class);
		if(sDirection != null)
		{
			if(sDirection.equals("down") || sDirection.equals("0"))
				direction = 0;
			else if(sDirection.equals("left") || sDirection.equals("1"))
				direction = 1;
			else if(sDirection.equals("up") || sDirection.equals("2"))
				direction = 2;
			else if(sDirection.equals("right") || sDirection.equals("3"))
				direction = 3;
		}
		
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(realm, rectangle, properties);
		state = 1;
	}
	
	public LevelObject_LevelExit(int id, float x, float y, int w, int h, int direction, int state)
	{
		this.id = id;
		asset = "levelExit";
		
		if(direction == 0 || direction == 2)
		{
			sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 6, 1));
			sprite.setOrigin(w / 2, h / 2);
		}
		else
		{
			sprite = new AnimatedSprite(x + w / 2 - h / 2, y + h / 2 - w / 2, h, w, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 6, 1));
			sprite.setOrigin(h / 2, w / 2);
		}
		sprite.setRotation(direction * 90);
		bounds = new Rectangle(x, y, w, h);
		this.state = state;
		stateChangedClient(state);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + direction + ";" + state;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		if(Server.isPlayer(entityId))
			if(state == 0)
			{
				//should not be the case
				DataPacket dp = new DataPacket(MessageType.MSG_POST_MESSAGE);
				dp.setString("You cannot do that!");
				dp.setBoolean(false);
				realm.sendToPlayer(entityId, dp);
			}
			else if(state == 1)
			{
				//fucking close the motherfucking realm and fucking port the motherfucking player to the motherfucking town
				realm.closeLair();
			}
	}
	
	private final int[] frames1 = new int[] { 0, 1, 2 };
	private final int[] frames2 = new int[] { 3, 4, 5 };
	
	@Override
	public void stateChangedClient(int state)
	{
		if(sprite != null)
		{
			if(state == 0)
				((AnimatedSprite) sprite).animate(100, frames1, true);
			else
				((AnimatedSprite) sprite).animate(100, frames2, true);
		}
	}
}
