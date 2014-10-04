package com.vhelium.lotig.scene.gamescene.server;

import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;

public class ShootRequest
{
	protected boolean isAvailable = false;
	public boolean piercing;
	public float maxRange;
	public float x;
	public float y;
	public float rotation;
	public float directionX;
	public float directionY;
	public String bulletType;
	public int damageType;
	public BulletOnHitEffect onhiteffect;
	
	public void createRequest(boolean piercing, int damageType, float maxRange, float x, float y, float rotation, float directionX, float directionY, String bulletType)
	{
		isAvailable = true;
		this.piercing = piercing;
		this.damageType = damageType;
		this.maxRange = maxRange;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.directionX = directionX;
		this.directionY = directionY;
		this.bulletType = bulletType;
	}
	
	public boolean isAvailable()
	{
		if(!isAvailable)
			return false;
		
		isAvailable = false;
		return true;
	}
}
