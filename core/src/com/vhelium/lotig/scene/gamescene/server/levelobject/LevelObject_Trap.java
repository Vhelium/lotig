package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServerFake;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Trap extends LevelObject
{
	public static final String NAME = "Trap";
	private String effectAsset;
	private String soundEffect;
	private Condition condition;
	private int animationTime;
	private int teamNr;
	private float damage;
	private float range;
	private int damageType = 2 /*default: Fire*/;
	private int duration = -1;
	private float durationSpent = 0f;
	
	public LevelObject_Trap(int id, Realm realm, Rectangle rectangle, String asset, String soundEffect, boolean animated, int animationTime, String effectAsset, int duration, int teamNr, float damage, int damageType, float range)
	{
		this(id, realm, rectangle, asset, soundEffect, animated, animationTime, effectAsset, duration, teamNr, damage, damageType, range, null);
	}
	
	public LevelObject_Trap(int id, Realm realm, Rectangle rectangle, String asset, String soundEffect, boolean animated, int animationTime, String effectAsset, int duration, int teamNr, float damage, int damageType, float range, Condition condition)//Server: at runtime
	{
		this.id = id;
		this.bounds = rectangle;
		this.animated = animated;
		this.animationTime = animationTime;
		this.asset = asset;
		this.soundEffect = soundEffect;
		this.effectAsset = effectAsset;
		this.damage = damage;
		this.damageType = damageType;
		this.range = range;
		this.duration = duration;
		this.teamNr = teamNr;
		this.condition = condition;
		
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(realm, rectangle, properties);
		event.entityCheck = true;
	}
	
	public LevelObject_Trap(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)//Server: loaded from map
	{
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("animated"))
			animated = Boolean.parseBoolean(tmxObjectProperties.get("animated", String.class));
		if(tmxObjectProperties.containsKey("animationTime"))
			animationTime = Integer.parseInt(tmxObjectProperties.get("animationTime", String.class));
		if(tmxObjectProperties.containsKey("asset"))
			asset = tmxObjectProperties.get("asset", String.class);
		if(tmxObjectProperties.containsKey("soundEffect"))
			soundEffect = tmxObjectProperties.get("soundEffect", String.class);
		if(tmxObjectProperties.containsKey("effectAsset"))
			effectAsset = tmxObjectProperties.get("effectAsset", String.class);
		if(tmxObjectProperties.containsKey("damage"))
			damage = Integer.parseInt(tmxObjectProperties.get("damage", String.class)) * (realm.getLevel().getDifficulty() + 1);
		if(tmxObjectProperties.containsKey("damageType"))
			damageType = Integer.parseInt(tmxObjectProperties.get("damageType", String.class));
		if(tmxObjectProperties.containsKey("range"))
			range = Integer.parseInt(tmxObjectProperties.get("range", String.class));
		
		teamNr = 99;
		
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(realm, rectangle, properties);
		event.entityCheck = true;
	}
	
	public LevelObject_Trap(int id, float x, float y, int w, int h, String asset, String soundEffect, boolean animated, int animationTime)//Client
	{
		this.id = id;
		this.asset = asset;
		this.soundEffect = soundEffect;
		this.animated = animated;
		this.animationTime = animationTime;
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, animated ? 2 : 1, 1));
		((AnimatedSprite) sprite).stopAnimationAt(0);
		if(animated)
			((AnimatedSprite) sprite).animate(animationTime);
		bounds = new Rectangle(x, y, w, h);
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + soundEffect + ";" + animated + ";" + animationTime;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		EntityServerMixin entity = realm.getEntity(entityId);
		
		if(entity == null || entity.getTeamNr() == teamNr)
			return;
		
		EntityServerFake owner = new EntityServerFake(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, teamNr);
		
		BulletOnHitEffect effect = new BulletOnHitEffect(damage, range, condition, effectAsset, 0);
		effect.setSoundEffect(soundEffect);
		realm.doAoeDamage(owner, bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, effect, damageType);
		
		realm.removeLevelObject(this);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		if(duration > 0)
		{
			durationSpent += delta;
			if(durationSpent >= duration)
				realm.removeLevelObject(this);
		}
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
	
	@Override
	public void setId(int id)
	{
		super.setId(id);
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(event.realm, event.rectangle, properties);
		event.entityCheck = true;
	}
}
