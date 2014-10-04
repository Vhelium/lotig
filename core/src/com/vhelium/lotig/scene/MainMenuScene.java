package com.vhelium.lotig.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GameScene.ConnectionType;
import com.vhelium.lotig.scene.gamescene.SavedGame;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.ISettingsCallback;

public class MainMenuScene extends AbstractScene implements IOnMenuItemClickListener, ISettingsCallback
{
	private enum SelectedModus
	{
		joinBT, hostBT, joinWL, hostWL, sp, none
	}
	
	private static final int MENU_OPTIONS = 1;
	private static final int MENU_QUIT = 2;
	
	private static final int MENU_SP = 3;
	private static final int MENU_MP = 4;
	
	private static final int MENU_MP_JOIN = 5;
	private static final int MENU_MP_HOST = 6;
	
	private static final int MENU_MP_JOIN_BT = 7;
	private static final int MENU_MP_JOIN_WLAN = 8;
	
	private static final int MENU_MP_HOST_BT = 9;
	private static final int MENU_MP_HOST_WLAN = 10;
	
	public static final int MENU_CREATE_NEW_PLAYER = 11;
	public static final int MENU_LOAD_PLAYER = 12;
	public static final int MENU_DELETE_PLAYER = 13;
	
	public static final int MENU_NEW_PLAYER_CREATED = 14;
	public static final int MENU_DIFFICULTY = 15;
	
	private MenuScene menuSceneMain;
	private MenuScene menuSceneMultiplayer;
	private MenuScene menuSceneMpJoin;
	private MenuScene menuSceneMpHost;
	private SavedGamesScene menuSceneSaves;
	private CharacterCreationScene menuCharacterCreation;
	private MenuScene menuSceneDifficulty;
	private OptionsScene menuSceneOptions;
	
	private SelectedModus selectedModus = SelectedModus.none;
	private SavedGame selectedSavedGame;
	
	private final HashMap<Integer, SavedGame> savedGames;
	
	private final List<String> header = new ArrayList<String>();
	
	int exitCode = 0;
	
	private Label txtHeader;
	private BitmapFont menuFont;
	
	private MenuScene currentScene;
	
	public MainMenuScene(Main activity, SceneManager sceneManager)
	{
		super(activity, sceneManager);
		requiresInput = true;
		savedGames = new HashMap<Integer, SavedGame>();
	}
	
	public MainMenuScene(Main activity, SceneManager sceneManager, int code)
	{
		this(activity, sceneManager);
		exitCode = code;
	}
	
