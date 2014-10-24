package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.runnable.PlayerRunnable;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Vortex extends LevelObject
{
	public static final String NAME = "Vrx";
	
	private float strengthFactor = 0.12f;
	private boolean rotation = true;
	
	public LevelObject_Vortex(final int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		this.state = 0;
		
		if(tmxObjectProperties.containsKey("asset"))
			asset = tmxObjectProperties.get("asset", String.class);
		if(tmxObjectProperties.containsKey("strengthFactor"))
			strengthFactor = Float.parseFloat(tmxObjectProperties.get("strengthFactor", String.class));
		if(tmxObjectProperties.containsKey("rotation"))
			rotation = Boolean.parseBoolean(tmxObjectProperties.get("rotation", String.class));
		
		event = new AreaUpdateEvent(realm, bounds, new PlayerRunnable()
		{
			@Override
			public void updateEnemy(float delta, EntityServer enemy)
			{
				if(!enemy.isDead())
				{
					if(Math.abs(enemy.getOriginX() - getOriginX()) > 12 || Math.abs(enemy.getOriginY() - getOriginY()) > 12)
					{
						float v = (float) Math.sqrt(Math.pow(enemy.getOriginX() - getOriginX(), 2) + Math.pow(enemy.getOriginY() - getOriginY(), 2));
						float directionX = (1 / v) * (getOriginX() - enemy.getOriginX());
						float directionY = (1 / v) * (getOriginY() - enemy.getOriginY());
						enemy.addMovementModifier("Vortex" + id, directionX, directionY, strengthFactor, 0.9f);
					}
					else
					{
						enemy.removeMovementModifier("Vortex" + id);
						enemy.setHp(-enemy.getMaxHp());
					}
				}
			}
		}, 40f);
		event.entityCheck = true;
	}
	
	public LevelObject_Vortex(final int id, float x, float y, int w, int h, String asset, boolean rotation)
	{
		this.id = id;
		this.asset = asset;
		this.rotation = rotation;
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 1, 1));
		sprite.setOrigin(w / 2, h / 2);
		((AnimatedSprite) sprite).stopAnimationAt(0);
		if(rotation)
			sprite.addAction(Actions.forever(Actions.rotateBy(360, 2f)));
		bounds = new Rectangle(x, y, w, h);
		
		stateChangedClient(state);
		
		event = new AreaUpdateEvent(null, bounds, new PlayerRunnable()
		{
			@Override
			public void updateClient(float delta, PlayerClient player)
			{
				if(!player.isDead())
				{
					if(Math.abs(player.getOriginX() - getOriginX()) > 12 || Math.abs(player.getOriginY() - getOriginY()) > 12)
					{
						float v = (float) Math.sqrt(Math.pow(player.getOriginX() - getOriginX(), 2) + Math.pow(player.getOriginY() - getOriginY(), 2));
						float directionX = (1 / v) * (getOriginX() - player.getOriginX());
						float directionY = (1 / v) * (getOriginY() - player.getOriginY());
						player.addMovementModifier("Vortex" + id, directionX, directionY, strengthFactor, 0.91f);
					}
					else
					{
						player.removeMovementModifier("Vortex" + id);
						player.die();
					}
				}
			}
		}, 40f);
		event.entityCheck = true;
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + rotation;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
}