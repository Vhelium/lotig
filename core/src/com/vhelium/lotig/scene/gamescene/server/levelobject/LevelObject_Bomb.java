package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Bomb extends LevelObject
{
	static final String NAME = "bomb";
	private BulletOnHitEffect ohe;
	private float delayLeft;
	private int damageType;
	private int shooterId;
	
	public LevelObject_Bomb(int id, Realm realm, float originX, float originY, int width, int height, String asset, float delay, BulletOnHitEffect ohe, int damageType, int shooterId)
	{
		this.id = id;
		this.asset = asset;
		this.ohe = ohe;
		this.delayLeft = delay;
		delayLeft = delay;
		this.shooterId = shooterId;
		this.damageType = damageType;
		bounds = new Rectangle(originX - width / 2, originY - height / 2, width, height);
	}
	
	public LevelObject_Bomb(int id, float x, float y, int w, int h, String asset)
	{
		this.id = id;
		this.asset = asset;
		bounds = new Rectangle(x, y, w, h);
		sprite = new Sprite(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), GameHelper.getInstance().getGameAsset(asset));
		sprite.addAction(Actions.forever(Actions.sequence(Actions.scaleTo(1.4f, 1.4f, 0.5f), Actions.scaleTo(1f, 1f, 0.5f))));
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		if(delayLeft > 0)
		{
			delayLeft -= delta;
			if(delayLeft <= 0)
			{
				if(realm.getEntity(shooterId) != null)
					realm.doAoeDamage(realm.getEntity(shooterId), bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, ohe, damageType);
				realm.removeLevelObject(this);
			}
		}
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset;
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
