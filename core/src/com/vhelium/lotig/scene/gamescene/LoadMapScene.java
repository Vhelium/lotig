package com.vhelium.lotig.scene.gamescene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.BoundOrthographicCamera;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.AbstractScene;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.GameScene.GameState;
import com.vhelium.lotig.scene.gamescene.client.Client;
import com.vhelium.lotig.scene.gamescene.client.ILoadMapCallback;

public class LoadMapScene extends AbstractScene
{
	boolean isStartingServer;
	Label text;
	BitmapFont font;
	ILoadMapCallback listener;
	final float loadingProceedDelay = 0.5f;
	float loadingProceedDelayLeft = 0f;
	boolean listenerInformed = false;
	
	public LoadMapScene(Main activity, SceneManager sceneManager, boolean isStartingServer)
	{
		super(activity, sceneManager);
		this.isStartingServer = isStartingServer;
	}
	
	@Override
	public void loadResources()
	{
		BoundOrthographicCamera camera = new BoundOrthographicCamera(SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		camera.setToOrtho(true, SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT);
		stage = new Stage(new StretchViewport(SceneManager.CAMERA_WIDTH, SceneManager.CAMERA_HEIGHT, camera), batch);
		this.camera = camera;
		
		font = new BitmapFont(true);
		text = new Label("null", new LabelStyle(font, Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		layoutLabel();
		stage.addActor(text);
		isLoaded = true;
	}
	
	private GameState prevGameState;
	
	@Override
	public void update(float delta)
	{
		if(!listenerInformed && loadingProceedDelayLeft > 0)
		{
			loadingProceedDelayLeft -= delta;
			if(loadingProceedDelayLeft <= 0)
			{
				loadingProceedDelayLeft = 0f;
				listenerInformed = true;
				listener.loadMapExecuted();
			}
		}
		
		if(prevGameState != sceneManager.getGameScene().getGameState())
		{
			switch(sceneManager.getGameScene().getGameState())
			{
				case CONNECTING_TO_SERVER:
					text.setText("Connecting to server..");
					break;
				
				case SEND_CLIENT_INFO:
					text.setText("Sending client info..");
					break;
				
				case LOADING_MAP:
					text.setText("Loading map..");
					break;
				
				case STARTING_SERVER:
					text.setText("Starting server..");
					break;
				
				case VERSION_CHECK:
					text.setText("Checking versions..");
					break;
				
				default:
					if(isStartingServer)//if map isnt loading yet and client is waiting for server to start:
					{
						text.setText("Starting server..");
					}
					else
					{
						text.setText("Initializing..");
					}
					break;
			}
			layoutLabel();
		}
		prevGameState = sceneManager.getGameScene().getGameState();
	}
	
	public void executeLoadMap(final Client client, final String mapName)
	{
		text.setText("Loading..");
		layoutLabel();
		loadingProceedDelayLeft = 0;
		this.listener = client;
		listenerInformed = false;
//		super.render(0);
		
		client.getLevel().loadMapInitiated();
		client.getLevel().loadMap(mapName);
		loadingProceedDelayLeft = loadingProceedDelay;
	}
	
	public void executeWaitUntilSceneLoaded(final ILoadMapCallback sender)
	{
		text.setText("Loading..");
		layoutLabel();
		this.listener = sender;
		listenerInformed = false;
		super.render(0);
		loadingProceedDelayLeft = loadingProceedDelay / 2;
	}
	
	private void layoutLabel()
	{
		text.pack();
		text.setX(20);
		text.setY(SceneManager.CAMERA_HEIGHT - text.getHeight() - 20);
	}
	
	@Override
	public void show()
	{
		super.show();
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
