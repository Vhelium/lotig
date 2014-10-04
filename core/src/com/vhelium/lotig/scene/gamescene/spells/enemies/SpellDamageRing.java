package com.vhelium.lotig.scene.gamescene.spells.enemies;

import java.util.HashMap;
import java.util.Map.Entry;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public class SpellDamageRing extends SpellEnemy
{
	public static String name = "Spike Ring";
	public static String effectAsset = "Spike Ring";
	public static String effectWarningAsset = "Spike Ring Warning";
	
	public float cooldownBase = 18000;
	public float cooldownPerLevel = 100f;
	
	public static float manaCostBase = 0;
	public static float manaCostPerLevel = 0;
	
	public float damageBase = 60;//!!!
	public float damagePerLevel = 50;
	
	public float damageBonusBase = 15;//!!!
	public float damageBonusPerLevel = 10;
	
	public int aoeRadiusBase = 400;
	
	public int aoeWidth = 125;
	public float warningSeconds = 2f;
	
	public int damageType = DamageType.Physical;
	
	public SpellDamageRing(int priority, HashMap<Integer, Float> conditions)
	{
		super(priority, conditions);
		instantCast = true;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public float getManaCost()
	{
		return getManaCostStatic(getLevel());
	}
	
	@Override
	public float getCooldown()
	{
		return cooldownBase + cooldownPerLevel * (getLevel() - 1);
	}
	
	@Override
	public void activate(final Realm realm, final int shooterId, final float x, final float y, float rotation, float dirX, float dirY)
	{
		final int radius = aoeRadiusBase;
		realm.playEffect(x, y, radius * 2, radius * 2, effectWarningAsset, (int) (warningSeconds * 1000f));
		Timer.schedule(new Task()
		{
			@Override
			public void run()
			{
				if(realm.getEntity(shooterId) == null || realm.getEntity(shooterId).isDead())
					return;
				realm.playEffect(x, y, radius * 2, radius * 2, effectAsset, 0);
				realm.playSound(SoundFile.spell_tremble, x, y);
				
				HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(x, y, radius);
				for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
				{
					if(entity.getValue().getTeamNr() != realm.getEntity(shooterId).getTeamNr())
					{
						double dist = Math.sqrt(Math.pow(x - entity.getValue().getOriginX(), 2) + Math.pow(y - entity.getValue().getOriginY(), 2));
						if(dist <= radius + 10 && dist >= radius - aoeWidth - 10)
						{
							//DEAL DAMAGE!
							float dmg = entity.getValue().DoDamage(getDamage(getLevel()) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(getLevel()) + 1), damageType, realm.isPvP());
							realm.sendDamageNumberToAllPlayers(entity.getKey(), dmg, damageType);
						}
					}
				}
			}
		}, warningSeconds);
	}
	
	public float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public float getDamage(int level)
	{
		return damageBase + damagePerLevel * (level - 1);
	}
	
	public float getBonusDamage(int level)
	{
		return damageBonusBase + damageBonusPerLevel * (level - 1);
	}
}
