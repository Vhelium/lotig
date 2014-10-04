package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class GameHUD_Status extends GameHUDMenu
{
	Sprite spriteBackground;
	float hpBarTop = 3;
	float hpBarBot = 88;
	float manaBarTop = 91;
	float manaBarBot = 176;
	float honorBarTop = 179;
	float honorBarBot = 264;
	
	Sprite hpBar;
	Sprite manaBar;
	Sprite honorBar;
	
	public GameHUD_Status(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("status");
		final TextureRegion textureRegionHpBar = GameHelper.$.getGuiAsset("barHp");
		final TextureRegion textureRegionManaBar = GameHelper.$.getGuiAsset("barMana");
		final TextureRegion textureRegionHonorBar = GameHelper.$.getGuiAsset("barHonor");
		
		spriteBackground = new Sprite(0, 0, textureRegionRight);
		this.addActor(spriteBackground);
		
		hpBar = new Sprite(3, hpBarTop, textureRegionHpBar);
		this.addActor(hpBar);
		
		manaBar = new Sprite(3, manaBarTop, textureRegionManaBar);
		this.addActor(manaBar);
		
		honorBar = new Sprite(3, honorBarTop, textureRegionHonorBar);
		this.addActor(honorBar);
	}
	
	@Override
	public void update(float delta)
	{
		hpBar.setHeight((hpBarBot - hpBarTop) * (player.getHp() / player.getMaxHp()));
		hpBar.setY(hpBarBot - hpBar.getHeight());
		
		manaBar.setHeight((manaBarBot - manaBarTop) * (player.getMana() / player.getMaxMana()));
		manaBar.setY(manaBarBot - manaBar.getHeight());
		
		honorBar.setHeight((honorBarBot - honorBarTop) * ((player.getFame() - player.getFameLastRank()) / (float) (player.getFameNextRank() - player.getFameLastRank())));
		honorBar.setY(honorBarBot - honorBar.getHeight());
	}
}
