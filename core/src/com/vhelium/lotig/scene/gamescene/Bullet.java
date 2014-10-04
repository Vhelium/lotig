package com.vhelium.lotig.scene.gamescene;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.MathUtils;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;

public class Bullet
{
	private Sprite sprite;
	private final Rectangle rectangle;
	private EntityServerMixin owner;
	private EntityMixin target;
	private final TMXMap tmxMap;
	private float x;
	private float y;
	private float width;
	private float height;
	private float speed;
	private float speedX;
	private float speedY;
	private float rotation;
	
	private final float maxRange;
	private float passedRange;
	
	private boolean piercing;
	private int damageType;
	private BulletOnHitEffect onhiteffect;
	private List<Integer> entitiesHit;
	
	private boolean isDead = false;
	private boolean deathByCollision = false;
	
	private boolean hasCollision = true;
	
	//CLIENT
	public Bullet(TMXMap tmxMap, float maxRange, float x, float y, float rotation, float directionX, float directionY, String bulletAsset, boolean hasCollision)
	{
		this.tmxMap = tmxMap;
		
		BulletAsset asset = GameHelper.getInstance().getBulletAsset(bulletAsset);
		sprite = new Sprite(asset.getTextureRegion());
		sprite.setPosition(x, y);
		rectangle = new Rectangle(x, y, 0, 0);
		setRotation(rotation);
		setWidth(asset.getWidth());
		setHeight(asset.getHeight());
		setSpeed(asset.getSpeed());
		this.maxRange = maxRange;
		setCollision(hasCollision);
		
		setX(x);
		setY(y);
		
		speedX = directionX * speed;
		speedY = directionY * speed;
	}
	
	//SERVER
	public Bullet(TMXMap tmxMap, EntityServerMixin owner, boolean piercing, int damageType, float maxRange, float x, float y, float rotation, float directionX, float directionY, String bulletAsset, BulletOnHitEffect onhiteffect, boolean hasCollision)
	{
		this.tmxMap = tmxMap;
		this.owner = owner;
		
		BulletAsset asset = GameHelper.getInstance().getBulletAsset(bulletAsset);
		rectangle = new Rectangle(x, y, 0, 0);
		setRotation(rotation);
		setWidth(asset.getWidth());
		setHeight(asset.getHeight());
		setSpeed(asset.getSpeed());
		this.maxRange = maxRange;
		this.piercing = piercing;
		this.damageType = damageType;
		this.onhiteffect = onhiteffect;
		setCollision(hasCollision);
		
		setX(x);
		setY(y);
		
		speedX = directionX * speed;
		speedY = directionY * speed;
	}
	
	//CLIENT
	public Bullet(TMXMap tmxMap, EntityMixin target, float maxRange, float x, float y, float rotation, float directionX, float directionY, String bulletAsset)
	{
		this.tmxMap = tmxMap;
		this.target = target;
		
		BulletAsset asset = GameHelper.getInstance().getBulletAsset(bulletAsset);
		sprite = new Sprite(asset.getTextureRegion());
		sprite.setPosition(x, y);
		rectangle = new Rectangle(x, y, 0, 0);
		setWidth(asset.getWidth());
		setHeight(asset.getHeight());
		setSpeed(asset.getSpeed());
		this.maxRange = maxRange;
		
		setX(x);
		setY(y);
		
		calculateSpeedToTarget();
	}
	
	//Server
	public Bullet(TMXMap tmxMap, EntityServerMixin owner, EntityMixin target, int damageType, float maxRange, float x, float y, String bulletAsset, BulletOnHitEffect onhiteffect)
	{
		this.tmxMap = tmxMap;
		this.owner = owner;
		this.target = target;
		this.onhiteffect = onhiteffect;
		
		BulletAsset asset = GameHelper.getInstance().getBulletAsset(bulletAsset);
		rectangle = new Rectangle(x, y, 0, 0);
		setWidth(asset.getWidth());
		setHeight(asset.getHeight());
		setSpeed(asset.getSpeed());
		this.maxRange = maxRange;
		this.damageType = damageType;
		
		setX(x);
		setY(y);
		
		calculateSpeedToTarget();
	}
	
	int collisionCheckDelay = 0;
	
