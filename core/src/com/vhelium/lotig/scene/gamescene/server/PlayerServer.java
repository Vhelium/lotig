package com.vhelium.lotig.scene.gamescene.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.client.player.MovementModifier;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCharge;

public class PlayerServer extends EntityServerMixin
{
	IServerLevelCallback callback;
	private String asset;
	
	public List<int[]> damageNumbersToInform;
	
	private float shootSpeed = 350;
	private float shootCooldownLeft = 0;
	
	private float fromX = 0;
	private float fromY = 0;
	private float toX = 0;
	private float toY = 0;
	
	private int lastPingTime = 1;
	private int timeSinceLastUpdate = 0;
	
	public boolean isLoading = false;
	
	private boolean isCharging = false;
	
	public boolean wasFullRegged = false;
	
	private int teamNr = 0;
	public boolean wasDead = false;
	
	public PlayerServer(IServerLevelCallback callback, int nr, DataPacket dp)
	{
		this.callback = callback;
		this.Nr = nr;
		rectangle = new Rectangle(0, 0, PlayerClient.WIDTH, PlayerClient.HEIGHT);
		damageNumbersToInform = new ArrayList<int[]>();
		
		asset = dp.getString();
		setRotation(dp.getFloat());
		
		setMaxHp(dp.getFloat());
		setHpRegSec(dp.getFloat());
		setMaxMana(dp.getFloat());
		setManaRegSec(dp.getFloat());
		
		shootSpeed = dp.getFloat();
		setDamage(dp.getFloat());
		setDamageBonus(dp.getFloat());
		
		setArmor(dp.getFloat());
		setFireRes(dp.getFloat());
		setColdRes(dp.getFloat());
		setLightningRes(dp.getFloat());
		setPoisonRes(dp.getFloat());
		
		setLifePerHit(dp.getFloat());
		setManaPerHit(dp.getFloat());
		
		setThorns(dp.getFloat());
		setAbsorb(dp.getFloat());
		
		setHp(dp.getFloat());
		setMana(dp.getFloat());
	}
	
