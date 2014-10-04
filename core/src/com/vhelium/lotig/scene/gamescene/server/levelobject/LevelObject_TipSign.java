package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.ToolTip;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_TipSign extends LevelObject
{
	public static final String NAME = "TipS";
	private String text;
	private ToolTip toolTip;
	
	public LevelObject_TipSign(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("text"))
		{
			text = tmxObjectProperties.get("text", String.class);
		}
	}
	
	public LevelObject_TipSign(int id, float x, float y, int w, int h, String text)
	{
		this.id = id;
		this.text = text;
		
		TextureRegion tr = GameHelper.getInstance().getGameAsset("TipSign");
		sprite = new Sprite(x + w / 2 - tr.getRegionWidth() / 2, y + h - tr.getRegionHeight(), tr.getRegionWidth(), tr.getRegionHeight(), tr);
		bounds = new Rectangle(x, y, w, h);
		
		toolTip = new ToolTip(sprite.getWidth() / 2, -10, text);
		
		MapProperties properties = new MapProperties();
		properties.put("showToolTip", String.valueOf(id));
		event = new OnEnterEvent(null, bounds, properties)
		{
			@Override
			protected void onEnter(PlayerClient player)
			{
				toolTip.show(sprite);
			}
			
			@Override
			protected void onLeft(PlayerClient player)
			{
				toolTip.hide();
			}
		};
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + text;
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