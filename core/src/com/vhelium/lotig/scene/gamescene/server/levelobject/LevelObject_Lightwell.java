package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Lightwell extends LevelObject
{
	public static final String NAME = "LtWl";
	private AnimatedSprite animSprite;
	private final int animationTime;
	private int respawn;
	private int healed;
	
	private float timeToRespawnLeft = 0;
	private float durationLeft = 0;
	
	public LevelObject_Lightwell(int id, Realm realm, Rectangle rectangle, int duration, int respawn, int healed)//server
	{
		this.id = id;
		this.bounds = rectangle;
		this.asset = "Lightwell";
		this.animated = false;
		this.animationTime = 120;
		this.respawn = respawn;
		this.timeToRespawnLeft = respawn;
		this.healed = healed;
		this.durationLeft = duration;
		
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(realm, rectangle, properties);
		
		state = 0;
	}
	
	public LevelObject_Lightwell(int id, float x, float y, int w, int h, String asset, boolean animated, int animationTime, int state)//client
	{
		this.id = id;
		this.asset = asset;
		this.animated = animated;
		this.animationTime = animationTime;
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 1, 1));
		TiledTextureRegion txtAnim = GameHelper.getInstance().getGameAssetTiledTextureRegion(asset + " Orb", 2, 1);
		animSprite = new AnimatedSprite(w / 2 - txtAnim.getTileWidth() / 2, h / 2 - txtAnim.getTileHeight() / 2, txtAnim.getTileWidth(), txtAnim.getTileHeight(), txtAnim);
		animSprite.animate(animationTime);
		sprite.addActor(animSprite);
		
		bounds = new Rectangle(x, y, w, h);
		
		this.state = state;
		stateChangedClient(state);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		if(durationLeft > 0)
		{
			durationLeft -= delta;
			if(durationLeft <= 0)
			{
				realm.removeLevelObject(this);
				return;
			}
		}
		if(timeToRespawnLeft > 0)
		{
			timeToRespawnLeft -= delta;
			if(timeToRespawnLeft <= 0)
			{
				state = 1;
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
		
		EntityServerMixin entity = realm.getEntity(entityId);
		if(entity != null)
		{
			entity.doHeal(healed);
			realm.sendDamageNumberToAllPlayers(entityId, healed, DamageType.Heal);
			realm.playSound(SoundFile.spell_bling, entity.getOriginX(), entity.getOriginY());
		}
		
		state = 0;
		timeToRespawnLeft = respawn;
		onStateChanged(realm);
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		if(animSprite != null)
			animSprite.setVisible(state == 0 ? false : true);
	}
	
	@Override
	public void setId(int id)
	{
		super.setId(id);
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(event.realm, event.rectangle, properties);
	}
}
