package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.StringTokenizer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;

public class GameHUD_Action extends GameHUDMenu
{
	Main activity;
	private TextButton cmdPort;
	Sprite spriteSelection;
	private ButtonSprite cmdSelectionLeft;
	private ButtonSprite cmdSelectionRight;
	private Label text;
	private Label textSelection;
	String[] props;
	String selectionName = null;
	int selectionMin;
	int selectionMax;
	int currentSelection = 1;
	
	public GameHUD_Action(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void setParam(String str)
	{
		param = str;
		if(param != null)
		{
			StringTokenizer st = new StringTokenizer(param.toString(), ";");
			text.setText(st.nextToken());
			cmdPort.setText(st.nextToken());
			cmdPort.pack();
			text.setX(106 - text.getWidth() / 2);
			if(st.hasMoreTokens())
				cmdPort.setEnabled(Boolean.parseBoolean(st.nextToken()));
			else
				cmdPort.setEnabled(true);
			if(st.hasMoreTokens())
			{
				//read out selectionName, startValue, endValue
				selectionName = st.nextToken();
				selectionMin = Integer.parseInt(st.nextToken());
				selectionMax = Integer.parseInt(st.nextToken());
				currentSelection = selectionMax;
				if(selectionMax == 0)
				{
					selectionName = null;
					selectionMax = 1;
					currentSelection = selectionMax;
				}
			}
			else
				selectionName = null;
			updateSelectionText();
		}
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("right");
		final TextureRegion textureRegionCmdPort = GameHelper.$.getGuiAsset("cmdPort");
		final TextureRegion textureRegionBackgroundSorting = GameHelper.$.getGuiAsset("backgroundSorting");
		final TextureRegion textureRegionCmdCatLeft = GameHelper.$.getGuiAsset("cmdCatLeft");
		final TextureRegion textureRegionCmdCatRight = new TextureRegion(textureRegionCmdCatLeft);
		textureRegionCmdCatRight.flip(true, false);
		
		Sprite spriteRight = new Sprite(587, 48, textureRegionRight);
		
		spriteSelection = new Sprite(textureRegionRight.getRegionWidth() / 2 - textureRegionBackgroundSorting.getRegionWidth() / 2, 105, textureRegionBackgroundSorting);
		spriteRight.addActor(spriteSelection);
		
		cmdPort = new TextButton(56, 160, textureRegionCmdPort, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				callback.onActivate();
			}
		});
		cmdPort.setEnabled(false);
		cmdPort.pack();
		spriteRight.addActor(cmdPort);
		
		cmdSelectionLeft = new ButtonSprite(spriteSelection.getX(), spriteSelection.getY(), textureRegionCmdCatLeft, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				currentSelection--;
				if(currentSelection < selectionMin)
					currentSelection = selectionMax;
				updateSelectionText();
			}
		});
		spriteRight.addActor(cmdSelectionLeft);
		
		cmdSelectionRight = new ButtonSprite(spriteSelection.getX() + spriteSelection.getWidth() - textureRegionCmdCatLeft.getRegionWidth(), spriteSelection.getY(), textureRegionCmdCatRight, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				currentSelection++;
				if(currentSelection > selectionMax)
					currentSelection = selectionMin;
				updateSelectionText();
			}
		});
		spriteRight.addActor(cmdSelectionRight);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		text.setAlignment(Align.top | Align.center);
		text.setPosition(17, 10);
		spriteRight.addActor(text);
		
		textSelection = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		textSelection.setAlignment(Align.top | Align.center);
		textSelection.setPosition(0, 0);
		spriteRight.addActor(textSelection);
		
		this.addActor(spriteRight);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	private void updateSelectionText()
	{
		if(selectionName != null)
		{
			textSelection.setText(selectionName + " " + currentSelection);
			textSelection.pack();
			textSelection.setX(spriteSelection.getX() + spriteSelection.getWidth() / 2 - textSelection.getWidth() / 2);
			textSelection.setY(spriteSelection.getY() + spriteSelection.getHeight() / 2 - textSelection.getHeight() / 2 + 2);
			cmdSelectionLeft.setEnabled(true);
			cmdSelectionLeft.setVisible(true);
			cmdSelectionRight.setEnabled(true);
			cmdSelectionRight.setVisible(true);
			spriteSelection.setVisible(true);
		}
		else
		{
			textSelection.setText("");
			cmdSelectionLeft.setEnabled(false);
			cmdSelectionLeft.setVisible(false);
			cmdSelectionRight.setEnabled(false);
			cmdSelectionRight.setVisible(false);
			spriteSelection.setVisible(false);
		}
	}
	
	IActionCallback callback;
	
	public void setCallback(IActionCallback callback)
	{
		this.callback = callback;
	}
	
	public int getCurrentSelection()
	{
		return currentSelection;
	}
	
	private String event;
	
	public void setEvent(String event)
	{
		this.event = event;
	}
	
	public String getEvent()
	{
		return event;
	}
}
