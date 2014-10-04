package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Font extends LevelObject
{
	public static final String NAME = "Font";
	String text = "";
	
	public LevelObject_Font(int id, Realm realm, Rectangle rectangle, MapProperties tmxObjectProperties)
	{
		this.id = id;
		this.bounds = rectangle;
		
		if(tmxObjectProperties.containsKey("text"))
			text = tmxObjectProperties.get("text", String.class);
	}
	
	public LevelObject_Font(int id, float x, float y, int w, int h, String text)
	{
		this.id = id;
		this.text = text;
		
		Label levelText = new Label(text, new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), Color.WHITE));
		levelText.setAlignment(Align.top | Align.left);
		bounds = new Rectangle(x + w / 2 - levelText.getWidth() / 2, y + h / 2 - levelText.getHeight() / 2, levelText.getWidth(), levelText.getHeight());
		sprite = new Group();
		sprite.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		sprite.addActor(levelText);
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + text;
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
}
