package com.vhelium.lotig.scene.gamescene.client.items;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public abstract class StackableItem extends Item
{
	protected int count = 1;
	
	public int getCount()
	{
		return count;
	}
	
	public void setCount(int value)
	{
		count = value;
	}
	
	public void countPlus()
	{
		count++;
	}
	
	public void countPlus(int value)
	{
		count += value;
	}
	
	public void countMinus()
	{
		count--;
	}
	
	public abstract StackableItem oneCopy();
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	@Override
	public String toStringTrimmed(BitmapFont font, int width)
	{
		String raw = toString();
		if(font.getBounds(raw).width > width)
		{
			raw = "";
			String sCount = " (" + count + ")";
			
			float countWidth = font.getBounds(sCount).width;
			int index = 0;
			while(index < NAME.length() && font.getBounds(raw).width + countWidth <= width)
			{
				raw += NAME.charAt(index);
				index++;
			}
			
			if(raw.length() >= 3)
				raw = raw.substring(0, raw.length() - 3);//remove last letter + 2, because too long
				
			if(!raw.isEmpty() && raw.charAt(raw.length() - 1) == ' ')
				raw = raw.substring(0, raw.length() - 1);//remove last letter, because empty
				
			raw += ".." + sCount;
		}
		return raw;
	}
}
