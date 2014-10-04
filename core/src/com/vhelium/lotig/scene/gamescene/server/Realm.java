package com.vhelium.lotig.scene.gamescene.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.constants.DropGenerator;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.IServerConnectionHandler;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Bullet;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.IConditionListener;
import com.vhelium.lotig.scene.gamescene.LootBag;
import com.vhelium.lotig.scene.gamescene.LootBagToInform;
import com.vhelium.lotig.scene.gamescene.RepeatedOnHitEffect;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ModBlink;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.server.levelobject.EventObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Chest;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Door;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Lever;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Liquid;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_NPC;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Tombstone;
import com.vhelium.lotig.scene.gamescene.spells.Spell;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCharge;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellHaste;

public class Realm
{
	public int realmID;
	protected boolean pvp;
	protected boolean dropsAllowed;
	
	protected TMXMap tmxMap;
	protected Main activity;
	protected ServerLevel level;
	protected IServerConnectionHandler connectionHandler;
	
	private static final float forcedUpdateTime = 12000f;
	public float forcedUpdateTimeLeft = 0;
	
	private static final float lairResetCounter = 10000f;
	private float lairResetCounterLeft = 0;
	
	public int bulletCounter = 0;
	public int enemyCounter = Server.MAX_SUPPORTED + 1;
	public int lootBagCounter = 0;
	protected final float playerUpdateIntervall = 70f;
	protected float playerUpdateTimeElapsed = 0f;
	
	protected final float healthUpdateIntervall = 150f;
	protected float healthUpdateTimeElapsed = 0f;
	
	protected final float bulletInformIntervall = 80f;
	protected float bulletInformTimeElapsed = 0f;
	
	protected final float lootBagsToInformIntervall = 85;
	protected float lootBagsToInformTimeElapsed = 0f;
	
	protected final float damageNumbersToInformIntervall = 75f;
	protected float damageNumbersToInformTimeElapsed = 0f;
	
	protected final float effectsToInformIntervall = 40f;
	protected float effectsToInformTimeElapsed = 0f;
	
	protected String currentLair = "no CurrentLair!!";
	protected int currentLevelNr = 1;
	
	protected ConcurrentHashMap<Integer, PlayerServer> players;
	protected ConcurrentHashMap<Integer, EntityServerMinion> playerMinions;
	protected List<Integer> playerMinionsToRemove;
	protected ConcurrentHashMap<Integer, EntityServer> enemies;
	protected EntityServer bossEntity = null;
	protected List<Integer> enemiesToRemove;
	protected ConcurrentHashMap<Integer, Bullet> playerBullets;
	protected List<Integer> playerBulletsToRemove;
	protected ConcurrentHashMap<Integer, Bullet> enemyBullets;
	protected List<Integer> enemyBulletsToRemove;
	protected ConcurrentHashMap<Integer, LootBag> lootbags;
	protected List<Integer> lootBagsToRemove;
	protected ConcurrentHashMap<Integer, LootBagToInform> bagsToInform;
	protected ArrayList<int[]> damageNumbersToInform;
	protected List<String> burnsToRemove;
	
	protected List<RepeatedOnHitEffect> delayedOnHitEffects;
	protected List<RepeatedOnHitEffect> delayedOnHitEffectsToRemove;
	
	protected List<Integer> bulletsToInform;
	
	public Realm(Main activity, int realmID, ServerLevel level, IServerConnectionHandler connectionHandler)
	{
		pvp = false;
		dropsAllowed = true;
		this.realmID = realmID;
		this.connectionHandler = connectionHandler;
		this.activity = activity;
		this.level = level;
		tmxMap = new TMXMap(activity, null, null);
		players = new ConcurrentHashMap<Integer, PlayerServer>();
		playerMinions = new ConcurrentHashMap<Integer, EntityServerMinion>();
		playerMinionsToRemove = new ArrayList<Integer>();
		enemies = new ConcurrentHashMap<Integer, EntityServer>();
		enemiesToRemove = new ArrayList<Integer>();
		playerBullets = new ConcurrentHashMap<Integer, Bullet>();
		playerBulletsToRemove = new ArrayList<Integer>();
		enemyBullets = new ConcurrentHashMap<Integer, Bullet>();
		enemyBulletsToRemove = new ArrayList<Integer>();
		bulletsToInform = new ArrayList<Integer>();
		lootbags = new ConcurrentHashMap<Integer, LootBag>();
		lootBagsToRemove = new ArrayList<Integer>();
		bagsToInform = new ConcurrentHashMap<Integer, LootBagToInform>();
		damageNumbersToInform = new ArrayList<int[]>();
		burnsToRemove = new ArrayList<String>();
		delayedOnHitEffects = new ArrayList<RepeatedOnHitEffect>();
		delayedOnHitEffectsToRemove = new ArrayList<RepeatedOnHitEffect>();
	}
	
	@SuppressWarnings("unchecked")
	public void update(float delta)
	{
		long millis = System.currentTimeMillis();
		
		if(forcedUpdateTimeLeft > 0)
			forcedUpdateTimeLeft -= delta;
		
		delayedOnHitEffectsToRemove.clear();
		for(RepeatedOnHitEffect effect : delayedOnHitEffects)
		{
			effect.repeatDelayElapsed += delta;
			if(effect.repeatDelayElapsed >= effect.getRepeatDelay())
			{
				effect.lowerRepeatCount();
				
				if(effect.onHitEffect != null && effect.onHitEffect.getAoeRadius() > 0)
				{
					if(effect.onHitEffect.centered)
						doAoeDamage(effect.owner, effect.owner.getOriginX(), effect.owner.getOriginY(), effect.onHitEffect, effect.damageType);
					else
						doAoeDamage(effect.owner, effect.originX, effect.originY, effect.onHitEffect, effect.damageType);
				}
				
				delayedOnHitEffectsToRemove.add(effect);
			}
		}
		for(RepeatedOnHitEffect effect : delayedOnHitEffectsToRemove)
			delayedOnHitEffects.remove(effect);
		
		playerBulletsToRemove.clear();
		for(Entry<Integer, Bullet> e : playerBullets.entrySet())
		{
			Bullet b = e.getValue();
			b.update(delta);
			if(!b.getIsDead())
			{
				if(b.getTarget() == null)
				{
					for(Entry<Integer, PlayerServer> player : players.entrySet())
					{
						if(!player.getValue().isDead() && player.getValue().getTeamNr() != b.getOwner().getTeamNr())//if target player is not neutral and not same team, then..
							if(checkBulletCollision(b, e.getKey(), player.getValue(), player.getKey()))
								break;
					}
					if(!b.getIsDead())
						for(Entry<Integer, EntityServer> enemy : enemies.entrySet())
						{
							if(checkBulletCollision(b, e.getKey(), enemy.getValue(), enemy.getKey()))
								break;
						}
				}
				else
					checkBulletCollision(b, e.getKey(), (EntityServerMixin) b.getTarget(), ((EntityServerMixin) b.getTarget()).Nr);
			}
			else if(b.getOnHitEffect() != null && b.getDeathByCollision() && b.getOnHitEffect().getAoeRadius() > 0)
				doAoeDamage(b);
			
			if(b.getIsDead())
			{
				playerBulletsToRemove.add(e.getKey());
			}
		}
		
		for(int i = 0; i < playerBulletsToRemove.size(); i++)
		{
			playerBullets.remove(playerBulletsToRemove.get(i));
		}
		
		enemyBulletsToRemove.clear();
		for(Entry<Integer, Bullet> e : enemyBullets.entrySet())
		{
			Bullet b = e.getValue();
			b.update(delta);
			if(!b.getIsDead())
			{
				if(b.getTarget() == null)
				{
					for(Entry<Integer, EntityServerMinion> minion : playerMinions.entrySet())
						if(checkBulletCollision(b, e.getKey(), minion.getValue(), minion.getKey()))
							break;
					if(!b.getIsDead())
						for(Entry<Integer, PlayerServer> player : players.entrySet())
							if(checkBulletCollision(b, e.getKey(), player.getValue(), player.getKey()))
								break;
				}
				else
					checkBulletCollision(b, e.getKey(), (EntityServerMixin) b.getTarget(), ((EntityServerMixin) b.getTarget()).Nr);
			}
			else if(b.getOnHitEffect() != null && b.getDeathByCollision() && b.getOnHitEffect().getAoeRadius() > 0)
				doAoeDamage(b);
			
			if(b.getIsDead())
			{
				enemyBulletsToRemove.add(e.getKey());
			}
		}
		
		for(int i = 0; i < enemyBulletsToRemove.size(); i++)
		{
			enemyBullets.remove(enemyBulletsToRemove.get(i));
		}
		
		for(Entry<Integer, PlayerServer> e : players.entrySet())
		{
			if(!e.getValue().isDead())
			{
				e.getValue().update(delta, millis);
				
				for(int[] nr : e.getValue().damageNumbersToInform)
					damageNumbersToInform.add(new int[] { e.getKey(), nr[0], nr[1] });
				e.getValue().damageNumbersToInform.clear();
				
				e.getValue().recoverHpAndMana(delta, pvp);
				
				//check for liquid dot
				boolean burnRemoveChecked = false;
				for(LevelObject_Liquid liquid : tmxMap.getLiquids())
					if(liquid.getType() == LiquidType.LAVA && e.getValue().getRectangle().collidesWith(liquid.getBounds()))
					{
						Condition burn = e.getValue().getCondition(UniqueCondition.Burning);
						if(burn == null || millis - burn.getAbsolutTimeSinceStart() > 1001)
						{
							requestCondition(e.getKey(), UniqueCondition.Burning, UniqueCondition.Burning, (int) (e.getValue().getMaxHp() / 4f), 5000, false, UniqueCondition.Burning, millis);
						}
						break;
					}
					else if(!burnRemoveChecked && liquid.getType() == LiquidType.WATER && e.getValue().getRectangle().collidesWith(liquid.getBounds()))
					{
						burnsToRemove.clear();
						for(Entry<String, Condition> condition : e.getValue().getConditions().entrySet())
							if(condition.getValue().containsValue(UniqueCondition.Burning))
								burnsToRemove.add(condition.getKey());
						burnRemoveChecked = true;
					}
				for(String burnTR : burnsToRemove)
					e.getValue().removeCondition(burnTR);
				
				if(e.getValue().isCharging())
				{
					boolean targetFound = false;
					for(Entry<Integer, PlayerServer> player : players.entrySet())
					{
						if(!player.getValue().isDead() && player.getValue().getTeamNr() != e.getValue().getTeamNr())//if target player is not neutral and not same team, then..
							if(e.getValue().getRectangle().collidesWith(player.getValue().getRectangle()))
							{
								onChargeTargetHit(e.getValue(), player.getValue());
								targetFound = true;
								break;
							}
					}
					if(!targetFound)
						for(Entry<Integer, EntityServer> enemy : enemies.entrySet())
						{
							if(e.getValue().getRectangle().collidesWith(enemy.getValue().getRectangle()))
							{
								onChargeTargetHit(e.getValue(), enemy.getValue());
								targetFound = true;
								break;
							}
						}
				}
			}
			else if(e.getValue().isDead() && !e.getValue().wasDead)
			{
				e.getValue().wasDead = true;
				
				onPlayerDeath(e.getKey(), e.getValue());
			}
		}
		
		enemiesToRemove.clear();
		for(Entry<Integer, EntityServer> e : enemies.entrySet())
		{
			if(e.getValue().isDead())
			{
				enemiesToRemove.add(e.getKey());
			}
			else
			{
//				if(e.getValue().isInUpdateRange(players.values()))
				e.getValue().update(delta, millis, players.values(), playerMinions.values());
				
				for(int[] nr : e.getValue().damageNumbersToInform)
					damageNumbersToInform.add(new int[] { e.getKey(), nr[0], nr[1] });
				e.getValue().damageNumbersToInform.clear();
				
				e.getValue().recoverHpAndMana(delta, pvp);
				
				if(e.getValue().shootRequest.isAvailable())
					shoot(e.getKey(), e.getValue().shootRequest);
			}
		}
		
		for(int i = 0; i < enemiesToRemove.size(); i++)
		{
			enemyDeath(enemiesToRemove.get(i));
		}
		
		playerMinionsToRemove.clear();
		for(Entry<Integer, EntityServerMinion> e : playerMinions.entrySet())
		{
			if(e.getValue().isDead())
			{
				playerMinionsToRemove.add(e.getKey());
			}
			else
			{
				e.getValue().update(delta, millis, enemies.values(), players.values());
				
				for(int[] nr : e.getValue().damageNumbersToInform)
					damageNumbersToInform.add(new int[] { e.getKey(), nr[0], nr[1] });
				e.getValue().damageNumbersToInform.clear();
				
				//check for liquid dot
				boolean burnRemoveChecked = false;
				for(LevelObject_Liquid liquid : tmxMap.getLiquids())
					if(liquid.getType() == LiquidType.LAVA && e.getValue().getRectangle().collidesWith(liquid.getBounds()))
					{
						Condition burn = e.getValue().getCondition(UniqueCondition.Burning);
						if(burn == null || millis - burn.getAbsolutTimeSinceStart() > 1001)
						{
							requestCondition(e.getKey(), UniqueCondition.Burning, UniqueCondition.Burning, (int) (e.getValue().getMaxHp() / 6f), 5000, false, UniqueCondition.Burning, millis);
						}
						break;
					}
					else if(!burnRemoveChecked && liquid.getType() == LiquidType.WATER && e.getValue().getRectangle().collidesWith(liquid.getBounds()))
					{
						burnsToRemove.clear();
						for(Entry<String, Condition> condition : e.getValue().getConditions().entrySet())
							if(condition.getValue().containsValue(UniqueCondition.Burning))
								burnsToRemove.add(condition.getKey());
						burnRemoveChecked = true;
					}
				for(String burnTR : burnsToRemove)
					e.getValue().removeCondition(burnTR);
				
				e.getValue().recoverHpAndMana(delta, pvp);
				
				if(e.getValue().shootRequest.isAvailable())
					shoot(e.getKey(), e.getValue().shootRequest);
			}
		}
		
		for(int i = 0; i < playerMinionsToRemove.size(); i++)
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_ENEMY_DEATH);
			dp.setInt(playerMinionsToRemove.get(i));
			
