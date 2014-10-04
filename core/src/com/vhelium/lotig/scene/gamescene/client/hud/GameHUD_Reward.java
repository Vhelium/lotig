package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.Item;

public class GameHUD_Reward extends GameHUDMenu
{
	private Main activity;
	TextureRegion textureRegionPvpScores;
	List<GameHUD_Reward_Group> itemQueue = new ArrayList<GameHUD_Reward_Group>();
	
	public GameHUD_Reward(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
		this.setZIndex(6);
	}
	
	public void displayReward(String title, int gold, int fame, Item... items)
	{
		GameHUD_Reward_Group item = new GameHUD_Reward_Group(this, title, gold, fame, items, activity);
		itemQueue.add(item);
		if(itemQueue.size() == 1)
			this.addActor(item);
	}
	
	@Override
	public void setParam(String str)
	{
		
	}
	
	@Override
	public void showUp()
	{
		
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		textureRegionPvpScores = GameHelper.$.getGuiAsset("questReward");
	}
	
	public void onEndReached(GameHUD_Reward_Group item)
	{
		itemQueue.remove(item);
		if(itemQueue.size() > 0)
			this.addActor(itemQueue.get(0));
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
