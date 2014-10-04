package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.Server;

public class LevelObject_TownExit extends LevelObject
{
	public static final String NAME = "TwEx";
	
	public LevelObject_TownExit(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(realm, rectangle, properties);
		state = 0;
	}
	
	public LevelObject_TownExit(int id, float x, float y, int w, int h, int state)
	{
		this.id = id;
		asset = "levelExit";
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 6, 1));
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
		return NAME + ";" + state;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		if(Server.isPlayer(entityId))
			if(state == 0)
			{
				//send fail message
				//and deny it!
				DataPacket dp = new DataPacket(MessageType.MSG_POST_MESSAGE);
				dp.setString("No active lair!");
				dp.setBoolean(false);
				realm.sendToPlayer(entityId, dp);
			}
			else if(state == 1)
			{
				//port motherfucking player to motherfucking fight realm
				realm.getLevel().playerJoinsRealm(realm.getPlayer(entityId), realm.getLevel().getRealmByName("fight"));
			}
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		if(sprite != null)
		{
			if(state == 0)
				((AnimatedSprite) sprite).animate(100, 0, 2, true);
			else
				((AnimatedSprite) sprite).animate(100, 3, 5, true);
		}
	}
}
