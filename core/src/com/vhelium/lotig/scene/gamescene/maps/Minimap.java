package com.vhelium.lotig.scene.gamescene.maps;

import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Line;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.RectangleDrawer;
import com.vhelium.lotig.components.ShapeRenderGroup;
import com.vhelium.lotig.components.ShapeRendererBatch;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class Minimap extends ShapeRenderGroup
{
	Main activity;
	int defaultX = 350;
	int maxY = 400;
	Rectangle playerRect;
	Rectangle exit;
	float scaleX, scaleY;
	boolean isInitializing = false;
	ShapeRendererBatch shapeRenderer;
	RectangleDrawer rectangleDrawer;
	
	ConcurrentHashMap<Integer, Rectangle> tempCollision;
	
	int sizeX, sizeY;
	
	boolean centered = true;
	
	public Minimap(Main activity, ShapeRendererBatch shapeRenderer)
	{
		super(shapeRenderer);
		this.activity = activity;
		this.shapeRenderer = shapeRenderer;
		rectangleDrawer = new RectangleDrawer(shapeRenderer);
		tempCollision = new ConcurrentHashMap<Integer, Rectangle>();
		this.setZIndex(4);
		playerRect = new Rectangle(0, 0, 0, 0);
		playerRect.setColor(Color.RED);
		playerRect.setAlpha(0.5f);
	}
	
	public void prepare(final TMXMap map)
	{
		isInitializing = true;
		exit = null;
		tempCollision.clear();
		
		scaleX = defaultX / (float) map.getMapWidth();
		scaleY = scaleX;
		
		sizeX = defaultX;
		sizeY = (int) (map.getMapHeight() * scaleY);
		
		if(sizeY > maxY)
		{
			sizeY = maxY;
			scaleY = sizeY / (float) map.getMapHeight();
			scaleX = scaleY;
			sizeX = (int) (map.getMapWidth() * scaleX);
			setCentered(this.centered);
		}
	}
	
	public void initialize(final Main activity, final TMXMap map)
	{
		Minimap.this.clear();
		rectangleDrawer.clearRectangles();
		
		Minimap.this.setX(SceneManager.CAMERA_WIDTH / 2 - sizeX / 2);
		Minimap.this.setY(SceneManager.CAMERA_HEIGHT / 2 - sizeY / 2);
		
		Minimap.this.addActor(rectangleDrawer);
		
		Line o = new Line(0, 0, map.getMapWidth() * scaleX, 0, shapeRenderer);
		o.setColor(Color.BLACK);
		o.setAlpha(0.4f);
		Minimap.this.addActor(o);//obe
		
		Line u = new Line(0, map.getMapHeight() * scaleY, map.getMapWidth() * scaleX, map.getMapHeight() * scaleY, shapeRenderer);
		u.setColor(Color.BLACK);
		u.setAlpha(0.4f);
		Minimap.this.addActor(u);//une
		
		Line l = new Line(0, 0, 0, map.getMapHeight() * scaleY, shapeRenderer);
		l.setColor(Color.BLACK);
		l.setAlpha(0.4f);
		Minimap.this.addActor(l);//linx
		
		Line r = new Line(map.getMapWidth() * scaleX, 0, map.getMapWidth() * scaleX, map.getMapHeight() * scaleY, shapeRenderer);
		r.setColor(Color.BLACK);
		r.setAlpha(0.4f);
		Minimap.this.addActor(r);//rächts
		
		for(Rectangle coll : map.getCollideTiles())
		{
			Rectangle rect = new Rectangle(coll.getX() * scaleX, coll.getY() * scaleY, coll.getWidth() * scaleX, coll.getHeight() * scaleY);
			rect.setColor(Color.BLACK);
			rect.setAlpha(0.5f);
			rectangleDrawer.addRectangle(rect);
			Minimap.this.addActor(rect);
		}
		
		//Generate Bitmap from collision layer
		if(map.getCollisionLayer() != null)
		{
			int tilesX = map.getMapWidth() / map.getTileSize(), tilesY = map.getMapHeight() / map.getTileSize();
			Pixmap pixmap = new Pixmap(tilesX, tilesY, Format.RGBA4444);
			pixmap.setColor(Color.BLACK);
			
			for(int y = 0; y < tilesY; y++)
				for(int x = 0; x < tilesX; x++)
					if(map.getTileIdInLayerAt(map.getCollisionLayer(), x, tilesY - 1 - y/*FLIP!*/) != 0)
						pixmap.drawPixel(x, y);
			
			Sprite sprite = new Sprite(0, 0, sizeX, sizeY, new TextureRegion(new Texture(pixmap)));
			sprite.setAlpha(0.5f);
			Minimap.this.addActor(sprite);
		}
		
		for(Rectangle coll : tempCollision.values())
			Minimap.this.addActor(coll);
		
		if(exit != null)
			Minimap.this.addActor(exit);
		
		playerRect.setWidth(PlayerClient.WIDTH * scaleX);
		playerRect.setHeight(PlayerClient.HEIGHT * scaleY);
		rectangleDrawer.addRectangle(playerRect);
		
		isInitializing = false;
	}
	
	public void updatePlayer(float x, float y, float offsetX, float offsetY)
	{
		playerRect.setX(x * scaleX - playerRect.getWidth() / 2);
		playerRect.setY(y * scaleY - playerRect.getHeight() / 2);
		
		if(!centered)
		{
			Minimap.this.setX(SceneManager.CAMERA_WIDTH / 2 - playerRect.getX() - playerRect.getWidth() / 2 + offsetX);
			Minimap.this.setY(SceneManager.CAMERA_HEIGHT / 2 - playerRect.getY() - playerRect.getHeight() / 2 + offsetY);
		}
	}
	
	public void setCentered(boolean centered)
	{
		this.centered = centered;
		if(centered)
		{
			Minimap.this.setX(SceneManager.CAMERA_WIDTH / 2 - sizeX / 2);
			Minimap.this.setY(SceneManager.CAMERA_HEIGHT / 2 - sizeY / 2);
		}
	}
	
	public void onNextLevelAdded(Rectangle rectangle)
	{
		exit = new Rectangle(rectangle.getX() * scaleX, rectangle.getY() * scaleY, rectangle.getWidth() * scaleX, rectangle.getHeight() * scaleY);
		exit.setColor(Color.YELLOW);
		exit.setAlpha(0.6f);
		rectangleDrawer.addRectangle(exit);
		if(!isInitializing)
			this.addActor(exit);
	}
	
	public void onCollisionAdded(int key, Rectangle rectangle)
	{
		Rectangle coll = new Rectangle(rectangle.getX() * scaleX, rectangle.getY() * scaleY, rectangle.getWidth() * scaleX, rectangle.getHeight() * scaleY);
		coll.setColor(Color.BLACK);
		coll.setAlpha(0.5f);
		rectangleDrawer.addRectangle(coll);
		tempCollision.put(key, coll);
		if(!isInitializing)
			this.addActor(coll);
	}
	
	public void onCollisionRemoved(final int key)
	{
		if(tempCollision.containsKey(key))
		{
			rectangleDrawer.removeRectangle(tempCollision.get(key));
			tempCollision.remove(key);
		}
	}
}
