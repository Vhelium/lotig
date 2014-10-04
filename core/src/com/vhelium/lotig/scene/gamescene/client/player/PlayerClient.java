package com.vhelium.lotig.scene.gamescene.client.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.DateTime;
import org.joda.time.Days;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.vhelium.lotig.Utility;
import com.vhelium.lotig.components.Line;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.components.ShapeRenderGroup;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.Effect;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GlobalSettings;
import com.vhelium.lotig.scene.gamescene.OnScreenInfo;
import com.vhelium.lotig.scene.gamescene.SavedGame;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;
import com.vhelium.lotig.scene.gamescene.client.EntityClientMixin;
import com.vhelium.lotig.scene.gamescene.client.hud.GameHUD;
import com.vhelium.lotig.scene.gamescene.client.hud.GameHUD_Action;
import com.vhelium.lotig.scene.gamescene.client.hud.IActionCallback;
import com.vhelium.lotig.scene.gamescene.client.items.Equip;
import com.vhelium.lotig.scene.gamescene.client.items.IPlayerAttributeListener;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.armor.EquipArmor;
import com.vhelium.lotig.scene.gamescene.client.items.armor.mod.ArmorModification;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.HealthPotion;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.ManaPotion;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Potion;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.WeaponModification;
import com.vhelium.lotig.scene.gamescene.maps.TMXMap;
import com.vhelium.lotig.scene.gamescene.quest.IQuestListener;
import com.vhelium.lotig.scene.gamescene.quest.Quest;
import com.vhelium.lotig.scene.gamescene.quest.QuestObjectiveKill;
import com.vhelium.lotig.scene.gamescene.quest.QuestType;
import com.vhelium.lotig.scene.gamescene.server.Difficulty;
import com.vhelium.lotig.scene.gamescene.server.levelobject.EventObject;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Chest;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Lever;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_Liquid;
import com.vhelium.lotig.scene.gamescene.spells.Spell;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellTimeWarp;

public class PlayerClient extends EntityClientMixin
{
	public static int WIDTH = 42;
	public static int HEIGHT = 42;
	private static final float LIQUID_SLOW = 0.5f;
	protected String playerClass;
	
	protected int fame = 0;
	protected int fameLastRank;
	protected int fameNextRank;
	
	private Inventory inventory;
	
	public GameHUD hud;
	protected SceneManager sceneManager;
	
	protected final HashMap<Integer, Spell> spells;
	protected final HashMap<Integer, Spell> hotkeySpells;
	protected final HashMap<Integer, Potion> hotkeyPotions;
	
	protected final ConcurrentHashMap<Integer, ConcurrentHashMap<QuestType, Quest>> questsPerDiffuculty;
	private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Quest>> parkedDungeonQuestsPerDifficulty;
	protected Quest storyQuestHost;
	private final List<Integer> uniqueChestsOpened = new ArrayList<Integer>();
	
	private final ConcurrentHashMap<QuestType, Boolean> newQuests = new ConcurrentHashMap<QuestType, Boolean>();
	
	public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> lairsPerDifficulty;
	public final ConcurrentHashMap<String, Integer> lairsHost;
	
	public final List<String> unlockedPlayerSkins;
	
	private final List<Integer> difficultiesFinished;
	
	protected final HashMap<String, Condition> conditions;
	protected final List<String> conditionsToRemove;
	
	protected ConcurrentHashMap<String, MovementModifier> movementModifiers;
	
	protected float movementSpeed = 0f;
	protected boolean isOnLiquid = false;
	
	protected boolean isMoving = false;
	
	protected final float maxVelocity = 0.4f;
	protected float velocityX = 0f;
	protected float velocityY = 0f;
	protected float directionX;
	protected float directionY;
	protected float padDirectionX;
	protected float padDirectionY;
	
	protected float shootRange = 0;
	protected String bulletAsset;
	protected String shootSound;
	
	protected float currentHp = 1f;
	protected float currentMana = 1f;
	
	protected float hpRegSec = 0f;
	protected float manaRegSec = 0f;
	
	protected int weaponDamage = 0;
	protected boolean piercing = false;
	private Spell preparedSpell;
	
	private int teamNr = 0;
	
	private final DirectionArrow directionArrow;
	
	public ConcurrentHashMap<Integer, Integer> daysWhenLastDaily;
	
	protected HashMap<String, Integer> baseAttributes;
	protected HashMap<String, Integer> tempAttributes;
	protected HashMap<String, Integer> permanentAttributes;
	protected HashMap<String, Integer> attributePotionsDrunk;
	private final List<IPlayerAttributeListener> attributeListeners = new ArrayList<IPlayerAttributeListener>();
	private boolean stunned = false;
	private boolean rooted = false;
	private boolean silenced = false;
	
	private final Line aimHelp;
	
	public PlayerClient(ClientLevel level, TMXMap tmxMap, String className, DataPacket dpLairs, SceneManager sceneManager)
	{
		super(level, tmxMap, WIDTH, HEIGHT, className);
		isMyPlayer = true;
		forceShootRepetition = true;
		this.playerClass = className;
		this.sceneManager = sceneManager;
		movementModifiers = new ConcurrentHashMap<String, MovementModifier>();
		baseAttributes = new HashMap<String, Integer>();
		tempAttributes = new HashMap<String, Integer>();
		permanentAttributes = new HashMap<String, Integer>();
		attributePotionsDrunk = new HashMap<String, Integer>();
		spells = new HashMap<Integer, Spell>();
		hotkeySpells = new HashMap<Integer, Spell>();
		hotkeyPotions = new HashMap<Integer, Potion>();
		conditions = new HashMap<String, Condition>();
		conditionsToRemove = new ArrayList<String>();
		unlockedPlayerSkins = new ArrayList<String>();
		
		difficultiesFinished = new ArrayList<Integer>();
		
		daysWhenLastDaily = new ConcurrentHashMap<Integer, Integer>();
		
		lairsHost = new ConcurrentHashMap<String, Integer>();
		lairsPerDifficulty = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>>();
		lairsPerDifficulty.put(Difficulty.Normal, new ConcurrentHashMap<String, Integer>());
		lairsPerDifficulty.put(Difficulty.Nightmare, new ConcurrentHashMap<String, Integer>());
		lairsPerDifficulty.put(Difficulty.Hell, new ConcurrentHashMap<String, Integer>());
		
		questsPerDiffuculty = new ConcurrentHashMap<Integer, ConcurrentHashMap<QuestType, Quest>>();
		questsPerDiffuculty.put(Difficulty.Normal, new ConcurrentHashMap<QuestType, Quest>());
		questsPerDiffuculty.put(Difficulty.Nightmare, new ConcurrentHashMap<QuestType, Quest>());
		questsPerDiffuculty.put(Difficulty.Hell, new ConcurrentHashMap<QuestType, Quest>());
		
		parkedDungeonQuestsPerDifficulty = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Quest>>();
		parkedDungeonQuestsPerDifficulty.put(Difficulty.Normal, new ConcurrentHashMap<String, Quest>());
		parkedDungeonQuestsPerDifficulty.put(Difficulty.Nightmare, new ConcurrentHashMap<String, Quest>());
		parkedDungeonQuestsPerDifficulty.put(Difficulty.Hell, new ConcurrentHashMap<String, Quest>());
		
		directionArrow = new DirectionArrow(this, GameHelper.getInstance().getGameAsset("DirectionArrow"));
		sprite.addActor(directionArrow);
		
		aimHelp = new Line(0, 0, 0, 0, sceneManager.getShapeRenderer());
		aimHelp.setColor(200 / 255f, 0, 0, 0.40f);
		aimHelp.setVisible(Boolean.parseBoolean(GlobalSettings.getInstance().getDataValue("Aim-Help")));
		ShapeRenderGroup grp = new ShapeRenderGroup(sceneManager.getShapeRenderer());
		grp.addActor(aimHelp);
		sprite.addActor(grp);
		
		if(!level.isHost())
		{
			int lairCount = dpLairs.getInt();
			for(int i = 0; i < lairCount; i++)
				getAvailableLairs().put(dpLairs.getString(), dpLairs.getInt());
			
			String sStoryQuestHost = dpLairs.getString();
			if(!sStoryQuestHost.equals(""))
				storyQuestHost = new Quest(sStoryQuestHost, level);
			else
				storyQuestHost = null;
		}
	}
	
	public void handleNextLair(DataPacket dp)
	{
		getAvailableLairs().put(dp.getString(), dp.getInt());
		level.saveGame();
	}
	
	public float getAttribute(String ATTR)
	{
		ATTR = ATTR.toUpperCase();
		float res = 0f;
		
		if(baseAttributes.containsKey(ATTR))
			res += baseAttributes.get(ATTR);
		
		if(tempAttributes.containsKey(ATTR))
			res += tempAttributes.get(ATTR);
		
		if(permanentAttributes.containsKey(ATTR))
			res += permanentAttributes.get(ATTR);
		//for each equip
		res += inventory.getAttributeValue(ATTR);
		//for each buff
		return res;
	}
	
