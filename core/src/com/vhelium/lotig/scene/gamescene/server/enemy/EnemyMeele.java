package com.vhelium.lotig.scene.gamescene.server.enemy;

import java.util.Collection;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class EnemyMeele extends EntityServer
{
	public EnemyMeele(Realm realm)
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
		if(target != null)
			executeGoTo();
	}
	
	@Override
	protected void onNoWaypointFound()
	{
		if(target != null)
			executeGoTo();
	}
	
	@Override
	protected void onNewTargetFound()
	{
		if(target != null)
			executeGoTo();
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		if(target != null)
			executeGoTo();
	}
	
	@Override
	protected void onTargetLost()
	{
		goTo(this.getX() + 100, this.getY() + 100);
	}
	
	private void executeGoTo()
	{
		goTo(target.getX() - 60 + GameHelper.getInstance().getRandom().nextInt(120), target.getY() - 60 + GameHelper.getInstance().getRandom().nextInt(160));
	}
}
