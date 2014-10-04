package com.vhelium.lotig.scene.gamescene.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.UniqueCondition;
import com.vhelium.lotig.scene.gamescene.client.player.MovementModifier;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Liquid;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;

public abstract class EntityServer extends EntityServerMixin
{
	protected int MIN_DISTANCE_TARGET_MOVED_TO_NOFIFY = 200;
	protected int TARGET_LOOSE_FACTOR = 2;
	private static final float LIQUID_SLOW = 0.5f;
	protected Realm realm;
	protected EntityServerMixin target;
	
	protected float globalSpellCooldown;
	protected float globalSpellCooldownLeft;
	protected List<SpellEnemy> spells;
	private SpellEnemy preparedSpell;
	
	protected float combatOutTime = 0f;
	protected final float combatResetTime = 3000f;
	
	public List<int[]> damageNumbersToInform;
	
	protected int level;
	
	//------Specific Variables-------
	protected String name = "null";
	protected String type = "null";
	protected String asset = "null";
	protected String bulletAsset = "null";
	protected String deathAnimation = "null";
	
	//HP, Mana, Damage, Armor and Res in upper class!
	
	protected int width;
	protected int height;
	
	protected float baseSpeed;
	protected float shootSpeed;
	protected float detectRange;
	protected float shootRange;
	protected boolean piercing;
	
	protected boolean roaming;
	public int roamingRange = 120;
	public int roamingDelay = 4000; //+1500
	private int roamingDelayLeft = roamingDelay * 2;
	private float roamingOriginX = 69.6969f;
	private float roamingOriginY = 69.6969f;
	
	protected final float maxVelocity = 0.4f;
	protected ConcurrentHashMap<String, MovementModifier> movementModifiers;
	//--------------------------------
	
	protected boolean oocRegen = true;
	
	private final float targetSearchDelay = 400;
	private float targetSearchDelayElapsed = targetSearchDelay - 10;
	
	protected float velocityX, velocityY = 0f;
	protected float directionTargetX, directionTargetY;
	protected boolean isOnLiquid = false;
	
	protected float[] targetPosition;
	
	protected float shootCooldownLeft = 0;
	
	private float tempX, tempY = 0f;
	private float prevX, prevY = 0f;
	
	private float prevTryAroundDirX, prevTryAroundDirY = 0;
	private char tempMainDir;
	
	protected float targetUpdateTime = 4000;
	protected float targetUpdateTimeElapsed;
	
	protected EntityServer(Realm realm)
	{
		this.realm = realm;
		damageNumbersToInform = new ArrayList<int[]>();
		spells = new ArrayList<SpellEnemy>();
		movementModifiers = new ConcurrentHashMap<String, MovementModifier>();
	}
	
	public void spawned()
	{
		
	}
	
	protected float movementSpeed;
	
	protected void calculateMovementSpeed()
	{
		movementSpeed = Math.max(0.06f * (getTempAttribute("SPD") / 100) + baseSpeed, 0.02f);
	}
	
