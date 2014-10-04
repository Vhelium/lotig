package com.vhelium.lotig.scene.gamescene.server.enemy;

import java.util.Collection;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class EnemyRanger extends EntityServer
{
	private double currentAngle = 0;
	private int failMoves = 0;
	
	public EnemyRanger(Realm realm)
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
		failMoves = 0;
		addToAngle(10, 40);
		
		if(target != null)
			calculateWaypoint(currentAngle);
	}
	
	@Override
	protected void onNoWaypointFound()
	{
		addToAngle(10, 20);
		failMoves = 0;
		
		if(target != null)
			calculateWaypoint(currentAngle);
	}
	
	@Override
	protected void onNewTargetFound()
	{
		failMoves = 0;
		currentAngle = 0; //TODO: Calculate entry angle to player!
		
		if(target != null)
			calculateWaypoint(currentAngle);
	}
	
	@Override
	protected void onTargetLost()
	{
		currentAngle = 0;
		failMoves = 0;
		goTo(this.getX() + 100, this.getY() + 100);
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		failMoves++;
		if(failMoves > 6)
		{
			addToAngle(10, 40);
			failMoves = 0;
		}
		if(target != null)
			calculateWaypoint(currentAngle);
	}
	
	private void calculateWaypoint(double degree)
	{
		goTo(target.getX() + (float) Math.cos(currentAngle) * this.shootRange * 0.75f, target.getY() + (float) Math.sin(currentAngle) * this.shootRange * 0.75f);
	}
	
	private void addToAngle(int from, int to)
	{
		currentAngle += Math.toRadians(from + GameHelper.getInstance().getRandom().nextInt(to - from + 1));
		currentAngle %= (2 * Math.PI);
	}
}
