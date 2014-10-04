package com.vhelium.lotig.scene.gamescene;

import com.vhelium.lotig.components.Rectangle;

public interface IClientMapCallback
{
	void onNextLevelAdded(Rectangle rectangle);
	
	void onCollisionAdded(int key, Rectangle bounds);
	
	void onCollisionRemoved(int key);
}
