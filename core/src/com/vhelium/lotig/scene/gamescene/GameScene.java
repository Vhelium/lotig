package com.vhelium.lotig.scene.gamescene;

import com.badlogic.gdx.Input.Keys;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.AbstractScene;
import com.vhelium.lotig.scene.MainMenuScene;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.client.Client;
import com.vhelium.lotig.scene.gamescene.client.ILoadMapCallback;
import com.vhelium.lotig.scene.gamescene.server.Server;

public class GameScene extends AbstractScene implements ILoadMapCallback
{
	public enum ConnectionType
	{
		Singleplayer, Bluetooth, Wlan
	}
	
	public enum GameState
	{
		STOPPED, CONNECTING_TO_SERVER, SEND_CLIENT_INFO, LOADING_MAP, RUNNING, STARTING_SERVER, VERSION_CHECK
	}
	
	private Client client;
	private Server server;
	private SavedGame savedGame;
	private GameState gameState = GameState.STOPPED;
	private boolean isHost = false;
	ConnectionType connectionType;
	int difficulty;
	
	public GameScene(Main activity, SceneManager sceneManager)
	{
		super(activity, sceneManager);
		requiresInput = true;
	}
	
	public void joinGame(ConnectionType connectionType, SavedGame savedGame)
	{
		isHost = false;
		this.connectionType = connectionType;
		this.savedGame = savedGame;
		
		GameHelper.getInstance().getLoadMapScene().executeWaitUntilSceneLoaded(this);
	}
	
	public void hostGame(ConnectionType connectionType, SavedGame savedGame, int difficulty)
	{
		isHost = true;
		this.connectionType = connectionType;
		this.savedGame = savedGame;
		this.difficulty = difficulty;
		
		GameHelper.getInstance().getLoadMapScene().executeWaitUntilSceneLoaded(this);
	}
	
	@Override
	public void loadResources()
	{
		super.loadResources();
		
		GlobalSettings.load();
		GameHelper.forceNewInstance();
		GameHelper.getInstance().load(activity, sceneManager);
		
		sceneManager.loadScene(GameHelper.getInstance().getLoadMapScene());
		
		client = new Client(activity, sceneManager);
		
		server = new Server(activity, sceneManager);
		
		isLoaded = true;
	}
	
	@Override
	public void loadMapExecuted()
	{
		boolean success = true;
		if(!isHost)
			success = client.start(connectionType, null, savedGame);
		else
		{
			boolean tutorialSeen = GlobalSettings.getInstance().getDataValue("tutorialseen") != null && GlobalSettings.getInstance().getDataValue("tutorialseen").equals("true");
			success = server.start(connectionType, difficulty, client.iccb, tutorialSeen);
			try
			{
				Thread.sleep(300);
			}
			catch (InterruptedException e)
			{
				Log.e("GameScene.out", "failed to sleep");
			}
			if(success)
				success = client.start(connectionType, server.iccb, savedGame);
		}
		if(!success)
		{
			setGameState(GameState.STOPPED);
			dispose();
			sceneManager.setScene(new MainMenuScene(activity, sceneManager, -2));
		}
	}
	
	@Override
	public void update(float delta)
	{
		if(client != null)
			client.update(delta);
		if(server != null)
			server.update(delta);
	}
	
	@Override
	public void show()
	{
		super.show();
		client.getLevel().setHUD(client.getLevel().hud);
	}
	
	@Override
	public void initCamera()
	{
		sceneManager.getCamera().setChaseActor(client.getLevel().getPlayer());
		sceneManager.getCamera().setBounds(0, 0, client.getLevel().getMapWidth(), client.getLevel().getMapHeight());
		sceneManager.getCamera().setBoundsEnabled(true);
		sceneManager.getCamera().update();
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.BACK || keycode == Keys.ESCAPE)
		{
			client.getLevel().onBackKeyDown();
			return true;
		}
		else if(keycode == Keys.H)
		{
			if(client.getLevel().hud.isActive)
				client.getLevel().hideHUD();
			else
				client.getLevel().showHUD();
			return true;
		}
		return false;
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
	
	public GameState getGameState()
	{
		return gameState;
	}
	
	public ConnectionType getConnectionType()
	{
		return connectionType;
	}
	
	public boolean isHost()
	{
		return isHost;
	}
	
	public void setGameState(GameState state)
	{
		gameState = state;
	}
	
	@Override
	public void dispose()
	{
		client.dispose();
		server.dispose();
	}
}
