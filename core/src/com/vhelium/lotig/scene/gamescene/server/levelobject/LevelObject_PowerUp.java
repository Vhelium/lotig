package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_PowerUp extends LevelObject
{
	public static final String NAME = "PwUp";
	private int animationTime;
	private String buff = null;
	HashMap<String, Integer> buffs;
	private int duration;
	private int respawn;
	
	private float timeToRespawnLeft = 0;
	
	public LevelObject_PowerUp(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("animated"))
			animated = Boolean.parseBoolean(tmxObjectProperties.get("animated", String.class));
		if(tmxObjectProperties.containsKey("animationTime"))
			animationTime = Integer.parseInt(tmxObjectProperties.get("animationTime", String.class));
		if(tmxObjectProperties.containsKey("asset"))
			asset = tmxObjectProperties.get("asset", String.class);
		if(tmxObjectProperties.containsKey("buff"))
			buff = tmxObjectProperties.get("buff", String.class);
		if(tmxObjectProperties.containsKey("duration"))
			duration = Integer.parseInt(tmxObjectProperties.get("duration", String.class));
		if(tmxObjectProperties.containsKey("respawn"))
			respawn = Integer.parseInt(tmxObjectProperties.get("respawn", String.class));
		
		if(buff != null)
		{
			buffs = new HashMap<String, Integer>();
			StringTokenizer st = new StringTokenizer(buff, ";");
			while(st.hasMoreTokens())
			{
				String[] b = st.nextToken().split(":");
				buffs.put(b[0], Integer.parseInt(b[1]));
			}
		}
		
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(realm, rectangle, properties);
		
		state = 1;
	}
	
	public LevelObject_PowerUp(int id, float x, float y, int w, int h, String asset, boolean animated, int animationTime, int state)
	{
		this.id = id;
		this.asset = asset;
		this.animated = animated;
		this.animationTime = animationTime;
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 3, 1));
		((AnimatedSprite) sprite).animate(animationTime);
		bounds = new Rectangle(x, y, w, h);
		
		this.state = state;
		stateChangedClient(state);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		if(timeToRespawnLeft > 0)
		{
			timeToRespawnLeft -= delta;
			if(timeToRespawnLeft <= 0)
			{
				state = 1;
				for(Entry<Integer, PlayerServer> e : realm.getPlayers().entrySet())
				{
					PlayerServer player = e.getValue();
					if(player.getRectangle().collidesWith(bounds))
					{
						trigger(realm, e.getKey());
						return;
					}
				}
				onStateChanged(realm);
			}
		}
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + animated + ";" + animationTime + ";" + state;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		if(state == 0)
			return;
		
		state = 0;
		timeToRespawnLeft = respawn;
		onStateChanged(realm);
		if(buffs != null)
			for(Entry<String, Integer> e : buffs.entrySet())
				realm.requestCondition(entityId, e.getKey(), "", e.getValue(), duration, false, "", System.currentTimeMillis());
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		if(sprite != null)
			sprite.setVisible(state == 0 ? false : true);
	}
}
