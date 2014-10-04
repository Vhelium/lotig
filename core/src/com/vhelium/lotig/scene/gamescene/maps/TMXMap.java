package com.vhelium.lotig.scene.gamescene.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.IClientMapCallback;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.quest.Quest;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.server.levelobject.EventObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Chest;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_LevelExit;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Liquid;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_NextLevel;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_TownExit;

public class TMXMap
{
	private boolean isServer = false;
	private final Main activity;
	private final OrthographicCamera camera;
	private FlippedTiledMapRenderer renderer;
	private TiledMap TMXtiledMap;
	int mapWidth, mapHeight, tileWidth, tileHeight;
	
	private MapProperties mapProperties;
	private TiledMapTileLayer collisionLayer;
	private List<TMXLayer> layers;
	private List<MapLayer> objectLayers;
	
	private List<Rectangle> collideTiles;
	private List<EventObject> eventTiles;
	private List<EventObject> eventTilesToRemove;
	private HashMap<String, ArrayList<EventObject>> staticEventTiles;
	public List<LevelObject> destroyableTiles;
	public List<Integer> destroyableTilesToRemove;
	private ConcurrentHashMap<Integer, LevelObject> levelObjects;
	private ConcurrentHashMap<Integer, LevelObject> dependingLevelObjects;
	private List<Integer> levelObjectsToRemove;
	private List<LevelObject_Liquid> liquids;
	private int levelObjectCounter = 0;
	private LevelObject nextLevel;
	
	private TMXAnimatedLayer animatedLayer;
	
	private List<EventObject> eventTilesClient;
	
	public Vector2 spawn;
	
	private String currentMapName;
	
	private final IClientMapCallback clientCallback;
	
	public TMXMap(Main activity, OrthographicCamera camera, IClientMapCallback clientCallback)
	{
		this.activity = activity;
		this.camera = camera;
		this.clientCallback = clientCallback;
	}
	
	public void loadMap(String mapName, SpriteBatch batch, boolean isServer)
	{
		currentMapName = mapName;
		this.isServer = isServer;
		Log.d("TMXMap.out", "start loading map " + mapName);
		
		TMXtiledMap = GameHelper.$.loadTiledMap("tmx/" + mapName + ".tmx");
		if(!isServer)
		{
			renderer = new FlippedTiledMapRenderer(TMXtiledMap, 1, batch);
			renderer.setView(camera);
		}
		
		MapProperties prop = TMXtiledMap.getProperties();
		tileWidth = prop.get("tilewidth", Integer.class);
		tileHeight = prop.get("tileheight", Integer.class);
		mapWidth = prop.get("width", Integer.class) * tileWidth;
		mapHeight = prop.get("height", Integer.class) * tileHeight;
		
		layers = new ArrayList<TMXLayer>();
		objectLayers = new ArrayList<MapLayer>();
		collisionLayer = null;
		for(MapLayer layer : TMXtiledMap.getLayers())
		{
			if(layer instanceof TiledMapTileLayer)
			{
				if(layer.getName().equalsIgnoreCase("Collision"))
					collisionLayer = (TiledMapTileLayer) layer;
				else
					layers.add(new TMXLayer(renderer, (TiledMapTileLayer) layer, camera));
			}
			else
				objectLayers.add(layer);
		}
		
		loadProperties();
		loadCollisionObjects();
		loadLiquidObjects();
		if(!isServer)
			loadAnimatedLayer();
		
		Log.d("TMXMap.out", "loaded tmx map..");
	}
	
