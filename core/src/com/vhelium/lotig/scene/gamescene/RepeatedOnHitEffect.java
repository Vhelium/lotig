package com.vhelium.lotig.scene.gamescene;

import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;

public class RepeatedOnHitEffect
{
	public float repeatDelayElapsed = 0f;
	public EntityServerMixin owner;
	public float originX;
	public float originY;
	public BulletOnHitEffect onHitEffect;
	public int damageType;
	
	public RepeatedOnHitEffect(EntityServerMixin owner, float originX, float originY, BulletOnHitEffect onHitEffect, int damageType)
	{
		this.owner = owner;
		this.originX = originX;
		this.originY = originY;
		this.onHitEffect = onHitEffect;
		this.damageType = damageType;
		onHitEffect.centered = false;
	}
	
	public RepeatedOnHitEffect(EntityServerMixin owner, BulletOnHitEffect onHitEffect, int damageType)
	{
		this.owner = owner;
		this.onHitEffect = onHitEffect;
		this.damageType = damageType;
		onHitEffect.centered = true;
	}
	
	public void setRepeatCount(int i)
	{
		onHitEffect.repeat = i;
	}
	
	public int getRepeatCount()
	{
		return onHitEffect.repeat;
	}
	
	public void lowerRepeatCount()
	{
		onHitEffect.repeat--;
	}
	
	public void setRepeatDelay(float f)
	{
		onHitEffect.delay = f;
	}
	
	public float getRepeatDelay()
	{
		return onHitEffect.delay;
	}
}
