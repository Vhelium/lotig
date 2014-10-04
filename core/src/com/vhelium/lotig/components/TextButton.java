package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.vhelium.lotig.scene.gamescene.SoundManager;

public class TextButton extends Button
{
	private Label label;
	private TextButtonStyle style;
	private boolean isHighlighted = false;
	
	protected TextButton(CharSequence text, TextButtonStyle style)
	{
		super();
		if(style != null)
		{
			setStyle(style);
			this.style = style;
			label = new Label(text, new LabelStyle(style.font, style.fontColor));
			label.setAlignment(Align.center);
			add(label).expand().fill();
			setSize(getPrefWidth(), getPrefHeight());
		}
	}
	
	public TextButton(float x, float y, TextureRegion region, BitmapFont font, CharSequence text, Color colorNormal, Color colorPressed, final OnMyClickListener listener)
	{
		this(x, y, region, font, text, colorNormal, colorPressed, null, listener);
	}
	
	public TextButton(float x, float y, TextureRegion region, BitmapFont font, CharSequence text, Color colorNormal, Color colorPressed, final String sound, final OnMyClickListener listener)
	{
		this(x, y, region.getRegionWidth(), region.getRegionHeight(), region, font, text, colorNormal, colorPressed, sound, listener);
	}
	
	public TextButton(float x, float y, BitmapFont font, CharSequence text, Color colorNormal, Color colorPressed, final OnMyClickListener listener)
	{
		this(x, y, font.getBounds(text).width, font.getBounds(text).height, null, font, text, colorNormal, colorPressed, null, listener);
	}
	
	public TextButton(float x, float y, float width, float height, TextureRegion region, BitmapFont font, CharSequence text, Color colorNormal, Color colorPressed, final String sound, final OnMyClickListener listener)
	{
		this(text, generateStyle(region, font, colorNormal, colorPressed, new Color(1, 0, 0, 1)));
		setBounds(x, y, width, height);
		if(listener != null)
			this.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					if(sound != null)
						SoundManager.playSound(sound);
					listener.onClick(event, actor);
				}
			});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		Color fontColor;
		if(isDisabled() && style.disabledFontColor != null)
			fontColor = style.disabledFontColor;
		else if(isPressed() && style.downFontColor != null)
			fontColor = style.downFontColor;
		else if(isHighlighted() && style.checkedFontColor != null)
			fontColor = style.checkedFontColor;
		else if(isOver() && style.overFontColor != null)
			fontColor = style.overFontColor;
		else
			fontColor = style.fontColor;
		if(fontColor != null)
			getLabel().getStyle().fontColor = fontColor;
		
		getColor().a = isDisabled() ? 0.3f * parentAlpha : 1.0f * parentAlpha;
		
		super.draw(batch, parentAlpha);
	}
	
	public void setHighlight(boolean value)
	{
		isHighlighted = value;
	}
	
	private boolean isHighlighted()
	{
		return isHighlighted;
	}
	
	private static TextButtonStyle generateStyle(TextureRegion region, BitmapFont font, Color colorNormal, Color colorPressed, Color colorHighlighted)
	{
		TextButtonStyle style = new TextButtonStyle();
		if(region != null)
			style.up = new TextureRegionDrawable(region);
		style.font = font;
		style.fontColor = colorNormal;
		style.downFontColor = colorPressed;
		style.disabledFontColor = new Color(colorNormal.r, colorNormal.g, colorNormal.b, 0.8f);
		style.checkedFontColor = colorHighlighted;
		return style;
	}
	
	public interface OnMyClickListener
	{
		public void onClick(ChangeEvent event, Actor actor);
	}
	
	public void setEnabled(boolean v)
	{
		setDisabled(!v);
	}
	
	@Override
	public void setStyle(ButtonStyle style)
	{
		if(style == null)
		{
			throw new NullPointerException("style cannot be null");
		}
		if(!(style instanceof TextButtonStyle))
			throw new IllegalArgumentException("style must be a TextButtonStyle.");
		super.setStyle(style);
		this.style = (TextButtonStyle) style;
		if(label != null)
		{
			TextButtonStyle textButtonStyle = (TextButtonStyle) style;
			LabelStyle labelStyle = label.getStyle();
			labelStyle.font = textButtonStyle.font;
			labelStyle.fontColor = textButtonStyle.fontColor;
			label.setStyle(labelStyle);
		}
	}
	
	@Override
	public TextButtonStyle getStyle()
	{
		return style;
	}
	
	public Label getLabel()
	{
		return label;
	}
	
	public void setText(String text)
	{
		label.setText(text);
	}
	
	public CharSequence getText()
	{
		return label.getText();
	}
}
