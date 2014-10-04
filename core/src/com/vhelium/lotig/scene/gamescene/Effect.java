package com.vhelium.lotig.scene.gamescene;

import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.IAnimationListener;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.client.EntityClient;
import com.vhelium.lotig.scene.gamescene.client.EntityClientMixin;

public class Effect extends AnimatedSprite
{
	EffectAutoRemover autoRemover;
	
	public Effect(float pX, float pY, TiledTextureRegion pTiledTextureRegion)
	{
		super(pX, pY, pTiledTextureRegion);
	}
	
	public Effect(float pX, float pY, float pWidth, float pHeight, TiledTextureRegion pTiledTextureRegion)
	{
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion);
	}
	
	public Effect(float pX, float pY, float scaleFactor, TiledTextureRegion pTiledTextureRegion)
	{
		super(pX, pY, pTiledTextureRegion.getTileWidth() * scaleFactor, pTiledTextureRegion.getTileHeight() * scaleFactor, pTiledTextureRegion);
	}
	
	public Effect(EntityClientMixin owner, TiledTextureRegion pTiledTextureRegion)
	{
		super(owner.getSprite().getWidth() / 2 - pTiledTextureRegion.getTileWidth() / 2, owner.getSprite().getHeight() / 2 - pTiledTextureRegion.getTileHeight() / 2, pTiledTextureRegion);
	}
	
	public Effect(EntityClientMixin owner, float pWidth, float pHeight, TiledTextureRegion pTiledTextureRegion)
	{
		super(owner.getSprite().getWidth() / 2 - pWidth / 2, owner.getSprite().getHeight() / 2 - pHeight / 2, pWidth, pHeight, pTiledTextureRegion);
	}
	
	public Effect(EntityClientMixin owner, float scaleFactor, TiledTextureRegion pTiledTextureRegion)
	{
		super(owner.getSprite().getWidth() / 2 - pTiledTextureRegion.getTileWidth() * scaleFactor / 2, owner.getSprite().getHeight() / 2 - pTiledTextureRegion.getTileHeight() * scaleFactor / 2, pTiledTextureRegion.getTileWidth() * scaleFactor, pTiledTextureRegion.getTileHeight() * scaleFactor, pTiledTextureRegion);
	}
	
	public static IAnimationListener getAnimationListener()
	{
		return new IAnimationListener()
		{
			@Override
			public void onAnimationFinished(final AnimatedSprite arg0)
			{
				arg0.remove();
			}
		};
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		if(autoRemover != null)
			autoRemover.update(delta);
	}
	
	public void setAutoRemove(final EntityClient owner, final String name, final int duration, final long absStartTime, final float timeDifferenceServer)
	{
		autoRemover = new EffectAutoRemover(this, owner, name, duration, absStartTime, timeDifferenceServer);
	}
	
	private class EffectAutoRemover
	{
		Effect effect;
		EntityClient owner;
		String name;
		float timeLeft;
		
		public EffectAutoRemover(Effect effect, EntityClient owner, String name, int duration, long absStartTime, float timeDifferenceServer)
		{
			this.effect = effect;
			this.owner = owner;
			this.name = name;
			
			timeLeft = duration - (System.currentTimeMillis() + timeDifferenceServer - absStartTime);
		}
		
		public void update(float delta)
		{
			timeLeft -= delta * 1000;
			if(timeLeft <= 0)
			{
				if(owner != null && !name.equals(""))
					owner.conditionEffects.remove(name);
				effect.remove();
			}
		}
	}
}
