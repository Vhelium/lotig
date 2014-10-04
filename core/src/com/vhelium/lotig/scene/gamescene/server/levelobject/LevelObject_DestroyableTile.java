package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_DestroyableTile extends LevelObject
{
	public static final String NAME = "DstrT";
	
	public LevelObject_DestroyableTile(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("animated"))
			animated = Boolean.parseBoolean(tmxObjectProperties.get("animated", String.class));
		if(tmxObjectProperties.containsKey("asset"))
			asset = tmxObjectProperties.get("asset", String.class);
		if(tmxObjectProperties.containsKey("collision"))
			collision = Boolean.parseBoolean(tmxObjectProperties.get("collision", String.class));
		if(tmxObjectProperties.containsKey("hp"))
			hp = Integer.parseInt(tmxObjectProperties.get("hp", String.class));
		
		this.maxHp = hp;
	}
	
	public LevelObject_DestroyableTile(int id, float x, float y, int w, int h, String asset, boolean animated, boolean collision, int hp)
	{
		this.id = id;
		this.asset = asset;
		this.animated = animated;
		this.collision = collision;
		this.maxHp = hp;
		this.hp = hp;
		if(animated)
		{
			sprite = new AnimatedSprite(x, y, w, h, new TiledTextureRegion(GameHelper.getInstance().getGameAsset(asset), 1, 2, true));
			((AnimatedSprite) sprite).animate(500);//TODO: animation time
		}
		else
			sprite = new Sprite(x, y, w, h, GameHelper.getInstance().getGameAsset(asset));
		
		bounds = new Rectangle(x, y, w, h);
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + asset + ";" + animated + ";" + collision + ";" + hp;
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