	public boolean isCollisionAt(float x, float y, float width, float height, boolean isBullet)
	{
		if(x < 0 || x > getMapWidth() - width || y < 0 || y > getMapHeight() - height)
			return true;
		
		//Check Collision tile layer
		if(collisionLayer != null)
		{
			final int left = (int) (x / getTileSize());
			final int right = (int) ((x + width) / getTileSize());
			final int top = (int) (y / getTileSize());
			final int bot = (int) ((y + height) / getTileSize());
			
			for(int tY = top; tY <= bot; tY++)
				for(int tX = left; tX <= right; tX++)
				{
					if(tX >= 0 && tX < getTileColumns() && tY >= 0 && tY < getTileRows())
					{
						final TiledMapTileLayer.Cell cell = collisionLayer.getCell(tX, getTileRows() - 1 - tY);
						if(cell != null)
						{
							final TiledMapTile tile = cell.getTile();
							if(tile != null && tile.getId() != 0)
								return true;
						}
					}
				}
		}
		
		for(final LevelObject obj : destroyableTiles)
		{
			if(x + width >= obj.getBounds().getX() && y + height >= obj.getBounds().getY() && x <= obj.getBounds().getX() + obj.getBounds().getWidth() && y <= obj.getBounds().getY() + obj.getBounds().getHeight())
			{
				if(isBullet)
					obj.lowerHealth();
				if(obj.getHp() <= 0 && isServer)
				{
					obj.onDestroyed();
					removeLevelObject(obj.getId(), null);//works because afterwards will be returned, so iterator will be stopped.
				}
				return true;
			}
		}
		
		for(final Rectangle rectangle : collideTiles)
		{
			if(x + width >= rectangle.getX() && y + height >= rectangle.getY() && x <= rectangle.getX() + rectangle.getWidth() && y <= rectangle.getY() + rectangle.getHeight())
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void loadProperties()
	{
		this.mapProperties = TMXtiledMap.getProperties();
	}
	
	public void loadCollisionObjects()
	{
		collideTiles = new ArrayList<Rectangle>();
		
		for(final MapLayer group : objectLayers)
			if(group.getProperties().containsKey("Collide"))
				for(final MapObject object : group.getObjects())
				{
					Rectangle collisionObject = new Rectangle(((RectangleMapObject) object).getRectangle(), true, mapHeight);
					collideTiles.add(collisionObject);
				}
	}
	
	public void loadLiquidObjects()
	{
		liquids = new ArrayList<LevelObject_Liquid>();
		
		for(final MapLayer group : objectLayers)
			if(group.getProperties().containsKey("Liquid"))
				for(final MapObject object : group.getObjects())
				{
					LevelObject_Liquid liquid = new LevelObject_Liquid(object.getName(), new Rectangle(((RectangleMapObject) object).getRectangle(), true, mapHeight));
					liquids.add(liquid);
				}
	}
	
	private void loadAnimatedLayer()
	{
		List<MapObject> animatedObjects = new ArrayList<MapObject>();
		for(final MapLayer group : objectLayers)
			if(group.getProperties().containsKey("Animated"))
				for(final MapObject object : group.getObjects())
					animatedObjects.add(object);
		
		animatedLayer = new TMXAnimatedLayer(renderer, camera);
		animatedLayer.loadAnimatedObjects(animatedObjects, getTileSize());
		animatedLayer.loadLiquidObjects(liquids, getTileSize());
	}
	
	public List<EntityServer> loadEnemies(Realm realm, int difficulty, int playerCount)
	{
		List<EntityServer> entities = new ArrayList<EntityServer>();
		
		for(final MapLayer group : objectLayers)
			if(group.getProperties().containsKey("Enemies"))
				for(final MapObject object : group.getObjects())
				{
					String[] name = object.getProperties().get("type", String.class).split("-");
					EntityServer ent = GameHelper.getInstance().getEnemyModel(name[0]).createEntity(difficulty, Integer.parseInt(name[1]), playerCount, realm);
					ent.setX(((RectangleMapObject) object).getRectangle().getX());
					ent.setY(mapHeight - ((RectangleMapObject) object).getRectangle().y - ((RectangleMapObject) object).getRectangle().height);
					ent.spawned();
					entities.add(ent);
				}
		
		return entities;
	}
	
	public void loadEventTiles(Realm realm)
	{
		eventTiles = new ArrayList<EventObject>();
		eventTilesToRemove = new ArrayList<EventObject>();
		eventTilesClient = new ArrayList<EventObject>();
		levelObjects = new ConcurrentHashMap<Integer, LevelObject>();
		dependingLevelObjects = new ConcurrentHashMap<Integer, LevelObject>();
		levelObjectsToRemove = new ArrayList<Integer>();
		destroyableTiles = new ArrayList<LevelObject>();
		staticEventTiles = new HashMap<String, ArrayList<EventObject>>();
		destroyableTilesToRemove = new ArrayList<Integer>();
		nextLevel = null;
		levelObjectCounter = 0;
		
		for(final MapLayer group : objectLayers)
			if(group.getProperties().containsKey("Events"))
			{
				for(final MapObject object : group.getObjects())
				{
					final String name = object.getName();
					final String type = object.getProperties().get("type", String.class);
					Rectangle rectangle = new Rectangle(((RectangleMapObject) object).getRectangle(), true, mapHeight);
					
					if(object.getProperties().get("type", String.class).equalsIgnoreCase("static"))
					{
						if(name.equalsIgnoreCase("Spawn"))
						{
							spawn = new Vector2((int) rectangle.getX(), (int) rectangle.getY());
						}
						else
						{
							if(!staticEventTiles.containsKey(name))
								staticEventTiles.put(name, new ArrayList<EventObject>());
							staticEventTiles.get(name).add(EventObject.getInstance(realm, rectangle, type, object.getProperties()));
						}
					}
					else if(name.equalsIgnoreCase("EventClient"))
						eventTilesClient.add(EventObject.getInstance(null, rectangle, type, object.getProperties()));
					else if(type.equalsIgnoreCase("LevelObject"))
					{
						if(realm != null)
						{
							String[] nameString = name.split(";");
							if(nameString.length == 1)
							{
								LevelObject obj = addNewLevelObjectServer(realm, rectangle, nameString[0], object.getProperties());
								if(nameString[0].equals("NextLevel") || nameString[0].equals("TownExit") || nameString[0].equals("LevelExit"))
									nextLevel = obj;
							}
							else
							{
								int id = levelObjectCounter++;
								LevelObject obj = LevelObject.getInstance(id, realm, rectangle, nameString[0], object.getProperties());
								dependingLevelObjects.put(Integer.parseInt(nameString[1]), obj);
							}
						}
					}
					else if(name.equalsIgnoreCase("EventAll"))
					{
						EventObject obj = EventObject.getInstance(realm, rectangle, type, object.getProperties());
						obj.entityCheck = true;
						eventTiles.add(obj);
					}
					else
					{
						eventTiles.add(EventObject.getInstance(realm, rectangle, type, object.getProperties()));
					}
				}
				
				if(realm != null)
					checkDependingLevelObjects(realm);
			}
	}
	
	public void checkDependingLevelObjects(Realm realm)
	{
		if(realm.getLevel().getStoryQuestHost() != null && dependingLevelObjects != null)
		{
			for(Entry<Integer, LevelObject> e : dependingLevelObjects.entrySet())
			{
				if(levelObjects.containsValue(e.getValue()))
				{
					if(e.getKey() != realm.getLevel().getStoryQuestHost().getStep() && !e.getValue().dying)
						realm.removeLevelObject(e.getValue());
				}
				else if(e.getKey() == realm.getLevel().getStoryQuestHost().getStep())
					realm.addLevelObjectAtRuntime(e.getValue());
			}
		}
	}
	
	public LevelObject addNewLevelObjectServer(Realm realm, Rectangle rectangle, String name, MapProperties tmxObjectProperties)
	{
		int id = levelObjectCounter++;
		LevelObject obj = LevelObject.getInstance(id, realm, rectangle, name, tmxObjectProperties);
		addLevelObject(id, obj, realm);
		return obj;
	}
	
	public LevelObject addNewLevelObjectServer(LevelObject obj, Realm realm)
	{
		int id = levelObjectCounter++;
		obj.setId(id);
		addLevelObject(id, obj, realm);
		return obj;
	}
	
	public void addLevelObject(int key, LevelObject obj, Realm realm)
	{
		levelObjects.put(key, obj);
		
		if(obj.getCollision())
		{
			if(obj.getHp() <= 0)//Client oder Server-Standard-Non-Destructive
				collideTiles.add(obj.getBounds());
			else
			{
				destroyableTiles.add(obj);
			}
			if(clientCallback != null)
				clientCallback.onCollisionAdded(key, obj.getBounds());
		}
		
		if(obj.getEvent() != null)
		{
			if(obj.getEvent().isClientSided())
				eventTilesClient.add(obj.getEvent());
			else
				eventTiles.add(obj.getEvent());
		}
		
		if(clientCallback != null && (obj instanceof LevelObject_LevelExit || obj instanceof LevelObject_TownExit || obj instanceof LevelObject_NextLevel))
			clientCallback.onNextLevelAdded(obj.getBounds());
		
		obj.onAdded(realm);
	}
	
	public void removeLevelObject(int key, Realm realm)
	{
		if(!levelObjects.containsKey(key))
			return;
		
		LevelObject obj = levelObjects.get(key);
		obj.dying = false;
		
		if(obj.getCollision())
		{
			if(collideTiles.contains(obj.getBounds()))
				collideTiles.remove(obj.getBounds());
			else if(destroyableTiles.contains(obj))
			{
				destroyableTiles.remove(obj);
				destroyableTilesToRemove.add(key);
			}
			if(clientCallback != null)
				clientCallback.onCollisionRemoved(key);
		}
		
		if(obj.getEvent() != null)
			eventTilesToRemove.add(obj.getEvent());
		
		levelObjectsToRemove.add(key);
		
		obj.onRemoved(realm);
	}
	
	public void removeCollision(LevelObject obj)
	{
		if(collideTiles.contains(obj.getBounds()))
		{
			collideTiles.remove(obj.getBounds());
			if(clientCallback != null)
				clientCallback.onCollisionRemoved(obj.id);
		}
	}
	
	public void updateEventObjects(float delta, ConcurrentHashMap<Integer, PlayerServer> players, ConcurrentHashMap<Integer, EntityServer> enemies)
	{
		for(EventObject obj : eventTiles)
		{
			obj.update(delta, players);
			if(obj.entityCheck)
				obj.updateEnemies(delta, enemies);
		}
		for(EventObject obj : eventTilesToRemove)
		{
			if(!obj.isClientSided())
				eventTiles.remove(obj);
			else
				eventTilesClient.remove(obj);
		}
		eventTilesToRemove.clear();
	}
	
	public void updateLevelObjectsToRemove()
	{
		for(Integer i : levelObjectsToRemove)
			levelObjects.remove(i);
		levelObjectsToRemove.clear();
	}
	
	public void updateEventObjectsClient(float delta, PlayerClient player)
	{
		for(EventObject obj : eventTilesClient)
		{
			obj.update(delta, player);
		}
	}
	
	public ConcurrentHashMap<Integer, LevelObject> getLevelObjects()
	{
		return levelObjects;
	}
	
	public List<EventObject> getEventObjectsClient()
	{
		return eventTilesClient;
	}
	
	public int getTileSize()
	{
		return tileWidth;
	}
	
	public int getMapWidth()
	{
		return mapWidth;
	}
	
	public int getMapHeight()
	{
		return mapHeight;
	}
	
	public int getTileColumns()
	{
		return mapWidth / tileWidth;
	}
	
	public int getTileRows()
	{
		return mapHeight / tileHeight;
	}
	
	public TMXAnimatedLayer getAnimatedLayer()
	{
		return animatedLayer;
	}
	
	public List<LevelObject_Liquid> getLiquids()
	{
		return liquids;
	}
	
	public List<Rectangle> getCollideTiles()
	{
		return collideTiles;
	}
	
	public TiledMapTileLayer getCollisionLayer()
	{
		return collisionLayer;
	}
	
	public LevelObject getNextLevel()
	{
		return nextLevel;
	}
	
	public TMXLayer getLayer(int layer)
	{
		return layers.get(layer);
	}
	
	public List<TMXLayer> getLayers()
	{
		return layers;
	}
	
	public EventObject getStaticEvent(String name)
	{
		return staticEventTiles.get(name).get(0);
	}
	
	public ArrayList<EventObject> getStaticEvents(String name)
	{
		return staticEventTiles.get(name);
	}
	
	public TiledMap getTMXMap()
	{
		return TMXtiledMap;
	}
	
	public String getCurrentMapName()
	{
		return currentMapName;
	}
	
	public int getNextLevelObjectId()
	{
		levelObjectCounter++;
		return levelObjectCounter;
	}
	
	public MapProperties getMapProperties()
	{
		return mapProperties;
	}
	
	public String getMapProperty(String prop)
	{
		return mapProperties.containsKey(prop) ? mapProperties.get(prop, String.class) : "";
	}
	
	public void refreshUniqueChests(List<Integer> uniqueChestsOpened)
	{
		if(levelObjects != null)
			for(LevelObject obj : levelObjects.values())
				if(obj instanceof LevelObject_Chest)
					if(uniqueChestsOpened.contains(((LevelObject_Chest) obj).getUniqueId()))
						((LevelObject_Chest) obj).clearItems();
	}
	
	public Quest getDungeonQuest(ClientLevel level)
	{
		if(mapProperties.containsKey("quest_dungeon"))
		{
			Quest quest = new Quest(mapProperties.get("quest_dungeon", String.class), level);
			return quest;
		}
		return null;
	}
	
	public int getTileIdInLayerAt(TiledMapTileLayer layer, int x, int y)
	{
		final TiledMapTileLayer.Cell cell = layer.getCell(x, getTileRows() - 1 - y);
		if(cell != null && cell.getTile() != null)
			return cell.getTile().getId();
		return 0;
	}
}
