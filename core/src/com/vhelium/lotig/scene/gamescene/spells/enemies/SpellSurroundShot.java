package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import com.badlogic.gdx.math.MathUtils;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public abstract class SpellSurroundShot extends SpellEnemy
{
	protected String bulletAsset;
	protected String castSound;
	
	protected float cooldownBase;
	protected float cooldownPerLevel;
	
	protected float manaCostBase;
	protected float manaCostPerLevel;
	
	protected float damageBase;
	protected float damagePerLevel;
	
	protected float damageBonusBase;
	protected float damageBonusPerLevel;
	
	protected int rangeBase;
	protected int rangePerLevel;
	
	protected int shotCount;
	
	protected int damageType;
	protected boolean piercing;
	
	public SpellSurroundShot(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = false;
	}
	
	@Override
	public float getManaCost()
	{
		return getManaCostStatic(getLevel());
	}
	
	@Override
	public float getCooldown()
	{
		return cooldownBase + cooldownPerLevel * (getLevel() - 1);
	}
	
	@Override
	public void activate(Realm realm, int shooterId, float x, float y, float rotation, float dirX, float dirY)
	{
		double initialAngle;
		if(dirX < 0)
			initialAngle = Math.atan(dirY / dirX) + Math.PI;
		else if(dirX == 0 && dirY > 0)
			initialAngle = Math.PI / 2;
		else if(dirX == 0 && dirY <= 0)
			initialAngle = Math.PI / 2 * 3;
		else
			initialAngle = Math.atan(dirY / dirX);
		
		BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(getLevel()) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(getLevel()) + 1), 0, "", 0);
		
		double angle = initialAngle;
		for(int i = 0; i < shotCount; i++)
		{
			if(i > 0)
			{
				angle = initialAngle - i * (Math.PI * 2 / shotCount);
				dirX = (float) Math.cos(angle);
				dirY = (float) Math.sin(angle);
				rotation = MathUtils.radiansToDegrees * ((float) Math.atan2(dirX, -dirY));
			}
			realm.shoot(shooterId, piercing, damageType, getRange(getLevel()), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, effect);
		}
	}
	
	@Override
	public String getCastSound()
	{
		return castSound;
	}
	
	protected float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	protected float getDamage(int level)
	{
		return damageBase + damagePerLevel * (level - 1);
	}
	
	protected float getBonusDamage(int level)
	{
		return damageBonusBase + damageBonusPerLevel * (level - 1);
	}
	
	protected int getRange(int level)
	{
		return rangeBase + rangePerLevel * (level - 1);
	}
}
