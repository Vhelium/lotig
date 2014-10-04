package com.vhelium.lotig.scene.gamescene.server.enemy.boss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import com.vhelium.lotig.constants.DropGenerator;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellPriorityComparor;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellDamageRing;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellGroundSlam;

public class BossEridus extends EntityServer
{
	float initX, initY;
	private int chaseTurnsLeft = 0;
	private final int maxChaseTurns = 12;
	private final int delayTime = 2600;
	private float delayTimeLeft = 0;
	
	public BossEridus(Realm realm, int difficulty, int level, int playerCount)
	{
		super(realm);
		
		SpellGroundSlam spellGS = new SpellGroundSlam(2, new HashMap<Integer, Float>());
		spellGS.aoeRadiusBase = 140;
		spellGS.aoeRadiusPerLevel = 4;
		spellGS.cooldownBase = 10000;
		spellGS.setLevel(4 + level);
		spells.add(spellGS);
		
		SpellDamageRing spellSpikes = new SpellDamageRing(1, new HashMap<Integer, Float>());
		spellSpikes.cooldownBase = 5000;
		spellSpikes.setLevel(5 + level);
		spells.add(spellSpikes);
		
		Collections.sort(spells, new SpellPriorityComparor());
	}
	
	@Override
	public void update(float delta, long millis, Collection<? extends EntityServerMixin>... enemies)
	{
		super.update(delta, millis, enemies);
		if(delayTimeLeft > 0)
		{
			delayTimeLeft -= delta;
			if(delayTimeLeft <= 0)
				chaseTurnsLeft = maxChaseTurns;
		}
	}
	
	@Override
	public void spawned()
	{
		initX = getX();
		initY = getY();
	}
	
	private void dropItems()
	{
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(DropGenerator.dropWeaponOrOffHand(this.level + 2, realm.getLevel().getDifficulty()));
		
		realm.dropItems(items, this.getOriginX(), this.getOriginY());
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
		if(chaseTurnsLeft > 0)
			moveChase();
		else
			delayTimeLeft = delayTime;
	}
	
	@Override
	protected void onNoWaypointFound()
	{
		chaseTurnsLeft = 0;
		delayTimeLeft = delayTime / 2;
	}
	
	@Override
	protected void onNewTargetFound()
	{
		chaseTurnsLeft = maxChaseTurns;
		moveChase();
	}
	
	@Override
	protected void onTargetLost()
	{
		onNoWaypointFound();
	}
	
	@Override
	protected void onTargedMovedFarSinceLastWP()
	{
		if(chaseTurnsLeft > 0)
			moveChase();
	}
	
	private void moveChase()
	{
		chaseTurnsLeft--;
		float newX = target.getX() - 60 + GameHelper.getInstance().getRandom().nextInt(120);
		float newY = target.getY() - 60 + GameHelper.getInstance().getRandom().nextInt(120);
		//Add signum(x/y) * minDistance?
		goTo(newX, newY);
	}
}