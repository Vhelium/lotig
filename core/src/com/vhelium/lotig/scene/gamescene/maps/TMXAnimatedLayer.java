package com.vhelium.lotig.scene.gamescene.maps;

import java.util.List;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Liquid;

public class TMXAnimatedLayer extends Group
{
	Texture animationSheet;
	OrthogonalTiledMapRenderer renderer;
	OrthographicCamera camera;
	
	public TMXAnimatedLayer(OrthogonalTiledMapRenderer renderer, OrthographicCamera camera)
	{
		animationSheet = GameHelper.$.loadTexture("gfx/tiled/animated.png");
		this.renderer = renderer;
		this.camera = camera;
	}
	
	public void loadAnimatedObjects(List<MapObject> animatedObjects, int tileSize)
	{
//		for(MapObject object : animatedObjects)
//		{
//			int columns = object.getWidth() / tileSize;
//			int rows = object.getHeight() / tileSize;
//			for(int y = 0; y < rows; y++)
//				for(int x = 0; x < columns; x++)
//				{
//					float posX = object.getX() + x * tileSize;
//					float posY = object.getY() + y * tileSize;
//					
//					animatedSprites.add(getAnimatedObjectTextureRegion(animationSheet, posX, posY, object.getTMXObjectProperties(), tileSize, activity));
//				}
//		}
	}
	
	public void loadLiquidObjects(List<LevelObject_Liquid> liquids, int tileSize)
	{
		for(LevelObject_Liquid liquid : liquids)
		{
			int columns = (int) (liquid.getBounds().getWidth() / tileSize);
			int rows = (int) (liquid.getBounds().getHeight() / tileSize);
			for(int y = 0; y < rows; y++)
				for(int x = 0; x < columns; x++)
				{
					float posX = liquid.getBounds().getX() + x * tileSize;
					float posY = liquid.getBounds().getY() + y * tileSize;
					AnimatedSprite sprite = new AnimatedSprite(posX, posY, getLiquidTextureRegion(animationSheet, liquid.getType(), tileSize));
					sprite.animate(330, true);
					this.addActor(sprite);
				}
		}
	}
	
//	private static AsnimatedSprite getAnimatedObjectTextureRegion(ITexture texture, float posX, float posY, TMXProperties<TMXObjectProperty> tmxObjectProperties, int tileSize, Main activity)
//	{
//		final int columnCount = texture.getWidth() / tileSize;
//		int startTile = 0;
//		int frames = 0;
//		int animationTime = 0;
//		for(TMXProperty property : tmxObjectProperties)
//		{
//			if(tmxObjectProperties.containsKey("startTile"))
//				startTile = Integer.parseInt(property.getValue());
//			if(tmxObjectProperties.containsKey("frames"))
//				frames = Integer.parseInt(property.getValue());
//			if(tmxObjectProperties.containsKey("animationTime"))
//				animationTime = Integer.parseInt(property.getValue());
//		}
//		
//		ITiledTextureRegion textureRegion = TextureRegionFactory.extractTiledFromTexture(texture, (startTile % columnCount) * tileSize, startTile / columnCount * tileSize, tileSize * frames, tileSize, frames, 1);
//		
//		AnimatedSprite sprite = new AnimatedSprite(posX, posY, tileSize, tileSize, textureRegion, activity.getVertexBufferObjectManager());
//		sprite.animate(animationTime, true);
//		
//		return sprite;
//	}
	
	private static TiledTextureRegion getLiquidTextureRegion(Texture texture, int type, int tileSize)
	{
		switch(type)
		{
			case LiquidType.WATER:
				return new TiledTextureRegion(texture, 0, 0, tileSize * 2, tileSize, 2, 1, true);
			case LiquidType.LAVA:
				return new TiledTextureRegion(texture, tileSize * 2, 0, tileSize * 2, tileSize, 2, 1, true);
			case LiquidType.POISON:
				return new TiledTextureRegion(texture, 0, tileSize, tileSize * 2, tileSize, 2, 1, true);
			default:
				return new TiledTextureRegion(texture, 0, 0, tileSize * 2, tileSize, 2, 1, true);
		}
	}
}
