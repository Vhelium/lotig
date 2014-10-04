package com.vhelium.lotig.scene.gamescene.client.items.weapon.mod;

public class ModPsycho extends WeaponModification
{
	@Override
	public String getName()
	{
		return WeaponModification.PSYCHO;
	}
	
	@Override
	public float getDmgFactor()
	{
		return 3;
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
