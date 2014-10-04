package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Label extends com.badlogic.gdx.scenes.scene2d.ui.Label
{
	private final float offsetY;
	
	public Label(CharSequence text, LabelStyle style)
	{
		super(text, style);
		offsetY = -style.font.getDescent();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		final float y = getY();
		final float offset = offsetY;
		setY(y + offset);
		super.draw(batch, parentAlpha);
		setY(y);
	}
	
	@Override
	public float getHeight()
	{
		return super.getHeight() - offsetY;
	}
	
	protected float getOffsetY()
	{
		return offsetY;
	}
}
