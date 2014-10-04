package com.vhelium.lotig.scene.gamescene.server.levelobject;

public interface ILevelObjectCallBack
{
	public void lowerHealth(LevelObject object);
	
	public void stateChanged(LevelObject object, int state);
}
