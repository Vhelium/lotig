package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.vhelium.lotig.constants.Log;

public class AnimatedSprite extends Group
{
	public boolean debug = false;
	private TiledTextureRegion tiledTextureRegion;
	private boolean animationRunning;
	private int currentAnimationIndex;
	private float animationProgress;
	private float frameTime;
	private boolean isFlipped = false;
	private boolean loop = false;
	private IAnimationListener listener;
	private int[] frames;
	
	public AnimatedSprite(float x, float y, TiledTextureRegion tiledTextureRegion)
	{
		this(x, y, tiledTextureRegion.getTileWidth(), tiledTextureRegion.getTileHeight(), tiledTextureRegion);
	}
	
	public AnimatedSprite(float x, float y, float width, float height, TiledTextureRegion tiledTextureRegion)
	{
		this.tiledTextureRegion = tiledTextureRegion;
		this.setBounds(x, y, width, height);
		currentAnimationIndex = 0;
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
				if(currentAnimationIndex >= frames.length - 1)
				{
					if(!loop)
					{
						currentAnimationIndex = frames.length - 1;
						onAnimationFinished();
					}
					else
						currentAnimationIndex = 0;
				}
				else
					currentAnimationIndex++;
			}
		}
		super.act(delta);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
		if(debug)
			Log.e("animatedsprite", "i: " + currentAnimationIndex + ",  frame: " + frames[currentAnimationIndex] + " --- progress: " + animationProgress);
		batch.draw(tiledTextureRegion.getTextureRegion(frames[currentAnimationIndex]), this.getX(), this.getY(), this.getOriginX(), this.getOriginY(), this.getWidth(), this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation());
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
	
	public void animate(float frameTime, int[] frames, boolean loop)
	{
		animate(frameTime, frames, loop, null);
	}
	
	public void animate(float frameTime, boolean loop, IAnimationListener listener)
	{
		animate(frameTime, getFrameDefaults(), loop, listener);
	}
	
	public void animate(float frameTime, int[] frames, boolean loop, IAnimationListener listener)
	{
		animationRunning = true;
		animationProgress = 0;
		this.frameTime = frameTime;
		this.frames = frames;
		this.currentAnimationIndex = 0;
		this.loop = loop;
		this.listener = listener;
	}
	
	public void stopAnimationAt(int frameIndex)
	{
		animationRunning = false;
		currentAnimationIndex = 0;
		animationProgress = 0;
		frames = new int[] { frameIndex };
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
	
	private int[] frameDefaults;
	
	private int[] getFrameDefaults()
	{
		if(frameDefaults == null)
		{
			frameDefaults = new int[tiledTextureRegion.getTileCount()];
			for(int i = 0; i < tiledTextureRegion.getTileCount(); i++)
				frameDefaults[i] = i;
		}
		return frameDefaults;
	}
}
