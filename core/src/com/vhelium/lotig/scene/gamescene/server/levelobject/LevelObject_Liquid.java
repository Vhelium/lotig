package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.maps.LiquidType;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_Liquid extends LevelObject
{
	private final int type;
	
	public LevelObject_Liquid(String name, Rectangle rectangle)
	{
		type = getType(name);
		this.bounds = rectangle;
	}
	
	@Override
	public String toStringFormat()
	{
		return null;
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
	
	private static int getType(String type)
	{
		if(type.equalsIgnoreCase("water"))
			return LiquidType.WATER;
		else if(type.equalsIgnoreCase("lava"))
			return LiquidType.LAVA;
		else if(type.equalsIgnoreCase("poison"))
			return LiquidType.POISON;
		else
			return -1;
	}
	
	public int getType()
	{
		return type;
	}
}
