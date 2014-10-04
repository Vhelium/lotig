package com.vhelium.lotig.components;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class RectangleDrawer extends Actor
{
	ShapeRendererBatch shape;
	List<Rectangle> rectangles;
	
	public RectangleDrawer(ShapeRendererBatch shape)
	{
		this.shape = shape;
		rectangles = new ArrayList<Rectangle>();
	}
	
	public void addRectangle(Rectangle rect)
	{
		rectangles.add(rect);
	}
	
	public void addRectangles(List<Rectangle> rects)
	{
		rectangles.addAll(rects);
	}
	
	public void removeRectangle(Rectangle rectangle)
	{
		if(rectangles.contains(rectangle))
			rectangles.remove(rectangle);
	}
	
	public void clearRectangles()
	{
		rectangles.clear();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shape.begin(ShapeType.Filled);
		for(Rectangle r : rectangles)
		{
			shape.setColor(r.getColor());
			shape.rect(getX() + r.getX(), getY() + r.getY(), r.getWidth(), r.getHeight());
		}
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