	@Override
	public void loadResources()
	{
		super.loadResources();
		for(int i = 0; i < SavedGame.maxSavedGameFiles; i++)
		{
			savedGames.put(i, SavedGame.load(i));
		}
		
		menuFont = GameHelper.getInstance().getMainFont(FontCategory.MainMenu, 36);
		
		txtHeader = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.MainMenu, 20), Color.WHITE));
		txtHeader.setPosition(0, 28);
		this.stage.addActor(txtHeader);
		
		createMenuScenes();
		buildSavedGamesMenu();
		
		setMenuScene(menuSceneMain);
		
		isLoaded = true;
	}
	
	private void buildSavedGamesMenu()
	{
		menuSceneSaves = new SavedGamesScene(this);
		for(int i = 0; i < SavedGame.maxSavedGameFiles; i++)
		{
			if(savedGames.get(i) == null)
			{
				menuSceneSaves.addMenuItem(MENU_CREATE_NEW_PLAYER, i, menuFont, "empty", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
			}
			else
			{
				menuSceneSaves.addMenuItem(MENU_LOAD_PLAYER, i, menuFont, "Save " + i + ": " + savedGames.get(i).getDataValue("playerclass"), new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
			}
		}
		menuSceneSaves.addMenuItem(MENU_DELETE_PLAYER, menuFont, "delete Character", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneSaves.build();
	}
	
	protected void createMenuScenes()
	{
		menuSceneOptions = new OptionsScene(activity, sceneManager);
		menuSceneOptions.loadResources();
		menuSceneOptions.setListener(this);
		
		//MENU SCENE MAIN
		menuSceneMain = new MenuScene(this);
		menuSceneMain.addMenuItem(MENU_SP, menuFont, "Singleplayer", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMain.addMenuItem(MENU_MP, menuFont, "Multiplayer", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMain.addMenuItem(MENU_OPTIONS, menuFont, "Options", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMain.addMenuItem(MENU_QUIT, menuFont, "Quit", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMain.build();
		
		//MENU SCENE MULTIPLAYER
		menuSceneMultiplayer = new MenuScene(this);
		menuSceneMultiplayer.addMenuItem(MENU_MP_JOIN, menuFont, "Join game", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMultiplayer.addMenuItem(MENU_MP_HOST, menuFont, "Host game", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMultiplayer.build();
		
		//MENU SCENE MULTIPLAYER JOIN:
		menuSceneMpJoin = new MenuScene(this);
		menuSceneMpJoin.addMenuItem(MENU_MP_JOIN_BT, menuFont, "Bluetooth", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMpJoin.addMenuItem(MENU_MP_JOIN_WLAN, menuFont, "Wlan", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMpJoin.build();
		
		//MENU SCENE MULTIPLAYER HOST:
		menuSceneMpHost = new MenuScene(this);
		menuSceneMpHost.addMenuItem(MENU_MP_HOST_BT, menuFont, "Bluetooth", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMpHost.addMenuItem(MENU_MP_HOST_WLAN, menuFont, "Wlan", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuSceneMpHost.build();
		
		//MENU SCENE CHARACTER CREATION:
		menuCharacterCreation = new CharacterCreationScene(this);
		menuCharacterCreation.addMenuItem(MENU_NEW_PLAYER_CREATED, 0, menuFont, "Barbarian", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuCharacterCreation.addMenuItem(MENU_NEW_PLAYER_CREATED, 1, menuFont, "Dark Priest", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuCharacterCreation.addMenuItem(MENU_NEW_PLAYER_CREATED, 2, menuFont, "Death Knight", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuCharacterCreation.addMenuItem(MENU_NEW_PLAYER_CREATED, 3, menuFont, "Druid", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuCharacterCreation.addMenuItem(MENU_NEW_PLAYER_CREATED, 4, menuFont, "Ranger", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuCharacterCreation.addMenuItem(MENU_NEW_PLAYER_CREATED, 5, menuFont, "Sorcerer", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		menuCharacterCreation.build();
	}
	
	private void setMenuSceneDifficulty(SavedGame savedGame)
	{
		//MENU SCENE GAME DIFFICULTY
		menuSceneDifficulty = new MenuScene(this);
		menuSceneDifficulty.addMenuItem(MENU_DIFFICULTY, 0, menuFont, "Normal", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		
		if(selectedSavedGame.getDataValue("diff0") != null && selectedSavedGame.getDataValue("diff0").equalsIgnoreCase("finished"))
		{
			menuSceneDifficulty.addMenuItem(MENU_DIFFICULTY, 1, menuFont, "Nightmare", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		}
		else
		{
			menuSceneDifficulty.addMenuItem(MENU_DIFFICULTY, -1, menuFont, "Nightmare", new Color(0.2f, 0.2f, 0.2f, 1), new Color(0.2f, 0.2f, 0.2f, 1));
		}
		
		if(selectedSavedGame.getDataValue("diff1") != null && selectedSavedGame.getDataValue("diff1").equalsIgnoreCase("finished"))
		{
			menuSceneDifficulty.addMenuItem(MENU_DIFFICULTY, 2, menuFont, "Hell", new Color(1, 1, 1, 1), new Color(1, 0, 0, 1));
		}
		else
		{
			menuSceneDifficulty.addMenuItem(MENU_DIFFICULTY, -1, menuFont, "Hell", new Color(0.2f, 0.2f, 0.2f, 1), new Color(0.2f, 0.2f, 0.2f, 1));
		}
		menuSceneDifficulty.build();
		
		setMenuScene(menuSceneDifficulty);
	}
	
	@Override
	public boolean onMenuItemClicked(MenuItem menuItem)
	{
		SoundManager.playSound(SoundFile.menu_selected);
		switch(menuItem.getID())
		{
			case MENU_SP:
				selectedModus = SelectedModus.sp;
				header.add("Singleplayer");
				updateHeaderText();
				setMenuScene(menuSceneSaves);
				return true;
				
			case MENU_MP:
				header.add("Multiplayer");
				updateHeaderText();
				setMenuScene(menuSceneMultiplayer);
				return true;
				
			case MENU_OPTIONS:
				header.add("Options");
				updateHeaderText();
				sceneManager.setScene(menuSceneOptions);
				return true;
				
			case MENU_QUIT:
				GameHelper.getPlatformResolver().kill();
				return true;
				
			case MENU_MP_JOIN:
				header.add("Join");
				updateHeaderText();
				setMenuScene(menuSceneMpJoin);
				return true;
				
			case MENU_MP_HOST:
				header.add("Host");
				updateHeaderText();
				setMenuScene(menuSceneMpHost);
				return true;
				
			case MENU_MP_JOIN_BT:
				header.add("Bluetooth");
				updateHeaderText();
				selectedModus = SelectedModus.joinBT;
				setMenuScene(menuSceneSaves);
				return true;
				
			case MENU_MP_JOIN_WLAN:
				header.add("Wlan");
				updateHeaderText();
				selectedModus = SelectedModus.joinWL;
				setMenuScene(menuSceneSaves);
				return true;
				
			case MENU_MP_HOST_BT:
				header.add("Bluetooth");
				updateHeaderText();
				selectedModus = SelectedModus.hostBT;
				setMenuScene(menuSceneSaves);
				return true;
				
			case MENU_MP_HOST_WLAN:
				header.add("Wlan");
				updateHeaderText();
				selectedModus = SelectedModus.hostWL;
				setMenuScene(menuSceneSaves);
				return true;
				
			case MENU_CREATE_NEW_PLAYER:
				if(!menuSceneSaves.isDeleteSaveGame)
				{
					menuCharacterCreation.saveGameNr = menuItem.getTag();
					setMenuScene(menuCharacterCreation);
					return true;
				}
				else
				{
					menuSceneSaves.abordDeleting();
					return true;
				}
				
			case MENU_NEW_PLAYER_CREATED:
				if(menuItem.getTag() == -1)
					return true;
				SavedGame savedGame = new SavedGame(menuCharacterCreation.saveGameNr);
				ConcurrentHashMap<String, String> datas = new ConcurrentHashMap<String, String>();
				switch(menuItem.getTag())
				{
					case 0:
						datas.put("playerclass", "Barbarian");
						break;
					case 1:
						datas.put("playerclass", "Dark Priest");
						break;
					case 2:
						datas.put("playerclass", "Death Knight");
						break;
					case 3:
						datas.put("playerclass", "Druid");
						break;
					case 4:
						datas.put("playerclass", "Ranger");
						break;
					case 5:
						datas.put("playerclass", "Sorcerer");
						break;
				}
				savedGame.save(datas);
				selectedSavedGame = savedGame;
				
				header.add(selectedSavedGame.getDataValue("playerclass"));
				updateHeaderText();
				
				if(selectedModus == SelectedModus.joinBT || selectedModus == SelectedModus.joinWL)
					onMenuItemClicked(MenuItem.getFakeMenuItem(MENU_DIFFICULTY));
				else
					setMenuSceneDifficulty(savedGame);
				return true;
				
			case MENU_LOAD_PLAYER:
				if(menuItem.getTag() == -1)
					return true;
				else if(!menuSceneSaves.isDeleteSaveGame)
				{
					selectedSavedGame = savedGames.get(menuItem.getTag());
					header.add(selectedSavedGame.getDataValue("playerclass"));
					updateHeaderText();
					if(selectedModus == SelectedModus.joinBT || selectedModus == SelectedModus.joinWL)
						onMenuItemClicked(MenuItem.getFakeMenuItem(MENU_DIFFICULTY));
					else
						setMenuSceneDifficulty(selectedSavedGame);
					return true;
				}
				else if(menuItem.getTag() < SavedGame.maxSavedGameFiles)
				{
					SavedGame.delete(menuItem.getTag());
					savedGames.put(menuItem.getTag(), null);
					buildSavedGamesMenu();
					setMenuScene(menuSceneSaves);
					return true;
				}
				else
				{
					menuSceneSaves.abordDeleting();
					return true;
				}
				
			case MENU_DIFFICULTY:
				if(menuItem.getTag() != -1)
				{
					sceneManager.setScene(GameHelper.getInstance().getLoadMapScene());
					switch(selectedModus)
					{
						case joinBT:
							sceneManager.getGameScene().joinGame(ConnectionType.Bluetooth, selectedSavedGame);
							return true;
							
						case joinWL:
							sceneManager.getGameScene().joinGame(ConnectionType.Wlan, selectedSavedGame);
							return true;
							
						case hostBT:
							sceneManager.getGameScene().hostGame(ConnectionType.Bluetooth, selectedSavedGame, menuItem.getTag());
							return true;
							
						case hostWL:
							sceneManager.getGameScene().hostGame(ConnectionType.Wlan, selectedSavedGame, menuItem.getTag());
							return true;
							
						case sp:
							sceneManager.getGameScene().hostGame(ConnectionType.Singleplayer, selectedSavedGame, menuItem.getTag());
							return true;
							
						default:
							return false;
					}
				}
				else
					return true;
				
			case MENU_DELETE_PLAYER:
				if(!menuSceneSaves.isDeleteSaveGame)
					menuSceneSaves.deleteSaveGameActivated();
				else
					menuSceneSaves.abordDeleting();
				return true;
				
			default:
				return false;
		}
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.BACK || keycode == Keys.ESCAPE)
		{
			SoundManager.playSound(SoundFile.menu_selected);
			if(currentScene.equals(menuSceneMain))
			{
				//exit
				return true;
			}
			else if(currentScene.equals(menuSceneMultiplayer) || currentScene.equals(menuSceneOptions))
			{
				header.remove(header.size() - 1);
				updateHeaderText();
				setMenuScene(menuSceneMain);
				return true;
			}
			else if(currentScene.equals(menuSceneMpJoin) || currentScene.equals(menuSceneMpHost))
			{
				header.remove(header.size() - 1);
				updateHeaderText();
				setMenuScene(menuSceneMultiplayer);
				return true;
			}
			else if(currentScene.equals(menuSceneSaves) || currentScene.equals(menuCharacterCreation))
			{
				header.remove(header.size() - 1);
				updateHeaderText();
				if(selectedModus == SelectedModus.joinBT || selectedModus == SelectedModus.joinWL)
				{
					setMenuScene(menuSceneMpJoin);
				}
				else if(selectedModus == SelectedModus.hostBT || selectedModus == SelectedModus.hostWL)
				{
					setMenuScene(menuSceneMpHost);
				}
				else
				{
					setMenuScene(menuSceneMain);
				}
				return true;
			}
			else if(currentScene.equals(menuSceneDifficulty))
			{
				header.remove(header.size() - 1);
				updateHeaderText();
				setMenuScene(menuSceneSaves);
				return true;
			}
			else
			{
				GameHelper.getPlatformResolver().kill();
				return true;
			}
		}
		
		return false;
	}
	
	private void setMenuScene(MenuScene menuScene)
	{
		stage.clear();
		menuScene.reset();
		currentScene = menuScene;
		stage.addActor(txtHeader);
		stage.addActor(menuScene);
	}
	
	private void updateHeaderText()
	{
		String h = "";
		for(String s : header)
			h += s + " - ";
		if(!h.equals(""))
			h = h.substring(0, h.length() - 3);
		txtHeader.setText(h);
		txtHeader.pack();
		
		txtHeader.setX(SceneManager.CAMERA_WIDTH / 2 - txtHeader.getWidth() / 2);
	}
	
	@Override
	public void onSettingChanged(String name, boolean value)
	{
		if(name.equals("Music"))
			SoundManager.setHasMusic(value);
		else if(name.equals("Sound"))
			SoundManager.setHasSound(value);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	@Override
	public void render(float delta)
	{
		super.render(delta);
	}
	
	@Override
	public void show()
	{
		super.show();
		SoundManager.switchMusic("menu");
	}
	
	@Override
	public void hide()
	{
		
	}
	
	@Override
	public void pause()
	{
		
	}
	
	@Override
	public void resume()
	{
		
	}
	
	@Override
	public void dispose()
	{
		
	}
}
