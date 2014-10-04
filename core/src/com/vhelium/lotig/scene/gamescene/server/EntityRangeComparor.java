package com.vhelium.lotig.scene.gamescene.server;

import java.util.Comparator;

public class EntityRangeComparor implements Comparator<Integer>
{
	@Override
	public int compare(Integer e1, Integer e2)
	{
		return e1 - e2;
	}
}
