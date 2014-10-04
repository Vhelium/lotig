package com.vhelium.lotig.scene.gamescene.server.enemy;

import java.util.Collection;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class EnemyTD extends EntityServer
{
	public EnemyTD(Realm realm)
	{
		super(realm);
	}
	
	@Override
	public void update(float delta, long millis, Collection<? extends EntityServerMixin>... enemies)
	{
		super.update(delta, millis, enemies);
	}
	
	@Override
	protected void onWaypointReached()
	{
		super.nextGoToInQueue();
	}
	
	@Override
	protected void onNoWaypointFound()
	{
		
	}
	
	@Override
	protected void onNewTargetFound()
	{
		
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		
	}
	
	@Override
	protected void onTargetLost()
	{
		
	}
}