	public void update(float delta, long millis)
	{
		if(shootCooldownLeft > 0)
			shootCooldownLeft -= delta;
		
		timeSinceLastUpdate += delta;
		float i = (float) timeSinceLastUpdate / lastPingTime;
		
		if(i > 1)
			i = 1;
		if(i < 0)
			i = 0;
		
		setX(inperpolateF(fromX, toX, i + 0.5f));
		setY(inperpolateF(fromY, toY, i + 0.5f));
		
		boolean prevStunned = stunned;
		stunned = false;
		boolean prevRooted = rooted;
		rooted = false;
		boolean prevSilenced = silenced;
		silenced = false;
		isCharging = false;
		for(Entry<String, Condition> condition : conditions.entrySet())
		{
			long elapsed = millis - condition.getValue().getAbsolutTimeSinceStart();
			if(elapsed >= condition.getValue().getDuration())
			{
				conditionsToRemove.add(condition.getKey());
			}
			
			if(condition.getKey().equals(SpellCharge.name))
				isCharging = true;
			
			else if(condition.getValue().getName().equalsIgnoreCase("Pot HOT") || condition.getValue().getName().equalsIgnoreCase("HOT"))
				this.setHp(getHp() + delta / condition.getValue().getDuration() * condition.getValue().getValue());
			
			else if(condition.getValue().getName().equalsIgnoreCase("Pot MOT") || condition.getValue().getName().equalsIgnoreCase("MOT"))
				this.setMana(getMana() + delta / condition.getValue().getDuration() * condition.getValue().getValue());
			
			else if(condition.getValue().containsValue(UniqueCondition.Stunned))
				stunned = true;
			
			else if(condition.getValue().containsValue(UniqueCondition.Silenced))
				silenced = true;
			
			else if(condition.getValue().containsValue("Root"))
				rooted = true;
			
			else if(condition.getValue().containsValue("Cursed"))
			{
				this.setHp(getHp() + delta / condition.getValue().getDuration() * condition.getValue().getValue());
				if((int) ((millis - condition.getValue().getAbsolutTimeSinceStart()) / 1000f) != (int) ((millis - condition.getValue().getAbsolutTimeSinceStart() - delta) / 1000f))
				{
					damageNumbersToInform.add(new int[] { (int) (1000f / condition.getValue().getDuration() * -condition.getValue().getValue()), DamageType.Absolute });
				}
			}
			
			else if(condition.getValue().containsValue(UniqueCondition.Burning))
			{
				float dmg = delta / condition.getValue().getDuration() * condition.getValue().getValue() * (1f - getFireRes() / 1500);
				this.setHp(getHp() - dmg);
				if((int) ((millis - condition.getValue().getAbsolutTimeSinceStart()) / 1000f) != (int) ((millis - condition.getValue().getAbsolutTimeSinceStart() - delta) / 1000f))
				{
					damageNumbersToInform.add(new int[] { (int) (1000f / condition.getValue().getDuration() * -condition.getValue().getValue() * (1f - getFireRes() / 1500)), DamageType.Fire });
				}
			}
			
			else if(condition.getValue().containsValue(UniqueCondition.Poisoned))
			{
				float dmg = delta / condition.getValue().getDuration() * condition.getValue().getValue() * (1f - getPoisonRes() / 1500);
				this.setHp(getHp() - dmg);
				if((int) ((millis - condition.getValue().getAbsolutTimeSinceStart()) / 1000f) != (int) ((millis - condition.getValue().getAbsolutTimeSinceStart() - delta) / 1000f))
				{
					damageNumbersToInform.add(new int[] { (int) (1000f / condition.getValue().getDuration() * -condition.getValue().getValue() * (1f - getPoisonRes() / 1500)), DamageType.Poison });
				}
			}
			
			else if(condition.getValue().containsValue("ABSORB"))
			{
				if(getAbsorb() <= 0)
					conditionsToRemove.add(condition.getKey());
			}
			
		}
		if(prevStunned != stunned)
		{
			DataPacket dp = new DataPacket(MessageType.MSG_PLAYER_STUNNED);
			dp.setBoolean(stunned);
			callback.sendDataPacketToPlayer(Nr, dp);
		}
		if(prevRooted != rooted)
		{
			DataPacket dp = new DataPacket(MessageType.MSG_PLAYER_ROOTED);
			dp.setBoolean(rooted);
			callback.sendDataPacketToPlayer(Nr, dp);
		}
		if(prevSilenced != silenced)
		{
			DataPacket dp = new DataPacket(MessageType.MSG_PLAYER_SILENCED);
			dp.setBoolean(silenced);
			callback.sendDataPacketToPlayer(Nr, dp);
		}
		
		for(String s : conditionsToRemove)
			removeCondition(s);
		
		conditionsToRemove.clear();
	}
	
	public void updatePositionSmooth(float x, float y, float rot)
	{
		fromX = toX;
		fromY = toY;
		toX = x;
		toY = y;
		setRotation(rot);
		lastPingTime = timeSinceLastUpdate;
		timeSinceLastUpdate = 0;
	}
	
	public float getShootSpeed()
	{
		return shootSpeed;
	}
	
	public void setShootspeed(float value)
	{
		shootSpeed = value;
	}
	
	public void shoot()
	{
		shootCooldownLeft = shootSpeed;
	}
	
	@Override
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	public boolean isShooting()
	{
		return shootCooldownLeft > 0;
	}
	
	private float inperpolateF(float a, float b, float i)
	{
		return (b - a) * i + a;
	}
	
	public String getBulletType()
	{
		return "Normal";
	}
	
	public String getAsset()
	{
		return asset;
	}
	
	@Override
	public int getTeamNr()
	{
		return teamNr;
	}
	
	public void setTeamNr(int team)
	{
		teamNr = team;
	}
	
