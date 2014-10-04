package com.vhelium.lotig.scene.gamescene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.vhelium.lotig.EventResult;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.PlatformResolver;
import com.vhelium.lotig.Utility;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.server.EnemyModel;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellBloodthirst;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCharge;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCleave;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellCriticalStrike;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellExtendedBloodthirst;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellExtendedRage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellFrostStrike;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellHaste;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellRage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellRavage;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellTerrifyingShout;
import com.vhelium.lotig.scene.gamescene.spells.barbarian.SpellThunderingRoar;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellAbsorb;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellArcaneExplosions;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellBondOfDebt;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellClutchOfDeath;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellEquilibrium;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellHellFire;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellLightwell;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellMassDispel;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellMassHeal;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellShadowCurse;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellSilence;
import com.vhelium.lotig.scene.gamescene.spells.darkpriest.SpellVampiricTouch;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellBladestorm;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellDeadlyBlow;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellFlameStrike;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellFleshReformation;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellInnerBeast;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellIntimidation;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellMithrilArmor;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellShieldBash;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellSpellShield;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellStunningStrike;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellSunderArmor;
import com.vhelium.lotig.scene.gamescene.spells.deathknight.SpellTaunt;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellBlastSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellDauntingGhost;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEarthquake;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEnrageSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellEntangle;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellFlameBreath;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellHealSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellIceDart;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellMeditationAura;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellOvercharge;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellSummonSpirit;
import com.vhelium.lotig.scene.gamescene.spells.druid.SpellThornArmor;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellBarrage;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellColdTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellDeadlyShot;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellEvasion;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellExplosiveShot;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellFireTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellLightningTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellMultiShot;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellPathfinder;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellPoisonousArrow;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellSpikeTrap;
import com.vhelium.lotig.scene.gamescene.spells.ranger.SpellUnleashTheBeasts;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellBlink;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFireball;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFirestorm;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFrostBomb;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellFrostNova;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellLivingBomb;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMagicTrick;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellManaBurn;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMeteorStrike;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellMoltenShield;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellThunderbolt;
import com.vhelium.lotig.scene.gamescene.spells.sorcerer.SpellTimeWarp;

public class GameHelper
{
	public static GameHelper $;
	
	private Main activity;
	
	private final HashMap<String, BulletAsset> bulletAssets;
	private final HashMap<String, EnemyModel> enemyModels;
	private final HashMap<String, TiledTextureRegion> entityAssets;
	
	private final TmxMapLoader tmxLoader;
	
	private final HashMap<FontCategory, HashMap<Integer, BitmapFont>> mainFonts;
	private final HashMap<FontCategory, String> fontCategoryFontNames;
	private final HashMap<String, Integer> attributePointPrice;
	private final HashMap<String, String> attributeNames;
	private final HashMap<Integer, OnScreenInfo> onScreenInfos;
	
	private final HashMap<Integer, String> playerTitles;
	private final ArrayList<Integer> playerTitleRequisites;
	
	private final HashMap<Integer, String> dialogues;
	
	private final HashMap<Integer, Integer> fameRequiredForHall;
	
	private final HashMap<Integer, String> storyQuests;
	private final List<String> dailyQuests;
	
	private final HashMap<String, HashMap<Integer, List<String>>> arenas;
	
	private final HashMap<String, List<String>> playerClassSpells;
	
	private final Random random;
	
	private TextureAtlas gameAssets;
	private TextureAtlas bullets;
	private TextureAtlas gui;
	private TextureAtlas items;
	private final HashMap<String, TextureRegion> spellIcons;
	
	private EffectManager effectManager;
//	private BluetoothAdapter btAdapter;
	
	float factX;
	float factY;
	
