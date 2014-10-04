package com.vhelium.lotig.scene.gamescene;

import com.vhelium.lotig.scene.gamescene.client.player.MovementModifier;

public class BulletOnHitEffect
{
	private final float damage;
	private final float lifeStealPercent;
	private final float aoeRadius;
	private String effect;
	private String soundEffect;
	private final int effectDuration;
	private Condition condition;
	public int repeat = 1;
	public boolean centered;
	public float delay;
	public boolean dontProccFirst = false;
	public boolean effectAttached = false;
	private MovementModifier movementModifier;
	
	public BulletOnHitEffect(float damage, float aoeRadius, String effect, int effectDuration)
	{
		this(damage, 0f, aoeRadius, effect, effectDuration);
	}
	
	public BulletOnHitEffect(float damage, float lifeStealPercent, float aoeRadius, String effect, int effectDuration)
	{
		this.damage = damage;
		this.aoeRadius = aoeRadius;
		this.effect = effect;
		this.effectDuration = effectDuration;
		this.lifeStealPercent = lifeStealPercent;
	}
	
	public BulletOnHitEffect(float damage, float aoeRadius, Condition condition)
	{
		this(damage, aoeRadius, condition, "", 0);
	}
	
	public BulletOnHitEffect(float damage, int aoeRadius, Condition condition, MovementModifier movementModifier)
	{
		this(damage, 0f, aoeRadius, condition, movementModifier, "", 0);
	}
	
	public BulletOnHitEffect(float damage, float aoeRadius, Condition condition, String effect, int effectDuration)
	{
		this(damage, 0f, aoeRadius, condition, null, effect, effectDuration);
	}
	
	public BulletOnHitEffect(float damage, float lifeStealPercent, float aoeRadius, Condition condition)
	{
		this(damage, lifeStealPercent, aoeRadius, condition, null, "", 0);
	}
	
	public BulletOnHitEffect(float damage, float lifeStealPercent, float aoeRadius, Condition condition, MovementModifier movementModifier, String effect, int effectDuration)
	{
		this.damage = damage;
		this.aoeRadius = aoeRadius;
		this.condition = condition;
		this.movementModifier = movementModifier;
		this.effect = effect;
		this.lifeStealPercent = lifeStealPercent;
		this.effect = effect;
		this.effectDuration = effectDuration;
	}
	
	public float getDamage()
	{
		return damage;
	}
	
	public float getAoeRadius()
	{
		return aoeRadius;
	}
	
	public String getSoundEffect()
	{
		return soundEffect;
	}
	
	public void setSoundEffect(String soundEffect)
	{
		this.soundEffect = soundEffect;
	}
	
	public String getEffect()
	{
		return effect;
	}
	
	public int getEffectDuration()
	{
		return effectDuration;
	}
	
	public Condition getCondition()
	{
		return condition;
	}
	
	public float getLifeStealPercent()
	{
		return lifeStealPercent;
	}
	
	public MovementModifier getMovementModifier()
	{
		return movementModifier;
	}
	
	public void setMovementModifier(MovementModifier movementModifier)
	{
		this.movementModifier = movementModifier;
	}
}
