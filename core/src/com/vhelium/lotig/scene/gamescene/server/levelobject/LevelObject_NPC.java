package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_NPC extends LevelObject
{
	static final String NAME = "npc";
	private int dialogueId;
	public int uniqueId = -1;
	public boolean doRemove = false;
	private float removeCountdown = 0f;
	private int animationTime;
	private boolean triggered = false;
	private static final int triggerRange = 200;
	
	public LevelObject_NPC(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)//server
	{
		state = 0;
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("animated"))
			animated = Boolean.parseBoolean(tmxObjectProperties.get("animated", String.class));
		if(tmxObjectProperties.containsKey("animationTime"))
			animationTime = Integer.parseInt(tmxObjectProperties.get("animationTime", String.class));
		if(tmxObjectProperties.containsKey("asset"))
			asset = tmxObjectProperties.get("asset", String.class);
		if(tmxObjectProperties.containsKey("dialogueId"))
			dialogueId = Integer.parseInt(tmxObjectProperties.get("dialogueId", String.class));
		if(tmxObjectProperties.containsKey("dialogue"))
			dialogueId = Integer.parseInt(tmxObjectProperties.get("dialogue", String.class));
		if(tmxObjectProperties.containsKey("uniqueId"))
			uniqueId = Integer.parseInt(tmxObjectProperties.get("uniqueId", String.class));
		if(tmxObjectProperties.containsKey("doRemove"))
			doRemove = Boolean.parseBoolean(tmxObjectProperties.get("doRemove", String.class));;
	}
	
	public LevelObject_NPC(int id, float x, float y, int w, int h, String asset, boolean animated, int animationTime, int dialogueId, int uniqueId)//client
	{
		state = 0;
		this.id = id;
		this.uniqueId = uniqueId;
		this.asset = asset;
		this.animated = animated;
		this.animationTime = animationTime;
		this.dialogueId = dialogueId;
		bounds = new Rectangle(x, y, w, h);
		TiledTextureRegion txt = GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, animated ? 2 : 1, 1);
		sprite = new AnimatedSprite(x + w / 2 - txt.getTileWidth() / 2, y + h / 2 - txt.getTileHeight() / 2, txt.getTileWidth(), txt.getTileHeight(), txt);
		((AnimatedSprite) sprite).stopAnimationAt(0);
		if(animated)
			((AnimatedSprite) sprite).animate(animationTime);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		if(removeCountdown > 0)
		{
			removeCountdown -= delta;
			if(removeCountdown <= 0)
				realm.removeLevelObject(this);
		}
	}
	
	@Override
	public void updateClient(float delta, PlayerClient player)
	{
		if(!triggered)
		{
			if(Math.pow(player.getOriginX() - (bounds.getX() + bounds.getWidth() / 2), 2) + Math.pow(player.getOriginY() - (bounds.getY() + bounds.getWidth() / 2), 2) <= triggerRange * triggerRange)
			{
				player.getLevel().onDialogue(bounds.getX() + bounds.getWidth() / 2, bounds.getY() - 2, dialogueId, this);
				triggered = true;
			}
		}
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + animated + ";" + animationTime + ";" + dialogueId + ";" + uniqueId;
	}
	
	public void setRemoveFlagTrue()
	{
		removeCountdown = 1200f;
		dying = true;
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		if(state == 1 && sprite != null)
		{
			//fade out
			sprite.addAction(Actions.alpha(0f, 1f));
		}
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
}
