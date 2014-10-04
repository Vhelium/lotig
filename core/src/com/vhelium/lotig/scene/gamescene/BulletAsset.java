package com.vhelium.lotig.scene.gamescene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BulletAsset
{
	private final float width;
	private final float height;
	private final float speed;
	private final TextureRegion textureRegion;
	
	public BulletAsset(float speed, TextureRegion textureRegion)
	{
		this.width = textureRegion.getRegionWidth();
		this.height = textureRegion.getRegionHeight();
		this.speed = speed;
		this.textureRegion = textureRegion;
	}
	
	public BulletAsset(float speed, TextureRegion textureRegion, int width, int height)
	{
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.textureRegion = textureRegion;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public TextureRegion getTextureRegion()
	{
		return textureRegion;
	}
}
