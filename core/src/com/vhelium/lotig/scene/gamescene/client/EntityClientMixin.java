package com.vhelium.lotig.scene.gamescene.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.Effect;
import com.vhelium.lotig.scene.gamescene.EntityMixin;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.EntityClient.AnimationStatus;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;

public abstract class EntityClientMixin implements EntityMixin
{
	protected ClientLevel level;
	protected TMXMap tmxMap;
	protected AnimatedSprite sprite;
	public HashMap<String, Effect> conditionEffects;
	protected boolean isMyPlayer = false;
	
	private boolean isDead = false;
	
	protected Sprite pvpFlag;
	
	private final List<DamageNumber> damageNumberQueue = new ArrayList<DamageNumber>();
	
	public String asset;
	protected boolean forceShootRepetition = false;
	protected float shootSpeed;
	protected float shootCooldownLeft = 0;
	
	protected boolean shootSpeedAnimationReset = false;
	
	protected float rotation = 0f;
	
	public EntityClientMixin(ClientLevel level, TMXMap tmxMap, int width, int height, String asset)
	{
		this.level = level;
		this.tmxMap = tmxMap;
		this.asset = asset;
		
		sprite = new AnimatedSprite(0, 0, GameHelper.getInstance().getEntityAssetCopy(asset));
		setWidth(width);
		setHeight(height);
		conditionEffects = new HashMap<String, Effect>();
	}
	
	protected void updateAnimation()
	{
		if(rotation > 135 || rotation <= -135) // BOT
		{
			if(isShooting())
				playAnimation(AnimationStatus.SHOOTING_BOT);
			else if(isMoving())
				playAnimation(AnimationStatus.WALKING_BOT);
			else
				playAnimation(AnimationStatus.IDLE_BOT);
		}
		else if(rotation <= -45) // LEFT
		{
			if(isShooting())
				playAnimation(AnimationStatus.SHOOTING_LEFT);
			else if(isMoving())
				playAnimation(AnimationStatus.WALKING_LEFT);
			else
				playAnimation(AnimationStatus.IDLE_LEFT);
		}
		else if(rotation <= 45) // TOP
		{
			if(isShooting())
				playAnimation(AnimationStatus.SHOOTING_TOP);
			else if(isMoving())
				playAnimation(AnimationStatus.WALKING_TOP);
			else
				playAnimation(AnimationStatus.IDLE_TOP);
		}
		else
		// RIGHT
		{
			if(isShooting())
				playAnimation(AnimationStatus.SHOOTING_RIGHT);
			else if(isMoving())
				playAnimation(AnimationStatus.WALKING_RIGHT);
			else
				playAnimation(AnimationStatus.IDLE_RIGHT);
		}
	}
	
	private AnimationStatus currentAnimation;
	
	private void playAnimation(AnimationStatus animation)
	{
		if(animation != currentAnimation || (!sprite.isAnimationRunning() && (!isShooting() || forceShootRepetition)))
		{
			currentAnimation = animation;
			
			switch(currentAnimation)
			{
				case IDLE_BOT:
					sprite.setFlippedHorizontal(false);
					sprite.stopAnimation(10);
					break;
				case IDLE_LEFT:
					sprite.setFlippedHorizontal(false);
					sprite.stopAnimation(5);
					break;
				case IDLE_TOP:
					sprite.setFlippedHorizontal(false);
					sprite.stopAnimation(0);
					break;
				case IDLE_RIGHT:
					sprite.setFlippedHorizontal(true);
					sprite.stopAnimation(5);
					break;
				
				case SHOOTING_BOT:
					sprite.setFlippedHorizontal(false);
					sprite.animate(shootSpeed / 2, 13, 14, false);
					break;
				case SHOOTING_LEFT:
					sprite.setFlippedHorizontal(false);
					sprite.animate(shootSpeed / 2, 8, 9, false);
					break;
				case SHOOTING_TOP:
					sprite.setFlippedHorizontal(false);
					sprite.animate(shootSpeed / 2, 3, 4, false);
					break;
				case SHOOTING_RIGHT:
					sprite.setFlippedHorizontal(true);
					sprite.animate(shootSpeed / 2, 8, 9, false);
					break;
				
				case WALKING_BOT:
					sprite.setFlippedHorizontal(false);
					sprite.animate(200, 11, 12, true);
					break;
				case WALKING_LEFT:
					sprite.setFlippedHorizontal(false);
					sprite.animate(200, 6, 7, true);//133
					break;
				case WALKING_TOP:
					sprite.setFlippedHorizontal(false);
					sprite.animate(200, 1, 2, true);
					break;
				case WALKING_RIGHT:
					sprite.setFlippedHorizontal(true);
					sprite.animate(200, 6, 7, true);//133
					break;
				case NONE:
					break;
				default:
					break;
			}
		}
	}
	
	protected void resetAnimation()
	{
		currentAnimation = AnimationStatus.NONE;
	}
	
	public abstract boolean isMoving();
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	public boolean isShooting()
	{
		return shootCooldownLeft > 0;
	}
	
	public AnimatedSprite getSprite()
	{
		return sprite;
	}
	
	private float x;
	
	public float getX()
	{
		return x;
	}
	
	public void setX(float x)
	{
		this.x = x;
		sprite.setX(x - width);
	}
	
	private float y;
	
	public float getY()
	{
		return y;
	}
	
	public void setY(float y)
	{
		this.y = y;
		sprite.setY(y - height / 2);
	}
	
	private float width;
	
	public float getWidth()
	{
		return width;
	}
	
	public void setWidth(float width)
	{
		this.width = width;
		sprite.setWidth(width * 3);
		setX(this.x);
	}
	
	private float height;
	
	public float getHeight()
	{
		return height;
	}
	
	public void setHeight(float height)
	{
		this.height = height;
		sprite.setHeight(height * 2);
		setY(this.y);
	}
	
	@Override
	public float getOriginX()
	{
		return getX() + getWidth() / 2;
	}
	
	@Override
	public float getOriginY()
	{
		return getY() + getHeight() / 2;
	}
	
	public float getRotation()
	{
		return rotation;
	}
	
	private DamageNumber lastDisplayedDamageNumber = null;
	
	public void addDamageNumberToQueue(DamageNumber damageNumber)
	{
		if(damageNumberQueue.size() < 8)
			damageNumberQueue.add(damageNumber);
	}
	
	public void updateDamageNumbers()
	{
		if(damageNumberQueue.size() > 0)
		{
			if(lastDisplayedDamageNumber == null || Math.abs(damageNumberQueue.get(0).getY() - lastDisplayedDamageNumber.getY()) > damageNumberQueue.get(0).getHeight())
			{
				getSprite().addActor(damageNumberQueue.get(0));
				lastDisplayedDamageNumber = damageNumberQueue.get(0);
				damageNumberQueue.remove(0);
			}
		}
	}
	
	public void onSkinChanged(String skin)
	{
		asset = skin;
		sprite.setTextureRegion(GameHelper.getInstance().getEntityAssetCopy(asset));
	}
	
	@Override
	public boolean isDead()
	{
		return isDead;
	}
	
	public void setDead(boolean dead)
	{
		isDead = dead;
		if(sprite != null)
			sprite.setVisible(!dead);
	}
	
	public void removePvPFlag()
	{
		if(pvpFlag != null)
			pvpFlag.remove();
	}
	
	public void setPvPFlag(Sprite flag)
	{
		removePvPFlag();
		pvpFlag = flag;
		getSprite().addActor(flag);
	}
}
