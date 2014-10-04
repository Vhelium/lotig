package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.components.TiledTextureRegion;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class GameHUD_SpellButtons extends GameHUDMenu
{
	private Main activity;
	private final ConcurrentHashMap<Integer, ButtonSprite> spellButtons;
	private final ConcurrentHashMap<Integer, Action> spellHighlightModifiers;
	private final List<Vector2> buttonPositions;
	private final ConcurrentHashMap<Integer, Sprite> spellHighlightSprites;
	private final ConcurrentHashMap<Integer, Sprite> spellCooldownSprites;
	private AnimatedSprite spellPreparedAnimation;
	private TextureRegion textureRegionOOM;
	private TextureRegion textureRegionUATC;
	
	public GameHUD_SpellButtons(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		spellButtons = new ConcurrentHashMap<Integer, ButtonSprite>();
		buttonPositions = new ArrayList<Vector2>();
		buttonPositions.add(new Vector2(20, 271));//  0                 0  1  2            3  4  5
		buttonPositions.add(new Vector2(92, 271));//  1                       P            6
		buttonPositions.add(new Vector2(164, 271));// 2                       P            7
		
		buttonPositions.add(new Vector2(591, 271));//   3
		buttonPositions.add(new Vector2(663, 271));//   4
		buttonPositions.add(new Vector2(735, 271));//   5
		buttonPositions.add(new Vector2(591, 343));//   6
		buttonPositions.add(new Vector2(591, 415));//   7
		
		spellHighlightSprites = new ConcurrentHashMap<Integer, Sprite>();
		spellCooldownSprites = new ConcurrentHashMap<Integer, Sprite>();
		spellHighlightModifiers = new ConcurrentHashMap<Integer, Action>();
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		final TextureRegion textureRegionCmdItem = GameHelper.$.getGuiAsset("cmdSpellHotkey");
		final TextureRegion textureRegionCmdSpellHighlighted = GameHelper.$.getGuiAsset("cmdSpellHighlighted");
		final TextureRegion textureRegionSpellCooldown = GameHelper.$.getGuiAsset("spellCooldown");
		textureRegionOOM = GameHelper.$.getGuiAsset("spellOOM");
		textureRegionUATC = GameHelper.$.getGuiAsset("spellUATC");
		
		final TiledTextureRegion textureRegionSpellPrepared = new TiledTextureRegion(GameHelper.$.getGuiAsset("spellPreparedAnimation"), 4, 2, true);
		spellPreparedAnimation = new AnimatedSprite(0, 0, textureRegionSpellPrepared);
		
		for(int i = 0; i < 8; i++)
		{
			final int id = i;
			
			spellHighlightSprites.put(id, new Sprite(0, 0, textureRegionCmdSpellHighlighted));
			ButtonSprite cmdSpell = new ButtonSprite(buttonPositions.get(i).x, buttonPositions.get(i).y, textureRegionCmdItem, new OnMyClickListener()
			{
				@Override
				public void onClick(ChangeEvent event, Actor actor)
				{
					hudCallback.requestSpell(id);
				}
			});
			this.addActor(cmdSpell);
			spellButtons.put(id, cmdSpell);
			spellCooldownSprites.put(id, new Sprite(cmdSpell.getX(), cmdSpell.getY(), textureRegionSpellCooldown));
			spellCooldownSprites.get(id).setHeight(0);
			this.addActor(spellCooldownSprites.get(id));
		}
	}
	
	@Override
	public void update(float delta)
	{
		for(Entry<Integer, Spell> hotkeySpell : player.getHotkeySpells().entrySet())
		{
			spellCooldownSprites.get(hotkeySpell.getKey()).setHeight(Math.max(45 * hotkeySpell.getValue().cooldownLeft / (hotkeySpell.getValue().getCooldown() * (100 - player.getAttribute("CDR")) / 100f), 0));
			spellCooldownSprites.get(hotkeySpell.getKey()).setY(spellButtons.get(hotkeySpell.getKey()).getY() + 45 - spellCooldownSprites.get(hotkeySpell.getKey()).getHeight());
			if(spellButtons.get(hotkeySpell.getKey()).getChildByTag(21) != null)
				spellButtons.get(hotkeySpell.getKey()).getChildByTag(21).setAlpha(hotkeySpell.getValue().cooldownLeft <= 0 ? 1 : 125);
		}
	}
	
	public void selectedSpellStatusChanged(int nr)
	{
		if(nr == -1)
		{
			for(int i = 0; i < 8; i++)
			{
				if(spellHighlightModifiers.containsKey(i))
					spellHighlightSprites.get(i).removeAction(spellHighlightModifiers.get(i));
				spellHighlightSprites.get(i).remove();
			}
		}
		else
		{
			for(int i = 0; i < 8; i++)
			{
				if(spellHighlightModifiers.containsKey(i))
					spellHighlightSprites.get(i).removeAction(spellHighlightModifiers.get(i));
				Action action = Actions.forever(Actions.sequence(Actions.alpha(1f, 0.5f), Actions.alpha(0f, 0.5f)));
				spellHighlightSprites.get(i).addAction(action);
				spellHighlightModifiers.put(i, action);
				spellButtons.get(i).addActor(spellHighlightSprites.get(i));
			}
		}
	}
	
	public void setSpellPrepared(final int slot, final boolean prepared)
	{
		spellPreparedAnimation.remove();
		if(prepared)
		{
			spellPreparedAnimation.animate(80, true);
			spellButtons.get(slot).addActor(spellPreparedAnimation);
		}
	}
	
	public void hotkeySpellChanged(final int slot, final Spell spell)
	{
		spellButtons.get(slot).clearChildren();
		if(spell != null)
		{
			Sprite sp = new Sprite(0, 0, GameHelper.getInstance().getSpellIconTexture(spell.getName()));
			sp.setTag(21);
			spellButtons.get(slot).addActor(sp);
		}
		GameHUD_SpellButtons.this.update(0);
	}
	
	public void playSpellOutOfMana(int slot)
	{
		if(spellButtons.get(slot).getChildByTag(101) != null)
			return;
		
		final Sprite oom = new Sprite(0, 0, textureRegionOOM);
		oom.addAction(Actions.sequence(Actions.alpha(1f, 0.3f), Actions.alpha(0f, 0.35f), Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				oom.remove();
			}
		})));
		oom.setTag(101);
		spellButtons.get(slot).addActor(oom);
	}
	
	public void playUnableToCast(int slot)
	{
		if(spellButtons.get(slot).getChildByTag(102) != null)
			return;
		
		final Sprite uatc = new Sprite(0, 0, textureRegionUATC);
		uatc.addAction(Actions.sequence(Actions.alpha(1f, 0.3f), Actions.alpha(0f, 0.35f), Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				uatc.remove();
			}
		})));
		uatc.setTag(102);
		spellButtons.get(slot).addActor(uatc);
	}
}
