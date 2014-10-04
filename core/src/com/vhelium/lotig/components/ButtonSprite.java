package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.gamescene.SoundManager;

public class ButtonSprite extends Button
{
	private ButtonSprite(ButtonStyle style)
	{
		super(style);
	}
	
	public ButtonSprite(float x, float y, TextureRegion up, final OnMyClickListener listener)
	{
		this(x, y, up, null, null, listener);
	}
	
	public ButtonSprite(int x, int y, TiledTextureRegion upAndDown, OnMyClickListener listener)
	{
		this(x, y, upAndDown.getTextureRegion(0), upAndDown.getTextureRegion(1), listener);
	}
	
	public ButtonSprite(float x, float y, TextureRegion up, TextureRegion down, final OnMyClickListener listener)
	{
		this(x, y, up, down, null, listener);
	}
	
	public ButtonSprite(float x, float y, TextureRegion up, final String sound, final OnMyClickListener listener)
	{
		this(x, y, up, null, sound, listener);
	}
	
	public ButtonSprite(float x, float y, TextureRegion up, TextureRegion down, final String sound, final OnMyClickListener listener)
	{
		this(generateStyle(up, down));
		setPosition(x, y);
		this.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				if(sound != null && !sound.isEmpty())
					SoundManager.playSound(sound);
				listener.onClick(event, actor);
			}
		});
	}
	
	private static ButtonStyle generateStyle(TextureRegion up, TextureRegion down)
	{
		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(up);
		style.down = down != null ? new TextureRegionDrawable(down) : null;
		return style;
	}
	
	public void setEnabled(boolean v)
	{
		setDisabled(!v);
	}
	
	public Sprite getChildByTag(int tag)
	{
		for(Actor actor : this.getChildren())
			if(actor instanceof Sprite && ((Sprite) actor).getTag() == tag)
				return (Sprite) actor;
		return null;
	}
}
