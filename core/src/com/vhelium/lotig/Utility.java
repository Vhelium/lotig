package com.vhelium.lotig;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Utility
{
	public static String getNormalizedText(BitmapFont font, String text, float textWidth)
	{
		String[] passages = text.split("\n", -1);
		String[][] words = new String[passages.length][];
		for(int p = 0; p < passages.length; p++)
			words[p] = passages[p].split(" ");
		
		StringBuilder normalizedText = new StringBuilder();
		
		for(int p = 0; p < passages.length; p++)
		{
			StringBuilder line = new StringBuilder();
			for(int w = 0; w < words[p].length; w++)
			{
				if(font.getBounds(line + words[p][w]).width > textWidth)
				{
					normalizedText.append(line).append('\n');
					line = new StringBuilder();
				}
				
				if(line.length() == 0)
					line.append(words[p][w]);
				else
					line.append(' ').append(words[p][w]);
				
				if(w == words[p].length - 1)
					normalizedText.append(line);
			}
			if(p != words.length - 1)
				normalizedText.append('\n');
		}
		return normalizedText.toString();
	}
	
	public static TextureRegion[][] splitTextureRegion(Texture texture, int x, int y, int width, int height, int tileWidth, int tileHeight, boolean flipY)
	{
		int rows = height / tileHeight;
		int cols = width / tileWidth;
		
		int startX = x;
		TextureRegion[][] tiles = new TextureRegion[rows][cols];
		for(int row = 0; row < rows; row++, y += tileHeight)
		{
			x = startX;
			for(int col = 0; col < cols; col++, x += tileWidth)
			{
				tiles[row][col] = new TextureRegion(texture, x, y, tileWidth, tileHeight);
				tiles[row][col].flip(false, flipY);
			}
		}
		
		return tiles;
	}
	
	public static TextureRegion[][] splitTextureRegion(TextureRegion region, int tileWidth, int tileHeight, boolean flipY)
	{
		int x, y;
		Texture texture = region.getTexture();
		if(!region.isFlipX())
			x = Math.round(region.getU() * texture.getWidth());
		else
			x = Math.round(region.getU2() * texture.getWidth());
		if(!region.isFlipY())
			y = Math.round(region.getV() * texture.getHeight());
		else
			y = Math.round(region.getV2() * texture.getHeight());
		int width = region.getRegionWidth();
		int height = region.getRegionHeight();
		
		return splitTextureRegion(texture, x, y, width, height, tileWidth, tileHeight, flipY);
	}
	
	public static void normalizeVector(float[] container, float x, float y)
	{
		double length = Math.sqrt(x * x + y * y);
		container[0] = (float) (x / length);
		container[1] = (float) (y / length);
	}
}
