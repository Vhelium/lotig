package com.vhelium.lotig.scene.gamescene.client.items.weapon.mod;

public class ModOrbs extends WeaponModification
{
	@Override
	public String getName()
	{
		return WeaponModification.ORBS;
	}
	
	@Override
	public float getDmgFactor()
	{
		return 0.75f;
	}
	
	@Override
	public float getAtkSpdFactor()
	{
		return 1.2f;
	}
	
	@Override
	public String getSpecialBulletAsset()
	{
		return "Mod Orbs";
	}
	
	@Override
	public boolean isPiercing()
	{
		return true;
	}
	
	@Override
	public float getRangeFactor()
	{
		return 0.5f;
	}
}