	@Override
	public void addCondition(Condition condition)
	{
		if(conditions.containsKey(condition.getName()))
			removeCondition(condition.getName());
		if(condition.isAttribute())
		{
			DataPacket dp = new DataPacket(MessageType.MSG_PLAYER_TEMP_ATTR_CHANGE);
			if(condition.getBuffs() == null)
			{
				dp.setInt(1);
				dp.setString(condition.getValueName());
				dp.setInt(condition.getValue());
			}
			else
			{
				dp.setInt(condition.getBuffs().size());
				for(Entry<String, Integer> e : condition.getBuffs().entrySet())
				{
					dp.setString(e.getKey());
					dp.setInt(e.getValue());
				}
			}
			callback.sendDataPacketToPlayer(Nr, dp);
		}
		conditions.put(condition.getName(), condition);
	}
	
	@Override
	public void removeCondition(String cond)
	{
		Condition condition = conditions.get(cond);
		if(condition == null)
			return;
		if(condition.isAttribute())
		{
			DataPacket dp = new DataPacket(MessageType.MSG_PLAYER_TEMP_ATTR_CHANGE);
			if(condition.getBuffs() == null)
			{
				dp.setInt(1);
				dp.setString(condition.getValueName());
				dp.setInt(-condition.getValue());
			}
			else
			{
				dp.setInt(condition.getBuffs().size());
				for(Entry<String, Integer> e : condition.getBuffs().entrySet())
				{
					dp.setString(e.getKey());
					dp.setInt(-e.getValue());
				}
			}
			callback.sendDataPacketToPlayer(Nr, dp);
		}
		if(!conditions.get(cond).isFinished())
		{
			DataPacket dp = new DataPacket(MessageType.MSG_REMOVE_CONDITION);
			dp.setInt(Nr);
			dp.setString(cond);
			callback.sendDataPacketToAllPlayersInThisRealm(Nr, dp);
		}
		if(condition.getAftereffect() != null)
		{
			Condition c = condition.getAftereffect();
			c.setAbsoluteTimeSinceStart(System.currentTimeMillis());
			addCondition(c);
			
			if(!c.getEffect().equals(""))
			{
				DataPacket dpOthers = new DataPacket();
				dpOthers.setInt(MessageType.MSG_ADD_CONDITION_EFFECT_OTHERS);
				dpOthers.setInt(Nr);
				dpOthers.setString(c.getName());
				dpOthers.setInt(c.getDuration());
				dpOthers.setLong(System.currentTimeMillis());
				dpOthers.setString(c.getEffect());
				
				callback.sendDataPacketToAllPlayersInThisRealmExcept(Nr, dpOthers);
				
				DataPacket dpAnswer = new DataPacket();
				dpAnswer.setInt(MessageType.MSG_ADD_CONDITION);
				dpAnswer.setString(c.getName());
				dpAnswer.setInt(c.getDuration());
				dpAnswer.setLong(System.currentTimeMillis());
				dpAnswer.setString(c.getEffect());
				
				callback.sendDataPacketToPlayer(Nr, dpAnswer);
			}
		}
		conditions.remove(cond);
	}
	
	public Condition getCondition(String name)
	{
		return conditions.get(name);
	}
	
	public HashMap<String, Condition> getConditions()
	{
		return conditions;
	}
	
	public void removeAllConditions()
	{
		List<String> condisToRem = new ArrayList<String>(conditions.keySet());
		for(String c : condisToRem)
			removeCondition(c);
	}
	
	public void setNewPosition(float x, float y)
	{
		fromX = x;
		fromY = y;
		toX = x;
		toY = y;
		setX(x);
		setY(y);
	}
	
	public boolean isCharging()
	{
		return isCharging;
	}
	
	public void setAsset(String skin)
	{
		this.asset = skin;
	}
	
	@Override
	public void addMovementModifier(String name, MovementModifier modifier)
	{
		addMovementModifier(name, modifier.dirX, modifier.dirY, modifier.velocity, modifier.decay);
	}
	
	@Override
	public void addMovementModifier(String name, float dirX, float dirY, float velocity, float decay)
	{
		DataPacket dp = new DataPacket(MessageType.MSG_ADD_MOVEMENT_MODIFIER);
		dp.setString(name);
		dp.setFloat(dirX);
		dp.setFloat(dirY);
		dp.setFloat(velocity);
		dp.setFloat(decay);
		callback.sendDataPacketToPlayer(this.Nr, dp);
	}
	
	public String getDeathAnimation()
	{
		return SoundFile.da_player;
	}
}
