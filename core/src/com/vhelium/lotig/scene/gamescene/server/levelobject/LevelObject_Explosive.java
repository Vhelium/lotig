package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServerFake;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Explosive extends LevelObject
{
	public static final String NAME = "Expl";
	private String effectAsset;
	private String soundEffect;
	private Condition condition;
	private int animationTime;
	private float damage;
	private float range;
	private int damageType = 2 /*default: fire*/;
	Realm realm;
	
	public LevelObject_Explosive(int id, Realm realm, Rectangle rectangle, String asset, String soundEffect, boolean animated, int animationTime, String effectAsset, float damage, int damageType, float range)
	{
		this(id, realm, rectangle, asset, soundEffect, animated, animationTime, effectAsset, damage, damageType, range, null);
	}
	
	public LevelObject_Explosive(int id, Realm realm, Rectangle rectangle, String asset, String soundEffect, boolean animated, int animationTime, String effectAsset, float damage, int damageType, float range, Condition condition)//Server: at runtime
	{
		this.id = id;
		this.realm = realm;
		this.bounds = rectangle;
		this.animated = animated;
		this.animationTime = animationTime;
		this.asset = asset;
		this.soundEffect = soundEffect;
		this.effectAsset = effectAsset;
		this.damage = damage;
		this.damageType = damageType;
		this.range = range;
		this.condition = condition;
		this.collision = true;
		this.maxHp = hp;
		this.hp = maxHp;
	}
	
	public LevelObject_Explosive(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)//Server: loaded from map
	{
		this.id = id;
		this.realm = realm;
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
			damage = Integer.parseInt(tmxObjectProperties.get("damage", String.class));
		if(tmxObjectProperties.containsKey("damageType"))
			damageType = Integer.parseInt(tmxObjectProperties.get("damageType", String.class));
		if(tmxObjectProperties.containsKey("range"))
			range = Integer.parseInt(tmxObjectProperties.get("range", String.class));
		if(tmxObjectProperties.containsKey("hp"))
			hp = Integer.parseInt(tmxObjectProperties.get("hp", String.class));
		
		this.collision = true;
		this.maxHp = hp;
	}
	
	public LevelObject_Explosive(int id, float x, float y, int w, int h, String asset, String soundEffect, boolean animated, int animationTime, int hp)//Client
	{
		this.id = id;
		this.asset = asset;
		this.soundEffect = soundEffect;
		this.animated = animated;
		this.animationTime = animationTime;
		this.collision = true;
		this.maxHp = hp;
		this.hp = hp;
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, animated ? 2 : 1, 1));
		((AnimatedSprite) sprite).stopAnimation(0);
		if(animated)
			((AnimatedSprite) sprite).animate(animationTime);
		bounds = new Rectangle(x, y, w, h);
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + soundEffect + ";" + animated + ";" + animationTime + ";" + hp;
	}
	
	@Override
	public void onDestroyed()
	{
		EntityServerFake owner = new EntityServerFake(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, -1);
		
		BulletOnHitEffect effect = new BulletOnHitEffect(damage, range, condition, effectAsset, 0);
		effect.setSoundEffect(soundEffect);
		realm.doAoeDamage(owner, bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, effect, damageType);
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
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
