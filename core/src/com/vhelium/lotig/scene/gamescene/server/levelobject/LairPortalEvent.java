package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LairPortalEvent extends OnEnterEvent
{
	public LairPortalEvent(Realm realm, Rectangle rectangle, MapProperties properties)
	{
		super(realm, rectangle, properties);
	}
}
