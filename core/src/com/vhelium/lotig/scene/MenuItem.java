package com.vhelium.lotig.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.vhelium.lotig.components.TextButton;

public class MenuItem extends TextButton
{
	IOnMenuItemClickListener listener;
	int id, tag;
	
	public MenuItem(int id, int tag, BitmapFont font, CharSequence text, Color colorNormal, Color colorPressed)
	{
		super(0, 0, font, text, colorNormal, colorPressed, null);
		this.id = id;
		this.tag = tag;
	}
	
	private MenuItem(int id)
	{
		super(null, null);
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getTag()
	{
		return tag;
	}
	
	public static MenuItem getFakeMenuItem(int id)
	{
		return new MenuItem(id);
	}
}
