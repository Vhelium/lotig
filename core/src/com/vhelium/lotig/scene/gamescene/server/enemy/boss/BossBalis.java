package com.vhelium.lotig.scene.gamescene.server.enemy.boss;

import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class BossBalis extends EntityServer
{
	public BossBalis(Realm realm, int playerCount)
	{
		super(realm);
	}
	
	private void dropItems()
	{
		//TODO: drops
	}
	
	@Override
	public void onDeath()
	{
		realm.onBossDamageTaken();
		dropItems();
		realm.onLevelFinished();
		realm.spawnTownPortal(this.getOriginX(), this.getOriginY() - 55);
	}
	
	@Override
	public float DoDamage(float damage, int damageType, boolean pvp)
	{
		realm.onBossDamageTaken();
		return super.DoDamage(damage, damageType, pvp);
	};
	
	@Override
	protected void onWaypointReached()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onNoWaypointFound()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onNewTargetFound()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onTargetLost()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		// TODO Auto-generated method stub
		
	}
}
