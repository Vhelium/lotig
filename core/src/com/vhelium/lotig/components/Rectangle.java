package com.vhelium.lotig.components;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class Rectangle extends Actor
{
	public Rectangle()
	{
		
	}
	
	public Rectangle(float x, float y, float width, float height)
	{
		this.setBounds(x, y, width, height);
	}
	
	public Rectangle(com.badlogic.gdx.math.Rectangle rectangle)
	{
		this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}
	
	public Rectangle(com.badlogic.gdx.math.Rectangle rectangle, boolean invertY, float mapHeight)
	{
		this(rectangle.x, invertY ? mapHeight - rectangle.y - rectangle.height : rectangle.y, rectangle.width, rectangle.height);
	}
	
	public Rectangle(Rectangle rectangle)
	{
		this(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}
	
	public void setRotationCenterX(final float pRotationCenterX)
	{
		this.setOriginX(pRotationCenterX);
	}
	
	public void setRotationCenterY(final float pRotationCenterY)
	{
		this.setOriginY(pRotationCenterY);
	}
	
	public void setRotationCenter(final float pRotationCenterX, final float pRotationCenterY)
	{
		this.setOrigin(pRotationCenterX, pRotationCenterY);
	}
	
	public void setAlpha(float a)
	{
		this.getColor().a = a;
	}
	
	public boolean collidesWith(Rectangle rectangle)
	{
		return rectangle.getX() + rectangle.getWidth() >= getX() && rectangle.getX() < getX() + getWidth() && rectangle.getY() + rectangle.getHeight() >= getY() && rectangle.getY() < getY() + getHeight();
	}
}
