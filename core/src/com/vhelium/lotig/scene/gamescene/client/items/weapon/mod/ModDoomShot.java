package com.vhelium.lotig.scene.gamescene.client.items.weapon.mod;

public class ModDoomShot extends WeaponModification
{
	@Override
	public String getName()
	{
		return WeaponModification.DOOM_SHOTS;
	}
	
	@Override
	public float getDmgFactor()
	{
		return 2;
	}
	
	@Override
	public float getAtkSpdFactor()
	{
		return 0.6f;
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
