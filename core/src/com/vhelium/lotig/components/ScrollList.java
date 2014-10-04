package com.vhelium.lotig.components;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class ScrollList extends Sprite
{
	final int MIN_FLING_DIST = 60;
	final float MIN_PIXEL_PER_MILLIS = 0.5f;//.65f
	float heighDifY;
	
	Main activity;
	
	OrthographicCamera camera;
	
	Color colorNormal = new Color(210 / 255f, 210 / 255f, 210 / 255f, 1);
	Color colorUnusable = new Color(110 / 255f, 110 / 255f, 110 / 255f, 1);
	
	float factX;
	float factY;
	
	int absX;
	int absY;
	int absWidth;
	int absHeight;
	
	// How far the user is allowed to drag past the "real" border
	final float springMaxDrag = 400;
	
	// How far the display moves when dragged to 'SpringMaxDrag'
	final float springMaxOffset = springMaxDrag / 3;
	
	final float springReturnRate = 0.1f;
	final float springReturnMin = 2.0f;
	final float deceleration = 500.0f; // pixels/second^2
	final float maxVelocity = 2000.0f; // pixels/second
	
	float velocityY;
	
	float currentPosX;
	float currentPosY;
	
	float unclampedViewOriginX;
	float unclampedViewOriginY;
	
	float offsetY = 0;
	float offsetX = 0;
	
	TextureRegion textureRegionItem;
	BitmapFont itemFont;
	int itemHeight = 38;
	
	Group scrollContainer;
	List<ScrollListItem> listItems;
	Label txtEmpty;
	
	int trackFingerID = -1;
	
	private float getTrackHeight()
	{
		return listItems.size() * (itemHeight - 1 + offsetY) + offsetY;
	}
	
	public ScrollList(Main activity, float x, float y, int width, int height, TextureRegion textureRegionBackground, TextureRegion textureRegionItem, BitmapFont itemFont, OrthographicCamera camera)
	{
		super(x, y, width, height, textureRegionBackground);
		
		this.activity = activity;
		this.camera = camera;
		
		listItems = new ArrayList<ScrollListItem>();
		this.textureRegionItem = textureRegionItem;
		this.itemFont = itemFont;
		
		factX = GameHelper.getInstance().getScaleFactorX();
		factY = GameHelper.getInstance().getScaleFactorY();
		
		absWidth = Math.round(width * factX);
		absHeight = Math.round(height * factY);
		
		setX(x);
		setY(y);
		
		currentPosX = offsetX;
		currentPosY = this.getY();
		
		scrollContainer = new Group();
		scrollContainer.setZIndex(0);
		this.addActor(scrollContainer);
		
		this.addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				getStage().setScrollFocus(ScrollList.this);
				if(this.contains(event.getStageX(), event.getStageY()))
				{
					trackFingerID = pointer;
					prevX = event.getStageX();
					prevY = event.getStageY();
					downX = prevX;
					downY = prevY;
					millisWhenDown = System.currentTimeMillis();
					unclampedViewOriginX = currentPosX;
					unclampedViewOriginY = currentPosY;
					velocityY = 0;
					return true;
				}
				return false;
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer)
			{
				if(pointer == trackFingerID)
				{
					float delta = event.getStageY() - prevY;
					unclampedViewOriginY += delta;
					prevY = event.getStageY();
				}
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				if(button != -1 && trackFingerID != -1 && pointer == trackFingerID)
				{
					if(Math.abs(downX - event.getStageX()) < 6 && Math.abs(downY - event.getStageY()) < 6)
					{
						itemClicked(event.getStageX(), event.getStageY());
					}
					//pixel / elapsed >= minVel ++ X > minDist && X < Y
					else
						checkForFlick(event);
					trackFingerID = -1;
				}
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
			{
				touchUp(event, x, y, pointer, -1);
			}
			
			private boolean contains(float stageX, float stageY)
			{
				return stageX >= getX() && stageX < getX() + getWidth() && stageY >= getY() && stageY < getY() + getHeight();
			}
		});
		
		txtEmpty = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 16), colorUnusable));
		txtEmpty.setZIndex(0);
		txtEmpty.setAlignment(Align.top | Align.left);
		this.addActor(txtEmpty);
	}
	
	public void setContent(final List<String> items, String emptyText)
	{
		scrollContainer.clearChildren();
		listItems.clear();
		float y = offsetY;
		if(items.size() > 0)
			for(int i = 0; i < items.size(); i++)
			{
				Color color;
				String text = items.get(i);
				if(text.charAt(0) == '#')
				{
					text = text.substring(1);
					color = colorUnusable;
				}
				else
					color = colorNormal;
				
				ScrollListItem item = new ScrollListItem(i, offsetX, y, ScrollList.this.getWidth() - offsetX * 2, itemHeight, text, color, textureRegionItem, itemFont);
				y += itemHeight - 1 + offsetY;
				listItems.add(item);
				scrollContainer.addActor(item);
			}
		else if(emptyText != null)
		{
			txtEmpty.setVisible(true);
			txtEmpty.setText(emptyText);
			txtEmpty.pack();
			txtEmpty.setPosition(getWidth() / 2 - txtEmpty.getWidth() / 2, 10);
		}
		else
		{
			txtEmpty.setVisible(false);
			txtEmpty.setText("");
			txtEmpty.pack();
			txtEmpty.setPosition(getWidth() / 2 - txtEmpty.getWidth() / 2, 10);
		}
	}
	
	public void resetPosition()
	{
		//reset position
		currentPosY = ScrollList.this.getY();
		velocityY = 0;
		unclampedViewOriginY = 0;
	}
	
	@Override
	public void act(float delta)
	{
		if(getTrackHeight() <= this.getHeight())
		{
			currentPosY = this.getY();
		}
		else if(trackFingerID != -1)
		{
			currentPosY = softClamp(unclampedViewOriginY);
		}
		else
		{
			// Apply Velocity
			applyVelocity(delta);
		}
		scrollContainer.setY(currentPosY - this.getY());
	}
	
	float prevX;
	float prevY;
	float downX;
	float downY;
	long millisWhenDown;
	