	public float getRes(String RES)
	{
		return getAttribute(RES) + getAttribute("INT") / 10;
	}
	
	public int getAttributeCount()
	{
		return baseAttributes.size();
	}
	
	public void onAttributeChanged()
	{
		float prevAS = shootSpeed;
		shootSpeed = calculateShootSpeed();
		if(prevAS != shootSpeed)
			shootSpeedAnimationReset = true;
		movementSpeed = calculateMovementSpeed();
		hpRegSec = calculateHpRecSec();
		manaRegSec = calculateManaRecSec();
		weaponDamage = (int) calculateWeaponDamage();
		
		for(IPlayerAttributeListener listener : attributeListeners)
			listener.playerAttributeChanged();
	}
	
	public void handleTempAttributeChange(DataPacket dp)
	{
		int size = dp.getInt();
		for(int i = 0; i < size; i++)
		{
			String valueName = dp.getString();
			int value = dp.getInt();
			if(tempAttributes.containsKey(valueName))
			{
				if(tempAttributes.get(valueName) + value == 0)
					tempAttributes.remove(valueName);
				else
					tempAttributes.put(valueName, tempAttributes.get(valueName) + value);
			}
			else
				tempAttributes.put(valueName, value);
		}
		
		onAttributeChanged();
	}
	
	public boolean requestPermanentAttributeIncrease(String attribute, int value)
	{
		if(attributePotionsDrunk.containsKey(attribute) && attributePotionsDrunk.get(attribute) >= Constants.attributePotionMaxDrunk)
			return false;
		else
		{
			//success
			increasePermanentAttribute(attribute, value);
			attributePotionsDrunk.put(attribute, attributePotionsDrunk.containsKey(attribute) ? attributePotionsDrunk.get(attribute) + 1 : 1);
			return true;
		}
	}
	
	private void increasePermanentAttribute(String attribute, int value)
	{
		attribute = attribute.toUpperCase();
		if(permanentAttributes.containsKey(attribute))
			permanentAttributes.put(attribute, permanentAttributes.get(attribute) + value);
		else
			permanentAttributes.put(attribute, value);
		onAttributeChanged();
	}
	
	public int getAttributePotionsDrunk(String attribute)
	{
		return attributePotionsDrunk.containsKey(attribute) ? attributePotionsDrunk.get(attribute) : 0;
	}
	
	public float getDamage()
	{
		return (1 + getAttribute("DMGPERCENT") / 100f) * (weaponDamage * (1f + (getAttribute("STR") / Constants.maxStrength) * Constants.dmgStrengthFactor));
	}
	
	private WeaponModification getWeaponMod()
	{
		Equip eq = getInventory().getEquipBySlot(Inventory.SLOT_ID_WEAPON);
		return (WeaponModification) (eq != null ? (eq.modification != null ? eq.modification : null) : null);
	}
	
	private boolean hasWeaponMod(String name)
	{
		return getWeaponMod() != null ? getWeaponMod().getName().equals(name) : false;
	}
	
	private float calculateShootSpeed()
	{
		float shootSpeedModifier = (float) (Math.log(getAttribute("DEX")) / Math.log(Constants.logDex)) + getAttribute("AS") / 1000f;
		if(shootSpeedModifier > 1.6f)
			shootSpeedModifier = 1.6f;
		return (2f - shootSpeedModifier) * 1000.0f / (getWeaponMod() != null ? getWeaponMod().getAtkSpdFactor() : 1f);
	}
	
	private float calculateWeaponDamage()
	{
		return Math.max(getAttribute("DMG"), 1);
	}
	
	public float calculateHpRecSec()
	{
		return getAttribute("VIT") / Constants.hpRegFact + getAttribute("HPREG");
	}
	
	public float calculateManaRecSec()
	{
		return getAttribute("WIS") / Constants.manaRegFact + getAttribute("MANAREG");
	}
	
	private float calculateMovementSpeed()
	{
		return Math.max(0.15f/*0.16*/+ 0.05f * (getAttribute("SPD") / Constants.maxSpeed + getAttribute("DEX") / Constants.maxDex), 0.02f); //between 0.16 & 0.25
	}
	
	public float getShootSpeed()
	{
		return shootSpeed;
	}
	
	public float getShootRange()
	{
		return shootRange * (getWeaponMod() != null ? getWeaponMod().getRangeFactor() : 1f);
	}
	
	public String getBulletAsset()
	{
		return (getWeaponMod() != null && getWeaponMod().getSpecialBulletAsset() != null ? getWeaponMod().getSpecialBulletAsset() : bulletAsset);
	}
	
	public boolean isPiercing()
	{
		return piercing || (getWeaponMod() != null && getWeaponMod().isPiercing());
	}
	
	float[] normalizedContainer = new float[2];
	
