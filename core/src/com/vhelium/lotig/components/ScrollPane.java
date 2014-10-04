package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.SnapshotArray;

public class ScrollPane extends Group
{
	private final Rectangle areaBounds = new Rectangle();
	private final Rectangle scissorBounds = new Rectangle();
	private final Vector2 lastPoint = new Vector2();
	private Drawable background;
	private TextureRegion fade;
	
	Group scrollContainer;
	
	boolean scrollX, scrollY;
	boolean touchScrollH, touchScrollV;
	
	float offset = 2;
	float amountX, amountY;
	int draggingPointer = -1;
	
	public ScrollPane(float x, float y, float width, float height)
	{
		areaBounds.set(0, 0, width, height);
		this.setBounds(x, y, width, height);
		
		scrollContainer = new Group();
		scrollContainer.setZIndex(0);
		this.addActor(scrollContainer);
		
		addCaptureListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				if(draggingPointer != -1)
					return false;
				if(pointer == 0 && button != 0)
					return false;
				getStage().setScrollFocus(ScrollPane.this);
				
				if(needsScrollX() && areaBounds.contains(x, y))
				{
					lastPoint.set(x, y);
					touchScrollH = true;
					draggingPointer = pointer;
					return true;
				}
				if(needsScrollY() && areaBounds.contains(x, y))
				{
					lastPoint.set(x, y);
					touchScrollV = true;
					draggingPointer = pointer;
					return true;
				}
				return false;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				if(pointer != draggingPointer)
					return;
				cancel();
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer)
			{
				if(pointer != draggingPointer)
					return;
				if(touchScrollH)
				{
					float delta = x - lastPoint.x;
					amountX += delta;
					amountX = Math.min(areaBounds.x, amountX);
					amountX = Math.max(areaBounds.x + areaBounds.width - scrollContainer.getWidth(), amountX);
					lastPoint.set(x, y);
				}
				else if(touchScrollV)
				{
					float delta = y - lastPoint.y;
					amountY += delta;
					amountY = Math.min(areaBounds.y, amountY);
					amountY = Math.max(areaBounds.y + areaBounds.height - scrollContainer.getHeight(), amountY);
					lastPoint.set(x, y);
				}
			}
			
//			@Override
//			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
//			{
//				touchUp(event, x, y, pointer, -1);
//			}
		});
	}
	
	/** If currently scrolling by tracking a touch down, stop scrolling. */
	public void cancel()
	{
		draggingPointer = -1;
		touchScrollH = false;
		touchScrollV = false;
	}
	
	public void addItemVertical(Actor actor)
	{
		scrollContainer.addActor(actor);
		updateItems();
	}
	
	public void updateItems()
	{
		amountX = 0;
		amountY = 0;
		float pos = 0;
		float maxWidth = 0;
		float height = 0;
		SnapshotArray<Actor> children = scrollContainer.getChildren();
		for(int i = 0, n = children.size; i < n; i++)
		{
			Actor actor = children.get(i);
			actor.setY(pos);
			
			if(actor.getWidth() > maxWidth)
				maxWidth = actor.getWidth();
			height += actor.getHeight() + 2;
			
			pos += actor.getHeight() + 2;
		}
		scrollContainer.setWidth(maxWidth);
		scrollContainer.setHeight(height);
	}
	
	@Override
	public void act(float delta)
	{
		final float millis = delta * 1000f;
		
		super.act(delta);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		// Setup transform for this group.
		applyTransform(batch, computeTransform());
		
		// Calculate the widget's position
		float y = areaBounds.y;
		if(scrollY)
			y += (int) amountY;
		float x = areaBounds.x;
		if(scrollX)
			x += (int) amountX;
		scrollContainer.setPosition(x, y);
		
		// Calculate the scissor bounds based on the batch transform, the available widget area and the camera transform. We need to
		// project those to screen coordinates for OpenGL ES to consume.
		getStage().calculateScissors(areaBounds, scissorBounds);
		
		// Draw the background
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		if(background != null)
			background.draw(batch, 0, 0, getWidth(), getHeight());
		//draw fade
		batch.flush();
		
		// Enable scissors for widget area and draw the widget.
		if(ScissorStack.pushScissors(scissorBounds))
		{
			drawChildren(batch, parentAlpha);
			ScissorStack.popScissors();
		}
		batch.flush();
		
		if(fade != null && needsScrollY() && scrollContainer.getY() > areaBounds.y + areaBounds.height - scrollContainer.getHeight())
			batch.draw(fade, areaBounds.x, areaBounds.y + areaBounds.height - fade.getRegionHeight(), areaBounds.width, fade.getRegionHeight());
		
		resetTransform(batch);
	}
	
	public void setFade(TextureRegion region)
	{
		this.fade = region;
	}
	
	public boolean isScrollX()
	{
		return scrollX;
	}
	
	public void setScrollX(boolean scrollX)
	{
		this.scrollX = scrollX;
	}
	
	public boolean isScrollY()
	{
		return scrollY;
	}
	
	public void setScrollY(boolean scrollY)
	{
		this.scrollY = scrollY;
	}
	
	private boolean needsScrollX()
	{
		return scrollX && scrollContainer.getWidth() > areaBounds.width;
	}
	
	private boolean needsScrollY()
	{
		return scrollY && scrollContainer.getHeight() > areaBounds.height;
	}
}
