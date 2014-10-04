package com.vhelium.lotig.scene.gamescene.client.hud;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.Utility;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.constants.Log;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.GlobalSettings;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;

public class GameHUD_Tipps extends GameHUDMenu
{
	private Sprite spriteBackground;
	private Label txtTitle;
	private Label txtContent;
	private BitmapFont fntContent;
	private List<String> tipps;
	private ButtonSprite cmdPrev;
	private ButtonSprite cmdNext;
	
	private int currentTipp;
	
	public GameHUD_Tipps(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		showTipp();
	}
	
	@Override
	public void loadResources(final Main activity)
	{
		spriteBackground = new Sprite(190, 120, 380, 240, GameHelper.$.getGuiAsset("backgroundConnectionInfo"));
		TextureRegion txtCmdTippNext = GameHelper.$.getGuiAsset("cmdTippNext");
		TextureRegion txtCmdTippPrev = new TextureRegion(txtCmdTippNext);
		txtCmdTippPrev.flip(true, false);
		
		txtTitle = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 24), Color.WHITE));
		txtTitle.setAlignment(Align.top | Align.left);
		txtTitle.setY(26);
		spriteBackground.addActor(txtTitle);
		
		fntContent = GameHelper.getInstance().getMainFont(FontCategory.InGame, 14);
		txtContent = new Label("", new LabelStyle(fntContent, Color.WHITE));
		txtContent.setAlignment(Align.center);
		txtContent.setY(75);
		spriteBackground.addActor(txtContent);
		
		cmdPrev = new ButtonSprite(30, spriteBackground.getHeight() - txtCmdTippPrev.getRegionHeight() - 30, txtCmdTippPrev, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				previous();
			}
		});
		spriteBackground.addActor(cmdPrev);
		
		cmdNext = new ButtonSprite(spriteBackground.getWidth() - 30 - txtCmdTippNext.getRegionWidth(), spriteBackground.getHeight() - txtCmdTippNext.getRegionHeight() - 30, txtCmdTippNext, new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				SoundManager.playSound(SoundFile.menu_ingame_selected);
				next();
			}
		});
		spriteBackground.addActor(cmdNext);
		
		this.addActor(spriteBackground);
		
		tipps = new ArrayList<String>();
		
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(Gdx.files.internal("xml/tipps.txt").read()));
			String line;
			while((line = br.readLine()) != null)
			{
				if(!line.startsWith("//") && !line.isEmpty())
					tipps.add(line);
			}
		}
		catch (Exception e)
		{
			Log.e("GameHelper.out", "failed loading story quests");
		}
		
		currentTipp = GlobalSettings.getInstance().getDataValue("lasttipp") != null ? Integer.parseInt(GlobalSettings.getInstance().getDataValue("lasttipp")) : 0;
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	private void next()
	{
		currentTipp++;
		if(currentTipp >= tipps.size())
			currentTipp = 0;
		showTipp();
		GlobalSettings.update("lasttipp", String.valueOf(currentTipp));
	}
	
	private void previous()
	{
		currentTipp--;
		if(currentTipp < 0)
			currentTipp = tipps.size() - 1;
		showTipp();
		GlobalSettings.update("lasttipp", String.valueOf(currentTipp));
	}
	
	private void showTipp()
	{
		txtTitle.setText("Tipp " + (currentTipp + 1));
		txtTitle.pack();
		txtTitle.setX(spriteBackground.getWidth() / 2 - txtTitle.getWidth() / 2);
		
		txtContent.setText(Utility.getNormalizedText(fntContent, tipps.get(currentTipp), 320));
		txtContent.pack();
		txtContent.setX(spriteBackground.getWidth() / 2 - txtContent.getWidth() / 2);
	}
}
