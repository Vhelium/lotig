package com.vhelium.lotig.scene.gamescene.client.items.weapon.mod;

public class ModSniper extends WeaponModification
{
	@Override
	public String getName()
	{
		return WeaponModification.SNIPER;
	}
	
	@Override
	public float getDmgFactor()
	{
		return 1.5f;
	}
	
	@Override
	public float getAtkSpdFactor()
	{
		return 0.6f;
	}
	
	@Override
	public String getSpecialBulletAsset()
	{
		return "Mod Sniper";
	}
	
	@Override
	public boolean isPiercing()
	{
		return true;
	}
	
	@Override
	public float getRangeFactor()
	{
		return 1.5f;
	}
}
