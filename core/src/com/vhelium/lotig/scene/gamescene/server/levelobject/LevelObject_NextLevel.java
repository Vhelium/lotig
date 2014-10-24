package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.Server;

public class LevelObject_NextLevel extends LevelObject
{
	public static final String NAME = "NxLv";
	
	int requiredStoryQuestStep = -1;
	int direction = 0;
	
	public LevelObject_NextLevel(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("requiredStoryQuestStep"))
			requiredStoryQuestStep = Integer.parseInt(tmxObjectProperties.get("requiredStoryQuestStep", String.class));
		
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
		
		refresh(realm);
	}
	
	public LevelObject_NextLevel(int id, float x, float y, int w, int h, int direction, int state)
	{
		this.id = id;
		asset = "levelExit";// http://youtu.be/fWwf4s05A38?t=13s
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 6, 1));
		sprite.setOrigin(w / 2, h / 2);
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
				//and deny it!
				DataPacket dp = new DataPacket(MessageType.MSG_POST_MESSAGE);
				dp.setString("You can not leave now!");
				dp.setBoolean(false);
				realm.sendToPlayer(entityId, dp);
			}
			else if(state == 1)
			{
				realm.nextLevel();
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
	
	@Override
	public void refresh(Realm realm)
	{
		int prevState = state;
		state = realm.getLevel().getStoryQuestHost() != null ? ((requiredStoryQuestStep == -1 || realm.getLevel().getStoryQuestHost().getStep() >= requiredStoryQuestStep) ? 1 : 0) : 1;
		if(state != prevState)
			onStateChanged(realm);
	}
}
