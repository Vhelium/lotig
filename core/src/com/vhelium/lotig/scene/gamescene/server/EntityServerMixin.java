package com.vhelium.lotig.scene.gamescene.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.EntityMixin;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.player.MovementModifier;

public abstract class EntityServerMixin implements EntityMixin
{
	public int realmID;
	public int Nr = -1;
	
	protected String soundHit = "eh_hit";
	
	protected HashMap<String, Condition> conditions = new HashMap<String, Condition>();
	protected List<String> conditionsToRemove = new ArrayList<String>();
	
	protected HashMap<String, Integer> tempAttributes = new HashMap<String, Integer>();
	
	protected final List<EntityServerMinion> minions = new ArrayList<EntityServerMinion>();
	
	//=============== HP =================
	private float hp;
	
	public float getHp()
	{
		return hp;
	}
	
	public void setHp(float hp)
	{
		this.hp = hp;
		if(this.hp > maxHp)
			this.hp = maxHp;
	}
	
	public void doHeal(float amount)
	{
		setHp(getHp() + amount);
	}
	
	private float maxHp;
	
	public float getMaxHp()
	{
		return maxHp;
	}
	
	public void setMaxHp(float maxHp)
	{
		this.maxHp = maxHp;
	}
	
	private float hpRegSec;
	
	public float getHpRegSec()
	{
		if(tempAttributes.containsKey("HPREG"))
			return hpRegSec + tempAttributes.get("HPREG");
		else
			return hpRegSec;
	}
	
	public void setHpRegSec(float hpRegSec)
	{
		this.hpRegSec = hpRegSec;
	}
	
	@Override
	public boolean isDead()
	{
		return hp <= 0;
	}
	
	//====================================
	
	//=============== MANA ===============
	private float mana;
	
	public float getMana()
	{
		return mana;
	}
	
	public void setMana(float mana)
	{
		this.mana = mana;
		if(this.mana > maxMana)
			this.mana = maxMana;
	}
	
	private float maxMana;
	
	public float getMaxMana()
	{
		return maxMana;
	}
	
	public void setMaxMana(float maxMana)
	{
		this.maxMana = maxMana;
	}
	
	private float manaRegSec;
	
	public float getManaRegSec()
	{
		if(tempAttributes.containsKey("MANAREG"))
			return manaRegSec + tempAttributes.get("MANAREG");
		else
			return manaRegSec;
	}
	
	public void setManaRegSec(float manaRegSec)
	{
		this.manaRegSec = manaRegSec;
	}
	
	//return true if hp/mana changed and players must be informed
	public boolean recoverHpAndMana(float delta, boolean pvp)
	{
		boolean returnValue = false;
		
		if(!pvp)
		{
			float hpOld = hp;
			setHp(hpOld + delta / (1000 / hpRegSec));
			if(hpOld != hp)
				returnValue = true;
		}
		
		float manaOld = mana;
		setMana(manaOld + delta / (1000 / manaRegSec));
		if(manaOld != mana)
			returnValue = true;
		
		return returnValue;
	}
	
	//===================================
	
	//=============== DMG ===============
	private float damage;
	
	public float getDamage()
	{
		if(tempAttributes.containsKey("DMG"))
			return damage + tempAttributes.get("DMG");
		else
			return damage;
	}
	
	public void setDamage(float damage)
	{
		this.damage = damage;
	}
	
	private float damageBonus;
	
	public float getBonusDamage()
	{
		if(tempAttributes.containsKey("BONUSDMG"))
			return damageBonus + tempAttributes.get("BONUSDMG");
		else
			return damageBonus;
	}
	
	public void setDamageBonus(float damageBonus)
	{
		this.damageBonus = damageBonus;
	}
	
	public float getRandomBonusDamage()
	{
		return GameHelper.$.getRandom().nextInt((int) getBonusDamage()) + 1;
	}
	
