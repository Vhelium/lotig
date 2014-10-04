package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.ButtonSprite;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.ShapeRendererBatch;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.HUD;
import com.vhelium.lotig.scene.SceneManager;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.ClientLevel;

public class GemStoreHUD extends HUD
{
	public static final int GEM_AMOUNT_50 = 50;
	public static final int GEM_AMOUNT_100 = 100;
	public static final int GEM_AMOUNT_250 = 250;
	public static final int GEM_AMOUNT_750 = 750;
	
	private ClientLevel level;
	private Sprite spriteBackground;
	private Label text;
	private ButtonSprite buy1;
	private ButtonSprite buy2;
	private ButtonSprite buy3;
	private ButtonSprite buy4;
	
	public GemStoreHUD(SpriteBatch spriteBatch, ShapeRendererBatch shape)
	{
		super(spriteBatch, shape);
	}
	
	public void onShowUp()
	{
		setLoading(false);
		if(GameHelper.getPlatformResolver().isSupportingGemStore())
		{
			GameHelper.getPlatformResolver().getIABManager().onShowUp(this);
		}
	}
	
	public void loadResources(Main activity, final ClientLevel level)
	{
		this.level = level;
		spriteBackground = new Sprite(0, 0, GameHelper.$.getGuiAsset("backgroundGemStore"));
		
//		final TextureRegion textureRegionPvpScores = GameHelper.$.getGuiAsset("gems");
//		Sprite sprite = new Sprite(444, 222, 88, 88, textureRegionPvpScores);
//		spriteBackground.addActor(sprite);
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 22), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(1, 1);
		spriteBackground.addActor(text);
		
		buy1 = new ButtonSprite(0, 0, GameHelper.$.getGuiAsset("gemsBuy1"), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(GameHelper.getPlatformResolver().isSupportingGemStore())
				{
					GameHelper.getPlatformResolver().getIABManager().onGemBuyClicked(GEM_AMOUNT_50);
				}
			}
		});
		buy1.setPosition(32, SceneManager.CAMERA_HEIGHT / 2 - buy1.getHeight() / 2);
		spriteBackground.addActor(buy1);
		
		buy2 = new ButtonSprite(0, 0, GameHelper.$.getGuiAsset("gemsBuy2"), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(GameHelper.getPlatformResolver().isSupportingGemStore())
				{
					GameHelper.getPlatformResolver().getIABManager().onGemBuyClicked(GEM_AMOUNT_100);
				}
			}
		});
		buy2.setPosition(buy1.getX() + buy1.getWidth() + 32, SceneManager.CAMERA_HEIGHT / 2 - buy2.getHeight() / 2);
		spriteBackground.addActor(buy2);
		
		buy3 = new ButtonSprite(0, 0, GameHelper.$.getGuiAsset("gemsBuy3"), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(GameHelper.getPlatformResolver().isSupportingGemStore())
				{
					GameHelper.getPlatformResolver().getIABManager().onGemBuyClicked(GEM_AMOUNT_250);
				}
			}
		});
		buy3.setPosition(buy2.getX() + buy2.getWidth() + 32, SceneManager.CAMERA_HEIGHT / 2 - buy3.getHeight() / 2);
		spriteBackground.addActor(buy3);
		
		buy4 = new ButtonSprite(0, 0, GameHelper.$.getGuiAsset("gemsBuy4"), new OnMyClickListener()
		{
			@Override
			public void onClick(ChangeEvent event, Actor actor)
			{
				if(GameHelper.getPlatformResolver().isSupportingGemStore())
				{
					GameHelper.getPlatformResolver().getIABManager().onGemBuyClicked(GEM_AMOUNT_750);
				}
			}
		});
		buy4.setPosition(buy3.getX() + buy3.getWidth() + 32, SceneManager.CAMERA_HEIGHT / 2 - buy4.getHeight() / 2);
		spriteBackground.addActor(buy4);
		
		this.addActor(spriteBackground);
	}
	
	public void giveGems(int amount)
	{
		level.getPlayer().getInventory().addGems(amount);
		level.saveGame();
	}
	
	private boolean isLoading = false;
	
	public void setLoading(boolean b)
	{
		isLoading = b;
		text.setText(b ? "is Loading..." : "");
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Keys.BACK || keycode == Keys.ESCAPE)
		{
			if(!isLoading)
			{
				level.setHUD(level.hud);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void dispose()
	{
		if(GameHelper.getPlatformResolver().isSupportingGemStore())
		{
			GameHelper.getPlatformResolver().getIABManager().dispose();
		}
		super.dispose();
	}
}