package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.Server;

public class LevelObject_BulletSpawn extends LevelObject
{
	public static final String NAME = "BuSpn";
	
	String bulletAsset;
	int damage = 50;
	int delay;
	float delayLeft;
	int delayVariation;
	int dirX;
	int dirY;
	float x;
	float y;
	float rotation;
	
	public LevelObject_BulletSpawn(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("bulletAsset"))
			bulletAsset = tmxObjectProperties.get("bulletAsset", String.class);
		if(tmxObjectProperties.containsKey("damage"))
			damage = Integer.parseInt(tmxObjectProperties.get("damage", String.class));
		if(tmxObjectProperties.containsKey("delay"))
			delay = Integer.parseInt(tmxObjectProperties.get("delay", String.class));
		if(tmxObjectProperties.containsKey("delayVariation"))
			delayVariation = Integer.parseInt(tmxObjectProperties.get("delayVariation", String.class));
		if(tmxObjectProperties.containsKey("dirX"))
			dirX = Integer.parseInt(tmxObjectProperties.get("dirX", String.class));
		if(tmxObjectProperties.containsKey("dirY"))
			dirY = Integer.parseInt(tmxObjectProperties.get("dirY", String.class));
		
		x = bounds.getX() + bounds.getWidth() / 2;
		y = bounds.getY() + bounds.getHeight() / 2;
		
		rotation = MathUtils.radiansToDegrees * ((float) Math.atan2(dirX, -dirY));
	}
	
	public LevelObject_BulletSpawn(int id, float x, float y, int w, int h)
	{
		this.id = id;
		
		bounds = new Rectangle(x, y, w, h);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		delayLeft -= delta;
		if(delayLeft <= 0)
		{
			delayLeft = delay - delayVariation + GameHelper.getInstance().getRandom().nextInt(delayVariation * 2 + 1);
			
			BulletOnHitEffect effect = new BulletOnHitEffect(damage, 0, "", 0);
			realm.shoot(Server.MAX_SUPPORTED + 1, false, DamageType.Physical, 1000, x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2, rotation, dirX, dirY, bulletAsset, effect);
		}
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME;
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