	public float DoDamage(float damage, int damageType, boolean pvp)
	{
		float reductionFactor;
		switch(damageType)
		{
			case DamageType.Physical:
				reductionFactor = getDamageReductionFactor(getArmor(), Constants.maxArmor, Constants.maxArmorReduction);
				break;
			case DamageType.Fire:
				reductionFactor = getDamageReductionFactor(getFireRes(), Constants.maxResi, Constants.maxResiReduction);
				break;
			case DamageType.Cold:
				reductionFactor = getDamageReductionFactor(getColdRes(), Constants.maxResi, Constants.maxResiReduction);
				break;
			case DamageType.Lightning:
				reductionFactor = getDamageReductionFactor(getLightningRes(), Constants.maxResi, Constants.maxResiReduction);
				break;
			case DamageType.Poison:
				reductionFactor = getDamageReductionFactor(getPoisonRes(), Constants.maxResi, Constants.maxResiReduction);
				break;
			default:
				reductionFactor = 1f;
				break;
		}
		
		float dmgDone = Math.max(damage * reductionFactor * (pvp ? Constants.pvpDmgMultiplier : 1), 1);
		
		if(getAbsorb() > 0)
		{
			float maxAbsorb = getAbsorb();
			if(maxAbsorb >= dmgDone)
			{
				minusAbsorb(dmgDone);
				return 0;//everything absorbed
			}
			else
			{
				minusAbsorb(maxAbsorb);
				dmgDone -= maxAbsorb;
			}
		}
		
		setHp(getHp() - dmgDone);
		
		return dmgDone;
	}
	
	protected float getDamageReductionFactor(float res, float maxRes, float maxReduct)
	{
		if(res >= 5000)
			return 1 - res / 10000f;
		else
			return (float) (1f - (Math.atan(res * 5 / maxRes) * maxReduct / 1.373f));
	}
	
	//===================================
	
	//============== ARMOR ==============
	private float armor;
	
	public float getArmor()
	{
		if(tempAttributes.containsKey("ARMOR"))
			return armor + tempAttributes.get("ARMOR");
		else
			return armor;
	}
	
	public void setArmor(float armor)
	{
		this.armor = armor;
	}
	
	private float thorns;
	
	public float getThorns()
	{
		if(tempAttributes.containsKey("THORNS"))
			return thorns + tempAttributes.get("THORNS");
		else
			return thorns;
	}
	
	public void setThorns(float thorns)
	{
		this.thorns = thorns;
	}
	
	private float absorb;
	
	public float getAbsorb()
	{
		if(tempAttributes.containsKey("ABSORB"))
			return absorb + tempAttributes.get("ABSORB");
		else
			return absorb;
	}
	
	public void setAbsorb(float absorb)
	{
		this.absorb = absorb;
	}
	
	public void minusAbsorb(float absorb)
	{
		if(tempAttributes.containsKey("ABSORB"))
		{
			if(tempAttributes.get("ABSORB") > absorb)
				tempAttributes.put("ABSORB", (int) (tempAttributes.get("ABSORB") - absorb));
			else if(tempAttributes.get("ABSORB") == absorb)
				tempAttributes.remove("ABSORB");
			else
			{
				this.absorb -= absorb - tempAttributes.get("ABSORB");
				tempAttributes.remove("ABSORB");
			}
		}
		else
			this.absorb -= absorb;
		
		if(this.absorb < 0)
			this.absorb = 0;
	}
	
	//===================================
	
	//=========== RESISTANCE ============
	private float fireRes;
	
	public float getFireRes()
	{
		if(tempAttributes.containsKey("FRES"))
			return fireRes + tempAttributes.get("FRES");
		else
			return fireRes;
	}
	
	public void setFireRes(float res)
	{
		this.fireRes = res;
	}
	
	private float coldRes;
	
	public float getColdRes()
	{
		if(tempAttributes.containsKey("CRES"))
			return coldRes + tempAttributes.get("CRES");
		else
			return coldRes;
	}
	
	public void setColdRes(float res)
	{
		this.coldRes = res;
	}
	
	private float lightningRes;
	
	public float getLightningRes()
	{
		if(tempAttributes.containsKey("LRES"))
			return lightningRes + tempAttributes.get("LRES");
		else
			return lightningRes;
	}
	