			sendToAllActivePlayers(dp);
			
			playerMinions.get(playerMinionsToRemove.get(i)).onDeath();
			playerMinions.get(playerMinionsToRemove.get(i)).getOwner().removeMinion(playerMinions.get(playerMinionsToRemove.get(i)));
			
			playerMinions.remove(playerMinionsToRemove.get(i));
		}
		
		if(players.size() > 1 || enemies.size() > 0 || playerMinions.size() > 0)//Share entity positions
		//wenn der spieler alleine ist ohne monster, will niemand anders die pos des spielers kennen und auch keine pos eines monsters (da es ja keine gibt)
		{
			playerUpdateTimeElapsed += delta;
			
			if(playerUpdateTimeElapsed >= playerUpdateIntervall)
			{
				playerUpdateTimeElapsed -= playerUpdateIntervall;
				//set up a packet containing all positions
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MSG_ENTITY_POS_UPDATE);
				dp.setInt(players.size() + enemies.size() + playerMinions.size());
				
				for(Entry<Integer, PlayerServer> e : players.entrySet())
				{
					PlayerServer player = e.getValue();
					dp.setInt(e.getKey());
					dp.setFloat(player.getX());
					dp.setFloat(player.getY());
					dp.setFloat(player.getRotation());
				}
				
				for(Entry<Integer, EntityServer> e : enemies.entrySet())
				{
					EntityServer enemy = e.getValue();
					dp.setInt(e.getKey());
					dp.setFloat(enemy.getX());
					dp.setFloat(enemy.getY());
					dp.setFloat(enemy.getRotation());
				}
				
				for(Entry<Integer, EntityServerMinion> e : playerMinions.entrySet())
				{
					EntityServerMinion minion = e.getValue();
					dp.setInt(e.getKey());
					dp.setFloat(minion.getX());
					dp.setFloat(minion.getY());
					dp.setFloat(minion.getRotation());
				}
				
				sendToAllActivePlayers(dp);
			}
		}
		if(players.size() > 0)
		{
			healthUpdateTimeElapsed += delta;
			
			if(healthUpdateTimeElapsed >= healthUpdateIntervall)
			{
				healthUpdateTimeElapsed -= healthUpdateIntervall;
				for(Entry<Integer, PlayerServer> e : players.entrySet())
					if(!e.getValue().wasFullRegged || e.getValue().getHp() != e.getValue().getMaxHp() || e.getValue().getMana() != e.getValue().getMaxMana())
					{
						DataPacket dp = new DataPacket();
						
						dp.setInt(MessageType.MSG_PLAYER_HEALTH_UPDATE);
						dp.setFloat(e.getValue().getHp());
						dp.setFloat(e.getValue().getMana());
						
						sendToPlayer(e.getKey(), dp);
						
						if(e.getValue().getHp() == e.getValue().getMaxHp() && e.getValue().getMana() == e.getValue().getMaxMana())
							e.getValue().wasFullRegged = true;
						else
							e.getValue().wasFullRegged = false;
					}
			}
		}
		
		tmxMap.updateEventObjects(delta, players, enemies);
		
		if(bulletsToInform.size() > 0)//Share bullets to remove
		{
			bulletInformTimeElapsed += delta;
			
			if(bulletInformTimeElapsed >= bulletInformIntervall)
			{
				bulletInformTimeElapsed -= bulletInformIntervall;
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MSG_REMOVE_BULLETS);
				dp.setInt(bulletsToInform.size());
				
				for(int i = 0; i < bulletsToInform.size(); i++)
					dp.setInt(bulletsToInform.get(i));
				
				bulletsToInform.clear();
				
				sendToAllActivePlayers(dp);
			}
		}
		
		if(bagsToInform.size() > 0)
		{
			lootBagsToInformTimeElapsed += delta;
			
			if(lootBagsToInformTimeElapsed >= lootBagsToInformIntervall)
			{
				lootBagsToInformTimeElapsed -= lootBagsToInformIntervall;
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MSG_ITEMS_DROPPED);//MSG ID
				dp.setInt(bagsToInform.size());//Bag count
				for(Entry<Integer, LootBagToInform> e : bagsToInform.entrySet())
				{
					dp.setInt(e.getKey());//Bag ID
					if(e.getValue().IsNew)//Player will know it's new, because he has no bag with this id
					{
						dp.setFloat(e.getValue().x);
						dp.setFloat(e.getValue().y);
					}
					dp.setInt(e.getValue().Items.size());
					for(Entry<Integer, Item> item : e.getValue().Items.entrySet())
					{
						dp.setInt(item.getKey());//Item position in bag
						dp.setString(item.getValue().toStringFormat());//Set Item expressed as a string
					}
				}
				
				bagsToInform.clear();
				
				sendToAllActivePlayers(dp);
			}
		}
		
		if(damageNumbersToInform.size() > 0)
		{
			damageNumbersToInformTimeElapsed += delta;
			
			if(damageNumbersToInformTimeElapsed >= damageNumbersToInformIntervall)
			{
				damageNumbersToInformTimeElapsed -= damageNumbersToInformIntervall;
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MGS_DAMAGE_NUMBER);
				dp.setInt(damageNumbersToInform.size());
				for(int[] numb : damageNumbersToInform)
				{
					dp.setInt(numb[0]);
					dp.setInt(numb[1]);
					dp.setInt(numb[2]);
					if(numb[0] == -1)
					{
						dp.setInt(numb[3]);
						dp.setInt(numb[4]);
					}
				}
				
				damageNumbersToInform.clear();
				
				sendToAllActivePlayers(dp);
			}
		}
		
		if(effectsToInform.size() > 0)
		{
			effectsToInformTimeElapsed += delta;
			
			if(effectsToInformTimeElapsed >= effectsToInformIntervall)
			{
				effectsToInformTimeElapsed -= effectsToInformIntervall;
				
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MSG_PLAY_EFFECT);
				dp.setInt(effectsToInform.size());
				for(EffectToInform effect : effectsToInform)
				{
					if(effect.targedId == -1)
					{
						dp.setBoolean(false);
						dp.setFloat(effect.originX);
						dp.setFloat(effect.originY);
						dp.setInt((int) effect.width);
						dp.setInt((int) effect.height);
						dp.setString(effect.effect);
						dp.setInt(effect.duration);
						dp.setBoolean(effect.loop);
					}
					else
					{
						dp.setBoolean(true);
						dp.setInt(effect.targedId);
						dp.setBoolean(effect.hook);
						dp.setInt((int) effect.width);
						dp.setInt((int) effect.height);
						dp.setString(effect.effect);
						dp.setInt(effect.duration);
						dp.setBoolean(effect.loop);
					}
					
					effectQueue.offer(effect.reset());
				}
				
				effectsToInform.clear();
				
				sendToAllActivePlayers(dp);
			}
		}
		
		if(tmxMap.destroyableTilesToRemove.size() > 0)
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_REMOVE_LEVEL_OBJECTS);
			dp.setInt(tmxMap.destroyableTilesToRemove.size());
			for(Integer i : tmxMap.destroyableTilesToRemove)
				dp.setInt(i);
			
			tmxMap.destroyableTilesToRemove.clear();
			
			sendToAllActivePlayers(dp);
		}
		
		if(tmxMap.getLevelObjects().size() > 0)
		{
			for(LevelObject obj : tmxMap.getLevelObjects().values())
				obj.update(delta, this);
		}
		tmxMap.updateLevelObjectsToRemove();
		
		if(lairResetCounterLeft > 0)
		{
			lairResetCounterLeft -= delta;
			if(lairResetCounterLeft <= 0)
			{
				closeLair();
			}
		}
	}
	
	private void onChargeTargetHit(EntityServerMixin charger, EntityServerMixin target)
	{
		if(!charger.conditions.containsKey(SpellCharge.name))
			return;
		
		//Damage enemy
		float dmg = target.DoDamage(charger.conditions.get(SpellCharge.name).getBuffs().get("ChargeDmg"), DamageType.Physical, pvp);
		sendDamageNumberToAllPlayers(target.Nr, -dmg, DamageType.Physical);
		//stun it
		requestCondition(target.Nr, UniqueCondition.Stunned, UniqueCondition.Stunned, 0, charger.conditions.get(SpellCharge.name).getBuffs().get("ChargeStun"), false, UniqueCondition.Stunned, System.currentTimeMillis());
		//remove charge buffs + haste
		charger.removeCondition(SpellCharge.name);
		charger.removeCondition(SpellHaste.name);
	}
	
	protected void onPlayerDeath(int id, PlayerServer player)
	{
		for(Condition cond : player.getConditions().values())
			if(cond.getConditionListener() != null)
				cond.getConditionListener().onDied(player);
		
		player.removeAllConditions();
		int tombId = tmxMap.getNextLevelObjectId();
		LevelObject ts = new LevelObject_Tombstone(tombId, this, player.getOriginX(), player.getOriginY());
		tmxMap.addLevelObject(tombId, ts, this);
		
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_PLAYER_DIED);
		dp.setInt(id);
		dp.setInt(tombId);
		dp.setBoolean(true);
		dp.setFloat(ts.getBounds().getX());
		dp.setFloat(ts.getBounds().getY());
		dp.setInt((int) ts.getBounds().getWidth());
		dp.setInt((int) ts.getBounds().getHeight());
		
		playEffect(player.getOriginX(), player.getOriginY(), player.getWidth(), player.getHeight(), player.getDeathAnimation(), Constants.deathAnimDuration, false);
		playSound(player.getDeathAnimation(), player.getOriginX(), player.getOriginY());
		
		sendToAllActivePlayers(dp);
	}
	
	private boolean checkBulletCollision(Bullet b, int bulletId, EntityServerMixin targetEntity, int targetEntityId)
	{
		if(!b.hasHitEntity(targetEntityId) && targetEntity.getRectangle().collidesWith(b.getRectangle()))
		{
			if(b.getOnHitEffect() == null || b.getOnHitEffect().getAoeRadius() <= 0)
			{
				float damageDealed = targetEntity.DoDamage(b.getOnHitEffect() == null ? b.getOwner().getDamage() + b.getOwner().getRandomBonusDamage() : b.getOnHitEffect().getDamage(), b.getDamageType(), pvp);
				if(b.getOnHitEffect() == null)
					b.getOwner().doHeal(b.getOwner().getLifePerHit());
				else if(b.getOnHitEffect().getLifeStealPercent() > 0)
				{
					b.getOwner().doHeal(b.getOnHitEffect().getLifeStealPercent() / 100f * damageDealed);
					sendDamageNumberToAllPlayers(b.getOwner().Nr, b.getOnHitEffect().getLifeStealPercent() / 100f * damageDealed, DamageType.Heal);
				}
				
				if(targetEntity.getThorns() > 0)
				{
					float thornDmg = b.getOwner().DoDamage(targetEntity.getThorns(), DamageType.Physical, pvp);
					sendDamageNumberToAllPlayers(b.getOwner().Nr, -thornDmg, DamageType.Physical);
				}
				
				if(!b.getPiercing())
				{
					sendDamageNumberToAllPlayers(targetEntityId, -damageDealed, b.getDamageType());
					b.setDead();
					bulletsToInform.add(bulletId);
				}
				else
				{
					sendDamageNumberToAllPlayers(targetEntityId, -damageDealed, b.getDamageType());
					b.hitEntity(targetEntityId);
				}
				
				if(b.getOnHitEffect() != null)
				{
					if(b.getOnHitEffect().getCondition() != null)
					{
						Condition cond = b.getOnHitEffect().getCondition();
						requestCondition(targetEntityId, cond.getName(), cond.getValueName(), cond.getValue(), cond.getDuration(), cond.isAttribute(), cond.getEffect(), System.currentTimeMillis(), cond.getAftereffect(), cond.getConditionListener());
					}
					if(b.getOnHitEffect().getMovementModifier() != null)
					{
						targetEntity.addMovementModifier(b.getOnHitEffect().getMovementModifier().name, b.getOnHitEffect().getMovementModifier());
					}
					
					if(!b.getOnHitEffect().getEffect().equals(""))
						playEffect(b.getOriginX(), b.getOriginY(), -1, -1, b.getOnHitEffect().getEffect(), b.getOnHitEffect().getEffectDuration());
				}
				
				if(b.getOnHitEffect() != null && b.getOnHitEffect().getSoundEffect() != null)
					playSound(b.getOnHitEffect().getSoundEffect(), b.getOriginX(), b.getOriginY());
				else if(targetEntity.getSoundHit() != null)
					playSound(targetEntity.getSoundHit(), b.getOriginX(), b.getOriginY());
				
				//Inform player (if it is one) that he got hit
				if(Server.isPlayer(targetEntityId))
				{
					DataPacket dpPlayer = new DataPacket();
					dpPlayer.setInt(MessageType.MSG_PLAYER_YOU_GOT_HIT);
					dpPlayer.setInt(b.getOwner().Nr);
					sendToPlayer(targetEntityId, dpPlayer);
				}
			}
			else
			//aoe
			{
				doAoeDamage(b);
				b.setDead();
				bulletsToInform.add(bulletId);
			}
			if(!b.getPiercing())
				return true;
			else
				return false;
		}
		return false;
	}
	
	private void doAoeDamage(Bullet b)
	{
		doAoeDamage(b.getOwner(), b.getOriginX(), b.getOriginY(), b.getOnHitEffect(), b.getDamageType());
	}
	
	public void doAoeDamage(EntityServerMixin owner, float originX, float originY, BulletOnHitEffect onHitEffect, int damageType)
	{
		if(onHitEffect != null && onHitEffect.dontProccFirst)
		{
			onHitEffect.dontProccFirst = false;
			if(onHitEffect.centered)
				delayedOnHitEffects.add(new RepeatedOnHitEffect(owner, onHitEffect, damageType));
			else
				delayedOnHitEffects.add(new RepeatedOnHitEffect(owner, originX, originY, onHitEffect, damageType));
			return;
		}
		
		float radius = onHitEffect.getAoeRadius();
		Rectangle aoe = new Rectangle(originX - radius, originY - radius, radius * 2, radius * 2);
		
		for(Entry<Integer, PlayerServer> player : players.entrySet())
		{
			if(player.getValue().getTeamNr() != owner.getTeamNr())//if target player is not same team, then..
				if(player.getValue().getRectangle().collidesWith(aoe))
				{
					float damageDealed = player.getValue().DoDamage(onHitEffect.getDamage(), damageType, pvp);
					if(onHitEffect.getCondition() != null)
					{
						Condition cond = onHitEffect.getCondition();
						requestCondition(player.getKey(), cond.getName(), cond.getValueName(), cond.getValue(), cond.getDuration(), cond.isAttribute(), cond.getEffect(), System.currentTimeMillis(), cond.getAftereffect(), cond.getConditionListener());
					}
					if(onHitEffect.getMovementModifier() != null)
					{
						player.getValue().addMovementModifier(onHitEffect.getMovementModifier().name, onHitEffect.getMovementModifier());
					}
					sendDamageNumberToAllPlayers(player.getKey(), -damageDealed, damageType);
				}
		}
		for(Entry<Integer, EntityServer> enemy : enemies.entrySet())
		{
			if(enemy.getValue().getTeamNr() != owner.getTeamNr())//if target player is not same team, then..
				if(enemy.getValue().getRectangle().collidesWith(aoe))
				{
					float damageDealed = enemy.getValue().DoDamage(onHitEffect.getDamage(), damageType, pvp);
					if(onHitEffect.getCondition() != null)
					{
						Condition cond = onHitEffect.getCondition();
						requestCondition(enemy.getKey(), cond.getName(), cond.getValueName(), cond.getValue(), cond.getDuration(), cond.isAttribute(), cond.getEffect(), System.currentTimeMillis(), cond.getAftereffect(), cond.getConditionListener());
					}
					if(onHitEffect.getMovementModifier() != null)
					{
						enemy.getValue().addMovementModifier(onHitEffect.getMovementModifier().name, onHitEffect.getMovementModifier());
					}
					sendDamageNumberToAllPlayers(enemy.getKey(), -damageDealed, damageType);
				}
		}
		for(final LevelObject obj : getMap().destroyableTiles)
		{
			if(obj.getBounds().collidesWith(aoe))
			{
				obj.lowerHealth();
				if(obj.getHp() <= 0)
				{
					obj.onDestroyed();
					removeLevelObject(obj);
				}
			}
		}
		
		if(!onHitEffect.getEffect().equals(""))
		{
			if(!onHitEffect.effectAttached)
				playEffect(originX, originY, onHitEffect.getAoeRadius() * 2, onHitEffect.getAoeRadius() * 2, onHitEffect.getEffect(), onHitEffect.getEffectDuration());
			else
				playEffect(owner.Nr, (int) onHitEffect.getAoeRadius() * 2, (int) onHitEffect.getAoeRadius() * 2, onHitEffect.getEffect(), onHitEffect.getEffectDuration());
		}
		
		if(onHitEffect.getSoundEffect() != null)
			playSound(onHitEffect.getSoundEffect(), originX, originY);
		
		if(onHitEffect.repeat > 1)
		{
			if(onHitEffect.centered)
				delayedOnHitEffects.add(new RepeatedOnHitEffect(owner, onHitEffect, damageType));
			else
				delayedOnHitEffects.add(new RepeatedOnHitEffect(owner, originX, originY, onHitEffect, damageType));
		}
	}
	
	public void sendDamageNumberToAllPlayers(int entityID, float damage, int damageType)
	{
		int[] number = new int[3];
		number[0] = entityID;
		number[1] = Math.round(damage);
		number[2] = damageType;
		damageNumbersToInform.add(number);
	}
	
	public void loadMap(String mapName)
	{
		if(tmxMap.getCurrentMapName() != null)
			level.informPlayersOtherMap(tmxMap.getCurrentMapName());
		
		//TODO: clear all the fucking lists..
		
		lairResetCounterLeft = 0;
		bulletCounter = 0;
		lootBagCounter = 0;
		enemyCounter = Server.MAX_SUPPORTED + 1;
		enemies.clear();
		bossEntity = null;
		enemiesToRemove.clear();
		playerBullets.clear();
		playerBulletsToRemove.clear();
		enemyBullets.clear();
		enemyBulletsToRemove.clear();
		lootbags.clear();
		lootBagsToRemove.clear();
		bagsToInform.clear();
		delayedOnHitEffects.clear();
		delayedOnHitEffectsToRemove.clear();
		damageNumbersToInform.clear();
		burnsToRemove.clear();
		
		for(PlayerServer player : players.values())
		{
			player.setTeamNr(0);
			player.isLoading = true;
		}
		
		tmxMap.loadMap(mapName, null, true);
		tmxMap.loadEventTiles(this);
		
		//spawn shrines:
		List<EventObject> spawns = tmxMap.getStaticEvents("ShrineSpawn");
		if(spawns != null)
			for(int i = 0; i < spawns.size(); i++)
				if(GameHelper.$.getRandom().nextInt(100) + 1 <= Constants.shrineSpawnChance)
				{
					//spawn shrine..
					tmxMap.addNewLevelObjectServer(this, spawns.get(i).getRectangle(), "Shrine", null);
				}
		
		//TODO: spawn chests
		
		List<EntityServer> enemiesToAdd = tmxMap.loadEnemies(this, level.getDifficulty(), level.getTotalPlayerCount());
		for(int i = 0; i < enemiesToAdd.size(); i++)
		{
			spawnEntity(enemiesToAdd.get(i));
		}
	}
	
	protected void spawnEntity(EntityServer entity)
	{
		entity.realmID = this.realmID;
		entity.Nr = enemyCounter + 1;
		enemyCounter++;
		enemies.put(entity.Nr, entity);
		if(entity.type.equals("boss"))
			onBossSpawned(entity);
	}
	
	private void onBossSpawned(EntityServer entity)
	{
		bossEntity = entity;
	}
	
	private void onBossDeath(EntityServer entity)
	{
		level.unlockedLairsPut(currentLair, 1);
		
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_NEXT_LAIR);
		dp.setString(currentLair);
		dp.setInt(1);
		level.sendDataPacketToAllConnectedPlayers(dp);
	}
	
	public void onBossDamageTaken()
	{
		if(bossEntity != null)
		{
			DataPacket dpBoss = new DataPacket();
			dpBoss.setInt(MessageType.MSG_BOSS_HP_UPDATE);
			dpBoss.setString(bossEntity.name);
			dpBoss.setFloat(Math.max(0, bossEntity.getHp() / bossEntity.getMaxHp()));
			
			sendToAllActivePlayers(dpBoss);
		}
	}
	
	public void playerHealthUpdate(int playerNr)
	{
		if(players.containsKey(playerNr))
		{
			DataPacket dpStat = new DataPacket();
			dpStat.setInt(MessageType.MSG_PLAYER_HEALTH_UPDATE);
			dpStat.setFloat(players.get(playerNr).getHp());
			dpStat.setFloat(players.get(playerNr).getMana());
			sendToPlayer(playerNr, dpStat);
		}
	}
	
	public void handleMapLoadCompleted(int playerNr) throws IOException//Player loaded the map. Now send entity infos and inform others
	{
		PlayerServer player = players.get(playerNr);
		player.setNewPosition(tmxMap.spawn.x, tmxMap.spawn.y);
		player.setRotation(180);
		
		//send all players & entities to the player INCLUDING LOOTBAGS :)
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_MAP_LOAD_COMPLETED);
		dp.setInt(getRealmPlayerCount() + enemies.size() + playerMinions.size());
		
		for(Entry<Integer, PlayerServer> e : players.entrySet())
		{
//			if(!e.getValue().isDead())
			{
				dp.setInt(e.getKey());
				dp.setString("Player" + e.getKey());
				dp.setString(e.getValue().getAsset());
				dp.setFloat(e.getValue().getX());
				dp.setFloat(e.getValue().getY());
				dp.setFloat(e.getValue().getRotation());
				dp.setInt(PlayerClient.WIDTH);
				dp.setInt(PlayerClient.HEIGHT);
				dp.setFloat(e.getValue().getShootSpeed());
				dp.setBoolean(e.getValue().isDead());
			}
			
		}
		for(Entry<Integer, EntityServer> e : enemies.entrySet())
		{
//			if(!e.getValue().isDead())
			{
				dp.setInt(e.getKey());
				dp.setString(e.getValue().name);
				dp.setString(e.getValue().asset);
				dp.setFloat(e.getValue().getX());
				dp.setFloat(e.getValue().getY());
				dp.setFloat(e.getValue().getRotation());
				dp.setInt((int) e.getValue().getWidth());
				dp.setInt((int) e.getValue().getHeight());
				dp.setFloat(e.getValue().getVisualShootSpeed());
				dp.setBoolean(e.getValue().isDead());
			}
		}
		for(Entry<Integer, EntityServerMinion> e : playerMinions.entrySet())
		{
//			if(!e.getValue().isDead())
			{
				dp.setInt(e.getKey());
				dp.setString(e.getValue().name);
				dp.setString(e.getValue().asset);
				dp.setFloat(e.getValue().getX());
				dp.setFloat(e.getValue().getY());
				dp.setFloat(e.getValue().getRotation());
				dp.setInt((int) e.getValue().getWidth());
				dp.setInt((int) e.getValue().getHeight());
				dp.setFloat(e.getValue().getVisualShootSpeed());
				dp.setBoolean(e.getValue().isDead());
			}
		}
		dp.setInt(lootbags.size());//Loot bag count
		for(Entry<Integer, LootBag> e : lootbags.entrySet())
		{
			dp.setInt(e.getKey());//ID
			dp.setFloat(e.getValue().getX());//X
			dp.setFloat(e.getValue().getY());//Y
			dp.setInt(e.getValue().getItems().size());//Item count
			for(Entry<Integer, Item> item : e.getValue().getItems().entrySet())
			{
				dp.setInt(item.getKey());
				dp.setString(item.getValue().toStringFormat());//Item in StringFormat
			}
		}
		dp.setInt(tmxMap.getLevelObjects().size());
		for(Entry<Integer, LevelObject> e : tmxMap.getLevelObjects().entrySet())
		{
			dp.setInt(e.getKey());//ID
			dp.setFloat(e.getValue().getBounds().getX());
			dp.setFloat(e.getValue().getBounds().getY());
			dp.setInt((int) e.getValue().getBounds().getWidth());
			dp.setInt((int) e.getValue().getBounds().getHeight());
			dp.setString(e.getValue().toStringFormat());
		}
		
		dp.setBoolean(!level.getRealmByName("fight").getCurrentMapName().equals("pretown") && !level.getRealmByName("fight").realmIsClosing);
		
		sendToPlayer(playerNr, dp);
		
		if(bossEntity != null)
		{
			DataPacket dpBoss = new DataPacket();
			dpBoss.setInt(MessageType.MSG_BOSS_HP_UPDATE);
			dpBoss.setString(bossEntity.name);
			dpBoss.setFloat(bossEntity.getHp() / bossEntity.getMaxHp());
			
			sendToPlayer(playerNr, dpBoss);
		}
		
		for(Entry<Integer, PlayerServer> e : players.entrySet())
		{
			DataPacket dpTeam = new DataPacket();
			dpTeam.setInt(MessageType.MSG_PLAYER_TEAM_JOINED);
			dpTeam.setInt(e.getKey());
			dpTeam.setInt(e.getValue().getTeamNr());
			sendToPlayer(playerNr, dpTeam);
		}
		
		player.isLoading = false;
	}
	
	public void addPlayer(int playerNr, PlayerServer player) throws IOException
	{
		player.setNewPosition(tmxMap.spawn.x, tmxMap.spawn.y);
		player.setTeamNr(0);
		player.setRotation(180);
		player.isLoading = true;
		player.realmID = this.realmID;
		
		players.put(playerNr, player);
		
		for(EntityServerMinion minion : player.getMinions())
		{
			enemyCounter++;
			int id = enemyCounter;
			minion.Nr = -id;
			minion.realmID = this.realmID;
			minion.setRealm(this);
			minion.setNewPosition(tmxMap.spawn.x, tmxMap.spawn.y);
			minion.setRotation(180);
			
			playerMinions.put(minion.Nr, minion);
		}
		
		//send MSG_LOAD_MAP_INFO to player:
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_LOAD_MAP_INFO);
		dpAnswer.setString(getCurrentMapName());
		Log.d("Realm.out", "sending map to load..");
		
		sendToPlayer(playerNr, dpAnswer);
		
		//inform other players (incl. class etc):
		informOtherPlayersAboutPlayer(playerNr, player, false);
		
		//send map name to other players (for quests)
		if(level.getTotalPlayerCount() > getRealmPlayerCount())//players outside of this realm?
		{
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_CLIENT_PLAYER_JOINED_OTHER_MAP);
			dpOthers.setString(this.getCurrentMapName());
			level.sendDataPacketToAllConnectedPlayersExceptInRealm(this, dpOthers);
		}
	}
	
	public void addEnemies(ConcurrentHashMap<Integer, EntityServer> ents)
	{
		DataPacket dp = new DataPacket(MessageType.MSG_ADD_ENTITIES);
		dp.setInt(ents.size());
		
		for(Entry<Integer, EntityServer> e : ents.entrySet())
		{
			if(!e.getValue().isDead())
			{
				dp.setInt(e.getKey());
				dp.setString(e.getValue().name);
				dp.setString(e.getValue().asset);
				dp.setFloat(e.getValue().getX());
				dp.setFloat(e.getValue().getY());
				dp.setFloat(e.getValue().getRotation());
				dp.setInt((int) e.getValue().getWidth());
				dp.setInt((int) e.getValue().getHeight());
				dp.setFloat(e.getValue().getVisualShootSpeed());
				dp.setBoolean(e.getValue().isDead());
			}
			
			e.getValue().realmID = this.realmID;
			e.getValue().Nr = e.getKey();
			enemies.put(e.getKey(), e.getValue());
		}
		
		sendToAllActivePlayers(dp);
	}
	
	public void addEntity(int id, EntityServer entity)
	{
		DataPacket dp = new DataPacket(MessageType.MSG_ADD_ENTITY);
		
		if(!entity.isDead())
		{
			dp.setInt(id);
			dp.setString(entity.name);
			dp.setString(entity.asset);
			dp.setFloat(entity.getX());
			dp.setFloat(entity.getY());
			dp.setFloat(entity.getRotation());
			dp.setInt((int) entity.getWidth());
			dp.setInt((int) entity.getHeight());
			dp.setFloat(entity.getVisualShootSpeed());
			dp.setBoolean(entity.isDead());
		}
		
		entity.realmID = this.realmID;
		entity.Nr = id;
		enemies.put(id, entity);
		
		sendToAllActivePlayers(dp);
	}
	
	public void addMinion(EntityServerMixin owner, EntityServerMinion minion)
	{
		int id;
		if(owner instanceof PlayerServer)
		{
			enemyCounter++;
			id = -(enemyCounter);
			minion.realmID = this.realmID;
			minion.Nr = id;
			owner.addMinion(minion);
			playerMinions.put(id, minion);
		}
		else
		{
			enemyCounter++;
			id = enemyCounter;
			minion.realmID = this.realmID;
			minion.Nr = id;
			owner.addMinion(minion);
			enemies.put(id, minion);
		}
		
		DataPacket dp = new DataPacket(MessageType.MSG_ADD_ENTITIES);
		dp.setInt(1);
		
		dp.setInt(minion.Nr);
		dp.setString(minion.name);
		dp.setString(minion.asset);
		dp.setFloat(minion.getX());
		dp.setFloat(minion.getY());
		dp.setFloat(minion.getRotation());
		dp.setInt((int) minion.getWidth());
		dp.setInt((int) minion.getHeight());
		dp.setFloat(minion.getVisualShootSpeed());
		dp.setBoolean(minion.isDead());
		
		sendToAllActivePlayers(dp);
	}
	
	public void addFriendlyMinion(EntityServerMinion minion)
	{
		enemyCounter++;
		int id = -(enemyCounter);
		minion.realmID = this.realmID;
		minion.Nr = id;
		playerMinions.put(id, minion);
		
		DataPacket dp = new DataPacket(MessageType.MSG_ADD_ENTITIES);
		dp.setInt(1);
		
		dp.setInt(minion.Nr);
		dp.setString(minion.name);
		dp.setString(minion.asset);
		dp.setFloat(minion.getX());
		dp.setFloat(minion.getY());
		dp.setFloat(minion.getRotation());
		dp.setInt((int) minion.getWidth());
		dp.setInt((int) minion.getHeight());
		dp.setFloat(minion.getVisualShootSpeed());
		dp.setBoolean(minion.isDead());
		
		sendToAllActivePlayers(dp);
	}
	
	public void removeFriendlyMinion(EntityServerMinion minion)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REMOVE_ENTITY);
		dp.setInt(minion.Nr);
		
		sendToAllActivePlayers(dp);
		
		playerMinions.remove(minion.Nr);
	}
	
	protected void removeAllEnemies()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REMOVE_ENTITIES);
		dp.setInt(enemies.size());
		for(Integer id : enemies.keySet())
			dp.setInt(id);
		sendToAllActivePlayers(dp);
		
		enemies.clear();
		enemiesToRemove.clear();
		enemyBullets.clear();
		enemyBulletsToRemove.clear();
	}
	
	public void informOtherPlayersAboutPlayer(int playerNr, PlayerServer player, boolean selfInform)
	{
		DataPacket dpOthers = new DataPacket();
		dpOthers.setInt(MessageType.MSG_ADD_ENTITIES);
		dpOthers.setInt(1 + player.getMinions().size());//player + minions
		
		dpOthers.setInt(playerNr);
		dpOthers.setString("Player" + playerNr);
		dpOthers.setString(player.getAsset());
		dpOthers.setFloat(player.getX());
		dpOthers.setFloat(player.getY());
		dpOthers.setFloat(player.getRotation());
		dpOthers.setInt(PlayerClient.WIDTH);
		dpOthers.setInt(PlayerClient.HEIGHT);
		dpOthers.setFloat(player.getShootSpeed());
		dpOthers.setBoolean(player.isDead());
		
		for(EntityServerMinion minion : player.getMinions())
		{
			dpOthers.setInt(minion.Nr);
			dpOthers.setString(minion.name);
			dpOthers.setString(minion.asset);
			dpOthers.setFloat(minion.getX());
			dpOthers.setFloat(minion.getY());
			dpOthers.setFloat(minion.getRotation());
			dpOthers.setInt((int) minion.getWidth());
			dpOthers.setInt((int) minion.getHeight());
			dpOthers.setFloat(minion.getVisualShootSpeed());
		}
		
		if(!selfInform)
			sendToAllActivePlayersExcept(playerNr, dpOthers);
		else
			sendToAllActivePlayers(dpOthers);
		
		DataPacket dpTeam = new DataPacket();
		dpTeam.setInt(MessageType.MSG_PLAYER_TEAM_JOINED);
		dpTeam.setInt(playerNr);
		dpTeam.setInt(player.getTeamNr());
		sendToAllActivePlayers(dpTeam);
	}
	
	public void removePlayer(int playerNr)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REMOVE_ENTITY);
		dp.setInt(playerNr);
		
		sendToAllActivePlayersExcept(playerNr, dp);
		
		for(EntityServerMinion minion : players.get(playerNr).getMinions())
		{
			DataPacket dpMinion = new DataPacket();
			dpMinion.setInt(MessageType.MSG_REMOVE_ENTITY);
			dpMinion.setInt(minion.Nr);
			
			minion.realmID = -1;
			sendToAllActivePlayersExcept(playerNr, dpMinion);
			
			playerMinions.remove(minion.Nr);
		}
		
		players.get(playerNr).realmID = -1;
		players.remove(playerNr);
		
		forcedUpdateTimeLeft = forcedUpdateTime;
	}
	
	public void setNewEntityPosition(int entityId, float newX, float newY)
	{
		if(entityId > Server.MAX_SUPPORTED && enemies.containsKey(entityId))
		{
			enemies.get(entityId).setNewPosition(newX, newY);
			
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_ENTITY_SET_NEW_POS);
			dp.setInt(entityId);
			dp.setFloat(newX);
			dp.setFloat(newY);
			sendToAllActivePlayers(dp);
		}
		else if(entityId <= Server.MAX_SUPPORTED && entityId > 0 && players.containsKey(entityId))
		{
			players.get(entityId).setNewPosition(newX, newY);
			
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_ENTITY_SET_NEW_POS);
			dp.setInt(entityId);
			dp.setFloat(newX);
			dp.setFloat(newY);
			sendToAllActivePlayers(dp);
		}
		else if(playerMinions.containsKey(entityId))
		{
			playerMinions.get(entityId).setNewPosition(newX, newY);
			
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_ENTITY_SET_NEW_POS);
			dp.setInt(entityId);
			dp.setFloat(newX);
			dp.setFloat(newY);
			sendToAllActivePlayers(dp);
		}
	}
	
	protected void enemyDeath(int enemyNr)
	{
		EntityServer entity = enemies.get(enemyNr);
		
		if(dropsAllowed && !entity.getType().equals("boss"))
		{
			dropItems(DropGenerator.generateDrop(entity.getLevel() + Constants.LevelsPerDifficulty * level.getDifficulty(), entity.getLevel(), level.getTotalPlayerCount()), entity.getOriginX(), entity.getOriginY());
		}
		else if(entity.getType().equals("boss"))
			onBossDeath(entity);
		
		for(Condition cond : entity.getConditions().values())
			if(cond.getConditionListener() != null)
				cond.getConditionListener().onDied(entity);
		
		ArrayList<int[]> toRemove = new ArrayList<int[]>();
		for(int i = 0; i < damageNumbersToInform.size(); i++)
			if(damageNumbersToInform.get(i)[0] == enemyNr)
				toRemove.add(damageNumbersToInform.get(i));
		for(int i = 0; i < toRemove.size(); i++)
		{
			damageNumbersToInform.remove(toRemove.get(i));
			int[] newI = new int[] { -1, toRemove.get(i)[1], toRemove.get(i)[2], (int) (entity.getX() + entity.getWidth() / 2), (int) (entity.getY()) };
			damageNumbersToInform.add(newI);
		}
		
		DataPacket dp = new DataPacket();
		if(entity.getType().equals("boss"))
			dp.setInt(MessageType.MSG_BOSS_DEATH);
		else
			dp.setInt(MessageType.MSG_ENEMY_DEATH);
		dp.setInt(enemyNr);
		sendToAllActivePlayers(dp);
		
		if(getRealmPlayerCount() < level.getTotalPlayerCount())
		{
			DataPacket dpOthers = new DataPacket();
			if(entity.getType().equals("boss"))
				dpOthers.setInt(MessageType.MSG_BOSS_DEATH);
			else
				dpOthers.setInt(MessageType.MSG_ENEMY_DEATH);
			dpOthers.setInt(-1);
			dpOthers.setString(entity.name);
			
			level.sendDataPacketToAllConnectedPlayersExceptInRealm(this, dpOthers);
		}
		
		playEffect(entity.getOriginX(), entity.getOriginY(), entity.getWidth(), entity.getHeight(), entity.getDeathAnimation(), Constants.deathAnimDuration, false);
		playSound(entity.getDeathAnimation(), entity.getOriginX(), entity.getOriginY());
		
		entity.onDeath();
		
		enemies.remove(enemyNr);
	}
	
	public void dropItems(List<Item> items, float x, float y)
	{
		//Merge consumable items:
		List<Item> mergedItems = new ArrayList<Item>();
		for(Item i : items)
			if(!mergedItems.contains(i) && i instanceof StackableItem)
				for(Item i2 : items)
					if(!mergedItems.contains(i2) && i != i2 && i2 instanceof StackableItem && i.NAME.equalsIgnoreCase(i2.NAME))//item1 and item2 the same
					{
						((StackableItem) i).countPlus(((StackableItem) i2).getCount());
						mergedItems.add(i2);
					}
		for(Item m : mergedItems)
			items.remove(m);
		
		int index = 0;
		LootBag nearestBag;
		while(index < items.size())//dropped items available for distributing
		{
			nearestBag = getNearestNotFullLootBag(x, y);//Get nearest-non-full bag (else return null)
			if(nearestBag == null)//No bag found
			{
				if(items.size() - index <= LootBag.maxSize)//Fill all Items ( <= bag max size) into a new created bag
				{
					List<Item> restItems = new ArrayList<Item>();
					for(int i = 0; i < items.size() - index; i++)
					{
						restItems.add(items.get(index));
						index++;
					}
					lootBagCounter++;
					lootbags.put(lootBagCounter, new LootBag(lootBagCounter, x, y, restItems));
					bagsToInform.put(lootBagCounter, new LootBagToInform(x, y, restItems));//IS NEW
				}
				else
				//Fill the first bag.maxSize items in a new bag
				{
					List<Item> firstItems = new ArrayList<Item>();
					for(int i = 0; i < LootBag.maxSize; i++)
					{
						firstItems.add(items.get(index));
						index++;
					}
					
					lootBagCounter++;
					lootbags.put(lootBagCounter, new LootBag(lootBagCounter, x, y, firstItems));
					bagsToInform.put(lootBagCounter, new LootBagToInform(x, y, firstItems));//IS NEW
				}
			}
			else
			//fill the first item of the drop list into an existing bag
			{
				int itemPos = nearestBag.addNewItem(items.get(index));
				if(!bagsToInform.containsKey(nearestBag.id))
					bagsToInform.put(nearestBag.id, new LootBagToInform());//IS NOT NEW
				bagsToInform.get(nearestBag.id).addItem(itemPos, nearestBag.getItems().get(itemPos));
				index++;
			}
		}
	}
	
	//returns a LootBag that is not Full & in Range. If nothing was found, return null
	private LootBag getNearestNotFullLootBag(float x, float y)
	{
		LootBag res = null;
		double shortestDist = LootBag.distributeRadius + 1;//get the nearest
		for(Entry<Integer, LootBag> e : lootbags.entrySet())
		{
			double dist = Math.sqrt(Math.pow(e.getValue().getX() - x, 2) + Math.pow(e.getValue().getY() - y, 2));//Pytagoras
			if(!e.getValue().isFull() && dist <= LootBag.distributeRadius)//not full && in range
			{
				if(dist < shortestDist)
				{
					res = e.getValue();
					shortestDist = dist;
				}
			}
		}
		return res;
	}
	
	public void handlePlayerPosUpdate(int playerNr, DataPacket dp)
	{
		if(!getPlayer(playerNr).isLoading && !getPlayer(playerNr).isDead())
			getPlayer(playerNr).updatePositionSmooth(dp.getFloat(), dp.getFloat(), dp.getFloat());
	}
	
	public void handleRequestShootBullet(int playerNr, DataPacket dp)
	{
		if(!getPlayer(playerNr).isLoading)
			shoot(playerNr, dp.getBoolean(), dp.getInt(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getFloat(), dp.getString());
	}
	
	public void handlePlayerStatusUpdate(int playerNr, DataPacket dp)
	{
		PlayerServer player = getPlayer(playerNr);
//		float prevShootSpeed = player.getShootSpeed();
		
		player.setMaxHp(dp.getFloat());
		player.setHpRegSec(dp.getFloat());
		player.setMaxMana(dp.getFloat());
		player.setManaRegSec(dp.getFloat());
		
		player.setShootspeed(dp.getFloat());
		player.setDamage(dp.getFloat());
		player.setDamageBonus(dp.getFloat());
		
		player.setArmor(dp.getFloat());
		player.setFireRes(dp.getFloat());
		player.setColdRes(dp.getFloat());
		player.setLightningRes(dp.getFloat());
		player.setPoisonRes(dp.getFloat());
		
		player.setLifePerHit(dp.getFloat());
		player.setManaPerHit(dp.getFloat());
		
		player.setThorns(dp.getFloat());
		player.setAbsorb(dp.getFloat());
		
		if(getRealmPlayerCount() > 1/* && prevShootSpeed != player.getShootSpeed()*/)
		{
			//Send dp to other players with new shootSpeed.
			DataPacket dpAnswer = new DataPacket();
			dpAnswer.setInt(MessageType.MSG_OTHER_PLAYER_SP_CHANGED);
			dpAnswer.setInt(playerNr);
			dpAnswer.setFloat(player.getShootSpeed());
			sendToAllActivePlayersExcept(playerNr, dpAnswer);
		}
	}
	
	public void handleRequestLootItem(int playerNr, DataPacket dp)
	{
		int bagId = dp.getInt();
		int slotNr = dp.getInt();
		LootBag lootBag = lootbags.get(bagId);
		
		if(lootBag != null)
		{
			DataPacket dpAnswer = new DataPacket();
			dpAnswer.setInt(MessageType.MSG_LOOT_ITEM_PICKED);
			dpAnswer.setInt(playerNr);
			dpAnswer.setInt(lootBag.id);
			
			if(slotNr == -1)//all
			{
				dpAnswer.setInt(lootBag.getItems().size());
				for(Entry<Integer, Item> e : lootBag.getItems().entrySet())
					dpAnswer.setInt(e.getKey());
				
				lootbags.remove(bagId);
			}
			else if(slotNr == -2)//multiple specific
			{
				int size = dp.getInt();
				dpAnswer.setInt(size);
				for(int i = 0; i < size; i++)
				{
					int slot = dp.getInt();
					dpAnswer.setInt(slot);
					lootBag.removeItem(slot);
				}
				
				if(lootBag.getItems().size() < 1)
					lootbags.remove(bagId);
			}
			else if(lootBag.getItems().containsKey(slotNr))
			{
				dpAnswer.setInt(1);//count
				dpAnswer.setInt(slotNr);
				lootBag.removeItem(slotNr);
				
				if(lootBag.getItems().size() < 1)
					lootbags.remove(bagId);
			}
			
			sendToAllActivePlayers(dpAnswer);
		}
	}
	
	public void handleRequestChestItem(int playerNr, DataPacket dp)
	{
		int chestId = dp.getInt();
		int slotNr = dp.getInt();
		LevelObject_Chest chest = (LevelObject_Chest) tmxMap.getLevelObjects().get(chestId);
		
		if(chest != null)
		{
			DataPacket dpAnswer = new DataPacket();
			dpAnswer.setInt(MessageType.MSG_CHEST_ITEM_PICKED);
			dpAnswer.setInt(playerNr);
			dpAnswer.setInt(chest.getId());
			
			if(slotNr == -1)//all
			{
				dpAnswer.setInt(chest.getItemStrings().size());
				for(Entry<Integer, String> e : chest.getItemStrings().entrySet())
				{
					dpAnswer.setInt(e.getKey());
					chest.itemPicked(e.getKey());
				}
			}
			else if(slotNr == -2)//multiple specific
			{
				int size = dp.getInt();
				dpAnswer.setInt(size);
				for(int i = 0; i < size; i++)
				{
					int slot = dp.getInt();
					dpAnswer.setInt(slot);
					chest.itemPicked(slot);
				}
			}
			else if(chest.getItemStrings().containsKey(slotNr))
			{
				dpAnswer.setInt(1);//count
				dpAnswer.setInt(slotNr);
				chest.itemPicked(slotNr);
			}
			
			sendToAllActivePlayers(dpAnswer);
			
			if(chest.isUnique() && !level.getUniqueChestsOpened().contains(chest.getUniqueId()))
			{
				level.getUniqueChestsOpened().add(chest.getUniqueId());
				
				DataPacket dpUn = new DataPacket();
				dpUn.setInt(MessageType.MSG_UNIQUE_CHEST_OPENED);
				dpUn.setInt(chest.getUniqueId());
				sendToPlayer(1, dpUn);
			}
		}
	}
	
	public void handleDropPlayerItem(int playerNr, DataPacket dp)
	{
		List<Item> item = new ArrayList<Item>();
		item.add(Item.getItemFromStringFormat(dp.getString()));
		dropItems(item, dp.getFloat(), dp.getFloat());
	}
	
	public void handleRequestSpell(int playerNr, DataPacket dp)
	{
		String name = dp.getString();
		int spellLevel = dp.getInt();
		
		Spell.activateSpell(name, spellLevel, getPlayer(playerNr), playerNr, dp, this);
	}
	
	public void handleRequestCondition(int playerNr, DataPacket dp)
	{
		String name = dp.getString();
		String valueName = dp.getString();
		int value = dp.getInt();
		int duration = dp.getInt();
		boolean isAttribute = dp.getBoolean();
		String effect = dp.getString();
		long absStartTime = System.currentTimeMillis();
		
		requestCondition(playerNr, name, valueName, value, duration, isAttribute, effect, absStartTime);
	}
	
	public void requestCondition(int entityId, String name, String valueName, int value, int duration, boolean isAttribute, String effect, long absStartTime)
	{
		requestCondition(entityId, name, valueName, value, duration, isAttribute, effect, absStartTime, null);
	}
	
	public void requestCondition(int entityId, String name, String valueName, int value, int duration, boolean isAttribute, String effect, long absStartTime, Condition aftereffect)
	{
		requestCondition(entityId, name, valueName, value, duration, isAttribute, effect, absStartTime, null, null);
	}
	
	public void requestCondition(int entityId, String name, String valueName, int value, int duration, boolean isAttribute, String effect, long absStartTime, Condition aftereffect, IConditionListener listener)
	{
		Condition condition = new Condition(name, valueName, value, duration, isAttribute, effect, absStartTime);
		if(aftereffect != null)
			condition.setAftereffect(aftereffect);
		if(listener != null)
			condition.setListener(listener);
		if(entityId <= Server.MAX_SUPPORTED && entityId > 0)
			getPlayer(entityId).addCondition(condition);
		else if(entityId > 0)
			enemies.get(entityId).addCondition(condition);
		else
			playerMinions.get(entityId).addCondition(condition);
		
		if(condition.getConditionListener() != null)
			condition.getConditionListener().onApplied(getEntity(entityId));
		
		if(!effect.equals(""))
		{
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_ADD_CONDITION_EFFECT_OTHERS);
			dpOthers.setInt(entityId);
			dpOthers.setString(name);
			dpOthers.setInt(duration);
			dpOthers.setLong(absStartTime);
			dpOthers.setString(effect);
			
			if(entityId <= Server.MAX_SUPPORTED && entityId > 0)
			{
				DataPacket dpAnswer = new DataPacket();
				dpAnswer.setInt(MessageType.MSG_ADD_CONDITION);
				dpAnswer.setString(name);
				dpAnswer.setInt(duration);
				dpAnswer.setLong(absStartTime);
				dpAnswer.setString(effect);
				sendToPlayer(entityId, dpAnswer);
				
				sendToAllActivePlayersExcept(entityId, dpOthers);
			}
			else
				sendToAllActivePlayers(dpOthers);
		}
	}
	
	public void requestCondition(int entityId, String name, ConcurrentHashMap<String, Integer> buffs, int duration, boolean isAttribute, String effect, long absStartTime)
	{
		Condition condition = new Condition(name, buffs, duration, isAttribute, effect, absStartTime);
		if(entityId <= Server.MAX_SUPPORTED && entityId > 0)
			getPlayer(entityId).addCondition(condition);
		else if(entityId > 0)
			enemies.get(entityId).addCondition(condition);
		else
			playerMinions.get(entityId).addCondition(condition);
		
		if(!effect.equals(""))
		{
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_ADD_CONDITION_EFFECT_OTHERS);
			dpOthers.setInt(entityId);
			dpOthers.setString(name);
			dpOthers.setInt(duration);
			dpOthers.setLong(absStartTime);
			dpOthers.setString(effect);
			
			if(entityId <= Server.MAX_SUPPORTED && entityId > 0)
			{
				DataPacket dpAnswer = new DataPacket();
				dpAnswer.setInt(MessageType.MSG_ADD_CONDITION);
				dpAnswer.setString(name);
				dpAnswer.setInt(duration);
				dpAnswer.setLong(absStartTime);
				dpAnswer.setString(effect);
				
				sendToPlayer(entityId, dpAnswer);
				
				sendToAllActivePlayersExcept(entityId, dpOthers);
			}
			else
				sendToAllActivePlayers(dpOthers);
		}
	}
	
	private final List<EffectToInform> effectsToInform = new ArrayList<EffectToInform>();
	
	public void playEffect(float originX, float originY, float width, float height, String effect, int duration)
	{
		playEffect(originX, originY, width, height, effect, duration, true);
	}
	
	public void playEffect(float originX, float originY, float width, float height, String effect, int duration, boolean loop)
	{
		if(!effectQueue.isEmpty())
			effectsToInform.add(effectQueue.poll().fill(originX, originY, width, height, effect, duration, loop));
		else
			effectsToInform.add(new EffectToInform(originX, originY, width, height, effect, duration, loop));
	}
	
	public void playEffect(int targedId, String effect, int duration)//hook has to be written first! (ClientLevel) currently always TRUE
	{
		if(!effectQueue.isEmpty())
			effectsToInform.add(effectQueue.poll().fill(targedId, true, effect, duration));
		else
			effectsToInform.add(new EffectToInform(targedId, true, effect, duration));
	}
	
	public void playEffect(int targedId, int width, int height, String effect, int duration)//hook has to be written first! (ClientLevel) currently always TRUE
	{
		if(!effectQueue.isEmpty())
			effectsToInform.add(effectQueue.poll().fill(targedId, true, width, height, effect, duration));
		else
			effectsToInform.add(new EffectToInform(targedId, true, width, height, effect, duration));
	}
	
	private final Queue<EffectToInform> effectQueue = new LinkedList<EffectToInform>();
	
	private class EffectToInform
	{
		public float originX, originY;
		public float width, height = 0;
		public String effect;
		public int targedId = -1;
		public boolean hook = false;
		public int duration = 0;
		public boolean loop = true;
		
		public EffectToInform(float originX, float originY, float width, float height, String effect, int duration, boolean loop)
		{
			fill(originX, originY, width, height, effect, duration, loop);
		}
		
		public EffectToInform(int targedId, boolean hook, String effect, int duration)
		{
			fill(targedId, hook, effect, duration);
		}
		
		public EffectToInform(int targedId, boolean hook, int width, int height, String effect, int duration)
		{
			fill(targedId, hook, width, height, effect, duration);
		}
		
		public EffectToInform fill(float originX, float originY, float width, float height, String effect, int duration, boolean loop)
		{
			this.originX = originX;
			this.originY = originY;
			this.width = width;
			this.height = height;
			this.effect = effect;
			this.duration = duration;
			this.loop = loop;
			return this;
		}
		
		public EffectToInform fill(int targedId, boolean hook, String effect, int duration)
		{
			this.targedId = targedId;
			this.hook = hook;
			this.effect = effect;
			this.duration = duration;
			return this;
		}
		
		public EffectToInform fill(int targedId, boolean hook, int width, int height, String effect, int duration)
		{
			this.targedId = targedId;
			this.hook = hook;
			this.width = width;
			this.height = height;
			this.effect = effect;
			this.duration = duration;
			return this;
		}
		
		public EffectToInform reset()
		{
			this.targedId = -1;
			return this;
		}
	}
	
	public void playSound(String sound, float x, float y)
	{
		for(PlayerServer player : players.values())
			if(/*!player.isDead() && */Math.pow(player.getOriginX() - x, 2) + Math.pow(player.getOriginY() - y, 2) <= SceneManager.CAMERA_WIDTH * SceneManager.CAMERA_WIDTH)
			{
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MSG_PLAY_SOUND);
				dp.setString(sound);
				sendToPlayer(player.Nr, dp);
			}
	}
	
	public void playSound(int id, String sound)
	{
		if(Server.isPlayer(id))
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_PLAY_SOUND);
			dp.setString(sound);
			sendToPlayer(id, dp);
		}
	}
	
	public void playSound(String sound)
	{
		for(PlayerServer player : players.values())
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_PLAY_SOUND);
			dp.setString(sound);
			sendToPlayer(player.Nr, dp);
		}
	}
	
	public void shoot(Integer shooterId, ShootRequest shootRequest)
	{
		if(shootRequest instanceof ShootRequestAimLock)
			shoot(shooterId, ((ShootRequestAimLock) shootRequest).target, shootRequest.damageType, shootRequest.maxRange, shootRequest.x, shootRequest.y, shootRequest.bulletType, shootRequest.onhiteffect);
		else
			shoot(shooterId, shootRequest.piercing, shootRequest.damageType, shootRequest.maxRange, shootRequest.x, shootRequest.y, shootRequest.rotation, shootRequest.directionX, shootRequest.directionY, shootRequest.bulletType);
	}
	
	public void shoot(int shooterId, boolean piercing, int damageType, float maxRange, float x, float y, float rotation, float dirX, float dirY, String bulletType)
	{
		shoot(shooterId, piercing, damageType, maxRange, x, y, rotation, dirX, dirY, bulletType, null, true);
	}
	
	public void shoot(int shooterId, boolean piercing, int damageType, float maxRange, float x, float y, float rotation, float dirX, float dirY, String bulletType, boolean hasCollision)
	{
		shoot(shooterId, piercing, damageType, maxRange, x, y, rotation, dirX, dirY, bulletType, null, hasCollision);
	}
	
	public void shoot(int shooterId, boolean piercing, int damageType, float maxRange, float x, float y, float rotation, float dirX, float dirY, String bulletType, BulletOnHitEffect onhiteffect)
	{
		shoot(shooterId, piercing, damageType, maxRange, x, y, rotation, dirX, dirY, bulletType, onhiteffect, true);
	}
	
	public void shoot(int shooterId, boolean piercing, int damageType, float maxRange, float x, float y, float rotation, float dirX, float dirY, String bulletType, BulletOnHitEffect onhiteffect, boolean hasCollision)
	{
		bulletCounter++;
		int id = bulletCounter;
		
		//set up a packet containing bullet infos
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_BULLET_SHOT);
		dpAnswer.setInt(id);
		dpAnswer.setInt(-1);
		dpAnswer.setFloat(maxRange);
		dpAnswer.setFloat(x);// x
		dpAnswer.setFloat(y);// y
		dpAnswer.setFloat(rotation);// rotation
		dpAnswer.setFloat(dirX);// dirX
		dpAnswer.setFloat(dirY);// dirY
		dpAnswer.setString(bulletType);// bulletType
		dpAnswer.setBoolean(hasCollision);//hasCollision
		dpAnswer.setInt(shooterId);
		
		sendToAllActivePlayers(dpAnswer);
		
		if(shooterId > Server.MAX_SUPPORTED)
		{
			Bullet bullet = new Bullet(tmxMap, enemies.get(shooterId), piercing, damageType, maxRange, x, y, rotation, dirX, dirY, bulletType, onhiteffect, hasCollision);
			enemyBullets.put(id, bullet);
		}
		else if(shooterId > 0)
		{
			Bullet bullet = new Bullet(tmxMap, players.get(shooterId), piercing, damageType, maxRange, x, y, rotation, dirX, dirY, bulletType, onhiteffect, hasCollision);
			playerBullets.put(id, bullet);
		}
		else
		{
			Bullet bullet = new Bullet(tmxMap, playerMinions.get(shooterId), piercing, damageType, maxRange, x, y, rotation, dirX, dirY, bulletType, onhiteffect, hasCollision);
			playerBullets.put(id, bullet);
		}
	}
	
	public void shoot(int shooterId, EntityServerMixin target, int damageType, float maxRange, float x, float y, String bulletType, BulletOnHitEffect onhiteffect)
	{
		bulletCounter++;
		int id = bulletCounter;
		
		//set up a packet containing bullet infos
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_BULLET_SHOT);
		dpAnswer.setInt(id);
		dpAnswer.setInt(target.Nr);
		dpAnswer.setFloat(maxRange);
		dpAnswer.setFloat(x);// x
		dpAnswer.setFloat(y);// y
		dpAnswer.setFloat(0);// rotation
		dpAnswer.setFloat(0);// dirX
		dpAnswer.setFloat(0);// dirY
		dpAnswer.setString(bulletType);// bulletType
		dpAnswer.setBoolean(false);//hasCollision
		dpAnswer.setInt(shooterId);
		
		sendToAllActivePlayers(dpAnswer);
		
		if(shooterId > Server.MAX_SUPPORTED)
		{
			EntityServerMixin owner = enemies.get(shooterId);
			enemyBullets.put(id, new Bullet(tmxMap, owner, target, damageType, maxRange, x, y, bulletType, onhiteffect));
		}
		else if(shooterId > 0)
		{
			EntityServerMixin owner = players.get(shooterId);
			playerBullets.put(id, new Bullet(tmxMap, owner, target, damageType, maxRange, x, y, bulletType, onhiteffect));
		}
		else
		{
			EntityServerMixin owner = playerMinions.get(shooterId);
			playerBullets.put(id, new Bullet(tmxMap, owner, target, damageType, maxRange, x, y, bulletType, onhiteffect));
		}
	}
	
	public void handlePlayerSetNewPosition(int playerNr, DataPacket dp)
	{
		DataPacket dpAnswer = new DataPacket(MessageType.MSG_ENTITY_SET_NEW_POS);
		dpAnswer.setInt(playerNr);
		dpAnswer.setFloat(dp.getFloat());
		dpAnswer.setFloat(dp.getFloat());
		sendToAllActivePlayersExcept(playerNr, dpAnswer);
	}
	
	public void sendToAllActivePlayers(DataPacket dp)
	{
		byte[] data = dp.finish();
		
		for(Entry<Integer, PlayerServer> e : players.entrySet())//SEND TO ALL CLIENTS
		{
			try
			{
				if(!e.getValue().isLoading)
					connectionHandler.sendData(e.getKey(), data);
			}
			catch (IOException ex)
			{
				Log.e("Realm", "could send data22 to player: " + e.getKey());
			}
		}
	}
	
	public void sendToAllActivePlayersExcept(int playerNr, DataPacket dp)
	{
		byte[] data = dp.finish();
		
		for(Entry<Integer, PlayerServer> e : players.entrySet())//SEND TO ALL CLIENTS EXCEPT ONE
		{
			try
			{
				if(e.getKey() != playerNr && !e.getValue().isLoading)
					connectionHandler.sendData(e.getKey(), data);
			}
			catch (IOException ex)
			{
				Log.e("Realm", "could send data33 to player: " + e.getKey());
			}
		}
	}
	
	public void sendToPlayer(int playerNr, DataPacket dp)
	{
		byte[] data = dp.finish();
		if(players.containsKey(playerNr))
			try
			{
				connectionHandler.sendData(playerNr, data);
			}
			catch (IOException e)
			{
				Log.e("Realm", "could send data11 to player: " + playerNr);
			}
	}
	
	public ConcurrentHashMap<Integer, PlayerServer> getPlayers()
	{
		return players;
	}
	
	public PlayerServer getPlayer(int playerNr)
	{
		return players.get(playerNr);
	}
	
	public TMXMap getMap()
	{
		return tmxMap;
	}
	
	public int getRealmPlayerCount()
	{
		return players.size();
	}
	
	public String getCurrentMapName()
	{
		return tmxMap.getCurrentMapName();
	}
	
	public void handleNpcSpoken(int playerNr, DataPacket dp)
	{
		int npcId = dp.getInt();
		int npcUniqueId = dp.getInt();
		LevelObject npc = tmxMap.getLevelObjects().get(npcId);
		
		if(npc instanceof LevelObject_NPC && ((LevelObject_NPC) npc).doRemove)
		{
			((LevelObject_NPC) npc).setRemoveFlagTrue();
			npc.setStateAndInform(1, this);
		}
		
		if(level.getTotalPlayerCount() > 1)
		{
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_NPC_SPOKEN);
			dpOthers.setInt(npcUniqueId);
			level.sendDataPacketToAllConnectedPlayersExcept(playerNr, dpOthers);
		}
	}
	
	public void handleOnEnterEvent(PlayerServer player, MapProperties properties)
	{
		if(!player.isLoading)
		{
			if(properties.containsKey("realmchange"))
			{
				level.playerJoinsRealm(player, level.getRealmByName(properties.get("realmchange", String.class)));
			}
			else if(properties.containsKey("levelchange"))
			{
				level.changeRealmMap(this, properties.get("levelchange", String.class));
			}
			else if(properties.containsKey("levelObjectTrigger"))
			{
				LevelObject obj = tmxMap.getLevelObjects().get(Integer.parseInt(properties.get("levelObjectTrigger", String.class)));
				if(obj != null)
					obj.trigger(this, player.Nr);
			}
			else if(properties.containsKey("questArea"))
			{
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MSG_AREA_ENTERED);
				dp.setString(properties.get("questArea", String.class));
				level.sendDataPacketToAllConnectedPlayers(dp);
			}
			else if(properties.containsKey("area"))
			{
				DataPacket dp = new DataPacket();
				dp.setInt(MessageType.MSG_AREA_ENTERED);
				dp.setString(properties.get("area", String.class));
				level.sendDataPacketToAllConnectedPlayers(dp);
			}
			else if(properties.containsKey("tempRealm"))
			{
				this.handleRequestTempRealm(player.Nr, properties.get("tempRealm", String.class));
			}
		}
	}
	
	public void handleOnEnterEvent(EntityServer enemy, MapProperties properties)
	{
		if(properties.containsKey("levelObjectTrigger"))
		{
			LevelObject obj = tmxMap.getLevelObjects().get(Integer.parseInt(properties.get("levelObjectTrigger", String.class)));
			if(obj != null)
				obj.trigger(this, enemy.Nr);
		}
	}
	
	public void handleOnLeftEvent(PlayerServer player, MapProperties properties)
	{
//		if(!player.isLoading)
//			for(TMXProperty property : properties)
//			{
//				
//			}
	}
	
	public void handleOnLeftEvent(EntityServer enemy, MapProperties properties)
	{
//		for(TMXProperty property : properties)
//		{
//			
//		}
	}
	
	public void shareLevelObjectStateChanged(LevelObject levelObject, int state)
	{
		DataPacket dpAnswer = new DataPacket();
		dpAnswer.setInt(MessageType.MSG_LEVEL_OBJECT_STATE_CHANGED);
		dpAnswer.setInt(levelObject.id);
		dpAnswer.setInt(state);
		
		sendToAllActivePlayers(dpAnswer);
	}
	
	public void handleJoinTeam(int playerNr, DataPacket dp)
	{
		int teamNr = dp.getInt();
		players.get(playerNr).setTeamNr(teamNr);
		ArrayList<EventObject> spawns = tmxMap.getStaticEvents("TeamSpawn");
		for(EventObject ev : spawns)
			if(ev.properties.containsKey("team") && Integer.parseInt(ev.properties.get("team", String.class)) == teamNr)
			{
				players.get(playerNr).setNewPosition(ev.getRectangle().getX(), ev.getRectangle().getY());
				
				DataPacket dpTeam = new DataPacket();
				dpTeam.setInt(MessageType.MSG_PLAYER_TEAM_JOINED);
				dpTeam.setInt(playerNr);
				dpTeam.setInt(teamNr);
				sendToAllActivePlayers(dpTeam);
			}
	}
	
	public void handleRequestRealm(int playerNr, DataPacket dp)
	{
		String mapName = dp.getString();
		
		if(mapName.equals("town"))
			requestTownPort(playerNr);
		else if(mapName.startsWith("pvp"))
			requestPvPRealm(mapName, players.get(playerNr));
		else if(mapName.startsWith("pvm"))
			requestPvERealm(mapName, players.get(playerNr));
		else
			requestFightRealm(mapName, -1, true, players.get(playerNr));
	}
	
	public void handleRequestTempRealm(int playerNr, String name)
	{
		String mapName = name;
		
		Realm tempRealm = level.getTempRealm(mapName);
		
		boolean playerIsOutside = players.get(playerNr).realmID != tempRealm.realmID;
		if(playerIsOutside)
		{
			Log.w("Realm", "Move player to temp realm!");
			level.playerJoinsRealm(players.get(playerNr), tempRealm);
		}
	}
	
	public void handleRequestRealmWithLairNr(int playerNr, DataPacket dp)
	{
		String mapName = dp.getString();
		
		if(mapName.equals("town"))
			requestTownPort(playerNr);//TODO: If necessary
		else if(mapName.startsWith("pvp"))
			requestPvPRealm(mapName, players.get(playerNr));//TODO: If necessary
		else if(mapName.startsWith("pvm"))
			requestPvERealm(mapName, players.get(playerNr));//TODO: If necessary
		else
			requestFightRealm(mapName, dp.getInt(), dp.getBoolean(), players.get(playerNr));
	}
	
	public void requestTownPort(int playerNr)
	{
		if(!getMap().getCurrentMapName().toLowerCase().equals("town") && !getMap().getMapProperty("disablePort").equals("true"))
			level.playerPortTown(playerNr);
	}
	
	private void requestFightRealm(String name, int levelNr, boolean forced, PlayerServer player)
	{
		Realm fightRealm = level.getRealmByName("fight");
		if(!forced && !fightRealm.getCurrentMapName().equals("pretown") && !fightRealm.getCurrentMapName().equalsIgnoreCase(name + levelNr))
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_CONFIRM_REALM_REQUEST);
			dp.setString(name);
			dp.setInt(levelNr);
			sendToPlayer(player.Nr, dp);
		}
		else
		{
			fightRealm.currentLair = name;
			if(levelNr != -1)
				fightRealm.currentLevelNr = levelNr;
			else
				fightRealm.currentLevelNr = level.getUnlockedLairs().containsKey(fightRealm.currentLair) ? level.getUnlockedLairs().get(fightRealm.currentLair) : 1;
			
			if(!fightRealm.getCurrentMapName().equalsIgnoreCase(fightRealm.currentLair + fightRealm.currentLevelNr))
				level.changeRealmMap("fight", fightRealm.currentLair + fightRealm.currentLevelNr);
			
			boolean playerIsInTown = player.realmID == level.getRealmByName("town").realmID;
			if(playerIsInTown)
			{
				Log.w("Realm", "Move player to fight realm!");
				level.playerJoinsRealm(player, fightRealm);
			}
		}
	}
	
	private void requestPvPRealm(String name, PlayerServer player)
	{
		Realm pvpRealm = level.getRealmByName("pvp");
		if(!pvpRealm.currentLair.equalsIgnoreCase(name))
		{
			pvpRealm.currentLair = name;
			level.changeRealmMap("pvp", pvpRealm.currentLair);
		}
		
		boolean playerIsInTown = player.realmID == level.getRealmByName("town").realmID;
		if(playerIsInTown)
		{
			Log.w("Realm", "Move player to pvp realm!");
			level.playerJoinsRealm(player, pvpRealm);
		}
	}
	
	private void requestPvERealm(String name, PlayerServer player)
	{
		Realm pvmRealm = level.getRealmByName("pvm");
		if(!pvmRealm.currentLair.equalsIgnoreCase(name))
		{
			pvmRealm.currentLair = name;
			level.changeRealmMap("pvm", pvmRealm.currentLair);
		}
		
		boolean playerIsInTown = player.realmID == level.getRealmByName("town").realmID;
		if(playerIsInTown)
		{
			Log.w("Realm", "Move player to pvm realm!");
			level.playerJoinsRealm(player, pvmRealm);
		}
	}
	
	public void handleRequestRespawn(int playerNr, DataPacket dp)
	{
		if(!players.get(playerNr).wasDead)
			return;
		players.get(playerNr).setHp(players.get(playerNr).getMaxHp() / 2);
		players.get(playerNr).setRotation(180);
		players.get(playerNr).removeAllConditions();
		players.get(playerNr).wasDead = false;
		
		level.playerJoinsRealm(players.get(playerNr), level.getRealmByName("town"));
	}
	
	public void nextLevel()
	{
		currentLevelNr++;
		level.unlockedLairsPut(currentLair, currentLevelNr);
		
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_NEXT_LAIR);
		dp.setString(currentLair);
		dp.setInt(currentLevelNr);
		level.sendDataPacketToAllConnectedPlayers(dp);
		
		level.changeRealmMap("fight", currentLair + currentLevelNr);
	}
	
	public void onLevelFinished()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_POST_MESSAGE);
		dp.setString("You will be ported in 10 seconds!");
		dp.setBoolean(true);
		sendToAllActivePlayers(dp);
		
		lairResetCounterLeft = lairResetCounter;
	}
	
	boolean realmIsClosing = false;
	
	public void closeLair()
	{
		realmIsClosing = true;
		for(Integer playerNr : players.keySet())
			level.playerPortTown(playerNr);
		
		level.changeRealmMap(this, "pretown");
		realmIsClosing = false;
	}
	
	private static EmptyRealm emptyRealm;
	
	public static EmptyRealm getEmptyRealm()
	{
		if(emptyRealm == null)
			emptyRealm = new EmptyRealm();
		
		return emptyRealm;
	}
	
	protected Realm()
	{
	}
	
	public EntityServerMixin getEntity(int shooterId)
	{
		if(shooterId <= Server.MAX_SUPPORTED && shooterId > 0)
			return players.get(shooterId);
		else if(shooterId > 0)
			return enemies.get(shooterId);
		else
			return playerMinions.get(shooterId);
	}
	
	public ConcurrentHashMap<Integer, EntityServer> getEnemies()
	{
		return enemies;
	}
	
	public void handleLevelPulled(int id)
	{
		LevelObject_Lever lever = (LevelObject_Lever) tmxMap.getLevelObjects().get(id);
		if(lever != null)
		{
			lever.setStateAndInform(1, this);//turn lever
			final int doorId = lever.getDoorId();
			for(LevelObject obj : tmxMap.getLevelObjects().values())
				//loop all level objects
				if(obj instanceof LevelObject_Door && ((LevelObject_Door) obj).getDoorId() == doorId)//door matching the lever
				{
					obj.setStateAndInform(1, this);//open door
					tmxMap.removeCollision(obj);//remove collision from door
					DataPacket dp = new DataPacket();
					dp.setInt(MessageType.MSG_LEVEL_OBJECT_REMOVE_COLLISION);
					dp.setInt(obj.id);
					sendToAllActivePlayers(dp);//inform players to remove collision
				}
		}
	}
	
	public void addLevelObjectAtRuntime(Rectangle rectangle, String name, MapProperties tmxObjectProperties)
	{
		LevelObject obj = tmxMap.addNewLevelObjectServer(this, rectangle, name, tmxObjectProperties);
		
		if(!obj.isServerOnly())
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_ADD_LEVEL_OBJECT);
			dp.setInt(obj.getId());
			dp.setFloat(obj.getBounds().getX());
			dp.setFloat(obj.getBounds().getY());
			dp.setInt((int) obj.getBounds().getWidth());
			dp.setInt((int) obj.getBounds().getHeight());
			dp.setString(obj.toStringFormat());
			
			sendToAllActivePlayers(dp);
		}
	}
	
	public void addLevelObjectAtRuntime(LevelObject obj)
	{
		tmxMap.addNewLevelObjectServer(obj, this);
		
		if(!obj.isServerOnly())
		{
			DataPacket dp = new DataPacket();
			dp.setInt(MessageType.MSG_ADD_LEVEL_OBJECT);
			dp.setInt(obj.getId());
			dp.setFloat(obj.getBounds().getX());
			dp.setFloat(obj.getBounds().getY());
			dp.setInt((int) obj.getBounds().getWidth());
			dp.setInt((int) obj.getBounds().getHeight());
			dp.setString(obj.toStringFormat());
			
			sendToAllActivePlayers(dp);
		}
	}
	
	public void removeLevelObject(LevelObject levelObject)
	{
		tmxMap.removeLevelObject(levelObject.getId(), this);
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REMOVE_LEVEL_OBJECTS);
		dp.setInt(1);
		dp.setInt(levelObject.getId());
		
		sendToAllActivePlayers(dp);
	}
	
	public void spawnTownPortal(float originX, float originY)
	{
		addLevelObjectAtRuntime(new Rectangle(originX - 24, originY - 24, 48, 48), "TownPortal", null);
	}
	
	public List<PlayerServer> getPlayersInRange(float x, float y, double radius)
	{
		List<PlayerServer> res = new ArrayList<PlayerServer>();
		
		for(PlayerServer player : players.values())
			if(Math.pow(player.getOriginX() - x, 2) + Math.pow(player.getOriginY() - y, 2) <= radius * radius)
				res.add(player);
		
		return res;
	}
	
	public List<EntityServerMixin> getPlayersAndMinionsInRange(float x, float y, double radius)
	{
		List<EntityServerMixin> res = new ArrayList<EntityServerMixin>();
		
		for(PlayerServer player : players.values())
			if(Math.pow(player.getOriginX() - x, 2) + Math.pow(player.getOriginY() - y, 2) <= radius * radius)
				res.add(player);
		
		for(EntityServerMinion minion : playerMinions.values())
			if(Math.pow(minion.getOriginX() - x, 2) + Math.pow(minion.getOriginY() - y, 2) <= radius * radius)
				res.add(minion);
		
		return res;
	}
	
	public HashMap<Integer, EntityServerMixin> getEntitiesInRange(float x, float y, double radius)
	{
		HashMap<Integer, EntityServerMixin> res = new HashMap<Integer, EntityServerMixin>();
		
		for(Entry<Integer, PlayerServer> player : players.entrySet())
			if(Math.pow(player.getValue().getOriginX() - x, 2) + Math.pow(player.getValue().getOriginY() - y, 2) <= radius * radius)
				res.put(player.getKey(), player.getValue());
		
		for(Entry<Integer, EntityServer> enemy : enemies.entrySet())
			if(Math.pow(enemy.getValue().getOriginX() - x, 2) + Math.pow(enemy.getValue().getOriginY() - y, 2) <= radius * radius)
				res.put(enemy.getKey(), enemy.getValue());
		
		for(Entry<Integer, EntityServerMinion> minion : playerMinions.entrySet())
			if(Math.pow(minion.getValue().getOriginX() - x, 2) + Math.pow(minion.getValue().getOriginY() - y, 2) <= radius * radius)
				res.put(minion.getKey(), minion.getValue());
		
		return res;
	}
	
	public EntityServerMixin getNearestHostileEntity(float x, float y, float radius, int searcherTeamNr)
	{
		HashMap<Double, EntityServerMixin> inRangeEntity = new HashMap<Double, EntityServerMixin>();
		
		for(Entry<Integer, PlayerServer> player : players.entrySet())
			if(!player.getValue().isDead() && player.getValue().getTeamNr() != searcherTeamNr)
			{
				double range = Math.sqrt(Math.pow(x - player.getValue().getOriginX(), 2) + Math.pow(y - player.getValue().getOriginY(), 2));
				if(range <= radius)
					inRangeEntity.put(range, player.getValue());
			}
		
		for(Entry<Integer, EntityServer> enemy : enemies.entrySet())
			if(!enemy.getValue().isDead() && enemy.getValue().getTeamNr() != searcherTeamNr)
			{
				double range = Math.sqrt(Math.pow(x - enemy.getValue().getOriginX(), 2) + Math.pow(y - enemy.getValue().getOriginY(), 2));
				if(range <= radius)
					inRangeEntity.put(range, enemy.getValue());
			}
		
		for(Entry<Integer, EntityServerMinion> minion : playerMinions.entrySet())
			if(!minion.getValue().isDead() && minion.getValue().getTeamNr() != searcherTeamNr)
			{
				double range = Math.sqrt(Math.pow(x - minion.getValue().getOriginX(), 2) + Math.pow(y - minion.getValue().getOriginY(), 2));
				if(range <= radius)
					inRangeEntity.put(range, minion.getValue());
			}
		
		double smallest = -1;
		for(Entry<Double, EntityServerMixin> e : inRangeEntity.entrySet())
		{
			if(smallest == -1 || e.getKey() <= smallest)
				smallest = e.getKey();
		}
		return inRangeEntity.get(smallest);
	}
	
	public void handleUnhandledPacket(int packetType, int playerNr, DataPacket dp)
	{
		
	}
	
	public int generateLootBagId()
	{
		lootBagCounter++;
		return lootBagCounter;
	}
	
	public boolean isPvP()
	{
		return pvp;
	}
	
	public ServerLevel getLevel()
	{
		return level;
	}
	
	public void handleIWannaDie(int playerNr)
	{
		PlayerServer pl = getPlayer(playerNr);
		if(pl != null)
			pl.setHp(-pl.getMaxHp());
	}
	
	public void handleRecoverHeal(int playerNr, float hp)
	{
		PlayerServer pl = getPlayer(playerNr);
		if(pl != null)
		{
			pl.doHeal(hp);
			sendDamageNumberToAllPlayers(playerNr, hp, DamageType.Heal);
			playSound(playerNr, SoundFile.spell_recover);
		}
	}
	
	public void handleEnemyOnFire(int playerNr, int attackerId)
	{
		EntityServerMixin entity = getEntity(attackerId);
		if(entity != null)
		{
			//LET IT BUUUUUUUUUUUUURN!!!
			playSound(SoundFile.burning_up, entity.getOriginX(), entity.getOriginY());
			requestCondition(attackerId, UniqueCondition.Burning, UniqueCondition.Burning, (int) (entity.getMaxHp() / 10f), 3000, false, UniqueCondition.Burning, System.currentTimeMillis());
		}
	}
	
	public void handleRandomBlink(int playerNr)
	{
		PlayerServer pl = getPlayer(playerNr);
		if(pl != null)
		{
			float newX = pl.getX(), newY = pl.getY();
			int triesLeft = 12;
			do
			{
				newX = pl.getX() - ModBlink.PORT_RANGE / 2 + GameHelper.$.getRandom().nextInt(ModBlink.PORT_RANGE);
				newY = pl.getY() - ModBlink.PORT_RANGE / 2 + GameHelper.$.getRandom().nextInt(ModBlink.PORT_RANGE);
				triesLeft--;
			} while(triesLeft > 0 && getMap().isCollisionAt(newX, newY, pl.getWidth(), pl.getHeight(), false));
			if(triesLeft > 0)
			{
				playSound(SoundFile.spell_port, pl.getOriginX(), pl.getOriginY());
				setNewEntityPosition(playerNr, newX, newY);
			}
		}
	}
}
