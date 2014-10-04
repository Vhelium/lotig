package com.vhelium.lotig.scene.gamescene.client.items.weapon.mod;

public class ModMultiShot extends WeaponModification
{
	@Override
	public String getName()
	{
		return WeaponModification.MULTI_SHOT;
	}
	
	@Override
	public float getDmgFactor()
	{
		return 0.4f;
	}
	
	@Override
	public float getAtkSpdFactor()
	{
		return 1f;
	}
	
	@Override
	public String getSpecialBulletAsset()
	{
		return null;
	}
	
	@Override
	public boolean isPiercing()
	{
		return false;
	}
	
	@Override
	public float getRangeFactor()
	{
		return 1f;
	}
}
