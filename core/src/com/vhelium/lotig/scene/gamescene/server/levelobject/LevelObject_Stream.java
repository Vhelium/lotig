package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.runnable.PlayerRunnable;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Stream extends LevelObject
{
	public static final String NAME = "Strm";
	
	private float strengthFactor = 0.1f;
	private int direction = 0;
	private int dirX = 0, dirY = 0;
	private int pauseDuration = 0;
	private int pauseFrequency = 0;
	private final int pauseLeft = 0;
	
	public LevelObject_Stream(final int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		this.state = 0;
		
		if(tmxObjectProperties.containsKey("asset"))
			asset = tmxObjectProperties.get("asset", String.class);
		if(tmxObjectProperties.containsKey("strengthFactor"))
			strengthFactor = Float.parseFloat(tmxObjectProperties.get("strengthFactor", String.class));
		if(tmxObjectProperties.containsKey("pauseDuration"))
		{
			pauseDuration = Integer.parseInt(tmxObjectProperties.get("pauseDuration", String.class));
			pauseDuration = pauseDuration - pauseDuration / 4 + GameHelper.$.getRandom().nextInt(pauseDuration / 2);
		}
		if(tmxObjectProperties.containsKey("pauseFrequency"))
		{
			pauseFrequency = Integer.parseInt(tmxObjectProperties.get("pauseDuration", String.class));
			pauseFrequency = pauseFrequency - pauseFrequency / 4 + GameHelper.$.getRandom().nextInt(pauseFrequency / 2);
		}
		
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
		dirX = direction == 1 ? -1 : (direction == 3 ? 1 : 0);
		dirY = direction == 0 ? 1 : (direction == 2 ? -1 : 0);
		
		event = new AreaUpdateEvent(realm, bounds, new PlayerRunnable()
		{
			@Override
			public void updateEnemy(float delta, EntityServer enemy)
			{
				if(!enemy.isDead())
				{
					enemy.addMovementModifier("Vortex" + id, dirX, dirY, strengthFactor, 0.8f);
				}
			}
		}, 40f);
		event.entityCheck = true;
	}
	
	public LevelObject_Stream(final int id, float x, float y, int w, int h, String asset, int direction)
	{
		this.id = id;
		this.asset = asset;
		this.direction = direction;
		
		dirX = direction == 1 ? -1 : (direction == 3 ? 1 : 0);
		dirY = direction == 0 ? 1 : (direction == 2 ? -1 : 0);
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 3, 1));
		((AnimatedSprite) sprite).animate(220);
		bounds = new Rectangle(x, y, w, h);
		
		stateChangedClient(state);
		
		event = new AreaUpdateEvent(null, bounds, new PlayerRunnable()
		{
			@Override
			public void updateClient(float delta, PlayerClient player)
			{
				if(!player.isDead())
				{
					player.addMovementModifier("Vortex" + id, dirX, dirY, strengthFactor, 0.8f);
				}
			}
		}, 40f);
		event.entityCheck = true;
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + direction;
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