	public void update(float delta)
	{
		setX(getX() + speedX * delta);
		setY(getY() + speedY * delta);
		
		passedRange += Math.sqrt(Math.pow(speedX * delta, 2) + Math.pow(speedY * delta, 2));
		
		if(target != null)//Aim lock
		{
			if(target.isDead() || passedRange >= maxRange || (Math.abs(speedX) <= 0.01 && Math.abs(speedY) <= 0.01))
			{
				isDead = true;
				deathByCollision = false;
			}
			else
			{
				if(collisionCheckDelay <= 0)
				{
					calculateSpeedToTarget();
					collisionCheckDelay = 2;
					
				}
				else
					collisionCheckDelay--;
			}
		}
		else
		{
			if(collisionCheckDelay <= 0)
			{
				if(hasCollision && tmxMap.isCollisionAt(getX(), getY(), getWidth(), getHeight(), true))
				{
					isDead = true;
					deathByCollision = true;
				}
				else if(passedRange >= maxRange)
				{
					isDead = true;
					deathByCollision = false;
				}
				collisionCheckDelay = 1;
			}
			else
				collisionCheckDelay--;
		}
	}
	
	private void calculateSpeedToTarget()
	{
		if(target != null)
		{
			//x2 = v2/v * x, v2 = 1
			float v = (float) Math.sqrt(Math.pow(target.getOriginX() - getOriginX(), 2) + Math.pow(target.getOriginY() - getOriginY(), 2));
			float directionX = (1 / v) * (target.getOriginX() - getOriginX());
			float directionY = (1 / v) * (target.getOriginY() - getOriginY());
			setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(directionX, -directionY)));
			
			speedX = directionX * speed;
			speedY = directionY * speed;
		}
	}
	
	public boolean getPiercing()
	{
		return piercing;
	}
	
	public int getDamageType()
	{
		return damageType;
	}
	
	public BulletOnHitEffect getOnHitEffect()
	{
		return onhiteffect;
	}
	
	public Rectangle getRectangle()
	{
		return rectangle;
	}
	
	public Sprite getSprite()
	{
		return sprite;
	}
	
	public EntityServerMixin getOwner()
	{
		return owner;
	}
	
	public float getX()
	{
		return x;
	}
	
	public void setX(float x)
	{
		this.x = x;
		
		rectangle.setX(x);
		if(sprite != null)
			sprite.setX(x);
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setY(float y)
	{
		this.y = y;
		
		rectangle.setY(y);
		if(sprite != null)
			sprite.setY(y);
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public void setWidth(float width)
	{
		this.width = width;
		
		rectangle.setWidth(width);
		rectangle.setRotationCenter(height / 2, width / 2);
		
		if(sprite != null)
		{
			sprite.setWidth(width);
			sprite.setOriginX(width / 2);
		}
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public void setHeight(float height)
	{
		this.height = height;
		
		rectangle.setHeight(height);
		rectangle.setRotationCenter(height / 2, width / 2);
		
		if(sprite != null)
		{
			sprite.setHeight(height);
			sprite.setOriginY(height / 2);
		}
	}
	
	public float getOriginX()
	{
		return getX() + getWidth() / 2;
	}
	
	public float getOriginY()
	{
		return getY() + getHeight() / 2;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
	
	public float getRotation()
	{
		return rotation;
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
		
		rectangle.setRotation(rotation);
		
		if(sprite != null)
			sprite.setRotation(rotation);
	}
	
	public boolean getIsDead()
	{
		return isDead;
	}
	
	public void setDead()
	{
		isDead = true;
	}
	
	public boolean getDeathByCollision()
	{
		return deathByCollision;
	}
	
	public boolean hasHitEntity(int entityId)
	{
		if(entitiesHit != null)
			return entitiesHit.contains(entityId);
		else
			return false;
	}
	
	public void hitEntity(int entityId)
	{
		if(entitiesHit == null)
			entitiesHit = new ArrayList<Integer>();
		entitiesHit.add(entityId);
	}
	
	public boolean hasCollision()
	{
		return hasCollision;
	}
	
	public void setCollision(boolean hasCollision)
	{
		this.hasCollision = hasCollision;
	}
	
	public EntityMixin getTarget()
	{
		return target;
	}
}
