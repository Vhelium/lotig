package com.vhelium.lotig.scene.gamescene.client;

import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.Effect;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;

public class EntityClient extends EntityClientMixin
{
	public enum AnimationStatus
	{
		IDLE_BOT, IDLE_LEFT, IDLE_TOP, IDLE_RIGHT, SHOOTING_BOT, SHOOTING_LEFT, SHOOTING_TOP, SHOOTING_RIGHT, WALKING_BOT, WALKING_LEFT, WALKING_TOP, WALKING_RIGHT, NONE
	}
	
	private final String name;
	private Sprite questMark = null;
	
	private float fromX = 0;
	private float fromY = 0;
	private float toX = 0;
	private float toY = 0;
	
	private int lastPingTime = 0;
	private int timeSinceLastUpdate = 0;
	
	private float interpolateValue;
	
	public EntityClient(ClientLevel level, TMXMap tmxMap, String name, int width, int height, String asset)
	{
		super(level, tmxMap, width, height, asset);
		if(level.isMultiplayerClient())
			interpolateValue = 0.3f;
		else
			interpolateValue = 0.0f;
		this.name = name;
	}
	
	public void update(float delta, long millis)
	{
		if(shootCooldownLeft > 0)
			shootCooldownLeft -= delta;
		
		timeSinceLastUpdate += delta;
		float i = (float) timeSinceLastUpdate / lastPingTime;
		
		if(i > 1)
			i = 1;
		if(i < 0)
			i = 0;
		
		setX(inperpolateF(fromX, toX, i + interpolateValue));
		setY(inperpolateF(fromY, toY, i + interpolateValue));
		
		updateAnimation();
		
		updateDamageNumbers();
	}
	
	public void updatePositionSmooth(float x, float y, float rot)
	{
		fromX = toX;
		fromY = toY;
		toX = x;
		toY = y;
		setRotation(rot);
		lastPingTime = timeSinceLastUpdate;
		timeSinceLastUpdate = 0;
	}
	
	public void shoot()
	{
		resetAnimation();
		shootCooldownLeft = shootSpeed;
	}
	
	@Override
	public boolean isMoving()
	{
		return fromX != toX || fromY != toY;
	}
	
	public void setNewPos(float x, float y)
	{
		lastPingTime = 0;
		timeSinceLastUpdate = 0;
		
		fromX = x;
		fromY = y;
		toX = x;
		toY = y;
		
		setX(x);
		setY(y);
	}
	
	private float inperpolateF(float a, float b, float i)
	{
		return (b - a) * i + a;
	}
	
	public String getBulletType()
	{
		return "Normal";
	}
	
	public void setShootSpeed(float value)
	{
		shootSpeed = value;
	}
	
	public void removeCondition(final String name)
	{
		if(conditionEffects.containsKey(name))
		{
			final Effect eff = conditionEffects.get(name);
			conditionEffects.remove(name);
			eff.remove();
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setQuestMark(final boolean b)
	{
		if(questMark != null)
			questMark.remove();
		if(b)
		{
			if(questMark == null)
				questMark = new Sprite(getSprite().getWidth() / 2 + EntityClient.this.getWidth() / 2 + 2, -2, GameHelper.getInstance().getGameAsset("QuestMark"));
			questMark.remove();
		}
	}
}
