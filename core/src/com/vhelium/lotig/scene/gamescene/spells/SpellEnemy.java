package com.vhelium.lotig.scene.gamescene.spells;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellAirBlow;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellEarthBall;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellEnhancedDefense;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellFireball;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellFlameBall;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellFlameShield;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellFlameSlam;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellFlameWall;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellGroundSlam;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellLightningBolt;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellLightningSurroundShot;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellManaBurn;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellMonsterMassAttackDown;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellMonsterMassAttackUp;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellMonsterMassHeal;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellMonsterMassWeakening;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellPiercingBullet;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellPoisonBombs;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellPoisonShot;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellPoisonTrap;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellResistanceShield;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellRootShot;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellSilenceShot;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellSlowAoE;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellSlowShot;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellStunShot;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellSuicide;
import com.vhelium.lotig.scene.gamescene.spells.enemies.SpellWaterSurroundShot;

public abstract class SpellEnemy extends Spell
{
	public int priority;
	public HashMap<Integer, Float> conditions;
	
	protected SpellEnemy(int priority, HashMap<Integer, Float> conditions)
	{
		this.priority = priority;
		this.conditions = conditions;
	}
	
	public boolean isReadyToCast(EntityServer enemy)
	{
		if(cooldownLeft > 0)
			return false;
		for(Entry<Integer, Float> condition : conditions.entrySet())
		{
			switch(condition.getKey())
			{
				case SpellCondition.HEALTH_PERCENTAGE_BELOW:
					if(enemy.getHp() / enemy.getMaxHp() * 100f <= condition.getValue())
						continue;
					else
						return false;
			}
		}
		return true;
	}
	
	public abstract void activate(Realm realm, int shooterId, float x, float y, float rotation, float dirX, float dirY);
	
	public void update(float delta)
	{
		if(cooldownLeft > 0)
			cooldownLeft -= delta;
	}
	
	public void useCooldown()
	{
		cooldownLeft = getCooldown();
	}
	
	public static SpellEnemy getSpell(String name, int level, int priority, HashMap<Integer, Float> conditions)
	{
		SpellEnemy res = null;
		
		if(name.equalsIgnoreCase(SpellEarthBall.name))
			res = new SpellEarthBall(priority, conditions);
		else if(name.equalsIgnoreCase(SpellAirBlow.name))
			res = new SpellAirBlow(priority, conditions);
		else if(name.equalsIgnoreCase(SpellAirBlow.name + " Wide"))
			res = new SpellAirBlow(priority, conditions, "Air Blow Wide");
		else if(name.equalsIgnoreCase(SpellEnhancedDefense.name))
			res = new SpellEnhancedDefense(priority, conditions);
		else if(name.equalsIgnoreCase(SpellFireball.name))
			res = new SpellFireball(priority, conditions);
		else if(name.equalsIgnoreCase(SpellFlameBall.name))
			res = new SpellFlameBall(priority, conditions);
		else if(name.equalsIgnoreCase(SpellFlameShield.name))
			res = new SpellFlameShield(priority, conditions);
		else if(name.equalsIgnoreCase(SpellFlameSlam.name))
			res = new SpellFlameSlam(priority, conditions);
		else if(name.equalsIgnoreCase(SpellFlameWall.name))
			res = new SpellFlameWall(priority, conditions);
		else if(name.equalsIgnoreCase(SpellGroundSlam.name))
			res = new SpellGroundSlam(priority, conditions);
		else if(name.equalsIgnoreCase(SpellMonsterMassAttackDown.name))
			res = new SpellMonsterMassAttackDown(priority, conditions);
		else if(name.equalsIgnoreCase(SpellMonsterMassAttackUp.name))
			res = new SpellMonsterMassAttackUp(priority, conditions);
		else if(name.equalsIgnoreCase(SpellMonsterMassHeal.name))
			res = new SpellMonsterMassHeal(priority, conditions);
		else if(name.equalsIgnoreCase("Piercing Bullet"))
			res = new SpellPiercingBullet(priority, conditions, "Piercing Bullet");
		else if(name.equalsIgnoreCase(SpellPoisonBombs.name))
			res = new SpellPoisonBombs(priority, conditions);
		else if(name.equalsIgnoreCase(SpellPoisonShot.name + " Wide"))
			res = new SpellPoisonShot(priority, conditions, SpellPoisonShot.name + " Wide");
		else if(name.equalsIgnoreCase(SpellPoisonShot.name))
			res = new SpellPoisonShot(priority, conditions);
		else if(name.equalsIgnoreCase(SpellPoisonTrap.name))
			res = new SpellPoisonTrap(priority, conditions);
		else if(name.equalsIgnoreCase(SpellRootShot.name))
			res = new SpellRootShot(priority, conditions);
		else if(name.equalsIgnoreCase(SpellSilenceShot.name))
			res = new SpellSilenceShot(priority, conditions);
		else if(name.equalsIgnoreCase(SpellSlowShot.name))
			res = new SpellSlowShot(priority, conditions);
		else if(name.equalsIgnoreCase(SpellSlowShot.name + " Wide"))
			res = new SpellSlowShot(priority, conditions, SpellSlowShot.name + " Wide");
		else if(name.equalsIgnoreCase(SpellResistanceShield.name))
			res = new SpellResistanceShield(priority, conditions);
		else if(name.equalsIgnoreCase(SpellStunShot.name))
			res = new SpellStunShot(priority, conditions);
		else if(name.equalsIgnoreCase(SpellStunShot.name + " Wide"))
			res = new SpellStunShot(priority, conditions, SpellStunShot.name + " Wide");
		else if(name.equalsIgnoreCase(SpellSuicide.name))
			res = new SpellSuicide(priority, conditions);
		else if(name.equalsIgnoreCase(SpellWaterSurroundShot.name))
			res = new SpellWaterSurroundShot(priority, conditions);
		else if(name.equalsIgnoreCase(SpellSlowAoE.name))
			res = new SpellSlowAoE(priority, conditions);
		else if(name.equalsIgnoreCase(SpellLightningBolt.name))
			res = new SpellLightningBolt(priority, conditions);
		else if(name.equalsIgnoreCase(SpellLightningSurroundShot.name))
			res = new SpellLightningSurroundShot(priority, conditions);
		else if(name.equalsIgnoreCase(SpellManaBurn.name))
			res = new SpellManaBurn(priority, conditions);
		else if(name.equalsIgnoreCase(SpellMonsterMassWeakening.name))
			res = new SpellMonsterMassWeakening(priority, conditions);
		
		if(res != null)
			res.setLevel(level);
		else
			Log.e("SpellEnemy", "SPELL NOT FOUND: " + name);
		
		return res;
	}
	
	@Override
	public String getDescription()
	{
		return null;
	}
	
	@Override
	public DataPacket generateRequest(float x, float y, float rotation, float dirX, float dirY)
	{
		return null;
	}
}
