package com.vhelium.lotig.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Line extends Actor
{
	float x1, y1, x2, y2;
	ShapeRendererBatch shape;
	
	public Line(float x1, float y1, float x2, float y2, ShapeRendererBatch shape)
	{
		setPosition(x1, y1, x2, y2);
		this.shape = shape;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shape.begin(ShapeType.Line);
		shape.setColor(getColor());
		shape.line(getX() + x1, getY() + y1, getX() + x2, getY() + y2);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
	
	public void setAlpha(float alpha)
	{
		getColor().a = alpha;
	}
	
	public void setPosition(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
