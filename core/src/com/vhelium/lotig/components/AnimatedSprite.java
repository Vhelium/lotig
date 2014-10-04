package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;

public class AnimatedSprite extends Group
{
	private TiledTextureRegion tiledTextureRegion;
	private boolean animationRunning;
	private int currentTileIndex;
	private float animationProgress;
	private float frameTime;
	private boolean isFlipped = false;
	private boolean loop = false;
	private IAnimationListener listener;
	private int frameStart, frameEnd;
	
	public AnimatedSprite(float x, float y, TiledTextureRegion tiledTextureRegion)
	{
		this(x, y, tiledTextureRegion.getTileWidth(), tiledTextureRegion.getTileHeight(), tiledTextureRegion);
	}
	
	public AnimatedSprite(float x, float y, float width, float height, TiledTextureRegion tiledTextureRegion)
	{
		this.tiledTextureRegion = tiledTextureRegion;
		this.setBounds(x, y, width, height);
		updateFlip();
	}
	
	@Override
	public void act(float delta)
	{
		final float millis = delta * 1000f;
		if(animationRunning)
		{
			animationProgress += millis;
			if(animationProgress >= frameTime)
			{
				animationProgress -= frameTime;
				if(currentTileIndex >= frameEnd)
				{
					if(!loop)
					{
						currentTileIndex = frameEnd;
						onAnimationFinished();
					}
					else
						currentTileIndex = frameStart;
				}
				else
					currentTileIndex++;
			}
		}
		super.act(delta);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
		batch.draw(tiledTextureRegion.getTextureRegion(currentTileIndex), this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation());
		super.draw(batch, parentAlpha);
	}
	
	public void animate(int frameTime)
	{
		animate(frameTime, true);
	}
	
	public void animate(float frameTime, boolean loop)
	{
		animate(frameTime, loop, null);
	}
	
	public void animate(float frameTime, int startFrame, int endFrame, boolean loop)
	{
		animate(frameTime, startFrame, endFrame, loop, null);
	}
	
	public void animate(float frameTime, boolean loop, IAnimationListener listener)
	{
		animate(frameTime, 0, tiledTextureRegion.getTileCount() - 1, loop, listener);
	}
	
	public void animate(float frameTime, int startFrame, int endFrame, boolean loop, IAnimationListener listener)
	{
		animationRunning = true;
		animationProgress = 0;
		this.frameTime = frameTime;
		this.frameStart = startFrame;
		this.currentTileIndex = startFrame;
		this.frameEnd = endFrame;
		this.loop = loop;
		this.listener = listener;
	}
	
	public void stopAnimation(int frameIndex)
	{
		animationRunning = false;
		currentTileIndex = frameIndex;
	}
	
	private void onAnimationFinished()
	{
		animationRunning = false;
		animationProgress = 0;
		if(listener != null)
			listener.onAnimationFinished(this);
	}
	
	public boolean isAnimationRunning()
	{
		return animationRunning;
	}
	
	public void setTextureRegion(TiledTextureRegion textureRegion)
	{
		tiledTextureRegion = textureRegion;
		updateFlip();
	}
	
	private void updateFlip()
	{
		for(TextureRegion reg : tiledTextureRegion.getRegions())
			reg.flip(isFlipped != reg.isFlipX(), false);
	}
	
	public void setFlippedHorizontal(boolean flip)
	{
		if(isFlipped != flip)
		{
			isFlipped = flip;
			for(TextureRegion reg : tiledTextureRegion.getRegions())
				reg.flip(true, false);
		}
	}
}
