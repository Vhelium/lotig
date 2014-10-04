package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.vhelium.lotig.scene.gamescene.EntityMixin;

public class BoundOrthographicCamera extends OrthographicCamera
{
	Vector2 realPosition = new Vector2();
	EntityMixin chaseActor;
	float fromX, fromY, toX, toY;
	boolean boundsEnabled = false;
	boolean init = false;
	
	public BoundOrthographicCamera(float cameraWidth, float cameraHeight)
	{
		super(cameraWidth, cameraHeight);
		position.x = cameraWidth / 2;
		position.y = cameraHeight / 2;
		realPosition.x = position.x;
		realPosition.y = position.y;
		init = true;
	}
	
	public void setBounds(int fromX, int fromY, int toX, int toY)
	{
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}
	
	public void setChaseActor(EntityMixin actor)
	{
		this.chaseActor = actor;
		update();
	}
	
	@Override
	public void update()
	{
		if(init)
		{
			if(chaseActor != null)
			{
				realPosition.x = chaseActor.getOriginX();
				realPosition.y = chaseActor.getOriginY();
			}
			if(boundsEnabled)
			{
				realPosition.x = Math.min(toX - viewportWidth / 2, Math.max(realPosition.x, fromX + viewportWidth / 2));
				realPosition.y = Math.min(toY - viewportHeight / 2, Math.max(realPosition.y, fromY + viewportHeight / 2));
			}
			if(chaseActor != null)
			{
				position.x = Math.round(realPosition.x);
				position.y = Math.round(realPosition.y);
			}
			super.update();
		}
	}
	
	public void setBoundsEnabled(boolean v)
	{
		boundsEnabled = v;
	}
}
