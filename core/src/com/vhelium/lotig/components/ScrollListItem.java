package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class ScrollListItem extends Sprite
{
	public static final int gapX = 8;
	private final Label itemText;
	private final Color colorNormal;
	private final Color colorHighlighted = new Color(190 / 255f, 0, 0, 1);
	private final int id;
	
	public ScrollListItem(int id, float pX, float pY, float pWidth, float pHeight, String text, Color color, TextureRegion pTextureRegion, BitmapFont font)
	{
		super(pX, pY, pWidth, pHeight, pTextureRegion);
		colorNormal = color;
		this.id = id;
		
		itemText = new Label(text, new LabelStyle(font, Color.WHITE));
		itemText.setAlignment(Align.top | Align.left);
		itemText.setColor(color);
		itemText.pack();
		itemText.setPosition(gapX, (pHeight - itemText.getHeight()) / 2);
		this.addActor(itemText);
	}
	
	private boolean isHighlighted;
	
	public void setHighlighted(boolean value)
	{
		if(value == isHighlighted)
			return;
		
		isHighlighted = value;
		
		if(isHighlighted)
			itemText.setColor(colorHighlighted);
		else
			itemText.setColor(colorNormal);
	}
	
	public void setText(String text)
	{
		itemText.setText(text);
	}
	
	public int getId()
	{
		return id;
	}
}
