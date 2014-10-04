package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerFake;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_AoeArea extends LevelObject
{
	static final String NAME = "aoea";
	private final int warningTime = 2500;
	private int delay;
	private float delayLeft;
	
	private String effectAsset;
	private String soundEffect;
	private float damage;
	private float range;
	private int damageType = 2 /*default: Fire*/;
	
	float nextX;
	float nextY;
	
	public LevelObject_AoeArea(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
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
		if(tmxObjectProperties.containsKey("delay"))
			delay = Integer.parseInt(tmxObjectProperties.get("delay", String.class));
		randomDelay();
		randomPos();
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		float prevDelayLeft = delayLeft;
		if(delayLeft > 0)
		{
			delayLeft -= delta;
			if(prevDelayLeft > warningTime && delayLeft <= warningTime)
			{
				realm.playEffect(nextX, nextY, -1, -1, "AoeAreaWarning", warningTime - 100, true);
			}
			else if(delayLeft <= 0)
			{
				randomDelay();
				
				EntityServerFake owner = new EntityServerFake(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, 99);
				BulletOnHitEffect ohe = new BulletOnHitEffect(damage, range, null, effectAsset, 0);
				ohe.setSoundEffect(SoundFile.stone_burst);
				realm.doAoeDamage(owner, nextX, nextY, ohe, damageType);
				
				if(soundEffect != null)
					realm.playSound(soundEffect, nextX, nextY);
				
				randomPos();
			}
		}
	}
	
	private void randomPos()
	{
		nextX = bounds.getX() + GameHelper.getInstance().getRandom().nextInt((int) bounds.getWidth());
		nextY = bounds.getY() + GameHelper.getInstance().getRandom().nextInt((int) bounds.getHeight());
	}
	
	private void randomDelay()
	{
		delayLeft = delay - delay / 3 + GameHelper.getInstance().getRandom().nextInt(delay / 3 * 2);
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
