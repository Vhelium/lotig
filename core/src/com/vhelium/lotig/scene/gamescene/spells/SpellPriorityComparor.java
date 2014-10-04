package com.vhelium.lotig.scene.gamescene.spells;

import java.util.Comparator;

public class SpellPriorityComparor implements Comparator<SpellEnemy>
{
	@Override
	public int compare(SpellEnemy s1, SpellEnemy s2)
	{
		return s2.priority - s1.priority;
	}
}
