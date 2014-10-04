package com.vhelium.lotig.scene.gamescene.server;

import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;

public class ShootRequestAimLock extends ShootRequest
{
	public EntityServerMixin target;
	
	public void createRequest(EntityServerMixin target, int damageType, float maxRange, float x, float y, String bulletType, BulletOnHitEffect ohe)
	{
		isAvailable = true;
		this.target = target;
		this.damageType = damageType;
		this.maxRange = maxRange;
		this.x = x;
		this.y = y;
		this.bulletType = bulletType;
		this.onhiteffect = ohe;
	}
	
	@Override
	public boolean isAvailable()
	{
		if(!isAvailable)
			return false;
		
		isAvailable = false;
		return true;
	}
}
