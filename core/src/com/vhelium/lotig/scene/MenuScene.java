package com.vhelium.lotig.scene;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScene extends Group
{
	private static final int offsetY = 4;
	
	IOnMenuItemClickListener listener;
	List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuScene(IOnMenuItemClickListener listener)
	{
		this.listener = listener;
	}
	
	public void addMenuItem(int id, BitmapFont font, CharSequence text, Color normal, Color pressed)
	{
		addMenuItem(id, -1, font, text, normal, pressed);
	}
	
	public void addMenuItem(int id, int tag, BitmapFont font, CharSequence text, Color normal, Color pressed)
	{
		final MenuItem item = new MenuItem(id, tag, font, text, normal, pressed);
		item.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				listener.onMenuItemClicked(item);
			}
		});
		items.add(item);
	}
	
	public void build()
	{
		float y = 0;
		for(int i = 0; i < items.size(); i++)
		{
			MenuItem item = items.get(i);
			item.getLabel().clearActions();
			item.pack();
			item.setX(SceneManager.CAMERA_WIDTH / 2 - item.getWidth() / 2);
			item.setY(y);
			y += offsetY + item.getHeight();
			item.getLabel().getColor().a = 0f;
			item.getLabel().addAction(Actions.alpha(1f, 0.5f));
			this.addActor(item);
		}
		
		float offsetTotalY = (y - offsetY) / 2;
		for(int i = 0; i < items.size(); i++)
		{
			items.get(i).setY(SceneManager.CAMERA_HEIGHT / 2 + items.get(i).getY() - offsetTotalY);
		}
	}
	
	public void reset()
	{
		for(int i = 0; i < items.size(); i++)
		{
			MenuItem item = items.get(i);
			item.getLabel().clearActions();
			item.getLabel().getColor().a = 0f;
			item.getLabel().addAction(Actions.alpha(1f, 0.5f));
		}
	}
}
