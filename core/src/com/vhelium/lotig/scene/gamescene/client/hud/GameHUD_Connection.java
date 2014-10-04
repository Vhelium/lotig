package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.EventResult;
import com.vhelium.lotig.IEventListener;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;

public class GameHUD_Connection extends GameHUDMenu implements IEventListener
{
	private Sprite spriteBackground;
	private Label text;
	private TextButton cmdActivateBT;
	
	public GameHUD_Connection(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		//register listener:
		GameHelper.getPlatformResolver().addEventListener(this);
	}
	
	@Override
	public void showUp()
	{
		if(!hudCallback.getConnectionType().equals("Singleplayer"))
			text.setText("Difficulty: " + getDifficultyById(player.getLevel().getDifficulty()) + "\nConnectionType: " + hudCallback.getConnectionType() + "\n" + GameHelper.$.getIPinfo());
		else
			text.setText("Difficulty: " + getDifficultyById(player.getLevel().getDifficulty()) + "\nConnectionType: " + hudCallback.getConnectionType());
		
		text.pack();
		text.setX(spriteBackground.getWidth() / 2 - text.getWidth() / 2);
		text.setY(13);
		
		if(!hudCallback.getConnectionType().equals("Bluetooth Server") || !GameHelper.getPlatformResolver().isSupportingBluetooth())
			cmdActivateBT.setVisible(false);
		else
		{
			cmdActivateBT.setVisible(true);
			if(GameHelper.getInstance().isBluetoothVisible())
				cmdActivateBT.setEnabled(false);
			else
				cmdActivateBT.setEnabled(true);
		}
	}
	
	@Override
	public void loadResources(final Main activity)
	{
		spriteBackground = new Sprite(300, 190, 240, 110, GameHelper.$.getGuiAsset("backgroundConnectionInfo"));
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 14), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		spriteBackground.addActor(text);
		
		cmdActivateBT = new TextButton(40, 57, 160, 30, GameHelper.$.getGuiAsset("cmdLoot"), GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), "Make Bluetooth visible", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(hudCallback.getConnectionType().equals("Bluetooth Server"))
				{
					cmdActivateBT.setEnabled(false);
					GameHelper.getPlatformResolver().makeBluetoothVisible();
				}
			}
		});
		spriteBackground.addActor(cmdActivateBT);
		
		this.addActor(spriteBackground);
	}
	
	@Override
	public void update(float delta)
	{
		if(GameHelper.getInstance().isBluetoothVisible())
			cmdActivateBT.setEnabled(false);
		else
			cmdActivateBT.setEnabled(true);
	}
	
	private String getDifficultyById(int diff)
	{
		switch(diff)
		{
			case 0:
				return "Normal";
			case 1:
				return "Nightmare";
			case 2:
				return "Hell";
			default:
				return "null";
		}
	}
	
	private boolean isDead = false;
	
	@Override
	public void dispose()
	{
		isDead = true;
		super.dispose();
	}
	
	@Override
	public boolean isDead()
	{
		return isDead;
	}
	
	@Override
	public void eventResultReceived(EventResult event)
	{
		switch(event.getRequestCode())
		{
			case EventResult.REQUEST_TURN_ON_DISCOVERABLE:
				GameHelper.getInstance().setBluetoothVisibleNow();
				break;
		}
	}
}