	public void setLightningRes(float res)
	{
		this.lightningRes = res;
	}
	
	private float poisonRes;
	
	public float getPoisonRes()
	{
		if(tempAttributes.containsKey("PRES"))
			return poisonRes + tempAttributes.get("PRES");
		else
			return poisonRes;
	}
	
	public void setPoisonRes(float res)
	{
		this.poisonRes = res;
	}
	
	//=================================
	
	//============ SPECIAL ============
	
	private float lifePerHit;
	
	public float getLifePerHit()
	{
		if(tempAttributes.containsKey("LPH"))
			return lifePerHit + tempAttributes.get("LPH");
		else
			return lifePerHit;
	}
	
	public void setLifePerHit(float lifePerHit)
	{
		this.lifePerHit = lifePerHit;
	}
	
	private float manaPerHit;
	
	public float getManaPerHit()
	{
		if(tempAttributes.containsKey("MPH"))
			return manaPerHit + tempAttributes.get("MPH");
		else
			return manaPerHit;
	}
	
	public void setManaPerHit(float manaPerHit)
	{
		this.manaPerHit = manaPerHit;
	}
	
	//=================================
	
	public abstract void addMovementModifier(String name, MovementModifier modifier);
	
	public abstract void addMovementModifier(String name, float dirX, float dirY, float velocity, float decay);
	
	//=========== Position ============
	protected Rectangle rectangle;
	protected float rotation = 0f;
	
	public Rectangle getRectangle()
	{
		return rectangle;
	}
	
	@Override
	public float getOriginX()
	{
		return rectangle.getX() + rectangle.getWidth() / 2;
	}
	
	@Override
	public float getOriginY()
	{
		return rectangle.getY() + rectangle.getHeight() / 2;
	}
	
	public float getX()
	{
		return rectangle.getX();
	}
	
	public void setX(float x)
	{
		rectangle.setX(x);
	}
	
	public float getY()
	{
		return rectangle.getY();
	}
	
	public void setY(float y)
	{
		rectangle.setY(y);
	}
	
	public float getWidth()
	{
		return rectangle.getWidth();
	}
	
	public float getHeight()
	{
		return rectangle.getHeight();
	}
	
	public float getRotation()
	{
		return rotation;
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	public abstract void addCondition(Condition condition);
	
	public abstract void removeCondition(String condition);
	
	protected boolean stunned = false;
	
	public boolean isStunned()
	{
		return stunned;
	}
	
	protected boolean rooted = false;
	
	public boolean isRooted()
	{
		return rooted;
	}
	
	protected boolean silenced = false;
	
	public boolean isSilenced()
	{
		return silenced;
	}
	
	public int getTempAttribute(String ATTR)
	{
		ATTR = ATTR.toUpperCase();
		if(tempAttributes.containsKey(ATTR))
			return tempAttributes.get(ATTR);
		else
			return 0;
	}
	
	public void removeRandomMali(int count)
	{
		List<String> negativeConditions = new ArrayList<String>();
		for(Entry<String, Condition> e : conditions.entrySet())
			if(e.getValue().isNegative())
				negativeConditions.add(e.getKey());
		int bonisToRemove = Math.min(negativeConditions.size(), count);
		for(int i = 0; i < bonisToRemove; i++)
			removeCondition(negativeConditions.get(i));
	}
	
	public abstract int getTeamNr();
	
	public List<EntityServerMinion> getMinions()
	{
		return minions;
	}
	
	public void addMinion(EntityServerMinion minion)
	{
		minions.add(minion);
	}
	
	public void removeMinion(EntityServerMinion minion)
	{
		minions.remove(minion);
	}
	
	public List<EntityServerMinion> getMinions(String name)
	{
		List<EntityServerMinion> res = new ArrayList<EntityServerMinion>();
		for(EntityServerMinion minion : minions)
			if(minion.name.equals(name))
				res.add(minion);
		return res;
	}
	
	public String getSoundHit()
	{
		return soundHit;
	}
}
