package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.StringTokenizer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public abstract class LevelObject
{
	protected ILevelObjectCallBack levelObjectCallback;
	public int id = -1;
	protected Group sprite;
	protected Rectangle bounds;
	protected String asset;
	protected boolean animated;
	protected EventObject event;
	protected boolean collision = false;
	protected int maxHp = -1;
	protected int hp = -1; //-1 = undestroyable
	protected boolean serverOnly = false;
	
	protected int state = 0;
	public boolean dying = false;
	
	public static LevelObject getInstance(int id, Realm realm, Rectangle rectangle, String name, MapProperties tmxObjectProperties)
	{
		if(name.equalsIgnoreCase("PowerUp"))
			return new LevelObject_PowerUp(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("DestroyableTile"))
			return new LevelObject_DestroyableTile(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Font"))
			return new LevelObject_Font(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("TownPortal"))
			return new LevelObject_TownPortal(id, realm, rectangle);
		else if(name.equalsIgnoreCase("Water") || name.equalsIgnoreCase("Lava") || name.equalsIgnoreCase("Poison"))
			return new LevelObject_Liquid(name, rectangle);
		else if(name.equalsIgnoreCase("Chest"))
			return new LevelObject_Chest(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Trap"))
			return new LevelObject_Trap(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Explosive"))
			return new LevelObject_Explosive(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("NPC"))
			return new LevelObject_NPC(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("RegenerationFountain"))
			return new LevelObject_RegenerationFountain(id, realm, rectangle);
		else if(name.equalsIgnoreCase("BulletSpawn"))
			return new LevelObject_BulletSpawn(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("TownExit"))
			return new LevelObject_TownExit(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("NextLevel"))
			return new LevelObject_NextLevel(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("LevelExit"))
			return new LevelObject_LevelExit(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Lever"))
			return new LevelObject_Lever(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Door"))
			return new LevelObject_Door(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("AoeArea"))
			return new LevelObject_AoeArea(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Vortex"))
			return new LevelObject_Vortex(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Stream"))
			return new LevelObject_Stream(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("Shrine"))
			return new LevelObject_Shrine(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("LairPortal"))
			return new LevelObject_LairPortal(id, realm, rectangle, tmxObjectProperties);
		else if(name.equalsIgnoreCase("TipSign"))
			return new LevelObject_TipSign(id, realm, rectangle, tmxObjectProperties);
		else
			return null;
	}
	
	public abstract String toStringFormat();
	
	public boolean getCollision()
	{
		return collision;
	}
	
	public int getHp()
	{
		return hp;
	}
	
	public EventObject getEvent()
	{
		return event;
	}
	
	public String getAsset()
	{
		return asset;
	}
	
	public boolean getAnimated()
	{
		return animated;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public float getOriginX()
	{
		return bounds != null ? bounds.getX() + bounds.getWidth() / 2 : 0;
	}
	
	public float getOriginY()
	{
		return bounds != null ? bounds.getY() + bounds.getHeight() / 2 : 0;
	}
	
	public void lowerHealth()
	{
		hp -= 1;
		if(levelObjectCallback != null)
			levelObjectCallback.lowerHealth(this);
	}
	
	public int getId()
	{
		return id;
	}
	
	public boolean isServerOnly()
	{
		return serverOnly;
	}
	
	public static LevelObject getLevelObjectFromStringFormat(int id, float x, float y, int w, int h, String properties)
	{
		StringTokenizer st;
		st = new StringTokenizer(properties, ";");
		String name = st.nextToken();
		
		if(name.equalsIgnoreCase(LevelObject_DestroyableTile.NAME))
			return new LevelObject_DestroyableTile(id, x, y, w, h, st.nextToken(), Boolean.parseBoolean(st.nextToken()), Boolean.parseBoolean(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_PowerUp.NAME))
			return new LevelObject_PowerUp(id, x, y, w, h, st.nextToken(), Boolean.parseBoolean(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Tombstone.NAME))
			return new LevelObject_Tombstone(id, x, y, w, h);
		else if(name.equalsIgnoreCase(LevelObject_Font.NAME))
			return new LevelObject_Font(id, x, y, w, h, st.nextToken());
		else if(name.equalsIgnoreCase(LevelObject_TownPortal.NAME))
			return new LevelObject_TownPortal(id, x, y, w, h);
		else if(name.equalsIgnoreCase(LevelObject_Chest.NAME))
			return new LevelObject_Chest(id, x, y, w, h, Integer.parseInt(st.nextToken()), st.nextToken());
		else if(name.equalsIgnoreCase(LevelObject_Lightwell.NAME))
			return new LevelObject_Lightwell(id, x, y, w, h, st.nextToken(), Boolean.parseBoolean(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Trap.NAME))
			return new LevelObject_Trap(id, x, y, w, h, st.nextToken(), st.nextToken(), Boolean.parseBoolean(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Explosive.NAME))
			return new LevelObject_Explosive(id, x, y, w, h, st.nextToken(), st.nextToken(), Boolean.parseBoolean(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_NPC.NAME))
			return new LevelObject_NPC(id, x, y, w, h, st.nextToken(), Boolean.parseBoolean(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_RegenerationFountain.NAME))
			return new LevelObject_RegenerationFountain(id, x, y, w, h);
		else if(name.equalsIgnoreCase(LevelObject_BulletSpawn.NAME))
			return new LevelObject_BulletSpawn(id, x, y, w, h);
		else if(name.equalsIgnoreCase(LevelObject_Bomb.NAME))
			return new LevelObject_Bomb(id, x, y, w, h, st.nextToken());
		else if(name.equalsIgnoreCase(LevelObject_TownExit.NAME))
			return new LevelObject_TownExit(id, x, y, w, h, Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_NextLevel.NAME))
			return new LevelObject_NextLevel(id, x, y, w, h, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_LevelExit.NAME))
			return new LevelObject_LevelExit(id, x, y, w, h, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Lever.NAME))
			return new LevelObject_Lever(id, x, y, w, h, Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Door.NAME))
			return new LevelObject_Door(id, x, y, w, h, st.nextToken(), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_DefenseTower.NAME))
			return new LevelObject_DefenseTower(id, x, y, w, h, Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Vortex.NAME))
			return new LevelObject_Vortex(id, x, y, w, h, st.nextToken(), Boolean.parseBoolean(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Stream.NAME))
			return new LevelObject_Stream(id, x, y, w, h, st.nextToken(), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_Shrine.NAME))
			return new LevelObject_Shrine(id, x, y, w, h, st.nextToken(), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_LairPortal.NAME))
			return new LevelObject_LairPortal(id, x, y, w, h, st.nextToken(), st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken()));
		else if(name.equalsIgnoreCase(LevelObject_TipSign.NAME))
			return new LevelObject_TipSign(id, x, y, w, h, st.nextToken());
		else
			return null;
	}
	
	public Actor getSprite()
	{
		return sprite;
	}
	
	public void update(float delta, Realm realm)
	{
		
	}
	
	public int getState()
	{
		return state;
	}
	
	public void setStateAndInform(int state, Realm realm)
	{
		this.state = state;
		onStateChanged(realm);
	}
	
	public void setState(int state)
	{
		this.state = state;
	}
	
	protected void onStateChanged(Realm realm)
	{
		realm.shareLevelObjectStateChanged(this, state);
	}
	
	public abstract void stateChangedClient(int state);
	
	public abstract void trigger(Realm realm, int entityId);
	
	public void registerCallback(ILevelObjectCallBack callback)
	{
		this.levelObjectCallback = callback;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void updateClient(float delta, PlayerClient player)
	{
		
	}
	
	public void refresh(Realm realm)
	{
		
	}
	
	public void onDestroyed()
	{
		
	}
	
	public void onAdded(Realm realm)
	{
		
	}
	
	public void onRemoved(Realm realm)
	{
		
	}
}
