package com.vhelium.lotig.scene.gamescene;

import com.vhelium.lotig.scene.gamescene.server.Difficulty;

public abstract class SavedGameVersionMerger
{
	public static void merge(int previousVersion, int currentVersion, SavedGame savedGame)
	{
		if(previousVersion <= 10)
		{
			//delete all quests
			for(int i = 0; i < Difficulty.DIFFICULTY_COUNT; i++)
				savedGame.removeData("quests" + i);
		}
	}
}