//	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY)
//	{
//		if(trackFingerID != -1 && !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY()))
//		{
//			checkForFlick(pSceneTouchEvent);
//			trackFingerID = -1;
//			return true;
//		}
//		else
//			switch(pSceneTouchEvent.getAction())
//			{
//				case TouchEvent.ACTION_DOWN:
//					trackFingerID = pSceneTouchEvent.getPointerID();
//					prevX = pSceneTouchEvent.getX();
//					prevY = pSceneTouchEvent.getY();
//					downX = prevX;
//					downY = prevY;
//					millisWhenDown = System.currentTimeMillis();
//					unclampedViewOriginX = currentPosX;
//					unclampedViewOriginY = currentPosY;
//					velocityY = 0;
//					return true;
//				case TouchEvent.ACTION_MOVE:
//					if(pSceneTouchEvent.getPointerID() == trackFingerID)
//					{
//						unclampedViewOriginY += pSceneTouchEvent.getY() - prevY;
//						prevY = pSceneTouchEvent.getY();
//					}
//					return true;
//				case TouchEvent.ACTION_UP:
//				case TouchEvent.ACTION_OUTSIDE:
//				case TouchEvent.ACTION_CANCEL:
//					if(trackFingerID != -1 && Math.abs(downX - pSceneTouchEvent.getX()) < 6 && Math.abs(downY - pSceneTouchEvent.getY()) < 6)
//					{
//						itemClicked(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
//					}
//					//pixel / elapsed >= minVel ++ X > minDist && X < Y
//					else if(trackFingerID != -1)
//						checkForFlick(pSceneTouchEvent);
//					trackFingerID = -1;
//					return true;
//				default:
//					return false;
//			}
//	}
	
	private void checkForFlick(InputEvent pSceneTouchEvent)
	{
		if(Math.abs(downY - pSceneTouchEvent.getStageY()) / (System.currentTimeMillis() - millisWhenDown) >= MIN_PIXEL_PER_MILLIS && Math.abs(downY - pSceneTouchEvent.getStageY()) >= MIN_FLING_DIST && Math.abs(downX - pSceneTouchEvent.getStageX()) < Math.abs(downY - pSceneTouchEvent.getStageY()))
		{
			velocityY = (downY - pSceneTouchEvent.getStageY()) * -3;
		}
	}
	
	private void itemClicked(float x, float y)
	{
		x -= getX() + scrollContainer.getX();
		y -= getY() + scrollContainer.getY();
		for(int i = 0; i < listItems.size(); i++)
		{
			if(listItems.get(i).contains(x, y))
			{
				onClick(listItems.get(i).getId());
			}
		}
	}
	
	public void onClick(int id)
	{
		//Override
	}
	
	public void highlightItem(int id)
	{
		listItems.get(id).setHighlighted(true);
	}
	
	public void unHighlightItem(int id)
	{
		if(listItems.size() > id)
			listItems.get(id).setHighlighted(false);
	}
	
	public void changeItemContent(int id, String content)
	{
		listItems.get(id).setText(content);
	}
	
	private float softClamp(float y)
	{
		float min = this.getY() + this.getHeight() - getTrackHeight();
		float max = this.getY();
		
		if(y < min)
		{
			return Math.max(y - min, -springMaxDrag) * springMaxOffset / springMaxDrag + min;
		}
		if(y > max)
		{
			return Math.min(y - max, springMaxDrag) * springMaxOffset / springMaxDrag + max;
		}
		return y;
	}
	
	private void applyVelocity(float elapsed)
	{
		float min = this.getY() + this.getHeight() - getTrackHeight();
		float max = this.getY();
		
		currentPosY += velocityY * elapsed;
		
		// Apply deceleration to gradually reduce velocity
		velocityY = MathUtils.clamp(velocityY, -maxVelocity, maxVelocity);
		velocityY = Math.max(Math.abs(velocityY) - elapsed * deceleration, 0f) * Math.signum(velocityY);
		
		if(currentPosY < min)
		{
			currentPosY = Math.min(currentPosY + (min - currentPosY) * springReturnRate + springReturnMin, min);
			velocityY = 0f;
		}
		if(currentPosY > max)
		{
			currentPosY = Math.max(currentPosY - (currentPosY - max) * springReturnRate - springReturnMin, max);
			velocityY = 0f;
		}
	}
	
