package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Shrine extends LevelObject
{
	public static final String NAME = "Shrn";
	private String soundEffect;
	private Condition condition;
	private final String buffType;
	private final float range = 100f;
	
	public LevelObject_Shrine(int id, Realm realm, Rectangle rectangle, MapProperties props)//Server
	{
		this.id = id;
		this.bounds = new Rectangle(rectangle);
		this.buffType = getRandomBuffType();
		this.state = 1;
		
		if(buffType.equals("shrine_hpreg"))
		{
			condition = new Condition("shrine", "HPREG", 100, 15000, true, buffType, 0);
		}
		else if(buffType.equals("shrine_manareg"))
		{
			condition = new Condition("shrine", "MANAREG", 50, 15000, true, buffType, 0);
		}
		else if(buffType.equals("shrine_atk"))
		{
			condition = new Condition("shrine", "DMGPERCENT", 40, 15000, true, buffType, 0);
		}
		else if(buffType.equals("shrine_def"))
		{
			condition = new Condition("shrine", "ARMOR", 500, 15000, true, buffType, 0);
		}
		else if(buffType.equals("shrine_spd"))
		{
			condition = new Condition("shrine", "SPD", 650, 15000, true, buffType, 0);
		}
		else if(buffType.equals("shrine_atkspd"))
		{
			condition = new Condition("shrine", "AS", 250, 15000, true, buffType, 0);
		}
		
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(realm, rectangle, properties);
	}
	
	public LevelObject_Shrine(int id, float x, float y, int w, int h, String type, int state)//Client
	{
		this.id = id;
		this.asset = type;
		this.buffType = type;
		this.state = state;
		
		TiledTextureRegion txt = GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 3, 1);
		sprite = new AnimatedSprite(x + w / 2 - txt.getTileWidth() / 2, y + h - txt.getTileHeight(), txt.getTileWidth(), txt.getTileHeight(), txt);
		stateChangedClient(state);
		bounds = new Rectangle(x, y, w, h);
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + buffType + ";" + state;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		EntityServerMixin entity = realm.getEntity(entityId);
		
		if(entity != null && state == 1)
		{
			this.setStateAndInform(0, realm);
//			realm.playEffect(this.getOriginX(), this.getOriginY(), range * 2, range * 2, buffType, 0);
//			realm.playSound(SoundFile.shrine, this.getOriginX(), this.getOriginY());
			for(PlayerServer player : realm.getPlayers().values())
				if(Math.abs(player.getOriginX() - getOriginX()) <= range && Math.abs(player.getOriginY() - getOriginY()) <= range)
					realm.requestCondition(player.Nr, condition.getName(), condition.getValueName(), condition.getValue(), condition.getDuration(), condition.isAttribute(), condition.getEffect(), System.currentTimeMillis());
		}
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	private final int[] frames = new int[] { 0, 1 };
	
	@Override
	public void stateChangedClient(int state)
	{
		if(sprite != null)
		{
			if(state == 0)
				((AnimatedSprite) sprite).stopAnimationAt(2);
			else
				((AnimatedSprite) sprite).animate(222, frames, true);
		}
	}
	
	private static String getRandomBuffType()
	{
		int r = GameHelper.$.getRandom().nextInt(6);
		switch(r)
		{
			case 0:
				return "shrine_hpreg";
			case 1:
				return "shrine_manareg";
			case 2:
				return "shrine_atk";
			case 3:
				return "shrine_def";
			case 4:
				return "shrine_spd";
			case 5:
				return "shrine_atkspd";
			default:
				return "shrine_hpreg";
		}
	}
	
	@Override
	public void setId(int id)
	{
		super.setId(id);
		MapProperties properties = new MapProperties();
		properties.put("levelObjectTrigger", String.valueOf(id));
		event = new OnEnterEvent(event.realm, event.rectangle, properties);
	}
}
