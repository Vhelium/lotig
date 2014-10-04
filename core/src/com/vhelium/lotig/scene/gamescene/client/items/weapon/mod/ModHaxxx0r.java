package com.vhelium.lotig.scene.gamescene.client.items.weapon.mod;

public class ModHaxxx0r extends WeaponModification
{
	@Override
	public String getName()
	{
		return WeaponModification.HAXXX0R;
	}
	
	@Override
	public float getDmgFactor()
	{
		return 0.2f;
	}
	
	@Override
	public float getAtkSpdFactor()
	{
		return 3f;
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
