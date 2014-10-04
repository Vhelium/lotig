package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.consumable.Potion;

public class GameHUD_PotionButtons extends GameHUDMenu
{
	private Main activity;
	private final ConcurrentHashMap<Integer, ButtonSprite> potionButtons;
	private final ConcurrentHashMap<Integer, Action> potionHighlightModifiers;
	private final ConcurrentHashMap<Integer, Sprite> potionHighlightSprites;
	private final List<Vector2> potionPositions;
	private TextureRegion textureRegionSpellCooldown;
	private final ConcurrentHashMap<Integer, Boolean> onCooldown;
	
	public GameHUD_PotionButtons(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		potionButtons = new ConcurrentHashMap<Integer, ButtonSprite>();
		potionHighlightSprites = new ConcurrentHashMap<Integer, Sprite>();
		potionHighlightModifiers = new ConcurrentHashMap<Integer, Action>();
		onCooldown = new ConcurrentHashMap<Integer, Boolean>();
		onCooldown.put(0, false);
		onCooldown.put(1, false);
		potionPositions = new ArrayList<Vector2>();
		potionPositions.add(new Vector2(164, 343));
		potionPositions.add(new Vector2(164, 415));
	}
	
	@Override
	public void showUp()
	{
		onCooldown.put(0, false);
		onCooldown.put(1, false);
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		final TextureRegion textureRegionCmdPotion = GameHelper.$.getGuiAsset("cmdSpellHotkey");
		final TextureRegion textureRegionCmdPotionHighlighted = GameHelper.$.getGuiAsset("cmdSpellHighlighted");
		textureRegionSpellCooldown = GameHelper.$.getGuiAsset("spellCooldown");
		
		for(int i = 0; i < 2; i++)
		{
			final int id = i;
			
			potionHighlightSprites.put(id, new Sprite(0, 0, textureRegionCmdPotionHighlighted));
			ButtonSprite cmdPotion = new ButtonSprite(potionPositions.get(id).x, potionPositions.get(id).y, textureRegionCmdPotion, new OnMyClickListener()
			{
				@Override
				public void onClick(ChangeEvent event, Actor actor)
				{
					hudCallback.requestPotion(id);
				}
			});
			cmdPotion.addActor(new Sprite(4, 3, 38, 38, GameHelper.getInstance().getGuiAsset("potionEmpty")));
			potionButtons.put(id, cmdPotion);
			this.addActor(cmdPotion);
		}
	}
	
	@Override
	public void update(float delta)
	{
		
	}
	
	private boolean potionSelected = false;
	
	public void setSelectedPotionStatus(boolean value)
	{
		if(value == potionSelected)
			return;
		
		potionSelected = value;
		
		if(!potionSelected)
		{
			for(int i = 0; i < 2; i++)
			{
				if(potionHighlightModifiers.containsKey(i))
					potionHighlightSprites.get(i).removeAction(potionHighlightModifiers.get(i));
				potionHighlightSprites.get(i).remove();
			}
		}
		else
		{
			for(int i = 0; i < 2; i++)
			{
				if(potionHighlightModifiers.containsKey(i))
					potionHighlightSprites.get(i).removeAction(potionHighlightModifiers.get(i));
				Action action = Actions.forever(Actions.sequence(Actions.alpha(1f, 0.5f), Actions.alpha(0f, 0.5f)));
				potionHighlightSprites.get(i).addAction(action);
				potionHighlightModifiers.put(i, action);
				potionButtons.get(i).addActor(potionHighlightSprites.get(i));
			}
		}
	}
	
	public void hotkeyPotionChanged(final int slot, final Potion potion)
	{
		potionButtons.get(slot).clearChildren();
		if(potion != null)
		{
			Sprite sp = new Sprite(6, 5, 34, 34, GameHelper.getInstance().getItemIcon(potion.NAME));
			potionButtons.get(slot).addActor(sp);
		}
		else
			potionButtons.get(slot).addActor(new Sprite(4, 3, 38, 38, GameHelper.getInstance().getGuiAsset("potionEmpty")));
		GameHUD_PotionButtons.this.update(0);
	}
	
	public void playCD(final int slot)
	{
		if(onCooldown.get(slot))
			return;
		
		final Sprite cd = new Sprite(2, 2, 41, 41, textureRegionSpellCooldown);
		cd.addAction(Actions.sequence(Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				onCooldown.put(slot, true);
			}
		}), Actions.alpha(1f, 0.35f), Actions.alpha(0f, 0.4f), Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				onCooldown.put(slot, false);
				cd.remove();
			}
		})));
		potionButtons.get(slot).addActor(cd);
	}
	
	public boolean isOnCD(int slot)
	{
		return onCooldown.get(slot);
	}
}
