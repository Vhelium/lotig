package com.vhelium.lotig.scene.gamescene;

import java.util.concurrent.ConcurrentHashMap;

public class Condition
{
	String name;
	String valueName = "";
	ConcurrentHashMap<String, Integer> buffs = null;
	int value;
	int duration;
	boolean isAttribute;
	String effect = "";
	long absolutTimeSinceStart;
	IConditionListener conditionListener;
	
	private Condition aftereffect;
	
	//Server
	public Condition(String name, String valueName, int value, int duration, boolean isAttribute, String effect, long absolutTimeSinceStart)
	{
		this.name = name;
		this.valueName = valueName;
		this.value = value;
		this.duration = duration;
		this.isAttribute = isAttribute;
		this.effect = effect;
		this.absolutTimeSinceStart = absolutTimeSinceStart;
	}
	
	public Condition(String name, ConcurrentHashMap<String, Integer> buffs, int duration, boolean isAttribute, String effect, long absolutTimeSinceStart)
	{
		this.name = name;
		this.buffs = buffs;
		this.duration = duration;
		this.isAttribute = isAttribute;
		this.effect = effect;
		this.absolutTimeSinceStart = absolutTimeSinceStart;
	}
	
	public Condition(String name, int duration, String effect, long absolutTimeSinceStart, IConditionListener conditionListener)
	{
		this.name = name;
		this.duration = duration;
		this.effect = effect;
		this.absolutTimeSinceStart = absolutTimeSinceStart;
		this.conditionListener = conditionListener;
	}
	
	//Client
	public Condition(String name, int duration, String effect, long absolutTimeSinceStart)
	{
		this.name = name;
		this.duration = duration;
		this.effect = effect;
		this.absolutTimeSinceStart = absolutTimeSinceStart;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getValueName()
	{
		return valueName;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public int getDuration()
	{
		return duration;
	}
	
	public boolean isAttribute()
	{
		return isAttribute;
	}
	
	public String getEffect()
	{
		return effect;
	}
	
	public long getAbsolutTimeSinceStart()
	{
		return absolutTimeSinceStart;
	}
	
	public ConcurrentHashMap<String, Integer> getBuffs()
	{
		return buffs;
	}
	
	public boolean containsValue(String name)
	{
		if(valueName.equalsIgnoreCase(name))
			return true;
		if(buffs != null)
			for(String s : buffs.keySet())
				if(s.equalsIgnoreCase(name))
					return true;
		return false;
	}
	
	public boolean isNegative()
	{
		boolean res = false;
		if(value < 0 || containsValue(UniqueCondition.Stunned) || containsValue(UniqueCondition.Burning) || containsValue("Slow") || containsValue("Cursed") || containsValue(UniqueCondition.Silenced) || containsValue(UniqueCondition.Poisoned))//TODO: ADD ALL THE NAME-MALIS!
			res = true;
		if(buffs != null)
			for(Integer i : buffs.values())
				if(i < 0)
					res = true;
		return res;
	}
	
	public boolean isFinished()
	{
		return System.currentTimeMillis() - absolutTimeSinceStart >= getDuration();
	}
	
	public Condition getAftereffect()
	{
		return aftereffect;
	}
	
	public void setAftereffect(Condition cond)
	{
		this.aftereffect = cond;
	}
	
	public void setAbsoluteTimeSinceStart(long currentTimeMillis)
	{
		absolutTimeSinceStart = currentTimeMillis;
	}
	
	public IConditionListener getConditionListener()
	{
		return conditionListener;
	}
	
	public void setListener(IConditionListener listener)
	{
		this.conditionListener = listener;
	}
}
