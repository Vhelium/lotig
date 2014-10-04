package com.vhelium.lotig.scene.gamescene.server;

import java.util.concurrent.ConcurrentHashMap;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_DefenseTower.DefenseTowerState;

public class EntityServerDefenseTower extends EntityServerMinion
{
	private int state = 1;
	private final ConcurrentHashMap<Integer, Condition> conditionsForStates = new ConcurrentHashMap<Integer, Condition>();
	private final ConcurrentHashMap<Integer, Integer> splashForStates = new ConcurrentHashMap<Integer, Integer>();
	
	public EntityServerDefenseTower(Realm realm)
	{
		super(realm, null);
		targetUpdateTime = 400 + GameHelper.getInstance().getRandom().nextFloat() * 200f;
		shootRequest = new ShootRequestAimLock();
		
//		conditionsForStates.put(DefenseTowerState.UNBUILT, null);
//		conditionsForStates.put(DefenseTowerState.BASE, null);
//		
//		conditionsForStates.put(DefenseTowerState.PHYSICAL1, null);
//		conditionsForStates.put(DefenseTowerState.PHYSICAL2, null);
//		conditionsForStates.put(DefenseTowerState.PHYSICAL3, null);
		splashForStates.put(DefenseTowerState.PHYSICAL4, 120);
		
		conditionsForStates.put(DefenseTowerState.ICE1, new Condition("TowerSlow", "SPD", -80, 3000, true, "Frozen", 0));
		conditionsForStates.put(DefenseTowerState.ICE2, new Condition("TowerSlow", "SPD", -110, 3500, true, "Frozen", 0));
		conditionsForStates.put(DefenseTowerState.ICE3, new Condition("TowerSlow", "SPD", -150, 4000, true, "Frozen", 0));
		
		conditionsForStates.put(DefenseTowerState.FIRE1, new Condition(UniqueCondition.Burning, UniqueCondition.Burning, 100, 5100, false, UniqueCondition.Burning, 0));
		conditionsForStates.put(DefenseTowerState.FIRE2, new Condition(UniqueCondition.Burning, UniqueCondition.Burning, 200, 6100, false, UniqueCondition.Burning, 0));
		conditionsForStates.put(DefenseTowerState.FIRE3, new Condition(UniqueCondition.Burning, UniqueCondition.Burning, 300, 7100, false, UniqueCondition.Burning, 0));
	}
	
	public static int getDamage(int state)
	{
		switch(state)
		{
			case DefenseTowerState.BASE:
				return 100;
			case DefenseTowerState.PHYSICAL1:
				return 140;
			case DefenseTowerState.PHYSICAL2:
				return 200;
			case DefenseTowerState.PHYSICAL3:
				return 250;
			case DefenseTowerState.PHYSICAL4:
				return 280;
			case DefenseTowerState.ICE1:
				return 60;
			case DefenseTowerState.ICE2:
				return 80;
			case DefenseTowerState.ICE3:
				return 120;
			case DefenseTowerState.FIRE1:
				return 100;
			case DefenseTowerState.FIRE2:
				return 120;
			case DefenseTowerState.FIRE3:
				return 160;
			default:
				return 0;
		}
	}
	
	public String getAssetForState()
	{
		switch(state)
		{
			case DefenseTowerState.BASE:
				return "Ranger";
			case DefenseTowerState.PHYSICAL1:
			case DefenseTowerState.PHYSICAL2:
			case DefenseTowerState.PHYSICAL3:
				return "Barbarian";
			case DefenseTowerState.PHYSICAL4:
				return "Death Knight";
			case DefenseTowerState.ICE1:
			case DefenseTowerState.ICE2:
			case DefenseTowerState.ICE3:
				return "Dark Priest";
			case DefenseTowerState.FIRE1:
			case DefenseTowerState.FIRE2:
			case DefenseTowerState.FIRE3:
				return "Sorcerer";
			default:
				return "Druid";
		}
	}
	
	private int getDamageTypeForState(int state)
	{
		switch(state)
		{
			case DefenseTowerState.BASE:
				return DamageType.Physical;
			case DefenseTowerState.PHYSICAL1:
			case DefenseTowerState.PHYSICAL2:
			case DefenseTowerState.PHYSICAL3:
				return DamageType.Physical;
			case DefenseTowerState.PHYSICAL4:
				return DamageType.Absolute;
			case DefenseTowerState.ICE1:
			case DefenseTowerState.ICE2:
			case DefenseTowerState.ICE3:
				return DamageType.Cold;
			case DefenseTowerState.FIRE1:
			case DefenseTowerState.FIRE2:
			case DefenseTowerState.FIRE3:
				return DamageType.Fire;
			default:
				return DamageType.Physical;
		}
	}
	
	@Override
	public void shoot(float directionX, float directionY)
	{
		if(isStunned())
			return;
		if(target != null)
		{
			shootCooldownLeft = shootSpeed - getTempAttribute("AS");
			BulletOnHitEffect ohe = new BulletOnHitEffect(this.getDamage(), 0f, (splashForStates.containsKey(state) ? splashForStates.get(state) : 0), conditionsForStates.get(state));
			((ShootRequestAimLock) shootRequest).createRequest(target, getDamageTypeForState(state), shootRange, this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, bulletAsset, ohe);
		}
	}
	
	public void setState(int state)
	{
		this.state = state;
	}
	
	@Override
	protected void onWaypointReached()
	{
		
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
	protected void onTargetLost()
	{
		targetUpdateTimeElapsed = targetUpdateTime;
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		
	}
	
	@Override
	public int getTeamNr()
	{
		return 0;
	}
}