//	@Override
//	public void draw(Batch batch, float alpha)
//	{
//		batch.flush();
//		scrollContainer.setY(currentPosY - this.getY());
//		
//		com.badlogic.gdx.math.Rectangle scissors = new com.badlogic.gdx.math.Rectangle();//TODO: 2 fucking new fucking objects per fucking call!!! 2 !!!!!11!!!!!!11111!!!!eins!!elf!!!!!111!!
//		com.badlogic.gdx.math.Rectangle clipBounds = new com.badlogic.gdx.math.Rectangle(getX(), getY(), getWidth(), getHeight());
//		Log.e("scrollist", "x: " + clipBounds.x + ", y: " + clipBounds.y + ", w: " + clipBounds.width + ", h: " + clipBounds.height);
//		ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
//		if(ScissorStack.pushScissors(scissors))
//		{
//			super.draw(batch, alpha);
//			batch.flush();
//			ScissorStack.popScissors();
//		}
//	}
	
	@Override
	public void draw(Batch batch, float alpha)
	{
		batch.flush();
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor(absX, absY, absWidth, absHeight);
		super.draw(batch, alpha);
		batch.flush();
		Gdx.gl.glScissor(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
	}
	
	@Override
	public void setX(final float pX)
	{
		super.setX(pX);
		
		absX = Math.round(pX * factX);
	}
	
	@Override
	public void setY(final float pY)
	{
		super.setY(pY);
		
		absY = Math.round((SceneManager.CAMERA_HEIGHT - pY - this.getHeight()) * factY);
	}
	
	public BitmapFont getFont()
	{
		return itemFont;
	}
	
	public int getElementTextWidth()
	{
		return (int) (ScrollList.this.getWidth() - ScrollListItem.gapX * 1.5f);
	}
}
