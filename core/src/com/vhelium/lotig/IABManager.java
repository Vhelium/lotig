package com.vhelium.lotig;

import com.vhelium.lotig.scene.gamescene.client.hud.GemStoreHUD;

public interface IABManager
{
	void onShowUp(GemStoreHUD gemStoreHUD);
	
	void onGemBuyClicked(int amount);
	
	void dispose();
}
