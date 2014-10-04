package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GlobalSettings;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.client.ISettingsCallback;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GameHUD_Settings extends GameHUDMenu
{
	List<ISettingsCallback> listeners = new ArrayList<ISettingsCallback>();
	Main activity;
	Sprite spriteBackground;
	TextureRegion textureRegionCmdActBt;
	
	private final HashMap<String, Boolean> settings = new HashMap<String, Boolean>();
	private final HashMap<String, TextButton> buttons = new HashMap<String, TextButton>();
	
	public GameHUD_Settings(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		for(Entry<String, TextButton> button : buttons.entrySet())
		{
			if(GlobalSettings.getInstance().getDataValue(button.getKey()) != null)
			{
				Boolean v = Boolean.parseBoolean(GlobalSettings.getInstance().getDataValue(button.getKey()));
				settings.put(button.getKey(), v);
				button.getValue().setText(v == true ? "Yes" : "No");
			}
			else
			{
				settings.put(button.getKey(), false);
				button.getValue().setText("No");
			}
		}
	}
	
	@Override
	public void loadResources(final Main activity)
	{
		this.activity = activity;
		final TextureRegion textureRegionBackground = GameHelper.$.getGuiAsset("backgroundSettings");
		textureRegionCmdActBt = GameHelper.$.getGuiAsset("cmdLoot");
		
		spriteBackground = new Sprite(SceneManager.CAMERA_WIDTH / 2 - textureRegionBackground.getRegionWidth() / 2, SceneManager.CAMERA_HEIGHT / 2 - textureRegionBackground.getRegionHeight() / 2, 300, 300, textureRegionBackground);
		
		loadButton("Music");
		loadButton("Sound");
//		loadButton("Vibrate");
		loadButton("Aim-Help");
		loadButton("Center-Minimap");
		
		int startY = 30;
		int startX = 30;
		for(String s : settings.keySet())
		{
			Label text = new Label(s, new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), Color.WHITE));
			text.setAlignment(Align.top | Align.left);
			text.setPosition(startX, startY - text.getHeight() / 2);
			text.pack();
			spriteBackground.addActor(text);
			
			buttons.get(s).setX(startX + 180);
			buttons.get(s).setY(startY - buttons.get(s).getHeight() / 2);
			
			startY += 44;
		}
		
		this.addActor(spriteBackground);
	}
	
	private void loadButton(final String name)
	{
		settings.put(name, false);
		
		TextButton cmd = new TextButton(0, 0, 50, 35, textureRegionCmdActBt, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "Yes", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				buttonClicked(name);
			}
		});
		spriteBackground.addActor(cmd);
		buttons.put(name, cmd);
	}
	
	private void buttonClicked(String setting)
	{
		boolean newValue = !settings.get(setting);
		
		settings.put(setting, newValue);
		GlobalSettings.update(setting, String.valueOf(newValue));
		buttons.get(setting).setText(newValue == true ? "Yes" : "No");
		
		for(ISettingsCallback listener : listeners)
			if(listener != null)
				listener.onSettingChanged(setting, newValue);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public void addListener(ISettingsCallback listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		addListener(player.getLevel());
	}
}