	private GameHelper()
	{
		tmxLoader = new TmxMapLoader();
		bulletAssets = new HashMap<String, BulletAsset>();
		enemyModels = new HashMap<String, EnemyModel>();
		entityAssets = new HashMap<String, TiledTextureRegion>();
		mainFonts = new HashMap<FontCategory, HashMap<Integer, BitmapFont>>();
		fontCategoryFontNames = new HashMap<FontCategory, String>();
		storyQuests = new HashMap<Integer, String>();
		dailyQuests = new ArrayList<String>();
		arenas = new HashMap<String, HashMap<Integer, List<String>>>();
		playerTitles = new HashMap<Integer, String>();
		playerClassSpells = new HashMap<String, List<String>>();
		onScreenInfos = new HashMap<Integer, OnScreenInfo>();
		fameRequiredForHall = new HashMap<Integer, Integer>();
		dialogues = new HashMap<Integer, String>();
		spellIcons = new HashMap<String, TextureRegion>();
		
		random = new Random();
		
		attributePointPrice = new HashMap<String, Integer>();
		attributeNames = new HashMap<String, String>();
		//TODO: multilanguage?
		attributePointPrice.put("STR", Price.STR);
		attributeNames.put("STR", "Strength");
		attributePointPrice.put("DEX", Price.DEX);
		attributeNames.put("DEX", "Dexterity");
		attributePointPrice.put("SPD", Price.SPD);
		attributeNames.put("SPD", "Speed");
		attributePointPrice.put("VIT", Price.VIT);
		attributeNames.put("VIT", "Vitality");
		attributePointPrice.put("WIS", Price.WIS);
		attributeNames.put("WIS", "Wisdom");
		attributePointPrice.put("INT", Price.INT);
		attributeNames.put("INT", "Intelligence");
		attributePointPrice.put("MAXHP", Price.MAXHP);
		attributeNames.put("MAXHP", "Max. HP");
		attributePointPrice.put("MAXMANA", Price.MAXMANA);
		attributeNames.put("MAXMANA", "Max. Mana");
		attributePointPrice.put("FRES", Price.FRES);
		attributeNames.put("FRES", "Fire Res.");
		attributePointPrice.put("CRES", Price.CRES);
		attributeNames.put("CRES", "Cold Res.");
		attributePointPrice.put("LRES", Price.LRES);
		attributeNames.put("LRES", "Lightning Res.");
		attributePointPrice.put("PRES", Price.CRES);
		attributeNames.put("PRES", "Poison Res.");
		attributePointPrice.put("DMG", Price.DMG);
		attributeNames.put("DMG", "Damage");
		attributePointPrice.put("BONUSDMG", Price.BONUSDMG);
		attributeNames.put("BONUSDMG", "Extra Damage");
		attributePointPrice.put("DMGPERCENT", Price.DMGPERCENT);
		attributeNames.put("DMGPERCENT", "Damage %");
		attributePointPrice.put("ARMOR", Price.ARMOR);
		attributeNames.put("ARMOR", "Armor");
		attributePointPrice.put("LPH", Price.LPH);
		attributeNames.put("LPH", "Life Per Hit");
		attributePointPrice.put("MPH", Price.MPH);
		attributeNames.put("MPH", "Mana Per Hit");
		attributePointPrice.put("THORNS", Price.THORNS);
		attributeNames.put("THORNS", "Thorns");
		attributePointPrice.put("CDR", Price.CDR);
		attributeNames.put("CDR", "CD reduction");
		
		playerTitles.put(0, "Rookie");
		playerTitles.put(50, "Recruit");
		playerTitles.put(120, "Adventurer");
		playerTitles.put(220, "Hunter");
		playerTitles.put(350, "Slayer");
		playerTitles.put(500, "Knight");
		playerTitles.put(800, "Champion");
		playerTitles.put(1200, "Baron");
		playerTitles.put(1600, "Lord");
		playerTitles.put(2000, "King");
		playerTitles.put(3000, "Legendary");
		
		fameRequiredForHall.put(1, 20);
		fameRequiredForHall.put(2, 100);
		fameRequiredForHall.put(3, 200);
		fameRequiredForHall.put(4, 400);
		
		playerTitleRequisites = new ArrayList<Integer>(playerTitles.keySet());
		Collections.sort(playerTitleRequisites);
		
//		dialogues.put(1, "Welcome to Sanctuary, Mortal$The only save place in%the Otherworld for now.$I know you have many questions.$You should visit the%Garden of Eden first.$See you there, Mortal.");
		dialogues.put(1, "short test..");
		dialogues.put(2, "I am glad to see%you here, Mortal$Take the time and%let the angels introduce%you to the Otherworld.$Go ahead now.");
		dialogues.put(3, "You will have noticed that you%have left your human skin, Mortal.$The Otherworld is the realm for%every mortal beeing after death..");
		dialogues.put(4, "In the Otherworld the gods and the angels rest%and Mortals find their eternal peace.$But the Otherworld has changed..");
		dialogues.put(5, "Hotrax, the God of hatred corrupted all%the other gods who rest in their lairs.$He made them all insane..");
		dialogues.put(6, "Now the insane gods seek death%and destruction in the Otherworld.$Mortals and angels alike%are not safe anymore..");
		dialogues.put(7, "The insane gods gather their minions%in their lairs for an assault.$They have to be stopped..");
		dialogues.put(8, "I see great power in you, Mortal!$You shall stop the insane gods%and bring peace to the Otherworld again..");
		dialogues.put(9, "Seek for the insane gods%in their lairs%and eliminate them!$You, Mortal,%are our only hope..");
		dialogues.put(10, "Good luck on your journey, Mortal.$The gods are against you..");
	}
	
	public static GameHelper getInstance()
	{
		if($ == null)
			forceNewInstance();
		
		return $;
	}
	
	public static void forceNewInstance()
	{
		$ = new GameHelper();
	}
	
	public void load(final Main activity, SceneManager sceneManager)
	{
		Log.w("GameHelper", "GameHelper loaded");
		this.activity = activity;
		
		platformResolver.init();
		
		loadMapScene = new LoadMapScene(activity, sceneManager, false);
		loadMapScene.loadResources();
		
		loadGameAssets();
		loadItemIcons();
		loadBulletAssets();
		loadGuiAssets();
		
		loadEnemyAssets();
		loadStoryQuests();
		loadDailyQuests();
		loadArenas();
		
		preloadFonts();
		loadOnScreenInfos();
		
		SoundManager.load(activity);
		
		factX = Gdx.graphics.getWidth() / SceneManager.CAMERA_WIDTH;
		factY = Gdx.graphics.getHeight() / SceneManager.CAMERA_HEIGHT;
		
		effectManager = new EffectManager(activity);
		effectManager.loadEffects();
	}
	
//	>>>>>>>>>>>>>>>>>>>>>>>>>>> [v] ASSETS [v] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public Texture loadTexture(String fileName)
	{
		return new Texture(Gdx.files.internal(fileName));
	}
	
	public TextureRegion loadTextureRegion(String fileName)
	{
		return loadTextureRegion(fileName, TextureFilter.Linear);
	}
	
	public TextureRegion loadTextureRegion(String fileName, TextureFilter filter)
	{
		Texture txt = new Texture(Gdx.files.internal(fileName));
		txt.setFilter(filter, filter);
		
		TextureRegion region = new TextureRegion(txt, 0, 0, txt.getWidth(), txt.getHeight());
		region.flip(false, true);
		
		return region;
	}
	
	public TiledTextureRegion loadTiledTextureRegion(String fileName, int columns, int rows)
	{
		return new TiledTextureRegion(new Texture(Gdx.files.internal(fileName)), columns, rows, true);
	}
	
	public TiledMap loadTiledMap(String fileName)
	{
		return tmxLoader.load(fileName);
	}
	
	public com.badlogic.gdx.audio.Sound loadSound(String sound)
	{
		return Gdx.files.internal("sfx/" + sound).exists() ? Gdx.audio.newSound(Gdx.files.internal("sfx/" + sound)) : null;
	}
	
