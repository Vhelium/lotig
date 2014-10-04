package com.vhelium.lotig.scene.gamescene.server.enemy;

import java.util.Collection;
import com.vhelium.lotig.Utility;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class EnemyNuker extends EntityServer
{
	private int currentAngle = 0;
	private int currentState = 0;
	private int failMoves = 0;
	private int repetitions = 0;
	
	private final float factor1 = 0.7f;
	private final float factor2 = 1.4f;
	
	public EnemyNuker(Realm realm)
	{
		super(realm);
		MIN_DISTANCE_TARGET_MOVED_TO_NOFIFY = 300;
		TARGET_LOOSE_FACTOR = 3;
	}
	
	@Override
	public void update(float delta, long millis, Collection<? extends EntityServerMixin>... enemies)
	{
		super.update(delta, millis, enemies);
	}
	
	@Override
	protected void onWaypointReached()
	{
		stateChange();
		failMoves = 0;
		
		if(target != null)
			calculateWaypoint();
	}
	
	@Override
	protected void onNoWaypointFound()
	{
		rotate();
		failMoves = 0;
		
		if(target != null)
			calculateWaypoint();
	}
	
	@Override
	protected void onNewTargetFound()
	{
		currentAngle = 0;
		failMoves = 0;
		repetitions = 0;
		
		if(target != null)
			calculateWaypoint();
	}
	
	@Override
	protected void onTargetLost()
	{
		currentAngle = 0;
		failMoves = 0;
		repetitions = 0;
		goTo(this.getX() + 100, this.getY() + 100);
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		failMoves++;
		if(failMoves > 3)
		{
			rotate();
			failMoves = 0;
		}
		if(target != null)
			calculateWaypoint();
	}
	
	private void rotate()
	{
		repetitions = 0;
		currentAngle = (currentAngle + 1) % 4;
	}
	
	private void stateChange()
	{
		currentState = (currentState + 1) % 2;
		repetitions++;
		if(repetitions > 8)
			rotate();
	}
	
	float[] normalizedContainer = new float[2];
	
	private void calculateWaypoint()
	{
		Utility.normalizeVector(normalizedContainer, this.getOriginX() - target.getOriginX(), this.getOriginY() - target.getOriginY());
		float distX = Math.abs(normalizedContainer[0]) * ((currentAngle == 0 || currentAngle == 1) ? 1 : -1) * distance(currentState);
		float distY = Math.abs(normalizedContainer[1]) * ((currentAngle == 1 || currentAngle == 2) ? 1 : -1) * distance(currentState);
		goTo(target.getOriginX() + distX, target.getOriginY() + distY);
		Log.e("Nuker #" + this.Nr, "distX: " + distX + ", distY: " + distY);
	}
	
	private float distance(int state)
	{
		return (currentState == 0 ? factor1 : factor2) * (2 / 3f * this.shootRange + 2 / 3f * GameHelper.$.getRandom().nextInt((int) this.shootRange));
	}
}
