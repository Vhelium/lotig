package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.HashMap;
import com.badlogic.gdx.graphics.Color;
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
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.SoundManager;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GameSubHUD_Spells extends GameHUDMenu
{
	private Main activity;
	private final HashMap<Integer, ButtonSprite> spellButtons;
	private final HashMap<Integer, Label> spellTexts;
	private Sprite spriteCmdSpellHighlighted;
	private Label txtSpellName;
	private Label txtSpellInfo;
	private Label txtSpellMana;
	private Label txtSpellCD;
	private int selectedSpellId = -1;
	
	public GameSubHUD_Spells(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		spellButtons = new HashMap<Integer, ButtonSprite>();
		spellTexts = new HashMap<Integer, Label>();
	}
	
	@Override
	public void showUp()
	{
		buttonClicked(-1);
		updateText();
		if(hudCallback.getCurrentOnScreenInfo() != null && hudCallback.getCurrentOnScreenInfo().id == 10/*Spells*/)
		{
			hudCallback.showOSI(11/*Select Spell*/);
		}
	}
	
	@Override
	public void hidden()
	{
		buttonClicked(-1);
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		final TextureRegion textureRegionBg = GameHelper.$.getGuiAsset("backgroundSpells");
		final TextureRegion textureRegionCmdSpell = GameHelper.$.getGuiAsset("cmdSpell");
		final TextureRegion textureRegionCmdSpellHighlighted = GameHelper.$.getGuiAsset("cmdSpellHighlighted");
		
		spriteCmdSpellHighlighted = new Sprite(0, 0, textureRegionCmdSpellHighlighted);
		this.addActor(new Sprite(214, 264, textureRegionBg));
		
		txtSpellName = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), Color.WHITE));
		txtSpellName.setAlignment(Align.top | Align.left);
		txtSpellName.setPosition(235, 280);
		this.addActor(txtSpellName);
		txtSpellInfo = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), Color.WHITE));
		txtSpellInfo.setAlignment(Align.top | Align.left);
		txtSpellInfo.setPosition(235, 315);
		this.addActor(txtSpellInfo);
		txtSpellMana = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), Color.WHITE));
		txtSpellMana.setAlignment(Align.top | Align.left);
		txtSpellMana.setPosition(412, 280);
		txtSpellMana.setColor(91 / 255f, 144 / 255f, 231 / 255f, 1);
		this.addActor(txtSpellMana);
		txtSpellCD = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 15), Color.WHITE));
		txtSpellCD.setAlignment(Align.top | Align.left);
		txtSpellCD.setPosition(495, 280);
		txtSpellCD.setColor(247 / 255f, 227 / 255f, 120 / 255f, 1);
		this.addActor(txtSpellCD);
		
		float posX = 240;
		float posY = 11;
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 4; x++)
			{
				final int nr = x + y * 4;
				ButtonSprite cmdSpell = new ButtonSprite(posX, posY, textureRegionCmdSpell, new OnMyClickListener()
				{
					@Override
					public void onClick(ChangeEvent event, Actor actor)
					{
						buttonClicked(nr);
						
					}
				});
				this.addActor(cmdSpell);
				spellButtons.put(nr, cmdSpell);
				Label lbl = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
				lbl.setAlignment(Align.top | Align.center);
				lbl.setPosition(0, 0);
				spellTexts.put(nr, lbl);
				spellTexts.get(nr).setPosition(posX + 23 - spellTexts.get(nr).getWidth() / 2, posY + 52 - spellTexts.get(nr).getHeight() / 2);
				
				this.addActor(spellTexts.get(nr));
				posX += 92;
			}
			posX = 240;
			posY += 87;
		}
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		this.player = player;
		
		for(int i = 0; i < spellButtons.size(); i++)
		{
			if(player.getSpells().containsKey(i))
			{
				spellButtons.get(i).addActor(new Sprite(0, 0, GameHelper.getInstance().getSpellIconTexture(player.getSpells().get(i).getName())));
				spellTexts.get(i).setText(String.valueOf(player.getSpells().get(i).getLevel()));
			}
		}
	}
	
	private void buttonClicked(int number)
	{
		final int nr = player.getSpells().containsKey(number) ? number : -1;
		if(selectedSpellId == nr)
			return;
		
		if(number != -1)
			SoundManager.playSound(SoundFile.menu_spell_selected);
		
		if(selectedSpellId != -1 && nr == -1 || selectedSpellId == -1 && nr != -1)
			hudCallback.selectedSpellStatusChanged(nr);
		
		if(selectedSpellId != -1)
		{
			txtSpellName.setText("");
			txtSpellInfo.setText("");
			txtSpellMana.setText("");
			txtSpellCD.setText("");
			spriteCmdSpellHighlighted.remove();
		}
		selectedSpellId = nr;
		if(nr != -1 && player.getSpells().containsKey(nr))
		{
			spellButtons.get(nr).addActor(spriteCmdSpellHighlighted);
			txtSpellName.setText(player.getSpells().get(nr).getName());
			txtSpellInfo.setText(Utility.getNormalizedText(GameHelper.getInstance().getMainFont(FontCategory.InGame, 13), player.getSpells().get(nr).getDescription(), 340));//TODO: GET FONT FROM LABEL!
			txtSpellMana.setText("Mana: " + (int) player.getSpells().get(nr).getManaCost());
			txtSpellCD.setText("CD : " + (player.getSpells().get(nr).getCooldown() * (100 - player.getAttribute("CDR")) / 100f / 1000));
			
			if(hudCallback.getCurrentOnScreenInfo() != null && hudCallback.getCurrentOnScreenInfo().id == 11/*Select Spell*/)
			{
				hudCallback.showOSI(12/*Spell Hotkey*/);
			}
		}
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	public void updateText()
	{
		for(int i = 0; i < spellButtons.size(); i++)
		{
			if(player.getSpells().containsKey(i))
			{
				spellTexts.get(i).setText(String.valueOf(player.getSpells().get(i).getLevel()));
				spellTexts.get(i).setPosition(spellButtons.get(i).getX() + 22 - spellTexts.get(i).getWidth() / 2, spellButtons.get(i).getY() + 52 - spellTexts.get(i).getHeight() / 2);
			}
		}
	}
	
	public void spellRequestedWhileActive(int slot)
	{
		if(selectedSpellId != -1)
		{
			SoundManager.playSound(SoundFile.menu_spell_assigned);
			player.getHotkeySpells().put(slot, player.getSpells().get(selectedSpellId));
			hudCallback.hotkeySpellChanged(slot, player.getSpells().get(selectedSpellId));
			buttonClicked(-1);
			
			if(hudCallback.getCurrentOnScreenInfo() != null && hudCallback.getCurrentOnScreenInfo().id == 12/*Spell Hotkey*/)
				hudCallback.showOSI(13/*Close Menu*/);
		}
		else
		{
			SoundManager.playSound(SoundFile.menu_spell_removed);
			player.getHotkeySpells().remove(slot);
			hudCallback.hotkeySpellChanged(slot, null);
		}
	}
}