	public void update(float delta, long millis, Collection<? extends EntityServerMixin>... enemies)
	{
		if(shootCooldownLeft > 0)
			shootCooldownLeft -= delta;
		
		calculateMovementSpeed();
		
		setVelocity(directionTargetX * movementSpeed, directionTargetY * movementSpeed);
		
		applyMoveModifiers(delta);
		
		boolean recalculate = false;
		if((velocityX != 0 || velocityY != 0) && !isStunned() && !isRooted())
		{
			tempX = getX();
			tempY = getY();
			prevX = tempX;
			prevY = tempY;
			
			if(walkMode != UniqueWalkDirection.NONE)
			{
				if(getMainDir() == tempMainDir)
				{
					if(walkMode == UniqueWalkDirection.walkOnlyRight || walkMode == UniqueWalkDirection.walkOnlyLeft)
					{
						setY(getY() + 3 * velocityY * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
						if(!isInSolid())
						{
							setY(tempY + velocityY * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
							walkMode = UniqueWalkDirection.NONE;
							prevTryAroundDirX = 0;
							prevTryAroundDirY = 0;
						}
						else
						{
							setY(tempY);
							setX(tempX + getVelocity(walkMode) * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
							if(isInSolid())
							{
								setX(tempX);
								walkMode = walkMode == UniqueWalkDirection.walkOnlyRight ? UniqueWalkDirection.walkOnlyLeft : UniqueWalkDirection.walkOnlyRight;
							}
						}
					}
					else
					{
						setX(getX() + 3 * velocityX * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
						if(!isInSolid())
						{
							setX(tempX + velocityX * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
							walkMode = UniqueWalkDirection.NONE;
							prevTryAroundDirX = 0;
							prevTryAroundDirY = 0;
						}
						else
						{
							setX(tempX);
							setY(tempY + getVelocity(walkMode) * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
							if(isInSolid())
							{
								setY(tempY);
								walkMode = walkMode == UniqueWalkDirection.walkOnlyTop ? UniqueWalkDirection.walkOnlyBot : UniqueWalkDirection.walkOnlyTop;
							}
						}
					}
				}
				else
				{
					walkMode = UniqueWalkDirection.NONE;
					prevTryAroundDirY = 0;
					prevTryAroundDirX = 0;
				}
				
				recalculate = true;
			}
			else if(Math.abs(velocityX) >= Math.abs(velocityY))//X movement is higher
			{
				boolean xSucceeded = false;
				prevTryAroundDirX = 0;
				setX(getX() + velocityX * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
				if(isInSolid())
				{
					setX(tempX);
					recalculate = true;
					
					if(velocityY >= 0)
						velocityY = getAbsoluteTargetDirectionVelocity();
					else
						velocityY = -getAbsoluteTargetDirectionVelocity();
				}
				else
				{
					xSucceeded = true;
					prevTryAroundDirY = 0;
				}
				
				setY(getY() + velocityY * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
				if(isInSolid())
				{
					setY(tempY);
					recalculate = true;
					
					if(xSucceeded)
					{
						//try x move again
						if(velocityX >= 0)
							velocityX = getAbsoluteTargetDirectionVelocity();
						else
							velocityX = -getAbsoluteTargetDirectionVelocity();
						
						float tempX2 = getX();
						setX(tempX + velocityX * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
						if(isInSolid())
						{
							setX(tempX2);
							xSucceeded = false;
						}
						
					}
					if(!xSucceeded)
					{
						setY(getY() + -velocityY * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
						if(isInSolid())
						{
							setY(tempY);
							//FUCKED (in a gap) :]
						}
						else
						{
							if(velocityY < 0)
								walkMode = UniqueWalkDirection.walkOnlyBot;
							else
								walkMode = UniqueWalkDirection.walkOnlyTop;
						}
					}
				}
				else if(!xSucceeded)//NOT inSolid (y)
				{
					if(prevTryAroundDirY != 0 && Math.signum(prevTryAroundDirY) != Math.signum(velocityY))
					{
						tempMainDir = 'x';
						if(previousWalkMode == UniqueWalkDirection.walkOnlyTop)
							walkMode = UniqueWalkDirection.walkOnlyBot;
						else
							walkMode = UniqueWalkDirection.walkOnlyTop;
						previousWalkMode = walkMode;
					}
					prevTryAroundDirY = velocityY;
				}
			}
			else
			//Y movement is higher
			{
				boolean ySucceeded = false;
				prevTryAroundDirY = 0;
				setY(getY() + velocityY * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
				if(isInSolid())
				{
					setY(tempY);
					recalculate = true;
					
					if(velocityX >= 0)
						velocityX = getAbsoluteTargetDirectionVelocity();
					else
						velocityX = -getAbsoluteTargetDirectionVelocity();
				}
				else
				{
					ySucceeded = true;
					if(walkMode != UniqueWalkDirection.NONE)
					{
						walkMode = UniqueWalkDirection.NONE;
						recalculate = true;
					}
					prevTryAroundDirX = 0;
				}
				
				setX(getX() + velocityX * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
				if(isInSolid())
				{
					setX(tempX);
					recalculate = true;
					
					if(ySucceeded)
					{
						//try y move again
						if(velocityY >= 0)
							velocityY = getAbsoluteTargetDirectionVelocity();
						else
							velocityY = -getAbsoluteTargetDirectionVelocity();
						
						float tempY2 = getY();
						setY(tempY + velocityY * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
						if(isInSolid())
						{
							setY(tempY2);
							ySucceeded = false;
						}
					}
					if(!ySucceeded)
					{
						setX(getX() + -velocityX * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
						if(isInSolid())
						{
							setX(tempX);
							//FUCKED (in a gap) :]
						}
						else
						{
							if(velocityY < 0)
								walkMode = UniqueWalkDirection.walkOnlyRight;
							else
								walkMode = UniqueWalkDirection.walkOnlyLeft;
						}
					}
				}
				else if(!ySucceeded)//NOT inSolid (x)
				{
					if(prevTryAroundDirX != 0 && Math.signum(prevTryAroundDirX) != Math.signum(velocityX))
					{
						tempMainDir = 'y';
						if(previousWalkMode == UniqueWalkDirection.walkOnlyRight)
							walkMode = UniqueWalkDirection.walkOnlyLeft;
						else
							walkMode = UniqueWalkDirection.walkOnlyRight;
						previousWalkMode = walkMode;
					}
					prevTryAroundDirX = velocityX;
				}
			}
		}
		
		if(targetPosition != null)
			if(Math.signum(prevX - targetPosition[0]) != Math.signum(getX() - targetPosition[0]) || Math.abs(getX() - targetPosition[0]) <= 4)
				if(Math.signum(prevY - targetPosition[1]) != Math.signum(getY() - targetPosition[1]) || Math.abs(getY() - targetPosition[1]) <= 4)
				{
					//signum signum signum...  ...signum
					targetPosition = null;
					setTagetDirection(0, 0);
					onWaypointReached();
				}
		
		if(!isShooting() && targetInRange() && !isStunned())//!isShooting() - be able to shoot in the head
		{
			//setRotation(MathUtils.radiansToDegrees * ((float)Math.atan2(target.getOriginX() - getOriginX(), target.getOriginY() - getOriginY())));
			
			//x2 = v2/v * x, v2 = 1
			float v = (float) Math.sqrt(Math.pow(target.getOriginX() - getOriginX(), 2) + Math.pow(target.getOriginY() - getOriginY(), 2));
			float directionX = (1 / v) * (target.getOriginX() - getOriginX());
			float directionY = (1 / v) * (target.getOriginY() - getOriginY());
			setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(directionX, -directionY)));
			
			shoot(directionX, directionY);
		}
		else if((velocityX != 0 || velocityY != 0) && !isVisualShooting() && !isStunned())
		{
			switch(walkMode)
			{
				case walkOnlyRight:
					setRotation(90);
					break;
				case walkOnlyLeft:
					setRotation(-90);
					break;
				case walkOnlyTop:
					setRotation(0);
					break;
				case walkOnlyBot:
					setRotation(180);
					break;
				default:
					setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(velocityX, -velocityY)));
					break;
			}
		}
		
		if(recalculate)
			recalculateDirectionToTarget();
		
		if(target == null)
		{
			targetSearchDelayElapsed += delta;
			if(targetSearchDelayElapsed > targetSearchDelay)
			{
				targetSearchDelayElapsed -= targetSearchDelay;
				target = getNearestEnemy(detectRange, enemies);
				if(target != null)
					onNewTargetFound();
			}
		}
		else if(target.realmID != this.realmID || target.isDead())
		{
			target = null;
			onTargetLost();
			roamingOriginX = getOriginX();
			roamingOriginY = getOriginY();
		}
		
		if(target != null && !noticedAboutDistChange && Math.abs(targetLastX - target.getX()) + Math.abs(targetLastY - target.getY()) > MIN_DISTANCE_TARGET_MOVED_TO_NOFIFY)//Ungenau (Faktor Wurzel(2)), aber scheiss egal, performance FTW
		{
			noticedAboutDistChange = true;
			onTargedMovedFarSinceLastWP();
		}
		
		stunned = false;
		rooted = false;
		silenced = false;
		for(Entry<String, Condition> condition : conditions.entrySet())
		{
			if(millis - condition.getValue().getAbsolutTimeSinceStart() >= condition.getValue().getDuration())
				conditionsToRemove.add(condition.getKey());
			
			else if(condition.getValue().getName().equalsIgnoreCase("HOT"))
				this.setHp(getHp() + delta / condition.getValue().getDuration() * condition.getValue().getValue());
			
			else if(condition.getValue().getName().equalsIgnoreCase("MOT"))
				this.setMana(getMana() + delta / condition.getValue().getDuration() * condition.getValue().getValue());
			
			else if(condition.getValue().getValueName().equalsIgnoreCase(UniqueCondition.Stunned))
				stunned = true;
			
			else if(condition.getValue().containsValue(UniqueCondition.Silenced))
				silenced = true;
			
			else if(condition.getValue().getValueName().equalsIgnoreCase("Root"))
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
				this.setHp(getHp() - delta / condition.getValue().getDuration() * condition.getValue().getValue());
				if((int) ((millis - condition.getValue().getAbsolutTimeSinceStart()) / 1000f) != (int) ((millis - condition.getValue().getAbsolutTimeSinceStart() - delta) / 1000f))
				{
					damageNumbersToInform.add(new int[] { (int) (1000f / condition.getValue().getDuration() * -condition.getValue().getValue()), DamageType.Fire });
				}
			}
			
			else if(condition.getValue().containsValue(UniqueCondition.Poisoned))
			{
				this.setHp(getHp() - delta / condition.getValue().getDuration() * condition.getValue().getValue());
				if((int) ((millis - condition.getValue().getAbsolutTimeSinceStart()) / 1000f) != (int) ((millis - condition.getValue().getAbsolutTimeSinceStart() - delta) / 1000f))
				{
					damageNumbersToInform.add(new int[] { (int) (1000f / condition.getValue().getDuration() * -condition.getValue().getValue()), DamageType.Poison });
				}
			}
			
			else if(condition.getValue().containsValue("ABSORB"))
			{
				if(getAbsorb() <= 0)
					conditionsToRemove.add(condition.getKey());
			}
		}
		
		for(String s : conditionsToRemove)
			removeCondition(s);
		
		conditionsToRemove.clear();
		
		isOnLiquid = false;
		for(LevelObject_Liquid liquid : realm.getMap().getLiquids())
			if(getX() + 1 + getWidth() - 2 >= liquid.getBounds().getX() && getY() + 1 + getHeight() - 2 >= liquid.getBounds().getY() && getX() + 1 <= liquid.getBounds().getX() + liquid.getBounds().getWidth() && getY() + 1 <= liquid.getBounds().getY() + liquid.getBounds().getHeight())
			{
				isOnLiquid = true;
				break;
			}
		
		targetUpdateTimeElapsed += delta;
		if(targetUpdateTimeElapsed >= targetUpdateTime)
		{
			targetUpdateTimeElapsed -= targetUpdateTime;
			if(target != null)
				refreshTarget(detectRange, enemies);
		}
		
		for(SpellEnemy spell : spells)
			spell.update(delta);
		
		if(target != null)
		{
			combatOutTime = 0;
			if(globalSpellCooldownLeft > 0)
				globalSpellCooldownLeft -= delta;
			if(!isStunned() && !isSilenced())
			{
				if(preparedSpell == null && globalSpellCooldownLeft <= 0)
				{
					for(SpellEnemy spell : spells)
					{
						if(spell.isReadyToCast(this))
						{
							if(spell.instantCast)
								useSpell(spell);
							else
								preparedSpell = spell;
						}
					}
				}
			}
		}
		
		if(roaming && target == null)
		{
			if(roamingOriginX == 69.6969f && roamingOriginY == 69.6969f)
			{
				roamingOriginX = getOriginX();
				roamingOriginY = getOriginY();
			}
			if(roamingDelayLeft > 0)
			{
				roamingDelayLeft -= delta;
				if(roamingDelayLeft <= 0)
				{
					goTo(roamingOriginX - roamingRange + GameHelper.getInstance().getRandom().nextInt(roamingRange * 2), roamingOriginY - roamingRange + GameHelper.getInstance().getRandom().nextInt(roamingRange * 2));
					roamingDelayLeft = roamingDelay + GameHelper.getInstance().getRandom().nextInt(1500);
				}
			}
		}
		
		if(target == null && oocRegen)
		{
			if(combatOutTime < combatResetTime)
				combatOutTime += delta;
			else
			{
				setHp(getHp() + getMaxHp() / 5 * delta / 1000f);
				setMana(getMana() + getMaxMana() / 5 * delta / 1000f);
			}
		}
	}
	
	private void useSpell(SpellEnemy spell)
	{
		spell.useCooldown();
		//x2 = v2/v * x, v2 = 1
		float v = (float) Math.sqrt(Math.pow(target.getOriginX() - getOriginX(), 2) + Math.pow(target.getOriginY() - getOriginY(), 2));
		float directionX = (1 / v) * (target.getOriginX() - getOriginX());
		float directionY = (1 / v) * (target.getOriginY() - getOriginY());
		setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(directionX, -directionY)));
		
		Log.e("Activating spell (Entitiyserver", ", " + spell.getName() + ", " + spell.getLevel() + ", " + this + ", " + Nr + ", " + realm);
		
		spell.activate(realm, Nr, getOriginX(), getOriginY(), getRotation(), directionX, directionY);
		if(spell.getCastSound() != null)
			realm.playSound(spell.getCastSound(), getOriginX(), getOriginY());
		
		globalSpellCooldownLeft = globalSpellCooldown;
	}
	
	private boolean targetInRange()
	{
		if(target == null || Math.sqrt(Math.pow(target.getOriginX() - getOriginX(), 2) + Math.pow(target.getOriginY() - getOriginY(), 2)) > shootRange)
			return false;
		return true;
	}
	
	protected abstract void onWaypointReached();
	
	protected abstract void onNoWaypointFound();
	
	protected abstract void onNewTargetFound();
	
	protected abstract void onTargetLost();
	
	protected abstract void onTargedMovedFarSinceLastWP();
	
	protected void refreshTarget(float maxDistance, Collection<? extends EntityServerMixin>... enemies)
	{
		EntityServerMixin prevTarget = target;
		target = getNearestEnemy(maxDistance * TARGET_LOOSE_FACTOR, enemies);
		if(target == prevTarget)
			return;
		else if(prevTarget != null && target == null)
		{
			onTargetLost();
			roamingOriginX = getOriginX();
			roamingOriginY = getOriginY();
		}
	}
	
	private float targetLastX = 0;
	private float targetLastY = 0;
	protected boolean noticedAboutDistChange = true;
	
	public void goTo(float x, float y)
	{
		targetPosition = getBestWaypoint(x, y);
		noticedAboutDistChange = false;
		prevTryAroundDirX = 0;
		prevTryAroundDirY = 0;
		walkMode = UniqueWalkDirection.NONE;
		previousWalkMode = UniqueWalkDirection.NONE;
		
		x -= getX();
		y -= getY();
		
		double length = Math.sqrt(x * x + y * y);
		setTagetDirection((float) (x / length), (float) (y / length));
		
		if(target != null)
		{
			targetLastX = target.getX();
			targetLastY = target.getY();
		}
	}
	
	public void goTo(Vector2 p)
	{
		goTo(p.x, p.y);
	}
	
	private List<Vector2> goToQueue;
	
	public void goToQueue(List<Vector2> gotos)
	{
		goToQueue = gotos;
		nextGoToInQueue();
	}
	
	protected void nextGoToInQueue()
	{
		if(goToQueue.size() > 0)
		{
			goTo(goToQueue.get(0));
			goToQueue.remove(0);
		}
		else
		{
			targetPosition = null;
			setTagetDirection(0, 0);
		}
	}
	
	private void recalculateDirectionToTarget()
	{
		if(targetPosition == null)
			return;
		
		float x = targetPosition[0] - getX();
		float y = targetPosition[1] - getY();
		
		double length = Math.sqrt(x * x + y * y);
		setTagetDirection((float) (x / length), (float) (y / length));
	}
	
	private float[] getBestWaypoint(float x, float y)
	{
		if(!realm.getMap().isCollisionAt(x, y, getWidth(), getHeight(), false))
		{
			return new float[] { x, y };
		}
		else
		{
			float tempX = getX() + 10;
			float tempY = getY() + 10;
			
			for(int layer = 1; layer <= 10; layer++)
			{
				for(int step = 1; step <= 4; step++)
				{
					for(int tile = 0; tile < layer * 2; tile++)
					{
						switch(step)
						{
							case 1:
								tempX -= 10;
								break;
							case 2:
								tempY -= 10;
								break;
							case 3:
								tempX += 10;
								break;
							case 4:
								tempY += 10;
								break;
						}
						
						if(!realm.getMap().isCollisionAt(tempX, tempY, getWidth(), getHeight(), false))
							return new float[] { tempX, tempY };
					}
				}
				tempX += 10;
				tempY += 10;
			}
			onNoWaypointFound();
			return new float[] { x, y };
		}
	}
	
	char getMainDir()
	{
		return Math.abs(velocityX) >= Math.abs(velocityY) ? 'x' : 'y';
	}
	
	private float getVelocity(UniqueWalkDirection walkMode)
	{
		if(walkMode == UniqueWalkDirection.walkOnlyRight || walkMode == UniqueWalkDirection.walkOnlyBot)
			return getAbsoluteTargetDirectionVelocity();
		else
			return -getAbsoluteTargetDirectionVelocity();
	}
	
	protected final HashMap<Double, EntityServerMixin> inRangeEntity = new HashMap<Double, EntityServerMixin>();
	
	protected EntityServerMixin getNearestEnemy(float maxDistance, Collection<? extends EntityServerMixin>... entities)
	{
		inRangeEntity.clear();
		for(Collection<? extends EntityServerMixin> group : entities)
			for(EntityServerMixin entity : group)
			{
				if(entity.getTeamNr() != this.getTeamNr())
				{
					double range = Math.sqrt(Math.pow(Math.abs(this.getX() - entity.getX()), 2) + Math.pow(Math.abs(this.getY() - entity.getY()), 2));
					if(range <= maxDistance && !entity.isDead())
						inRangeEntity.put(range, entity);
				}
			}
		double smallest = -1;
		for(Entry<Double, EntityServerMixin> e : inRangeEntity.entrySet())
		{
			if(smallest == -1 || e.getKey() <= smallest)
				smallest = e.getKey();
		}
		return inRangeEntity.get(smallest);
	}
	
	protected Boolean isInSolid()
	{
		return realm.getMap().isCollisionAt(getX(), getY(), getWidth(), getHeight(), false);
	}
	
	protected Boolean moveOutOfSolidX(int stepX, int iteration)
	{
		float tempPosX = getX();
		
		for(int x = 0; x < iteration; x++)
		{
			setX((int) (stepX + getX()));
			
			if(!isInSolid())
			{
				return true;
			}
		}
		
		setX(tempPosX);
		
		return false;
	}
	
	protected Boolean moveOutOfSolidY(int stepY, int iteration)
	{
		float tempPosY = getY();
		
		for(int y = 0; y < iteration; y++)
		{
			setY((int) (stepY + getY()));
			
			if(!isInSolid())
			{
				return true;
			}
		}
		
		setY(tempPosY);
		
		return false;
	}
	
	public void setTagetDirection(float x, float y)
	{
		directionTargetX = x;
		directionTargetY = y;
	}
	
	public void setVelocity(float x, float y)
	{
		velocityX = x;
		velocityY = y;
	}
	
	private float getAbsoluteTargetDirectionVelocity()
	{
		calculateMovementSpeed();
		final float x2 = directionTargetX * movementSpeed * directionTargetX * movementSpeed;
		final float y2 = directionTargetY * movementSpeed * directionTargetY * movementSpeed;
		return (float) Math.sqrt(x2 + y2);
	}
	
	@Override
	public void addMovementModifier(String name, MovementModifier modifier)
	{
		if(movementModifiers.containsKey(name))
			movementModifiers.get(name).set(name, modifier.dirX, modifier.dirY, modifier.velocity, modifier.decay);
		else
			movementModifiers.put(name, modifier);
	}
	
	@Override
	public void addMovementModifier(String name, float dirX, float dirY, float velocity, float decay)
	{
		if(movementModifiers.containsKey(name))
			movementModifiers.get(name).set(name, dirX, dirY, velocity, decay);
		else
			movementModifiers.put(name, new MovementModifier(name, dirX, dirY, velocity, decay));
	}
	
	public void removeMovementModifier(String name)
	{
		if(movementModifiers.containsKey(name))
			movementModifiers.remove(name);
	}
	
	List<String> modsToRemove = new ArrayList<String>();
	
	protected void applyMoveModifiers(float delta)
	{
		for(Entry<String, MovementModifier> mod : movementModifiers.entrySet())
		{
			velocityX += mod.getValue().velocity * mod.getValue().dirX;
			if(Math.abs(velocityX) > maxVelocity)
				velocityX = Math.signum(velocityX) * maxVelocity;
			
			velocityY += mod.getValue().velocity * mod.getValue().dirY;
			if(Math.abs(velocityY) > maxVelocity)
				velocityY = Math.signum(velocityY) * maxVelocity;
			
			mod.getValue().velocity *= mod.getValue().decay;
			
			if(!mod.getValue().validate())
				modsToRemove.add(mod.getKey());
		}
		
		for(String tr : modsToRemove)
			movementModifiers.remove(tr);
		modsToRemove.clear();
	}
	
	public ShootRequest shootRequest = new ShootRequest();
	
	public void shoot(float directionX, float directionY)
	{
		if(isStunned())
			return;
		shootCooldownLeft = shootSpeed - getTempAttribute("AS");
		
		if(preparedSpell == null)
		{
			shootRequest.createRequest(piercing, DamageType.Physical, shootRange, this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, this.getRotation(), directionX, directionY, bulletAsset);
		}
		else
		{
			useSpell(preparedSpell);
			preparedSpell = null;
		}
	}
	
	public boolean isShooting()
	{
		return shootCooldownLeft > 0;
	}
	
	public boolean isVisualShooting()
	{
		return shootCooldownLeft > shootSpeed - getVisualShootSpeed();
	}
	
	public boolean isMoving()
	{
		return velocityX != 0 || velocityY != 0;
	}
	
	private UniqueWalkDirection walkMode = UniqueWalkDirection.NONE;
	private UniqueWalkDirection previousWalkMode = UniqueWalkDirection.NONE;
	
	private enum UniqueWalkDirection
	{
		walkOnlyRight, walkOnlyLeft, walkOnlyTop, walkOnlyBot, NONE
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public String getAsset()
	{
		return asset;
	}
	
	@Override
	public void addCondition(Condition condition)
	{
		if(conditions.containsKey(condition.getName()))
			removeCondition(condition.getName());
		if(condition.isAttribute())
		{
			if(condition.getBuffs() == null)
			{
				int strt = tempAttributes.containsKey(condition.getValueName()) ? tempAttributes.get(condition.getValueName()) : 0;
				tempAttributes.put(condition.getValueName(), strt + condition.getValue());
			}
			else
			{
				for(Entry<String, Integer> e : condition.getBuffs().entrySet())
				{
					int strt = tempAttributes.containsKey(e.getKey()) ? tempAttributes.get(e.getKey()) : 0;
					tempAttributes.put(e.getKey(), strt + e.getValue());
				}
			}
		}
		conditions.put(condition.getName(), condition);
	}
	
	@Override
	public void removeCondition(String condition)
	{
		Condition con = conditions.get(condition);
		if(con == null)
			return;
		conditions.remove(condition);
		if(con.isAttribute())
		{
			if(con.getBuffs() == null && tempAttributes.containsKey(con.getValueName()))
			{
				if(tempAttributes.get(con.getValueName()) - con.getValue() != 0)
				{
					tempAttributes.put(con.getValueName(), tempAttributes.get(con.getValueName()) - con.getValue());
				}
				else
				{
					tempAttributes.remove(con.getValueName());
				}
			}
			else if(con.getBuffs() != null)
			{
				for(Entry<String, Integer> e : con.getBuffs().entrySet())
				{
					if(tempAttributes.containsKey(con.getValueName()))
					{
						if(tempAttributes.get(e.getKey()) - con.getValue() != 0)
						{
							tempAttributes.put(e.getKey(), tempAttributes.get(e.getKey()) - e.getValue());
						}
						else
						{
							tempAttributes.remove(e.getKey());
						}
					}
				}
			}
		}
		if(!con.isFinished())
		{
			DataPacket dp = new DataPacket(MessageType.MSG_REMOVE_CONDITION);
			dp.setInt(Nr);
			dp.setString(condition);
			realm.sendToAllActivePlayers(dp);
		}
		if(con.getAftereffect() != null)
		{
			Condition c = con.getAftereffect();
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
				
				realm.sendToAllActivePlayers(dpOthers);
			}
		}
	}
	
	public Condition getCondition(String name)
	{
		return conditions.get(name);
	}
	
	public HashMap<String, Condition> getConditions()
	{
		return conditions;
	}
	
	public void setTarget(EntityServerMixin ent)
	{
		if(ent != null)
		{
			target = ent;
			onNewTargetFound();
		}
	}
	
	@Override
	public int getTeamNr()
	{
		return 99;
	}
	
	public void onDeath()
	{
		
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setRectangle(Rectangle rectangle)
	{
		this.rectangle = rectangle;
	}
	
	public void setAsset(String asset)
	{
		this.asset = asset;
	}
	
	public void setBulletAsset(String bulletAsset)
	{
		this.bulletAsset = bulletAsset;
	}
	
	public String getDeathAnimation()
	{
		return deathAnimation;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public void setBaseSpeed(float baseSpeed)
	{
		this.baseSpeed = baseSpeed;
	}
	
	public void setShootSpeed(float shootSpeed)
	{
		this.shootSpeed = shootSpeed;
	}
	
	public void setDetectRange(float detectRange)
	{
		this.detectRange = detectRange;
	}
	
	public void setShootRange(float shootRange)
	{
		this.shootRange = shootRange;
	}
	
	public void setPiercing(boolean piercing)
	{
		this.piercing = piercing;
	}
	
	public void setNewPosition(float x, float y)
	{
		walkMode = UniqueWalkDirection.NONE;
		tempX = 0f;
		tempY = 0f;
		prevX = 0f;
		prevY = 0f;
		setX(x);
		setY(y);
	}
	
	public void setRealm(Realm realm)
	{
		this.realm = realm;
	}
	
	protected boolean isInCombat()
	{
		return combatOutTime >= combatResetTime;
	}
	
	private final float updateRange = 1.5f * SceneManager.CAMERA_WIDTH;
	
	public boolean isInUpdateRange(Collection<PlayerServer> values)
	{
		for(PlayerServer player : values)
		{
			if(Math.pow(player.getOriginX() - this.getOriginX(), 2) + Math.pow(player.getOriginY() - this.getOriginY(), 2) <= updateRange * updateRange)
				return true;
		}
		return false;
	}
	
	public float getVisualShootSpeed()
	{
		return shootSpeed < 1200f ? shootSpeed : Constants.visualShootSpeed;
	}
}