	public void update(float delta, long millis, Touchpad rotationControl, Touchpad movementControl)
	{
		if(shootCooldownLeft > 0)
			shootCooldownLeft -= delta;
		
		float moveDirectionX = 0;
		float moveDirectionY = 0;
		if(manualX != 0 || manualY != 0 && !isStunned())
		{
			setVelocity(manualX * movementSpeed, manualY * movementSpeed);
			isMoving = true;
			
			if(!isShooting())
			{
				directionX = manualX;
				directionY = manualY;
				setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(directionX, -directionY)));
			}
		}
		else if(movementControl.isTouched() && !isStunned() && !isRooted())
		{
			Utility.normalizeVector(normalizedContainer, movementControl.getKnobPercentX(), movementControl.getKnobPercentY());
			moveDirectionX = normalizedContainer[0];
			moveDirectionY = normalizedContainer[1];
			
			setVelocity(moveDirectionX * movementSpeed, moveDirectionY * movementSpeed);
			isMoving = true;
		}
		else
		{
			setVelocity(0, 0);
			isMoving = false;
		}
		
		applyMoveModifiers(delta);
		
		setX(getX() + velocityX * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
		if(velocityX != 0)
		{
			if(isInSolid())
			{
				if(!moveOutOfSolidX(-(int) (velocityX / Math.abs(velocityX)), tmxMap.getTileSize()))
					if(!moveOutOfSolidX((int) (velocityX / Math.abs(velocityX)), tmxMap.getTileSize()))
					{
						// in the middle of an object! :) GG
					}
			}
		}
		
		setY(getY() + velocityY * (isOnLiquid ? LIQUID_SLOW : 1f) * delta);
		if(velocityY != 0)
		{
			if(isInSolid())
			{
				if(!moveOutOfSolidY(-(int) (velocityY / Math.abs(velocityY)), tmxMap.getTileSize()))
					if(!moveOutOfSolidY((int) (velocityY / Math.abs(velocityY)), tmxMap.getTileSize()))
					{
						// in the middle of an object! :) GG
					}
			}
		}
		
		if(getX() > tmxMap.getMapWidth() - getWidth())
			setX(tmxMap.getMapWidth() - getWidth());
		
		if(getY() > tmxMap.getMapHeight() - getHeight())
			setY(tmxMap.getMapHeight() - getHeight());
		
		if(getX() < 0)
			setX(0);
		
		if(getY() < 0)
			setY(0);
		if(rotationControl.isTouched())
		{
			Utility.normalizeVector(normalizedContainer, rotationControl.getKnobPercentX(), rotationControl.getKnobPercentY());
			padDirectionX = normalizedContainer[0];
			padDirectionY = normalizedContainer[1];
		}
		if(rotationControl.isTouched() && !isShooting() && !isStunned())
		{
			shoot();
		}
		else if(movementControl.isTouched() && !isShooting() && !isStunned())
		{
			directionX = moveDirectionX;
			directionY = moveDirectionY;
			setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(directionX, -directionY)));
		}
		
		updateAnimation();
		
		updateDamageNumbers();
		
		for(Entry<Integer, Spell> spell : spells.entrySet())
			if(spell.getValue().cooldownLeft > 0)
				spell.getValue().cooldownLeft -= delta;
		
		for(Entry<String, Condition> condition : conditions.entrySet())
		{
			if(millis - condition.getValue().getAbsolutTimeSinceStart() >= condition.getValue().getDuration())
				conditionsToRemove.add(condition.getKey());
		}
		
		for(final String s : conditionsToRemove)
		{
			if(conditionEffects.containsKey(s))
			{
				conditionEffects.get(s).remove();
				conditionEffects.remove(s);
			}
			conditions.remove(s);
		}
		conditionsToRemove.clear();
		
		isOnLiquid = false;
		for(LevelObject_Liquid liquid : tmxMap.getLiquids())
			if(getX() + 1 + getWidth() - 2 >= liquid.getBounds().getX() && getY() + 1 + getHeight() - 2 >= liquid.getBounds().getY() && getX() + 1 <= liquid.getBounds().getX() + liquid.getBounds().getWidth() && getY() + 1 <= liquid.getBounds().getY() + liquid.getBounds().getHeight())
			{
				isOnLiquid = true;
				break;
			}
		
		if(aimHelp.isVisible() && rotationControl.isTouched())
		{
			aimHelp.setPosition(sprite.getWidth() / 2, sprite.getHeight() / 2, sprite.getWidth() / 2 + padDirectionX * SceneManager.CAMERA_WIDTH * 1.1f, sprite.getHeight() / 2 + padDirectionY * SceneManager.CAMERA_WIDTH * 1.1f);
		}
		else
			aimHelp.setPosition(0, 0, 0, 0);
	}
	
	public void addAttributeListener(IPlayerAttributeListener listener)
	{
		attributeListeners.add(listener);
	}
	
	protected Boolean isInSolid()
	{
		return tmxMap.isCollisionAt(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, false);
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
	
	public void shoot()
	{
		if(isStunned())
			return;
		if(shootSpeedAnimationReset)
		{
			resetAnimation();
			shootSpeedAnimationReset = false;
		}
		shootCooldownLeft = shootSpeed;
		
		if(hasWeaponMod(WeaponModification.PSYCHO))
		{
			//random shoot direction:
			float angle = (float) (Math.PI * 2 * GameHelper.$.getRandom().nextDouble());
			directionX = (float) Math.cos(angle);
			directionY = (float) Math.sin(angle);
			setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(directionX, -directionY)));
		}
		else
		{
			directionX = padDirectionX;
			directionY = padDirectionY;
			setRotation(MathUtils.radiansToDegrees * ((float) Math.atan2(directionX, -directionY)));
		}
		
		if(preparedSpell == null)
		{
			//send dp with bullet via callback to ClientLevel who sends it to the server
			level.shareBulletShot(isPiercing(), DamageType.Physical, getShootRange(), this.getX() + this.getWidth() / 2 - GameHelper.getInstance().getBulletAsset(getBulletAsset()).getWidth() / 2 + directionX * 16, this.getY() + this.getHeight() / 2 - GameHelper.getInstance().getBulletAsset(getBulletAsset()).getHeight() / 2 + directionY * 16, this.getRotation(), directionX, directionY, getBulletAsset());
			
			if(hasWeaponMod(WeaponModification.MULTI_SHOT))
			{
				double alpha;
				if(directionX < 0)
					alpha = Math.atan(directionY / directionX) + Math.PI;
				else if(directionX == 0 && directionY > 0)
					alpha = Math.PI / 2;
				else if(directionX == 0 && directionY <= 0)
					alpha = Math.PI / 2 * 3;
				else
					alpha = Math.atan(directionY / directionX);
				
				double beta = alpha - 0.35f;
				float bDirX = (float) Math.cos(beta);
				float bDirY = (float) Math.sin(beta);
				float bRot = MathUtils.radiansToDegrees * ((float) Math.atan2(bDirX, -bDirY));
				level.shareBulletShot(isPiercing(), DamageType.Physical, getShootRange(), this.getX() + this.getWidth() / 2 - GameHelper.getInstance().getBulletAsset(getBulletAsset()).getWidth() / 2 + bDirX * 16, this.getY() + this.getHeight() / 2 - GameHelper.getInstance().getBulletAsset(getBulletAsset()).getHeight() / 2 + bDirY * 16, bRot, bDirX, bDirY, getBulletAsset());
				
				double gamma = alpha + 0.35f;
				float gDirX = (float) Math.cos(gamma);
				float gDirY = (float) Math.sin(gamma);
				float gRot = MathUtils.radiansToDegrees * ((float) Math.atan2(gDirX, -gDirY));
				level.shareBulletShot(isPiercing(), DamageType.Physical, getShootRange(), this.getX() + this.getWidth() / 2 - GameHelper.getInstance().getBulletAsset(getBulletAsset()).getWidth() / 2 + gDirX * 16, this.getY() + this.getHeight() / 2 - GameHelper.getInstance().getBulletAsset(getBulletAsset()).getHeight() / 2 + gDirY * 16, gRot, gDirX, gDirY, getBulletAsset());
			}
			
			SoundManager.playSound(shootSound);
		}
		else
		{
			preparedSpell.cooldownLeft = preparedSpell.getCooldown() * (100 - getAttribute("CDR")) / 100f;
			level.sendDataPacket(preparedSpell.generateRequest(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, getRotation(), directionX, directionY));
			if(preparedSpell.getCastSound() != null)
				SoundManager.playSound(preparedSpell.getCastSound());
			else
				SoundManager.playSound(SoundFile.spell_missile);
			hud.getCallback().setSpellPrepared(-1, false);
			preparedSpell = null;
		}
	}
	
	public void requestSpell(int slot)
	{
		if(isStunned() || isSilenced())
		{
			hud.playUnableToCast(slot);
			return;
		}
		Spell spell = hotkeySpells.get(slot);
		
		if(spell == null || spell.cooldownLeft > 0)
			return;
		
		if(this.getMana() < spell.getManaCost())
		{
			hud.getCallback().playSpellOutOfMana(slot);
			return;
		}
		
		if(spell.instantCast)
		{
			spell.cooldownLeft = spell.getCooldown() * (100 - getAttribute("CDR")) / 100f;
			level.sendDataPacket(spell.generateRequest(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, getRotation(), directionX, directionY));
		}
		else
		{
			if(spell != preparedSpell)
			{
				preparedSpell = spell;
				hud.getCallback().setSpellPrepared(slot, true);
			}
			else
			{
				preparedSpell = null;
				hud.getCallback().setSpellPrepared(-1, false);
			}
			//TODO: DISABLE AFTER SOME SECONDS
		}
	}
	
	public void requestPotion(int slot)
	{
		if(isStunned())
			return;
		Potion potion = hotkeyPotions.get(slot);
		
		if(potion != null)
		{
			inventory.useItem(potion, true);
			hud.playPotionCD(slot);
		}
	}
	
	List<Integer> potionHKsToRemove = new ArrayList<Integer>();;
	
	public void onPotionRemoved(Potion potion)
	{
		for(Entry<Integer, Potion> e : hotkeyPotions.entrySet())
			if(e.getValue() == potion)
			{
				hud.getCallback().hotkeyPotionChanged(e.getKey(), null);
				potionHKsToRemove.add(e.getKey());
			}
		for(Integer i : potionHKsToRemove)
			hotkeyPotions.remove(i);
		potionHKsToRemove.clear();
	}
	
	public void setVelocity(float x, float y)
	{
		velocityX = x;
		velocityY = y;
	}
	
	public void resetVelocity()
	{
		velocityX = 0;
		velocityY = 0;
		movementModifiers.clear();
	}
	
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
	
	@Override
	public boolean isMoving()
	{
		return isMoving;
	}
	
	public float manualX = 0f;
	public float manualY = 0f;
	
	public void setHp(float hp)
	{
		currentHp = hp;
		
	}
	
	public void addHp(float hp)
	{
		setHp(getHp() + hp);
	}
	
	public float getHp()
	{
		return currentHp;
	}
	
	public void setMana(float mana)
	{
		currentMana = mana;
	}
	
	public void addMana(float mana)
	{
		setMana(getMana() + mana);
	}
	
	public float getMana()
	{
		return currentMana;
	}
	
	public void initInventory(IHUD_InventoryCallback hudCallback)
	{
		inventory = new Inventory(this, hudCallback);
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public HashMap<Integer, Spell> getSpells()
	{
		return spells;
	}
	
	public HashMap<Integer, Spell> getHotkeySpells()
	{
		return hotkeySpells;
	}
	
	public float getMaxHp()
	{
		return getAttribute("MAXHP");
	}
	
	public float getMaxMana()
	{
		return getAttribute("MAXMANA");
	}
	
	public void handleOnEnterEvent(PlayerClient player, final MapProperties properties, EventObject event)
	{
		if(properties.containsKey("showPort"))
		{
			final String mapName = properties.get("showPort", String.class);
			String anzeigeName = properties.get("name", String.class);
			if(anzeigeName == null)
				anzeigeName = mapName;
			anzeigeName = anzeigeName.replace("%", "\n");
			hud.getMenu("action").setParam("Port to:\n\n" + anzeigeName + ";" + "Port");
			((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
			{
				@Override
				public void onActivate()
				{
					DataPacket dp = new DataPacket();
					dp.setInt(MessageType.MSG_REQUEST_REALM);
					dp.setString(mapName);
					level.sendDataPacket(dp);
					hud.getCallback().hide("action");
				}
			});
			((GameHUD_Action) hud.getMenu("action")).setEvent("showPort");
			hud.getCallback().show("action");
		}
		else if(properties.containsKey("showLairPort"))
		{
			final String lairName = properties.get("showLairPort", String.class);
			String anzeigeName = properties.get("name", String.class);
			if(anzeigeName == null)
				anzeigeName = lairName;
			anzeigeName = anzeigeName.replace("%", "\n");
			if(getAvailableLairs().containsKey(lairName))
				hud.getMenu("action").setParam("Port to:\n\n" + anzeigeName + ";" + "Port;true;Layer;1;" + (lairName.equals("special") ? 0 : getAvailableLairs().get(lairName)));
			else if(properties.get("unlocked", String.class) != null)
				hud.getMenu("action").setParam("Port to:\n\n" + anzeigeName + ";" + "Port;true;Layer;1;" + Integer.parseInt(properties.get("unlocked", String.class)));
			else
				hud.getMenu("action").setParam("Port to:\n\n" + anzeigeName + ";" + "Locked;false");
			((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
			{
				@Override
				public void onActivate()
				{
					int currentSelection = ((GameHUD_Action) hud.getMenu("action")).getCurrentSelection();
					if(!getAvailableLairs().containsKey(lairName) || currentSelection <= getAvailableLairs().get(lairName))
					{
						DataPacket dp = new DataPacket();
						dp.setInt(MessageType.MSG_REQUEST_REALM_WITH_LAIR_NR);
						dp.setString(lairName);
						dp.setInt(currentSelection);
						dp.setBoolean(false);
						level.sendDataPacket(dp);
					}
					else
						hud.getCallback().postMessage("Cannot do that!", false);
					hud.getCallback().hide("action");
				}
			});
			((GameHUD_Action) hud.getMenu("action")).setEvent("showLairPort");
			hud.getCallback().show("action");
		}
		else if(properties.containsKey("showFamePort"))
		{
			final int fameNr = Integer.parseInt(properties.get("showFamePort", String.class));
			final String anzeigeName = properties.get("name", String.class).replace("%", "\n");
			if(isHallOfFameUnlocked(fameNr))
				hud.getMenu("action").setParam("Port to:\n\n" + anzeigeName + ";" + "Port;true");
			else
				hud.getMenu("action").setParam("Port to:\n\n" + anzeigeName + "\n\nFame required: " + getFameNeededForHallOfFame(fameNr) + ";" + "Locked;false");
			((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
			{
				@Override
				public void onActivate()
				{
					DataPacket dp = new DataPacket();
					dp.setInt(MessageType.MSG_REQUEST_TEMP_REALM);
					dp.setString("hallOfFame" + fameNr);
					level.sendDataPacket(dp);
					hud.getCallback().hide("action");
				}
			});
			((GameHUD_Action) hud.getMenu("action")).setEvent("showFamePort");
			hud.getCallback().show("action");
		}
		else if(properties.containsKey("lairportals"))
		{
			level.setOnLairPortals(true);
		}
		else if(properties.containsKey("joinTeam"))
		{
			String team = properties.get("name", String.class);
			hud.getMenu("action").setParam("Join Team:\n\n" + team + ";" + team);
			((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
			{
				@Override
				public void onActivate()
				{
					teamNr = Integer.parseInt(properties.get("joinTeam", String.class));
					DataPacket dp = new DataPacket();
					dp.setInt(MessageType.MSG_JOIN_TEAM);
					dp.setInt(teamNr);
					level.sendDataPacket(dp);
					hud.getCallback().hide("action");
					
					ArrayList<EventObject> spawns = tmxMap.getStaticEvents("TeamSpawn");
					for(EventObject ev : spawns)
						if(ev.properties.containsKey("team") && Integer.parseInt(ev.properties.get("team", String.class)) == teamNr)
						{
							setX(ev.getRectangle().getX());
							setY(ev.getRectangle().getY());
						}
				}
			});
			((GameHUD_Action) hud.getMenu("action")).setEvent("joinTeam");
			hud.getCallback().show("action");
		}
		else if(properties.containsKey("requestpvpreset"))
		{
			hud.getMenu("action").setParam("You want to reset\nthe PvP scores?;Reset");
			((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
			{
				@Override
				public void onActivate()
				{
					DataPacket dp = new DataPacket();
					dp.setInt(MessageType.MSG_REQUEST_PVP_SCORE_RESET);
					level.sendDataPacket(dp);
					hud.getCallback().hide("action");
				}
			});
			((GameHUD_Action) hud.getMenu("action")).setEvent("requestpvpreset");
			hud.getCallback().show("action");
		}
		else if(properties.containsKey("showLever"))
		{
			final int leverId = Integer.parseInt(properties.get("showLever", String.class));
			if(tmxMap.getLevelObjects().containsKey(leverId))
			{
				final LevelObject_Lever lever = (LevelObject_Lever) tmxMap.getLevelObjects().get(leverId);
				if(lever.getState() == 0)
				{
					hud.getMenu("action").setParam("Lever;" + "Pull;true");
					((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
					{
						@Override
						public void onActivate()
						{
							DataPacket dp = new DataPacket();
							dp.setInt(MessageType.MSG_LEVER_PULLED);
							dp.setInt(leverId);
							level.sendDataPacket(dp);
							hud.getCallback().hide("action");
						}
					});
					((GameHUD_Action) hud.getMenu("action")).setEvent("showLever");
					hud.getCallback().show("action");
				}
			}
		}
		else if(properties.containsKey("itemSold"))
		{
			hud.getMenu("selling").setParam(properties.get("itemSold", String.class));
			hud.getCallback().show("selling");
		}
		else if(properties.containsKey("enchanting"))
		{
			hud.getMenu("enchanting").setParam(properties.get("enchanting", String.class));
			hud.getCallback().show("enchanting");
		}
		else if(properties.containsKey("skinSelection"))
		{
			hud.getCallback().show("skinSelection");
		}
		else if(properties.containsKey("defenseTower"))
		{
			hud.getCallback().getMenu("defenseTower").setParam(Integer.parseInt(properties.get("defenseTower", String.class)));
			hud.getCallback().show("defenseTower");
		}
		else if(properties.containsKey("glyphmaster"))
		{
			hud.getMenu("glyphmaster").setParam(properties.get("glyphmaster", String.class));
			hud.getCallback().show("glyphmaster");
		}
		else if(properties.containsKey("stash"))
		{
			hud.getCallback().getMenu("stash").setParam(Integer.parseInt(properties.get("stash", String.class)));
			hud.getCallback().show("stash");
		}
		else if(properties.containsKey("dailyquest"))
		{
			if(getQuests().containsKey(QuestType.Daily))
				hud.getMenu("action").setParam("Daily quest in\nprogress.;Accept;false");
			else if(isDailyQuestAvailable())
				hud.getMenu("action").setParam("There is a random\ndaily quest available!;Accept");
			else
				hud.getMenu("action").setParam("No daily quest\navailable. Check\nback tomorrow!;Accept;false");
			((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
			{
				@Override
				public void onActivate()
				{
					Quest daily = new Quest(GameHelper.getInstance().getRandomDailyQuest(), level);
					setQuest(daily.getQuestType(), daily, level.getDifficulty());
					
					hud.getCallback().postMessage("new quest available!", true);
					
					daysWhenLastDaily.put(level.getDifficulty(), Days.daysBetween(GameHelper.getInstance().getDefaultDate(), new DateTime()).getDays());
					hud.getMenu("action").setParam("Accepted!;Accept;false");
				}
			});
			((GameHUD_Action) hud.getMenu("action")).setEvent("dailyquest");
			hud.getCallback().show("action");
		}
		else if(properties.containsKey("showChest"))
		{
			int chestId = Integer.parseInt(properties.get("showChest", String.class));
			if(tmxMap.getLevelObjects().containsKey(chestId))
			{
				LevelObject_Chest chest = (LevelObject_Chest) tmxMap.getLevelObjects().get(chestId);
				//Display loot display
				hud.setLootBagInRange(chest.getFakeLootBag());
			}
		}
		else if(properties.containsKey("showOnScreenInfo"))
		{
			hud.showOnScreenInfo(Integer.parseInt(properties.get("showOnScreenInfo", String.class)));
		}
		if(properties.containsKey("showGemStore"))
		{
			hud.getMenu("action").setParam("Enter Gemstore.. ;" + "Enter");
			((GameHUD_Action) hud.getMenu("action")).setCallback(new IActionCallback()
			{
				@Override
				public void onActivate()
				{
					level.requestOpenGemStore();
				}
			});
			((GameHUD_Action) hud.getMenu("action")).setEvent("showGemStore");
			hud.getCallback().show("action");
		}
	}
	
	public void handleOnLeftEvent(PlayerClient player, MapProperties properties)
	{
		if(properties.containsKey("showPort") || properties.containsKey("showGemStore") || properties.containsKey("showLever") || properties.containsKey("showLairPort") || properties.containsKey("showFamePort") || properties.containsKey("joinTeam") || properties.containsKey("requestpvpreset") || properties.containsKey("dailyquest"))
		{
//			if(((GameHUD_Action) hud.getMenu("action")).getEvent().equals(property.getName()))
			hud.getCallback().hide("action");
		}
		else if(properties.containsKey("lairportals"))
		{
			level.setOnLairPortals(false);
		}
		else if(properties.containsKey("itemSold"))
		{
			hud.getCallback().hide("selling");
		}
		else if(properties.containsKey("defenseTower"))
		{
			hud.getCallback().hide("defenseTower");
		}
		else if(properties.containsKey("enchanting"))
		{
			hud.getCallback().hide("enchanting");
		}
		else if(properties.containsKey("skinSelection"))
		{
			hud.getCallback().hide("skinSelection");
		}
		else if(properties.containsKey("glyphmaster"))
		{
			hud.getCallback().hide("glyphmaster");
		}
		else if(properties.containsKey("stash"))
		{
			hud.getCallback().hide("stash");
		}
		else if(properties.containsKey("showChest"))
		{
			int chestId = Integer.parseInt(properties.get("showChest", String.class));
			if(tmxMap.getLevelObjects().containsKey(chestId))
			{
				hud.fakeLootBBagLeft();
			}
		}
		else if(properties.containsKey("showOnScreenInfo"))
		{
			OnScreenInfo osi = GameHelper.getInstance().getOnScreenInfo(Integer.parseInt(properties.get("showOnScreenInfo", String.class)));
			hud.hideOnScreenInfo(osi.id);
			for(int i : osi.getLinked())
				hud.hideOnScreenInfo(i);
		}
	}
	
	public boolean collidesWith(Rectangle rectangle)
	{
		return (getX() + getWidth() >= rectangle.getX() && getY() + getHeight() >= rectangle.getY() && getX() <= rectangle.getX() + rectangle.getWidth() && getY() <= rectangle.getY() + rectangle.getHeight());
	}
	
	public void setTeamNr(int team)
	{
		teamNr = team;
	}
	
	public HashMap<Integer, Potion> getHotkeyPotions()
	{
		return hotkeyPotions;
	}
	
	public void requestCondition(String name, String valueName, int value, int duration, boolean isAttribute, String effect)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_REQUEST_CONDITION);
		dp.setString(name);
		dp.setString(valueName);
		dp.setInt(value);
		dp.setInt(duration);
		dp.setBoolean(isAttribute);
		dp.setString(effect);
		level.sendDataPacket(dp);
	}
	
	public void addCondition(final String name, final int duration, final long absStartTime, final String effect)
	{
		Condition condition = new Condition(name, duration, effect, absStartTime);
		conditions.put(name, condition);
		if(!effect.isEmpty())
		{
			if(conditionEffects.containsKey(name))
				return;
			
			TiledTextureRegion texture = GameHelper.getInstance().getEffectManager().getEffectAsset(effect);
			if(texture == null)
				Log.e("PlayerClient", "Condition: Missing Texture: " + effect);
			Effect e = new Effect(PlayerClient.this, texture);
			e.animate(GameHelper.getInstance().getEffectManager().getAnimationTime(effect), true);
			getSprite().addActor(e);
			conditionEffects.put(name, e);
		}
	}
	
	public void removeCondition(final String name)
	{
		if(conditions.containsKey(name))
		{
			if(!conditions.get(name).getEffect().isEmpty() && conditionEffects.containsKey(name))
			{
				final Effect eff = conditionEffects.get(name);
				conditionEffects.remove(name);
				eff.remove();
			}
			conditions.remove(name);
		}
	}
	
	public void spellLevelUp(String spellName)
	{
		for(Spell spell : spells.values())
			if(spell.getName().equals(spellName))
				spell.levelUp();
		hud.getCallback().updateSpellLevelText();
	}
	
	public DataPacket collectDataForServer(DataPacket dp)//in MSG_SEND_CLIENT_INFO --> to server --> handleClientInfo() --> (server)level.addPlayer(playerNr, dp)
	{
		if(level.isHost())
		{
			dp.setInt(lairsPerDifficulty.get(level.getDifficulty()).size());
			for(Entry<String, Integer> e : lairsPerDifficulty.get(level.getDifficulty()).entrySet())
			{
				dp.setString(e.getKey());
				dp.setInt(e.getValue());
			}
			
			if(questsPerDiffuculty.get(level.getDifficulty()).get(QuestType.Story) != null)
				dp.setString(questsPerDiffuculty.get(level.getDifficulty()).get(QuestType.Story).getStringFormat());
			else
				dp.setString("");
			
			dp.setInt(uniqueChestsOpened.size());
			for(Integer id : uniqueChestsOpened)
				dp.setInt(id);
		}
		
		dp.setString(asset);
		dp.setFloat(rotation);
		
		setServerSidedStats(dp);
		
		dp.setFloat(currentHp);
		dp.setFloat(currentMana);
		
		return dp;
	}
	
	public void setServerSidedStats(DataPacket dp)//NOTICE: IF YOU CHANGE A VALUE: PlayerServer.PlayerServer() && Realm.handlePlayerStatusUpdate() TO BE ADJUSTED!!
	{//TODO: ADD ALL SERVER SIDED ATTRIBUTES!
		dp.setFloat(getAttribute("MAXHP"));
		dp.setFloat(hpRegSec);
		dp.setFloat(getAttribute("MAXMANA"));
		dp.setFloat(manaRegSec);
		
		dp.setFloat(getShootSpeed());
		dp.setFloat(getDamage());
		dp.setFloat(getAttribute("BONUSDMG"));
		
		dp.setFloat(getRes("ARMOR"));
		dp.setFloat(getRes("FRES"));
		dp.setFloat(getRes("CRES"));
		dp.setFloat(getRes("LRES"));
		dp.setFloat(getRes("PRES"));
		
		dp.setFloat(getAttribute("LPH"));
		dp.setFloat(getAttribute("MPH"));
		
		dp.setFloat(getAttribute("THORNS"));
		dp.setFloat(getAttribute("ABSORB"));
	}
	
	public ConcurrentHashMap<String, String> getSaveData()
	{
		ConcurrentHashMap<String, String> datas = new ConcurrentHashMap<String, String>();
		
		datas.put("new", "false");
		
		datas.put("playerclass", playerClass);
		
		for(int d = 0; d < Difficulty.DIFFICULTY_COUNT; d++)
		{
			String lairString = "";
			int l = 0;
			for(Entry<String, Integer> e : lairsPerDifficulty.get(d).entrySet())
			{
				lairString += e.getKey() + ";" + e.getValue();
				if(l < lairsPerDifficulty.get(d).size() - 1)
					lairString += ";";
				l++;
			}
			datas.put("lairs" + d, lairString);
		}
		for(int i = 0; i < 12; i++)
			datas.put("equip" + i, inventory.getEquipBySlot(i) != null ? inventory.getEquipBySlot(i).toStringFormat() : "");
		for(Entry<Integer, Spell> e : getHotkeySpells().entrySet())
		{
			datas.put("hotkey" + e.getKey(), e.getValue().getName());
		}
		for(Entry<Integer, Potion> e : getHotkeyPotions().entrySet())
		{
			datas.put("hotkeypot" + e.getKey(), e.getValue().NAME);
		}
		String itemString = "";
		int catCount = 0;
		for(List<Item> items : inventory.getItems().values())
		{
			int catSize = 0;
			for(Item item : items)
			{
				itemString += item.toStringFormat();
				if(catCount < inventory.getItems().size() - 1 || catSize < items.size() - 1)
					itemString += ",";
				catSize++;
			}
			catCount++;
		}
		datas.put("items", itemString);
		
		datas.put("gold", String.valueOf(inventory.getGold()));
		
		for(Entry<Integer, Spell> e : getSpells().entrySet())
			datas.put("spell" + e.getKey(), String.valueOf(e.getValue().getLevel()));
		
		datas.put("invmaxsize", String.valueOf(inventory.getMaxSize()));
		
		datas.put("fame", String.valueOf(fame));
		
		for(int i = 0; i < Difficulty.DIFFICULTY_COUNT; i++)
		{
			String questString = "";
			for(Quest quest : questsPerDiffuculty.get(i).values())
				if(quest.getQuestType() != QuestType.Dungeon)
					questString += quest.getStringFormat() + ",";
			if(questString.length() > 0 && questString.charAt(questString.length() - 1) == ',')
				questString = questString.substring(0, questString.length() - 1);
			datas.put("quests" + i, questString);
			
			datas.put("dayswhenlastdaily" + i, String.valueOf(daysWhenLastDaily.get(i)));
		}
		
		String stAttrDrunk = "";
		int drunkCount = 0;
		for(Entry<String, Integer> permaAttr : attributePotionsDrunk.entrySet())
		{
			stAttrDrunk += permaAttr.getKey() + ";" + permaAttr.getValue();
			if(drunkCount < permanentAttributes.size() - 1)
				stAttrDrunk += ";";
			drunkCount++;
		}
		datas.put("attrpotionsdrunk", stAttrDrunk);
		
		String stPermaAttr = "";
		int atrCount = 0;
		for(Entry<String, Integer> permaAttr : permanentAttributes.entrySet())
		{
			stPermaAttr += permaAttr.getKey() + ";" + permaAttr.getValue();
			if(atrCount < permanentAttributes.size() - 1)
				stPermaAttr += ";";
			atrCount++;
		}
		datas.put("permanentattributes", stPermaAttr);
		
		for(int i = 0; i < Difficulty.DIFFICULTY_COUNT; i++)
			if(difficultiesFinished.contains(i))
				datas.put("diff" + i, "finished");
		
		String chestString = "";
		for(Integer id : uniqueChestsOpened)
			chestString += id + ",";
		if(chestString.length() > 0 && chestString.charAt(chestString.length() - 1) == ',')
			chestString = chestString.substring(0, chestString.length() - 1);
		datas.put("uniqueChestsOpened", chestString);
		
		datas.put("currentSkin", asset);
		
		String unlockedSkins = "";
		for(String skin : unlockedPlayerSkins)
			unlockedSkins += skin + ",";
		if(unlockedSkins.length() > 0 && unlockedSkins.charAt(unlockedSkins.length() - 1) == ',')
			unlockedSkins = unlockedSkins.substring(0, unlockedSkins.length() - 1);
		datas.put("unlockedSkins", unlockedSkins);
		
		return datas;
	}
	
	public String getStashString(int nr)
	{
		if(inventory.getStash(nr).size() < 1)
			return "";
		String res = inventory.getStash(nr).size() + ";";
		int i = 0;
		for(Entry<Integer, Item> e : inventory.getStash(nr).entrySet())
		{
			res += e.getKey() + ";" + e.getValue().toStringFormat();
			if(i < inventory.getStash(nr).size() - 1)
				res += ";";
			i++;
		}
		return res;
	}
	
	private void initializeNewPlayer()
	{
		if(playerClass.equalsIgnoreCase("Barbarian"))
		{
			inventory.useItem(Item.getItemFromStringFormat("Claymore:1:DMG:90:1:1"), false);
			inventory.useItem(Item.getItemFromStringFormat("Insignia:1:STR:5:1:1"), false);
		}
		else if(playerClass.equalsIgnoreCase("Dark Priest"))
		{
			inventory.useItem(Item.getItemFromStringFormat("Scepter:1:DMG:90:1:1"), false);
			inventory.useItem(Item.getItemFromStringFormat("Tome:1:WIS:5:1:1"), false);
		}
		else if(playerClass.equalsIgnoreCase("Death Knight"))
		{
			inventory.useItem(Item.getItemFromStringFormat("Longsword:1:DMG:90:1:1"), false);
			inventory.useItem(Item.getItemFromStringFormat("Shield:1:ARMOR:20:1:1"), false);
		}
		else if(playerClass.equalsIgnoreCase("Druid"))
		{
			inventory.useItem(Item.getItemFromStringFormat("Rod:1:DMG:90:1:1"), false);
			inventory.useItem(Item.getItemFromStringFormat("Charm:1:WIS:5:1:1"), false);
		}
		else if(playerClass.equalsIgnoreCase("Ranger"))
		{
			inventory.useItem(Item.getItemFromStringFormat("Bow:1:DMG:90:1:1"), false);
			inventory.useItem(Item.getItemFromStringFormat("Trophy:1:DEX:5:1:1"), false);
		}
		else if(playerClass.equalsIgnoreCase("Sorcerer"))
		{
			inventory.useItem(Item.getItemFromStringFormat("Wand:1:DMG:75:1:1"), false);
			inventory.useItem(Item.getItemFromStringFormat("Focus:1:INT:5:1:1"), false);
		}
		
		getInventory().addItem(HealthPotion.getPotion(1, 2), true, false);
		getInventory().addItem(ManaPotion.getPotion(1, 2), true, true);
		
		for(Spell spell : this.getSpells().values())
			spell.setLevel(1);
		hud.getCallback().updateSpellLevelText();
		
		unlockedPlayerSkins.add(playerClass);
		if(!playerClass.equals("Druid"))
			unlockedPlayerSkins.add("Druid");
		if(!playerClass.equals("Ranger"))
			unlockedPlayerSkins.add("Ranger");
		
		if(Constants.isDebugging)
		{
			
		}
	}
	
	public void initialize(SavedGame savedGame)
	{
		//TODO: read out all data.. (all stats, money, honor, ITEMS, ..)
		boolean tutorialSeen = GlobalSettings.getInstance().getDataValue("tutorialseen") != null && GlobalSettings.getInstance().getDataValue("tutorialseen").equals("true");
		
		for(int d = 0; d < Difficulty.DIFFICULTY_COUNT; d++)
		{
			if(savedGame.getDataValue("lairs" + d) != null)
			{
				StringTokenizer st = new StringTokenizer(savedGame.getDataValue("lairs" + d), ";");
				while(st.hasMoreTokens())
				{
					lairsPerDifficulty.get(d).put(st.nextToken(), Integer.parseInt(st.nextToken()));
				}
			}
		}
		
		for(int i = 0; i < 12; i++)
			if(savedGame.getDataValue("equip" + i) != null && !savedGame.getDataValue("equip" + i).equals(""))
				inventory.useItem(Item.getItemFromStringFormat(savedGame.getDataValue("equip" + i)), false);
		
		for(int i = 0; i < 8; i++)
			if((savedGame.getDataValue("hotkey" + i) != null))
			{
				String name = savedGame.getDataValue("hotkey" + i);
				for(Spell spell : getSpells().values())
					if(spell.getName().equals(name))
					{
						getHotkeySpells().put(i, spell);
						hud.getCallback().hotkeySpellChanged(i, spell);
						break;
					}
			}
		
		if(savedGame.getDataValue("items") != null)
		{
			StringTokenizer st = new StringTokenizer(savedGame.getDataValue("items"), ",");
			while(st.hasMoreTokens())
				inventory.addItem(Item.getItemFromStringFormat(st.nextToken()), false, false);
			inventory.doUpdate(ItemCategory.All);
		}
		
		if(savedGame.getDataValue("gold") != null)
			inventory.addGold(Integer.parseInt(savedGame.getDataValue("gold")));
		
		for(Entry<Integer, Spell> e : getSpells().entrySet())
		{
			String entry = savedGame.getDataValue("spell" + e.getKey());
			if(entry != null)
				e.getValue().setLevel(Integer.parseInt(entry));
		}
		
		if(savedGame.getDataValue("invmaxsize") != null)
			inventory.setMaxSize(Integer.parseInt(savedGame.getDataValue("invmaxsize")));
		
		if(savedGame.getDataValue("fame") != null)
			addFame(Integer.parseInt(savedGame.getDataValue("fame")));
		
		for(int i = 0; i < Difficulty.DIFFICULTY_COUNT; i++)
			if(savedGame.getDataValue("diff" + i) != null && savedGame.getDataValue("diff" + i).equalsIgnoreCase("finished"))
				difficultiesFinished.add(i);
		
		for(int i = 0; i < Difficulty.DIFFICULTY_COUNT; i++)
		{
			if(savedGame.getDataValue("quests" + i) != null)
			{
				StringTokenizer st = new StringTokenizer(savedGame.getDataValue("quests" + i), ",");
				while(st.hasMoreTokens())
				{
					Quest quest = new Quest(st.nextToken(), level);
					setQuest(quest.getQuestType(), quest, i);
				}
			}
			else if(i == 0 && !tutorialSeen)//if NEW to game AND no quests exist and difficulty normal, give tutorial quest
				setQuest(QuestType.Story, new Quest(GameHelper.getInstance().getStoryQuest(1), level), i);
			else if(!difficultiesFinished.contains(i))//
				setQuest(QuestType.Story, new Quest(GameHelper.getInstance().getStoryQuest(2), level), i);
			
			if(savedGame.getDataValue("dayswhenlastdaily" + i) != null)
				daysWhenLastDaily.put(i, Integer.parseInt(savedGame.getDataValue("dayswhenlastdaily" + i)));
			else
				daysWhenLastDaily.put(i, Days.daysBetween(GameHelper.getInstance().getDefaultDate(), new DateTime()).getDays() - 1);
		}
		
		if(savedGame.getDataValue("attrpotionsdrunk") != null)
		{
			StringTokenizer st = new StringTokenizer(savedGame.getDataValue("attrpotionsdrunk"), ";");
			while(st.hasMoreTokens())
			{
				attributePotionsDrunk.put(st.nextToken(), Integer.parseInt(st.nextToken()));
			}
		}
		
		if(savedGame.getDataValue("permanentattributes") != null)
		{
			StringTokenizer st = new StringTokenizer(savedGame.getDataValue("permanentattributes"), ";");
			while(st.hasMoreTokens())
			{
				permanentAttributes.put(st.nextToken(), Integer.parseInt(st.nextToken()));
			}
		}
		
		for(int i = 0; i < 4; i++)
			if(GlobalSettings.getInstance().getDataValue("stash" + i) != null)
				initializeStash(i, GlobalSettings.getInstance().getDataValue("stash" + i));
		
		for(int i = 0; i < 2; i++)
			if((savedGame.getDataValue("hotkeypot" + i) != null))
			{
				String name = savedGame.getDataValue("hotkeypot" + i);
				Item item = null;
				for(Item it : getInventory().getItems().get(ItemCategory.Consumable))
					if(it.NAME.equals(name))
					{
						item = it;
						break;
					}
				if(item != null && item instanceof Potion)
				{
					getHotkeyPotions().put(i, (Potion) item);
					hud.getCallback().hotkeyPotionChanged(i, (Potion) item);
				}
			}
		
		if(savedGame.getDataValue("uniqueChestsOpened") != null)
		{
			StringTokenizer st = new StringTokenizer(savedGame.getDataValue("uniqueChestsOpened"), ",");
			while(st.hasMoreTokens())
			{
				uniqueChestsOpened.add(Integer.parseInt(st.nextToken()));
			}
		}
		
		if(savedGame.getDataValue("currentSkin") != null)
			onSkinChanged(savedGame.getDataValue("currentSkin"));
		
		if(savedGame.getDataValue("unlockedSkins") != null && !savedGame.getDataValue("unlockedSkins").isEmpty())
		{
			StringTokenizer st = new StringTokenizer(savedGame.getDataValue("unlockedSkins"), ",");
			while(st.hasMoreTokens())
				unlockedPlayerSkins.add(st.nextToken());
		}
		
		if(savedGame.getDataValue("new") == null)
			initializeNewPlayer();
		
		setHp(getMaxHp());
		setMana(getMaxMana());
		onAttributeChanged();
	}
	
	public void initializeStash(int nr, String stash)
	{
		if(stash.equals(""))
			return;
		StringTokenizer st = new StringTokenizer(stash, ";");
		int count = Integer.parseInt(st.nextToken());
		for(int i = 0; i < count; i++)
		{
			inventory.getStash(nr).put(Integer.parseInt(st.nextToken()), Item.getItemFromStringFormat(st.nextToken()));
		}
	}
	
	public boolean containsSpell(String spellName)
	{
		for(Spell spell : spells.values())
			if(spell.getName().equals(spellName))
				return true;
		return false;
	}
	
	public int addFame(int value)
	{
		if(value + fame < 0)
			value = -fame;
		else
			this.fame += value;
		
		hud.getMenu("fame").setParam(fame);
		
		List<IQuestListener> toLoopIQuestListeners = new ArrayList<IQuestListener>(level.getQuestListeners());
		for(IQuestListener listener : toLoopIQuestListeners)
			listener.onFameChanged(value);
		
		hud.getCallback().updatePlayerText();
		
		Integer[] fameRanks = GameHelper.getInstance().getRanks(fame);
		this.fameLastRank = fameRanks[0];
		this.fameNextRank = fameRanks[1];
		
		return value;
	}
	
	public void arenaFameReward(int fame)
	{
		addFame(fame);
		hud.displayReward("Arena finished!", 0, fame);
	}
	
	public boolean isStunned()
	{
		return stunned;
	}
	
	public void setStunned(boolean b)
	{
		stunned = b;
	}
	
	public boolean isRooted()
	{
		return rooted;
	}
	
	public void setRooted(boolean b)
	{
		rooted = b;
	}
	
	public boolean isSilenced()
	{
		return silenced;
	}
	
	public void setSilenced(boolean b)
	{
		silenced = b;
	}
	
	public String getPlayerClass()
	{
		return playerClass;
	}
	
	public ClientLevel getLevel()
	{
		return level;
	}
	
	public ConcurrentHashMap<String, Integer> getAvailableLairs()
	{
		if(level.isHost())
			return lairsPerDifficulty.get(level.getDifficulty());
		else
			return lairsHost;
	}
	
	public Integer getDaysWhenLastDaily()
	{
		return daysWhenLastDaily.get(level.getDifficulty());
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>QUESTS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public ConcurrentHashMap<QuestType, Quest> getQuests()
	{
		return questsPerDiffuculty.get(level.getDifficulty());
	}
	
	public ConcurrentHashMap<String, Quest> getParkedDungeonQuests()
	{
		return parkedDungeonQuestsPerDifficulty.get(level.getDifficulty());
	}
	
	private boolean setQuest(QuestType type, Quest quest, int difficulty)
	{
		if(difficulty == level.getDifficulty())
		{
			if(quest.getQuestObjective() instanceof QuestObjectiveKill)
				level.onKillQuestReceived(quest);
			switch(type)
			{
				case Dungeon:
					setDungeonQuest(quest, difficulty);
					return true;
				case Story:
					setStoryQuest(quest, difficulty);
					return true;
				case Daily:
					setDailyQuest(quest, difficulty);
					return true;
			}
		}
		else
		{
			questsPerDiffuculty.get(difficulty).put(quest.getQuestType(), quest);
		}
		return false;
	}
	
	public void onQuestFinished(Quest q)
	{
		if(q.getQuestType() == QuestType.Dungeon)
			dungeonQuestFinished(level.getMapName());
		else if(q.getQuestType() == QuestType.Story)
			storyQuestFinished();
		else if(q.getQuestType() == QuestType.Daily)
			dailyQuestFinished();
		
		if(q.getQuestObjective() instanceof QuestObjectiveKill)
			level.onKillQuestFinished(q);
		
		giveQuestReward(q, q.getQuestType());
	}
	
	public void giveQuestReward(Quest quest, QuestType questType)
	{
		addFame(quest.getFameReward());
		getInventory().addGold(quest.getGoldReward());
		if(quest.getItemReward() != null)
		{
			for(Item item : quest.getItemReward())
				getInventory().addItem(item, true, false);
			getInventory().doUpdate(ItemCategory.All);
			
			hud.displayReward("Quest completed!", quest.getGoldReward(), quest.getFameReward(), quest.getItemReward());
		}
		else
			hud.displayReward("Quest completed!", quest.getGoldReward(), quest.getFameReward());
	}
	
	private void onNewQuestAvailable(QuestType questType)
	{
		if(!hud.getCallback().getCharacterMenu().getQuestHUD().isActive)
		{
			newQuests.put(questType, true);
			hud.getMenu("tools").setParam(1);
			hud.getMenu("character").setParam(1);
		}
		else
		{
			onQuestHUDOpened();
		}
	}
	
	public void onQuestRemoved(QuestType questType)
	{
		newQuests.put(questType, false);
		if(!hasNewQuest())
			onQuestHUDOpened();
	}
	
	private boolean hasNewQuest()
	{
		for(Boolean b : newQuests.values())
			if(b)
				return true;
		return false;
	}
	
	public void onQuestHUDOpened()
	{
		for(QuestType type : newQuests.keySet())
			newQuests.put(type, false);
		
		hud.getMenu("tools").setParam(0);
		hud.getMenu("character").setParam(0);
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>DUNGEON QUEST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public boolean setDungeonQuest(Quest quest, int difficulty)
	{
		boolean isNew = true;
		if(questsPerDiffuculty.get(difficulty).containsKey(quest.getQuestType()))//should not be the case btw.
			level.unregisterQuest(questsPerDiffuculty.get(difficulty).get(quest.getQuestType()));
		
		if(getParkedDungeonQuests().containsKey(level.getMapName()))
		{
			if(!getParkedDungeonQuests().get(level.getMapName()).isFinished())
			{
				quest = getParkedDungeonQuests().get(level.getMapName());
				isNew = false;
			}
			else
				return false;
		}
		this.questsPerDiffuculty.get(difficulty).put(quest.getQuestType(), quest);
		level.registerQuest(quest);
		
		if(hud.getMenu("character").isActive)
			hud.getCallback().updateQuestText();
		
		if(isNew)
			onNewQuestAvailable(quest.getQuestType());
		
		return isNew;
	}
	
	public void dungeonQuestFinished(String oldMap)
	{
		if(getQuests().containsKey(QuestType.Dungeon))
		{
			getParkedDungeonQuests().put(oldMap, getQuests().get(QuestType.Dungeon));
			level.unregisterQuest(getQuests().get(QuestType.Dungeon));
			
			if(getQuests().get(QuestType.Dungeon).isFinished())
				onQuestRemoved(QuestType.Dungeon);
			
			getQuests().remove(QuestType.Dungeon);
			hud.getCallback().updateQuestText();
		}
	}
	
	public void handleOtherMapLoaded(String oldMap)
	{
		if(getParkedDungeonQuests().containsKey(oldMap))
			getParkedDungeonQuests().remove(oldMap);
		
		if(oldMap.equals("tutorial"))
			GlobalSettings.update("tutorialseen", "true");
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>STORY QUEST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public Quest getAvailableStoryQuest()
	{
		if(level.isHost())
			return questsPerDiffuculty.get(level.getDifficulty()).get(QuestType.Story);
		else
			return storyQuestHost;
	}
	
	public void setStoryQuest(Quest quest, int difficulty)
	{
		if(questsPerDiffuculty.get(difficulty).containsKey(quest.getQuestType()))
			level.unregisterQuest(questsPerDiffuculty.get(difficulty).get(quest.getQuestType()));
		questsPerDiffuculty.get(difficulty).put(quest.getQuestType(), quest);
		
		if(!lairsPerDifficulty.get(level.getDifficulty()).containsKey(quest.getLair()))
			lairsPerDifficulty.get(level.getDifficulty()).put(quest.getLair(), 1);
		
		if(level.isHost() && level.getDifficulty() == difficulty)
		{
			level.registerQuest(quest);
			
			if(hud.getMenu("character").isActive)
				hud.getCallback().updateQuestText();
			
			onNewQuestAvailable(quest.getQuestType());
		}
	}
	
	public void storyQuestFinished()
	{
		if(level.isHost())//should anyway be the case..
		{
			GlobalSettings.update("tutorialseen", "true");
			if(getQuests().containsKey(QuestType.Story))
			{
				level.unregisterQuest(getQuests().get(QuestType.Story));
				String nextQuest = GameHelper.getInstance().getStoryQuest(getQuests().get(QuestType.Story).getStep() + 1);
				if(nextQuest != null)
				{
					Quest story = new Quest(nextQuest, level);
					getQuests().put(QuestType.Story, story);
					level.registerQuest(story);
					hud.getCallback().updateQuestText();
					hud.getCallback().postMessage("New story quest available!", true);
					onNewQuestAvailable(QuestType.Story);
					
					if(!lairsPerDifficulty.get(level.getDifficulty()).containsKey(story.getLair()))
					{
						lairsPerDifficulty.get(level.getDifficulty()).put(story.getLair(), 1);
						
						DataPacket dp = new DataPacket();
						dp.setInt(MessageType.MSG_NEXT_LAIR);
						dp.setString(story.getLair());
						dp.setInt(1);
						level.sendDataPacket(dp);
					}
					
					//Share current quest progress
					DataPacket dpQuest = new DataPacket();
					dpQuest.setInt(MessageType.MSG_QUEST_UPDATE_HOST);
					dpQuest.setInt(story.getStep());
					dpQuest.setInt(0);
					level.sendDataPacket(dpQuest);
				}
				else
				{
					DataPacket dpQuest = new DataPacket();
					dpQuest.setInt(MessageType.MSG_QUEST_UPDATE_HOST);
					dpQuest.setInt(-1);//quest line over
					dpQuest.setInt(0);
					level.sendDataPacket(dpQuest);
					
					getQuests().remove(QuestType.Story);
					hud.getCallback().postMessage("Story quest finished!", true);
					onQuestRemoved(QuestType.Story);
					hud.getCallback().updateQuestText();
					
					if(!difficultiesFinished.contains(level.getDifficulty()))
						difficultiesFinished.add(level.getDifficulty());
				}
			}
		}
	}
	
	public void updateStoryQuestHost(int step, int progress)
	{
		if(storyQuestHost != null && storyQuestHost.getStep() == step)
		{
			storyQuestHost.setProgress(progress);
			if(hud.getMenu("character").isActive)
				hud.getCallback().updateQuestText();
		}
		else
		{
			String sQuest = GameHelper.getInstance().getStoryQuest(step);
			if(sQuest != null)
			{
				storyQuestHost = new Quest(sQuest, level);
				storyQuestHost.setProgress(progress);
				if(hud.getMenu("character").isActive)
					hud.getCallback().updateQuestText();
				onNewQuestAvailable(QuestType.Story);
				hud.getCallback().postMessage("New story quest available!", true);
			}
			else
			{
				storyQuestHost = null;
				hud.getCallback().postMessage("Story quest finished!", true);
				onQuestRemoved(QuestType.Story);
				hud.getCallback().updateQuestText();
			}
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Daily QUEST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	private void setDailyQuest(Quest quest, int difficulty)
	{
		if(questsPerDiffuculty.get(difficulty).containsKey(quest.getQuestType()))
			level.unregisterQuest(questsPerDiffuculty.get(difficulty).get(quest.getQuestType()));
		questsPerDiffuculty.get(difficulty).put(quest.getQuestType(), quest);
		
		if(level.getDifficulty() == difficulty)
		{
			level.registerQuest(quest);
			
			if(hud.getMenu("character").isActive)
				hud.getCallback().updateQuestText();
			
			onNewQuestAvailable(quest.getQuestType());
		}
	}
	
	private void dailyQuestFinished()
	{
		if(getQuests().containsKey(QuestType.Daily))
		{
			level.unregisterQuest(getQuests().get(QuestType.Daily));
			getQuests().remove(QuestType.Daily);
			hud.getCallback().updateQuestText();
			onQuestRemoved(QuestType.Daily);
		}
	}
	
	private boolean isDailyQuestAvailable()
	{
		if(Days.daysBetween(GameHelper.getInstance().getDefaultDate(), new DateTime()).getDays() > getDaysWhenLastDaily())
			return true;
		else
			return false;
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public void setDirectionArrow(float x, float y)
	{
		directionArrow.setTarget(x, y);
	}
	
	public void hideDirectionArrow()
	{
		directionArrow.disable();
	}
	
	public int[] onDeathPenalty()
	{
		int[] res = new int[2];
		res[0] = -addFame(-Constants.FameLossPerDeath);
		res[1] = inventory.removeGold((int) (inventory.getGold() * Constants.GoldLossFactor));
		
		return res;
	}
	
	public String getTitle()
	{
		return GameHelper.getInstance().getPlayerTitle(fame);
	}
	
	private boolean isHallOfFameUnlocked(int fameNr)
	{
		return fame >= GameHelper.getInstance().getFameForHall(fameNr);
	}
	
	private int getFameNeededForHallOfFame(int fameNr)
	{
		return GameHelper.getInstance().getFameForHall(fameNr);
	}
	
	public void onAimHelpChanged(boolean value)
	{
		aimHelp.setVisible(value);
	}
	
	public void onResume()
	{
		
	}
	
	public void useHpPotion(HealthPotion healthPotion)
	{
		requestCondition("Pot HOT", "", healthPotion.getRestoreValue(), Constants.HealthPotionDuration, false, "Hp Pot");
	}
	
	public void useManaPotion(ManaPotion manaPotion)
	{
		requestCondition("Pot MOT", "", manaPotion.getRestoreValue(), Constants.ManaPotionDuration, false, "Mana Pot");
	}
	
	public int getFame()
	{
		return fame;
	}
	
	public int getFameLastRank()
	{
		return fameLastRank;
	}
	
	public int getFameNextRank()
	{
		return fameNextRank;
	}
	
	public void lowerCooldowns(int value)
	{
		for(Spell spell : spells.values())
			if(!spell.getName().equals(SpellTimeWarp.name))
				spell.cooldownLeft = Math.max(0.01f, spell.cooldownLeft - value);
	}
	
	public void handleUniqueChestOpened(int id)
	{
		if(!uniqueChestsOpened.contains(id))
		{
			uniqueChestsOpened.add(id);
			level.saveGame();
		}
	}
	
	public void changeSkin(String skin)
	{
		asset = skin;
		sprite.setTextureRegion(GameHelper.getInstance().getEntityAssetCopy(asset));
		
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_CHANGE_SKIN);
		dp.setString(skin);
		level.sendDataPacket(dp);
	}
	
	public List<String> getUnlockedSkins()
	{
		return unlockedPlayerSkins;
	}
	
	public void die()
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_I_WANNA_DIE);
		level.sendDataPacket(dp);
	}
	
	public void handleYouGotHit(int attackerId)
	{
		for(Equip eq : getInventory().getEquipList())
			if(eq instanceof EquipArmor && eq.modification != null)
				((ArmorModification) eq.modification).onPlayerHit(this, attackerId);
	}
}