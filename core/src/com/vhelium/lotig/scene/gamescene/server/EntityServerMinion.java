package com.vhelium.lotig.scene.gamescene.server;

import java.util.Collection;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class EntityServerMinion extends EntityServer
{
	protected final int MIN_DISTANCE_OWNER_MOVED_TO_NOFIFY = 180;
	private final EntityServerMixin owner;
	private float lifeTime;
	private float lifeTimeElapsed;
	
	private float ownerLastX = 0;
	private float ownerLastY = 0;
	
	private boolean isOffensive = false;
	
	public EntityServerMinion(Realm realm, EntityServerMixin owner)
	{
		super(realm);
		this.owner = owner;
		roaming = false;
	}
	
	@Override
	public void update(float delta, long millis, Collection<? extends EntityServerMixin>... enemiesIncludingPlayers)
	{
		if(owner != null && Math.abs(ownerLastX - owner.getX()) + Math.abs(ownerLastY - owner.getY()) > MIN_DISTANCE_OWNER_MOVED_TO_NOFIFY)//Ungenau (Faktor Wurzel(2)), aber scheiss egal, performance FTW
		{
			onTargedMovedFarSinceLastWP();
		}
		
		if(lifeTime > 0)
		{
			lifeTimeElapsed += delta;
			if(lifeTimeElapsed >= lifeTime)
				setHp(-getMaxHp());
		}
		
		super.update(delta, millis, enemiesIncludingPlayers);
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
		executeGoTo();
	}
	
	@Override
	protected void onNewTargetFound()
	{
		executeGoTo();
	}
	
	@Override
	protected void onTargetLost()
	{
		executeGoTo();
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		executeGoTo();
	}
	
	@Override
	public void goTo(float x, float y)
	{
		super.goTo(x, y);
		
		if(owner != null)
		{
			ownerLastX = owner.getX();
			ownerLastY = owner.getY();
		}
	}
	
	private void executeGoTo()
	{
		if(isOffensive && target != null)
			goTo(target.getX() - 60 + GameHelper.getInstance().getRandom().nextInt(120), target.getY() - 60 + GameHelper.getInstance().getRandom().nextInt(160));
		else if(owner != null)
			goTo(owner.getX() - 60 + GameHelper.getInstance().getRandom().nextInt(120), owner.getY() - 60 + GameHelper.getInstance().getRandom().nextInt(160));
	}
	
	public void setIsOffensive(boolean isOffensive)
	{
		this.isOffensive = isOffensive;
	}
	
	public EntityServerMixin getOwner()
	{
		return owner;
	}
	
	public void setLifeTime(float time)
	{
		this.lifeTime = time;
	}
	
	public void onSummonned()
	{
		executeGoTo();
	}
	
	@Override
	public int getTeamNr()
	{
		return owner.getTeamNr();
	}
}
