package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vhelium.lotig.Utility;

public class TiledTextureRegion
{
	Texture texture;
	int tileColumns, tileRows, tileWidth, tileHeight;
	int textureX, textureY, regionWidth, regionHeight;
	boolean flipY;
	TextureRegion[] textureRegions;
	
	public TiledTextureRegion(Texture texture, int tileColumns, int tileRows, boolean flipY)
	{
		this(texture, 0, 0, texture.getWidth(), texture.getHeight(), tileColumns, tileRows, flipY);
	}
	
	public TiledTextureRegion(Texture texture, int textureX, int textureY, int regionWidth, int regionHeight, int tileColumns, int tileRows, boolean flipY)
	{
		this.texture = texture;
		this.flipY = flipY;
		this.tileColumns = tileColumns;
		this.tileRows = tileRows;
		this.tileWidth = regionWidth / tileColumns;
		this.tileHeight = regionHeight / tileRows;
		this.textureX = textureX;
		this.textureY = textureY;
		this.regionWidth = regionWidth;
		this.regionHeight = regionHeight;
		this.textureRegions = new TextureRegion[tileColumns * tileRows];
		
		for(int y = 0; y < tileRows; y++)
			for(int x = 0; x < tileColumns; x++)
			{
				textureRegions[x + y * tileColumns] = new TextureRegion(texture, textureX + x * tileWidth, textureY + y * tileHeight, tileWidth, tileHeight);
				textureRegions[x + y * tileColumns].flip(false, flipY);
			}
	}
	
	/** @param region
	 *            AtlasRegion only used for metrics
	 * @param tileColumns
	 *            column count
	 * @param tileRows
	 *            row count
	 * @param flipY
	 *            if the new TextureRegion is flipped. Previous flip of the AtlasRegion is IGNORED! */
	public TiledTextureRegion(AtlasRegion region, int tileColumns, int tileRows, boolean flipY)
	{
		this.texture = region.getTexture();
		this.flipY = flipY;
		this.tileColumns = tileColumns;
		this.tileRows = tileRows;
		this.textureRegions = new TextureRegion[tileColumns * tileRows];
		
		this.tileWidth = region.getRegionWidth() / tileColumns;
		this.tileHeight = region.getRegionHeight() / tileRows;
		
		if(!region.isFlipX())
			this.textureX = Math.round(region.getU() * texture.getWidth());
		else
			this.textureX = Math.round(region.getU2() * texture.getWidth());
		if(!region.isFlipY())
			this.textureY = Math.round(region.getV() * texture.getHeight());
		else
			this.textureY = Math.round(region.getV2() * texture.getHeight());
		this.regionWidth = region.getRegionWidth();
		this.regionHeight = region.getRegionHeight();
		
		TextureRegion[][] split = Utility.splitTextureRegion(texture, textureX, textureY, regionWidth, regionHeight, tileWidth, tileHeight, flipY);
		
		for(int y = 0; y < tileRows; y++)
			for(int x = 0; x < tileColumns; x++)
				textureRegions[x + y * tileColumns] = split[y][x];
	}
	
	public TextureRegion getTextureRegion(int tileIndex)
	{
		return textureRegions[tileIndex];
	}
	
	public float getTileWidth()
	{
		return tileWidth;
	}
	
	public float getTileHeight()
	{
		return tileHeight;
	}
	
	public int getTileCount()
	{
		return tileColumns * tileRows;
	}
	
	public TextureRegion[] getRegions()
	{
		return textureRegions;
	}
	
	public void dispose()
	{
		if(textureRegions.length > 0)
			textureRegions[0].getTexture().dispose();
	}
	
	public TiledTextureRegion copy()
	{
		return new TiledTextureRegion(texture, textureX, textureY, regionWidth, regionHeight, tileColumns, tileRows, flipY);
	}
}
