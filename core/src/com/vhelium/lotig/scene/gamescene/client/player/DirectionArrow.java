package com.vhelium.lotig.scene.gamescene.client.player;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vhelium.lotig.components.Sprite;

public class DirectionArrow extends Sprite
{
	PlayerClient player;
	int gapSize = 38;
	double rotation, targetX, targetY;
	float centerX, centerY;
	boolean enabled = true;
	
	public DirectionArrow(final PlayerClient player, TextureRegion pTextureRegion)
	{
		super(0, 0, pTextureRegion);
		this.player = player;
		this.setOriginX(pTextureRegion.getRegionWidth() / 2);
		this.setOriginY(pTextureRegion.getRegionHeight() / 2);
		centerX = player.getSprite().getWidth() / 2 - pTextureRegion.getRegionWidth() / 2;
		centerY = player.getSprite().getHeight() / 2 - pTextureRegion.getRegionHeight() / 2;
		setX(centerX);
		setY(centerY);
	}
	
	@Override
	public void act(float delta)
	{
		if(enabled)
		{
			double x = targetX - player.getOriginX();
			double y = targetY - player.getOriginY();
			rotation = Math.toDegrees((Math.atan2(x, -y)));
			DirectionArrow.this.setRotation((float) rotation);
			
			float v = (float) Math.sqrt(x * x + y * y);
			DirectionArrow.this.setX(centerX + (float) ((x / v) * gapSize));
			DirectionArrow.this.setY(centerY + (float) ((y / v) * gapSize));
		}
		super.act(delta);
	}
	
	public void setTarget(float x, float y)
	{
		targetX = x;
		targetY = y;
		setVisible(true);
		enabled = true;
	}
	
	public void disable()
	{
		setVisible(false);
		enabled = false;
	}
}