	public Music loadMusic(String music)
	{
		return Gdx.audio.newMusic(Gdx.files.internal("mfx/" + music));
	}
	
	public TiledTextureRegion getEntityAssetCopy(String asset)
	{
		if(!entityAssets.containsKey(asset))
			entityAssets.put(asset, loadTiledTextureRegion("gfx/entities/" + asset + ".png", 5, 3));
		
		return entityAssets.get(asset).copy();
	}
	
//	>>>>>>>>>>>>>>>>>>>>>>>>>>> [^] ASSETS [^] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
//	>>>>>>>>>>>>>>>>>>>>>>>>>>> [v] PRE LOADS [v] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	private void loadStoryQuests()
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(Gdx.files.internal("xml/storyquests.txt").read()));
			String line;
			int i = 1;
			while((line = br.readLine()) != null)
			{
				if(!line.startsWith("//") && !line.isEmpty())
					storyQuests.put(i, line);
				i++;
			}
		}
		catch (Exception e)
		{
			Log.e("GameHelper.out", "failed loading story quests");
		}
	}
	
	private void loadDailyQuests()
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(Gdx.files.internal("xml/dailyquests.txt").read()));
			String line;
			while((line = br.readLine()) != null)
			{
				if(!line.startsWith("//") && !line.isEmpty())
					dailyQuests.add(line);
			}
		}
		catch (Exception e)
		{
			Log.e("GameHelper.out", "failed loading daily quests");
		}
	}
	
	private void loadArenas()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document enemyDom = null;
		
		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			enemyDom = db.parse(Gdx.files.internal("xml/arenas.xml").read());
		}
		catch (ParserConfigurationException pce)
		{
			Log.e("GameHelper", "Error loading arenas: " + pce.getMessage());
		}
		catch (SAXException e)
		{
			Log.e("GameHelper", "Error loading arenas: " + e.getMessage());
		}
		catch (IOException e)
		{
			Log.e("GameHelper", "Error loading arenas: " + e.getMessage());
		}
		
		//get the root elememt
		Element docEle = enemyDom.getDocumentElement();
		
		//get a nodelist of <arena> elements
		NodeList nlArenas = docEle.getElementsByTagName("arena");
		if(nlArenas != null && nlArenas.getLength() > 0)
		{
			for(int iArena = 0; iArena < nlArenas.getLength(); iArena++)
			{
				if(nlArenas.item(iArena).getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				//get the arena element
				Element elArena = (Element) nlArenas.item(iArena);
				String name = elArena.getAttribute("name");
				
				arenas.put(name, new HashMap<Integer, List<String>>());
				
				NodeList nlRounds = elArena.getElementsByTagName("round");
				for(int iRound = 0; iRound < nlRounds.getLength(); iRound++)
				{
					if(nlRounds.item(iRound).getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					//get the arena element
					Element elRound = (Element) nlRounds.item(iRound);
					Integer roundNr = Integer.parseInt(elRound.getAttribute("nr"));
					
					arenas.get(name).put(roundNr, new ArrayList<String>());
					
					NodeList nlEntities = elRound.getChildNodes();
					for(int iEntity = 0; iEntity < nlEntities.getLength(); iEntity++)
					{
						String nodeName = nlEntities.item(iEntity).getNodeName();
						String nodeValue = nlEntities.item(iEntity).getTextContent();
						
						if(nodeName.equals("entity"))
						{
							arenas.get(name).get(roundNr).add(nodeValue);
						}
					}
				}
			}
		}
	}
	
	private void loadOnScreenInfos()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document enemyDom = null;
		
		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			enemyDom = db.parse(Gdx.files.internal("xml/onscreeninfos.xml").read());
		}
		catch (ParserConfigurationException pce)
		{
			Log.e("GameHelper", "Error loading onscreeninfos: " + pce.getMessage());
		}
		catch (SAXException e)
		{
			Log.e("GameHelper", "Error loading onscreeninfos: " + e.getMessage());
		}
		catch (IOException e)
		{
			Log.e("GameHelper", "Error loading onscreeninfos: " + e.getMessage());
		}
		
		//get the root elememt
		Element docEle = enemyDom.getDocumentElement();
		
		//get a nodelist of <info> elements
		NodeList nlInfos = docEle.getElementsByTagName("info");
		if(nlInfos != null && nlInfos.getLength() > 0)
		{
			for(int iInfo = 0; iInfo < nlInfos.getLength(); iInfo++)
			{
				if(nlInfos.item(iInfo).getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				//get the info element
				Element elInfo = (Element) nlInfos.item(iInfo);
				int id = Integer.parseInt(elInfo.getAttribute("id"));
				
				OnScreenInfo osi = new OnScreenInfo(id);
				
				NodeList nlInfoProps = elInfo.getChildNodes();
				for(int iInfoProp = 0; iInfoProp < nlInfoProps.getLength(); iInfoProp++)
				{
					String nodeName = nlInfoProps.item(iInfoProp).getNodeName();
					String nodeValue = nlInfoProps.item(iInfoProp).getTextContent();
					
					if(nodeName.equals("text"))
						osi.setText(nodeValue);
					else if(nodeName.equals("textX"))
						osi.setTextX(Integer.parseInt(nodeValue));
					else if(nodeName.equals("textY"))
						osi.setTextY(Integer.parseInt(nodeValue));
					else if(nodeName.equals("asset"))
						osi.setAsset(nodeValue);
					else if(nodeName.equals("assetX"))
						osi.setAssetX(Integer.parseInt(nodeValue));
					else if(nodeName.equals("assetY"))
						osi.setAssetY(Integer.parseInt(nodeValue));
					else if(nodeName.equals("linked"))
					{
						String[] ss = nodeValue.split(";");
						for(String s : ss)
							osi.addLink(Integer.parseInt(s));
					}
				}
				
				osi.load(activity);
				
				onScreenInfos.put(id, osi);
			}
		}
	}
	
	private void loadBulletAssets()
	{
		bullets = new TextureAtlas(Gdx.files.internal("gfx/packs/bullets.atlas"));
		for(AtlasRegion region : bullets.getRegions())
			region.flip(false, true);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document enemyDom = null;
		
		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			enemyDom = db.parse(Gdx.files.internal("xml/bullets.xml").read());
		}
		catch (ParserConfigurationException pce)
		{
			Log.e("GameHelper", "Error loading Enemey Assets: " + pce.getMessage());
		}
		catch (SAXException e)
		{
			Log.e("GameHelper", "Error loading Enemey Assets: " + e.getMessage());
		}
		catch (IOException e)
		{
			Log.e("GameHelper", "Error loading Enemey Assets: " + e.getMessage());
		}
		
		//get the root elememt
		Element docEle = enemyDom.getDocumentElement();
		
		//get a nodelist of <bullet> elements
		NodeList nl = docEle.getElementsByTagName("bullet");
		if(nl != null && nl.getLength() > 0)
		{
			for(int i = 0; i < nl.getLength(); i++)
			{
				if(nl.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				String name;
				String asset = null;
				float speed = 0f;
				int width = 0;
				int height = 0;
				
				//get the bullet element
				Element el = (Element) nl.item(i);
				name = el.getAttribute("name");
				
				NodeList nlAttr = el.getChildNodes();
				for(int a = 0; a < nlAttr.getLength(); a++)
				{
					if(nlAttr.item(a).getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					String nodeName = nlAttr.item(a).getNodeName();
					String attrName = nlAttr.item(a).getTextContent();
					
					if(nodeName.equals("asset"))
					{
						asset = attrName;
					}
					else if(nodeName.equals("speed"))
					{
						speed = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("width"))
					{
						width = Integer.parseInt(attrName);
					}
					else if(nodeName.equals("height"))
					{
						height = Integer.parseInt(attrName);
					}
				}
				
				if(asset != null)
				{
					TextureRegion tr = bullets.findRegion(asset);
					if(tr == null)
						throw new NullPointerException("Bullet Texture Missing: " + asset);
					if(width == 0 || height == 0)
					{
						width = tr.getRegionWidth();
						height = tr.getRegionHeight();
					}
					bulletAssets.put(name, new BulletAsset(speed, tr, width, height));
				}
			}
		}
	}
	
	private void loadEnemyAssets()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document enemyDom = null;
		
		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			enemyDom = db.parse(Gdx.files.internal("xml/enemies.xml").read());
		}
		catch (ParserConfigurationException pce)
		{
			Log.e("GameHelper", "Error loading Enemey Assets: " + pce.getMessage());
		}
		catch (SAXException e)
		{
			Log.e("GameHelper", "Error loading Enemey Assets: " + e.getMessage());
		}
		catch (IOException e)
		{
			Log.e("GameHelper", "Error loading Enemey Assets: " + e.getMessage());
		}
		
		//get the root elememt
		Element docEle = enemyDom.getDocumentElement();
		
		//get a nodelist of <enemy> elements
		NodeList nl = docEle.getElementsByTagName("enemy");
		if(nl != null && nl.getLength() > 0)
		{
			for(int i = 0; i < nl.getLength(); i++)
			{
				if(nl.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				EnemyModel model = new EnemyModel();
				
				//get the enemy element
				Element el = (Element) nl.item(i);
				model.name = el.getAttribute("name");
				
				NodeList nlAttr = el.getChildNodes();
				for(int a = 0; a < nlAttr.getLength(); a++)
				{
					if(nlAttr.item(a).getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					String nodeName = nlAttr.item(a).getNodeName();
					String attrName = nlAttr.item(a).getTextContent();
					
					if(nodeName.equals("type"))
					{
						model.type = attrName;
					}
					else if(nodeName.equals("asset"))
					{
						model.asset = attrName;
					}
					else if(nodeName.equals("bulletAsset"))
					{
						model.bulletAsset = attrName;
					}
					else if(nodeName.equals("width"))
					{
						model.width = Integer.parseInt(attrName);
					}
					else if(nodeName.equals("height"))
					{
						model.height = Integer.parseInt(attrName);
					}
					else if(nodeName.equals("hp"))
					{
						model.hp = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("hpPL"))
					{
						model.hpPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("mana"))
					{
						model.mana = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("manaPL"))
					{
						model.manaPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("damage"))
					{
						model.damage = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("damagePL"))
					{
						model.damagePL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("damageBonus"))
					{
						model.damageBonus = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("damageBonusPL"))
					{
						model.damageBonusPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("armorPL"))
					{
						model.armorPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("fireRes"))
					{
						model.fireRes = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("fireResPL"))
					{
						model.fireResPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("coldRes"))
					{
						model.coldRes = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("coldResPL"))
					{
						model.coldResPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("lightningRes"))
					{
						model.lightningRes = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("lightningResPL"))
					{
						model.lightningResPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("poisonRes"))
					{
						model.poisonRes = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("poisonResPL"))
					{
						model.poisonResPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("speed"))
					{
						model.speed = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("speedPL"))
					{
						model.speedPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("shootSpeed"))
					{
						model.shootSpeed = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("shootSpeedPL"))
					{
						model.shootSpeedPL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("detectRange"))
					{
						model.detectRange = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("detectRangePL"))
					{
						model.detectRangePL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("shootRange"))
					{
						model.shootRange = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("shootRangePL"))
					{
						model.shootRangePL = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("piercing"))
					{
						model.piercing = Boolean.parseBoolean(attrName);
					}
					else if(nodeName.equals("roaming"))
					{
						model.roaming = Boolean.parseBoolean(attrName);
					}
					else if(nodeName.equals("oocRegen"))
					{
						model.oocRegen = Boolean.parseBoolean(attrName);
					}
					else if(nodeName.equals("globalspellcooldown"))
					{
						model.globalSpellCooldown = Float.parseFloat(attrName);
					}
					else if(nodeName.equals("deathAnimation"))
					{
						model.deathAnimation = attrName;
					}
					else if(nodeName.equals("spells"))
					{
						model.spells = new ArrayList<SpellEnemy>();
						
						//get a nodelist of <spell> elements
						NodeList nlSpells = el.getElementsByTagName("spell");
						if(nlSpells != null && nlSpells.getLength() > 0)
						{
							for(int s = 0; s < nlSpells.getLength(); s++)
							{
								if(nlSpells.item(s).getNodeType() != Node.ELEMENT_NODE)
									continue;
								
								Element elSpell = (Element) nlSpells.item(s);
								
								String name = elSpell.getAttribute("name");
								int level = Integer.parseInt(elSpell.getAttribute("level"));
								int priority = Integer.parseInt(elSpell.getAttribute("priority"));
								
								HashMap<Integer, Float> condis = new HashMap<Integer, Float>();
								
								//get a nodelist of <condition> elements
								NodeList nlCondis = elSpell.getElementsByTagName("condition");
								if(nlCondis != null && nlCondis.getLength() > 0)
								{
									for(int c = 0; c < nlCondis.getLength(); c++)
									{
										if(nlCondis.item(c).getNodeType() != Node.ELEMENT_NODE)
											continue;
										
										Element elCondi = (Element) nlCondis.item(c);
										
										condis.put(Integer.parseInt(elCondi.getAttribute("id")), Float.parseFloat(nlCondis.item(c).getTextContent()));
									}
								}
								
								SpellEnemy spell = SpellEnemy.getSpell(name, level, priority, condis);
								if(spell != null)
									model.spells.add(spell);
							}
						}
					}
				}
				
				//add it to list
				enemyModels.put(model.name, model);
			}
		}
	}
	
	private void loadItemIcons()
	{
		items = new TextureAtlas(Gdx.files.internal("gfx/packs/items.atlas"));
		for(AtlasRegion region : items.getRegions())
			region.flip(false, true);
	}
	
	private void loadGuiAssets()
	{
		gui = new TextureAtlas(Gdx.files.internal("gfx/packs/gui.atlas"));
		for(AtlasRegion region : gui.getRegions())
			region.flip(false, true);
		loadSpellIcons();
	}
	
	public AtlasRegion getGuiAsset(String name)
	{
		AtlasRegion reg = gui.findRegion(name);
		if(reg == null)
			Log.e("GameHelper", "Game Asset not found: " + name);
		return reg;
	}
	
	private void loadGameAssets()
	{
		gameAssets = new TextureAtlas(Gdx.files.internal("gfx/packs/assets.atlas"));
		for(AtlasRegion region : gameAssets.getRegions())
			region.flip(false, true);
	}
	
	public AtlasRegion getGameAsset(String name)
	{
		AtlasRegion reg = gameAssets.findRegion(name);
		if(reg == null)
			Log.e("GameHelper", "Game Asset not found: " + name);
		return reg;
	}
	
	public TiledTextureRegion getGameAssetTiledTextureRegion(String fileName, int columns, int rows)
	{
		return new TiledTextureRegion(getGameAsset(fileName), columns, rows, true);
	}
	
	private void loadSpellIcons()
	{
		AtlasRegion spellIconAtlas = gui.findRegion("spellIcons");
		TextureRegion[][] spellIconTexture = Utility.splitTextureRegion(spellIconAtlas, 45, 45, true);
		
		playerClassSpells.put("Barbarian", new ArrayList<String>());
		playerClassSpells.put("Dark Priest", new ArrayList<String>());
		playerClassSpells.put("Druid", new ArrayList<String>());
		playerClassSpells.put("Ranger", new ArrayList<String>());
		playerClassSpells.put("Death Knight", new ArrayList<String>());
		playerClassSpells.put("Sorcerer", new ArrayList<String>());
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>  BARBARIAN  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		
		spellIcons.put(SpellCleave.name, spellIconTexture[0][0]);
		playerClassSpells.get("Barbarian").add(SpellCleave.name);
		spellIcons.put(SpellCriticalStrike.name, spellIconTexture[0][1]);
		playerClassSpells.get("Barbarian").add(SpellCriticalStrike.name);
		spellIcons.put(SpellBloodthirst.name, spellIconTexture[0][2]);
		playerClassSpells.get("Barbarian").add(SpellBloodthirst.name);
		spellIcons.put(SpellExtendedBloodthirst.name, spellIconTexture[0][3]);
		playerClassSpells.get("Barbarian").add(SpellExtendedBloodthirst.name);
		spellIcons.put(SpellRage.name, spellIconTexture[0][4]);
		playerClassSpells.get("Barbarian").add(SpellRage.name);
		spellIcons.put(SpellExtendedRage.name, spellIconTexture[0][5]);
		playerClassSpells.get("Barbarian").add(SpellExtendedRage.name);
		spellIcons.put(SpellFrostStrike.name, spellIconTexture[0][6]);
		playerClassSpells.get("Barbarian").add(SpellFrostStrike.name);
		spellIcons.put(SpellHaste.name, spellIconTexture[0][7]);
		playerClassSpells.get("Barbarian").add(SpellHaste.name);
		spellIcons.put(SpellRavage.name, spellIconTexture[0][8]);
		playerClassSpells.get("Barbarian").add(SpellRavage.name);
		spellIcons.put(SpellTerrifyingShout.name, spellIconTexture[0][9]);
		playerClassSpells.get("Barbarian").add(SpellTerrifyingShout.name);
		spellIcons.put(SpellThunderingRoar.name, spellIconTexture[0][10]);
		playerClassSpells.get("Barbarian").add(SpellThunderingRoar.name);
		spellIcons.put(SpellCharge.name, spellIconTexture[0][11]);
		playerClassSpells.get("Barbarian").add(SpellCharge.name);
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>  DARK PRIEST  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		
		spellIcons.put(SpellMassHeal.name, spellIconTexture[1][0]);
		playerClassSpells.get("Dark Priest").add(SpellMassHeal.name);
		spellIcons.put(SpellAbsorb.name, spellIconTexture[1][1]);
		playerClassSpells.get("Dark Priest").add(SpellAbsorb.name);
		spellIcons.put(SpellShadowCurse.name, spellIconTexture[1][2]);
		playerClassSpells.get("Dark Priest").add(SpellShadowCurse.name);
		spellIcons.put(SpellVampiricTouch.name, spellIconTexture[1][3]);
		playerClassSpells.get("Dark Priest").add(SpellVampiricTouch.name);
		spellIcons.put(SpellHellFire.name, spellIconTexture[1][4]);
		playerClassSpells.get("Dark Priest").add(SpellHellFire.name);
		spellIcons.put(SpellEquilibrium.name, spellIconTexture[1][5]);
		playerClassSpells.get("Dark Priest").add(SpellEquilibrium.name);
		spellIcons.put(SpellArcaneExplosions.name, spellIconTexture[1][6]);
		playerClassSpells.get("Dark Priest").add(SpellArcaneExplosions.name);
		spellIcons.put(SpellMassDispel.name, spellIconTexture[1][7]);
		playerClassSpells.get("Dark Priest").add(SpellMassDispel.name);
		spellIcons.put(SpellSilence.name, spellIconTexture[1][8]);
		playerClassSpells.get("Dark Priest").add(SpellSilence.name);
		spellIcons.put(SpellLightwell.name, spellIconTexture[1][9]);
		playerClassSpells.get("Dark Priest").add(SpellLightwell.name);
		spellIcons.put(SpellClutchOfDeath.name, spellIconTexture[1][10]);
		playerClassSpells.get("Dark Priest").add(SpellClutchOfDeath.name);
		spellIcons.put(SpellBondOfDebt.name, spellIconTexture[1][11]);
		playerClassSpells.get("Dark Priest").add(SpellBondOfDebt.name);
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>  DEATH KNIGHT  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		
		spellIcons.put(SpellDeadlyBlow.name, spellIconTexture[2][0]);
		playerClassSpells.get("Death Knight").add(SpellDeadlyBlow.name);
		spellIcons.put(SpellFlameStrike.name, spellIconTexture[2][1]);
		playerClassSpells.get("Death Knight").add(SpellFlameStrike.name);
		spellIcons.put(SpellFleshReformation.name, spellIconTexture[2][2]);
		playerClassSpells.get("Death Knight").add(SpellFleshReformation.name);
		spellIcons.put(SpellInnerBeast.name, spellIconTexture[2][3]);
		playerClassSpells.get("Death Knight").add(SpellInnerBeast.name);
		spellIcons.put(SpellIntimidation.name, spellIconTexture[2][4]);
		playerClassSpells.get("Death Knight").add(SpellIntimidation.name);
		spellIcons.put(SpellMithrilArmor.name, spellIconTexture[2][5]);
		playerClassSpells.get("Death Knight").add(SpellMithrilArmor.name);
		spellIcons.put(SpellShieldBash.name, spellIconTexture[2][6]);
		playerClassSpells.get("Death Knight").add(SpellShieldBash.name);
		spellIcons.put(SpellSpellShield.name, spellIconTexture[2][7]);
		playerClassSpells.get("Death Knight").add(SpellSpellShield.name);
		spellIcons.put(SpellStunningStrike.name, spellIconTexture[2][8]);
		playerClassSpells.get("Death Knight").add(SpellStunningStrike.name);
		spellIcons.put(SpellSunderArmor.name, spellIconTexture[2][9]);
		playerClassSpells.get("Death Knight").add(SpellSunderArmor.name);
		spellIcons.put(SpellTaunt.name, spellIconTexture[2][10]);
		playerClassSpells.get("Death Knight").add(SpellTaunt.name);
		spellIcons.put(SpellBladestorm.name, spellIconTexture[2][11]);
		playerClassSpells.get("Death Knight").add(SpellBladestorm.name);
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>  DRUID  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		
		spellIcons.put(SpellSummonSpirit.name, spellIconTexture[3][0]);
		playerClassSpells.get("Druid").add(SpellSummonSpirit.name);
		spellIcons.put(SpellHealSpirit.name, spellIconTexture[3][1]);
		playerClassSpells.get("Druid").add(SpellHealSpirit.name);
		spellIcons.put(SpellEnrageSpirit.name, spellIconTexture[3][2]);
		playerClassSpells.get("Druid").add(SpellEnrageSpirit.name);
		spellIcons.put(SpellBlastSpirit.name, spellIconTexture[3][3]);
		playerClassSpells.get("Druid").add(SpellBlastSpirit.name);
		spellIcons.put(SpellEarthquake.name, spellIconTexture[3][4]);
		playerClassSpells.get("Druid").add(SpellEarthquake.name);
		spellIcons.put(SpellEntangle.name, spellIconTexture[3][5]);
		playerClassSpells.get("Druid").add(SpellEntangle.name);
		spellIcons.put(SpellIceDart.name, spellIconTexture[3][6]);
		playerClassSpells.get("Druid").add(SpellIceDart.name);
		spellIcons.put(SpellFlameBreath.name, spellIconTexture[3][7]);
		playerClassSpells.get("Druid").add(SpellFlameBreath.name);
		spellIcons.put(SpellMeditationAura.name, spellIconTexture[3][8]);
		playerClassSpells.get("Druid").add(SpellMeditationAura.name);
		spellIcons.put(SpellDauntingGhost.name, spellIconTexture[3][9]);
		playerClassSpells.get("Druid").add(SpellDauntingGhost.name);
		spellIcons.put(SpellOvercharge.name, spellIconTexture[3][10]);
		playerClassSpells.get("Druid").add(SpellOvercharge.name);
		spellIcons.put(SpellThornArmor.name, spellIconTexture[3][1]);
		playerClassSpells.get("Druid").add(SpellThornArmor.name);
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>  RANGER  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		
		spellIcons.put(SpellExplosiveShot.name, spellIconTexture[4][0]);
		playerClassSpells.get("Ranger").add(SpellExplosiveShot.name);
		spellIcons.put(SpellLightningTrap.name, spellIconTexture[4][1]);
		playerClassSpells.get("Ranger").add(SpellLightningTrap.name);
		spellIcons.put(SpellColdTrap.name, spellIconTexture[4][2]);
		playerClassSpells.get("Ranger").add(SpellColdTrap.name);
		spellIcons.put(SpellFireTrap.name, spellIconTexture[4][3]);
		playerClassSpells.get("Ranger").add(SpellFireTrap.name);
		spellIcons.put(SpellSpikeTrap.name, spellIconTexture[4][4]);
		playerClassSpells.get("Ranger").add(SpellSpikeTrap.name);
		spellIcons.put(SpellEvasion.name, spellIconTexture[4][5]);
		playerClassSpells.get("Ranger").add(SpellEvasion.name);
		spellIcons.put(SpellMultiShot.name, spellIconTexture[4][6]);
		playerClassSpells.get("Ranger").add(SpellMultiShot.name);
		spellIcons.put(SpellDeadlyShot.name, spellIconTexture[4][7]);
		playerClassSpells.get("Ranger").add(SpellDeadlyShot.name);
		spellIcons.put(SpellBarrage.name, spellIconTexture[4][8]);
		playerClassSpells.get("Ranger").add(SpellBarrage.name);
		spellIcons.put(SpellUnleashTheBeasts.name, spellIconTexture[4][9]);
		playerClassSpells.get("Ranger").add(SpellUnleashTheBeasts.name);
		spellIcons.put(SpellPoisonousArrow.name, spellIconTexture[4][10]);
		playerClassSpells.get("Ranger").add(SpellPoisonousArrow.name);
		spellIcons.put(SpellPathfinder.name, spellIconTexture[4][11]);
		playerClassSpells.get("Ranger").add(SpellPathfinder.name);
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>  Sorcerer  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		
		spellIcons.put(SpellBlink.name, spellIconTexture[5][0]);
		playerClassSpells.get("Sorcerer").add(SpellBlink.name);
		spellIcons.put(SpellFireball.name, spellIconTexture[5][1]);
		playerClassSpells.get("Sorcerer").add(SpellFireball.name);
		spellIcons.put(SpellFrostNova.name, spellIconTexture[5][2]);
		playerClassSpells.get("Sorcerer").add(SpellFrostNova.name);
		spellIcons.put(SpellMeteorStrike.name, spellIconTexture[5][3]);
		playerClassSpells.get("Sorcerer").add(SpellMeteorStrike.name);
		spellIcons.put(SpellFrostBomb.name, spellIconTexture[5][4]);
		playerClassSpells.get("Sorcerer").add(SpellFrostBomb.name);
		spellIcons.put(SpellTimeWarp.name, spellIconTexture[5][5]);
		playerClassSpells.get("Sorcerer").add(SpellTimeWarp.name);
		spellIcons.put(SpellLivingBomb.name, spellIconTexture[5][6]);
		playerClassSpells.get("Sorcerer").add(SpellLivingBomb.name);
		spellIcons.put(SpellMoltenShield.name, spellIconTexture[5][7]);
		playerClassSpells.get("Sorcerer").add(SpellMoltenShield.name);
		spellIcons.put(SpellFirestorm.name, spellIconTexture[5][8]);
		playerClassSpells.get("Sorcerer").add(SpellFirestorm.name);
		spellIcons.put(SpellManaBurn.name, spellIconTexture[5][9]);
		playerClassSpells.get("Sorcerer").add(SpellManaBurn.name);
		spellIcons.put(SpellThunderbolt.name, spellIconTexture[5][10]);
		playerClassSpells.get("Sorcerer").add(SpellThunderbolt.name);
		spellIcons.put(SpellMagicTrick.name, spellIconTexture[5][11]);
		playerClassSpells.get("Sorcerer").add(SpellMagicTrick.name);
	}
	
	public TextureRegion getItemIcon(String name)
	{
		return items.findRegion(name);
	}
	
	private void preloadFonts()
	{
		fontCategoryFontNames.put(FontCategory.MainMenu, "DroidSans");
		fontCategoryFontNames.put(FontCategory.InGame, "DroidSans");
		fontCategoryFontNames.put(FontCategory.Level, "DroidSans");
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/" + fontCategoryFontNames.get(FontCategory.MainMenu) + ".ttf"));
		
		loadMainFont(FontCategory.MainMenu, 36, generator);
		loadMainFont(FontCategory.MainMenu, 28, generator);
		loadMainFont(FontCategory.MainMenu, 20, generator);
		
		generator.dispose();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("font/" + fontCategoryFontNames.get(FontCategory.InGame) + ".ttf"));
		
		loadMainFont(FontCategory.InGame, 24, generator);
		loadMainFont(FontCategory.InGame, 22, generator);
		loadMainFont(FontCategory.InGame, 20, generator);
		loadMainFont(FontCategory.InGame, 18, generator);
		loadMainFont(FontCategory.InGame, 17, generator);
		loadMainFont(FontCategory.InGame, 16, generator);
		loadMainFont(FontCategory.InGame, 15, generator);
		loadMainFont(FontCategory.InGame, 13, generator);
		loadMainFont(FontCategory.InGame, 12, generator);
		
		loadMainFont(FontCategory.Level, 20, generator);
		generator.dispose();
	}
	
	private void loadMainFont(FontCategory cat, int size, FreeTypeFontGenerator generator)
	{
		if(!mainFonts.containsKey(cat))
			mainFonts.put(cat, new HashMap<Integer, BitmapFont>());
		
		for(Entry<FontCategory, HashMap<Integer, BitmapFont>> e : mainFonts.entrySet())
			if(e.getKey() != cat && fontCategoryFontNames.get(e.getKey()).equals(fontCategoryFontNames.get(cat)) && mainFonts.get(e.getKey()).containsKey(size))
			{
				mainFonts.get(cat).put(size, mainFonts.get(e.getKey()).get(size));
				return;
			}
		
		FreeTypeFontParameter params = new FreeTypeFontParameter();
		params.flip = true;
		params.magFilter = TextureFilter.Linear;
		params.minFilter = TextureFilter.Linear;
		params.size = size;
		params.characters = "/():;.-+?!\'1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		BitmapFont font = generator.generateFont(params);
		mainFonts.get(cat).put(size, font);
	}
	
	public BitmapFont getMainFont(FontCategory cat, int size)
	{
		if(!mainFonts.containsKey(cat) || !mainFonts.get(cat).containsKey(size))
		{
			Log.w("GameHelper", "has to load font at runtime: " + size);
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/" + fontCategoryFontNames.get(cat) + ".ttf"));
			loadMainFont(cat, size, generator);
			generator.dispose();
		}
		return mainFonts.get(cat).get(size);
	}
	
