package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Sprite extends Group
{
	private final TextureRegion textureRegion;
	private int tag = -1;
	
	public Sprite(TextureRegion textureRegion)
	{
		this(0, 0, textureRegion);
	}
	
	public Sprite(float x, float y, TextureRegion textureRegion)
	{
		this(x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), textureRegion);
	}
	
	public Sprite(float x, float y, float width, float height, TextureRegion textureRegion)
	{
		if(textureRegion == null)
			throw new NullPointerException("TextureRegion == null");
		this.textureRegion = textureRegion;
		this.setBounds(x, y, width, height);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
		batch.draw(textureRegion, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation());
		super.draw(batch, parentAlpha);
	}
	
	public boolean contains(float x, float y)
	{
		return x >= getX() && x < getX() + getWidth() && y >= getY() && y < getY() + getHeight();
	}
	
	public void setAlpha(float a)
	{
		this.getColor().a = a;
	}
	
	public void setTag(int tag)
	{
		this.tag = tag;
	}
	
	public int getTag()
	{
		return tag;
	}
	
	public Sprite getChildByTag(int tag)
	{
		for(Actor actor : this.getChildren())
			if(actor instanceof Sprite && ((Sprite) actor).getTag() == tag)
				return (Sprite) actor;
		return null;
	}
}
