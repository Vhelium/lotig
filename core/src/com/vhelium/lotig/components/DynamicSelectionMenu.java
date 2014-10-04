package com.vhelium.lotig.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.gamescene.SoundFile;

public class DynamicSelectionMenu extends Group
{
	Main activity;
	TextureRegion textureRegionItem;
	BitmapFont menuFont;
	List<TextButton> buttons;
	String selectedButton = null;
	TextButtonComparor comparor = new TextButtonComparor();
	
	public DynamicSelectionMenu(Main activity, float x, float y, TextureRegion textureRegionItem, BitmapFont menuFont)
	{
		super();
		this.setPosition(x, y);
		
		this.activity = activity;
		this.textureRegionItem = textureRegionItem;
		this.menuFont = menuFont;
		buttons = new ArrayList<TextButton>();
	}
	
	public void addItem(final String text, final boolean sort)
	{
		final TextButton cmd = new TextButton(0, 0 + buttons.size() * textureRegionItem.getRegionHeight(), textureRegionItem, menuFont, text, new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				for(TextButton txt : buttons)
				{
					txt.setHighlight(false);
				}
				((TextButton) actor).setHighlight(true);
				selectedButton = text;
				onButtonClicked(text);
			}
		});
		buttons.add(cmd);
		this.addActor(cmd);
		if(sort)
			sortItems();
	}
	
	public void removeItem(final String text, final boolean sort)
	{
		TextButton buttonToRemove = null;
		for(TextButton cmd : buttons)
			if(cmd.getText().equals(text))
			{
				buttonToRemove = cmd;
				break;
			}
		if(buttonToRemove != null)
			buttons.remove(buttonToRemove);
		
		DynamicSelectionMenu.this.clearChildren();
		if(sort)
			Collections.sort(buttons, comparor);
		for(int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).setY(0 + i * textureRegionItem.getRegionHeight());
			DynamicSelectionMenu.this.addActor(buttons.get(i));
		}
	}
	
	public void sortItems()
	{
		Collections.sort(buttons, comparor);
		for(int i = 0; i < buttons.size(); i++)
			buttons.get(i).setY(0 + i * textureRegionItem.getRegionHeight());
	}
	
	public void unHighlight()
	{
		for(TextButton txt : buttons)
		{
			txt.setHighlight(false);
		}
		selectedButton = null;
	}
	
	public void onButtonClicked(String text)
	{
		
	}
	
	public String getSelectedItem()
	{
		return selectedButton;
	}
	
	public void setContent(final List<String> content, final boolean sort, final String selected)
	{
		unHighlight();
		DynamicSelectionMenu.this.clearChildren();
		buttons.clear();
		for(String s : content)
			addItem(s, false);
		if(sort)
			sortItems();
		if(selected != null)
			setSelectedItem(selected);
	}
	
	public void setSelectedItem(String asset)
	{
		unHighlight();
		for(TextButton cmd : buttons)
			if(cmd.getText().toString().equals(asset))
			{
				cmd.setHighlight(true);
				selectedButton = asset;
			}
	}
	
	@Override
	public float getWidth()
	{
		return textureRegionItem != null ? textureRegionItem.getRegionWidth() : 0;
	}
}