//	>>>>>>>>>>>>>>>>>>>>>>>>>>> [^] PRE LOADS [^] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	private LoadMapScene loadMapScene;
	
	public LoadMapScene getLoadMapScene()
	{
		return loadMapScene;
	}
	
	public BulletAsset getBulletAsset(String asset)
	{
		return bulletAssets.get(asset);
	}
	
	public EnemyModel getEnemyModel(String name)
	{
		return enemyModels.get(name);
	}
	
	public int getPricePerAttributePoint(String ATTR)
	{
		if(attributePointPrice.containsKey(ATTR.toUpperCase()))
			return attributePointPrice.get(ATTR.toUpperCase());
		else
			return 0;
	}
	
	public HashMap<String, String> getAttributeNames()
	{
		return attributeNames;
	}
	
	public Random getRandom()
	{
		return random;
	}
	
	private String ipInfo;
	
	public void setIPinfo(String info)
	{
		ipInfo = info;
	}
	
	public String getIPinfo()
	{
		return ipInfo;
	}
	
	public TextureRegion getSpellIconTexture(String spell)
	{
		if(spellIcons.containsKey(spell))
			return spellIcons.get(spell);
		else
			return spellIcons.get(SpellCleave.name);
	}
	
	private long lastBtVisibleUpdate = -1;
	
	public void setBluetoothVisibleNow()
	{
		lastBtVisibleUpdate = System.currentTimeMillis();
	}
	
	public boolean isBluetoothVisible()
	{
		return (lastBtVisibleUpdate == -1) ? false : (System.currentTimeMillis() - lastBtVisibleUpdate <= EventResult.DISCOVERABLE_DURATION * 1000);
	}
	
	public float getScaleFactorX()
	{
		return factX;
	}
	
	public float getScaleFactorY()
	{
		return factY;
	}
	
	public EffectManager getEffectManager()
	{
		return effectManager;
	}
	
	public String getStoryQuest(int i)
	{
		return storyQuests.get(i);
	}
	
	public String getRandomDailyQuest()
	{
		return dailyQuests.get(random.nextInt(dailyQuests.size()));
	}
	
	public HashMap<Integer, List<String>> getArenaRounds(String mapName)
	{
		return arenas.get(mapName);
	}
	
	private final DateTime dd = new DateTime("2000-1-1T00:01:00.000-00:01");
	
	public DateTime getDefaultDate()
	{
		return dd;
	}
	
	public String getPlayerTitle(int fame)
	{
		int highestPossible = 0;
		for(Entry<Integer, String> e : playerTitles.entrySet())
		{
			if(e.getKey() <= fame && e.getKey() >= highestPossible)
				highestPossible = e.getKey();
		}
		return playerTitles.get(highestPossible);
	}
	
	public Integer[] getRanks(int fame)
	{
		Integer[] ranks = new Integer[2];
		for(int i = 0; i < playerTitleRequisites.size(); i++)
			if(playerTitleRequisites.get(i) > fame)
			{
				ranks[0] = i > 0 ? playerTitleRequisites.get(i - 1) : 0;
				ranks[1] = playerTitleRequisites.get(i);
				return ranks;
			}
		return null;
	}
	
	public HashMap<String, List<String>> getPlayerClassSpells()
	{
		return playerClassSpells;
	}
	
	protected static PlatformResolver platformResolver = null;
	
	public static PlatformResolver getPlatformResolver()
	{
		return platformResolver;
	}
	
	public OnScreenInfo getOnScreenInfo(int id)
	{
		return onScreenInfos.get(id);
	}
	
	public int getFameForHall(int hall)
	{
		return fameRequiredForHall.get(hall);
	}
	
	public String getDialogue(int dialogueId)
	{
		return dialogues.containsKey(dialogueId) ? dialogues.get(dialogueId) : "null";
	}
	
	public static void setPlatformResolver(PlatformResolver pr)
	{
		platformResolver = pr;
	}
	
	public void unloadTextures()
	{
		for(Entry<String, TiledTextureRegion> txt : entityAssets.entrySet())
		{
			txt.getValue().dispose();
		}
		entityAssets.clear();
		gameAssets.dispose();
		bullets.dispose();
		gui.dispose();
		items.dispose();
		List<Integer> unloadedFontSizes = new ArrayList<Integer>();
		for(HashMap<Integer, BitmapFont> fonts : mainFonts.values())
			for(Entry<Integer, BitmapFont> e : fonts.entrySet())
				if(!unloadedFontSizes.contains(e.getKey()))
				{
					e.getValue().dispose();
					unloadedFontSizes.add(e.getKey());
				}
		unloadedFontSizes.clear();
	}
}
