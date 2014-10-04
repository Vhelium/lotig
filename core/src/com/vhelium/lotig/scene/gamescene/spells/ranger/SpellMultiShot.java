package com.vhelium.lotig.scene.gamescene.spells.ranger;

import com.badlogic.gdx.math.MathUtils;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMultiShot extends Spell
{
	public static String name = "Multi-Shot";
	public static String bulletAsset = "Multi-Shot";
	
	public static float cooldownBase = 3000;
	public static float cooldownPerLevel = 100;
	
	public static float manaCostBase = 150;
	public static float manaCostPerLevel = 12;
	
	public static float damageBase = 90;
	public static float damagePerLevel = 25;
	
	public static float damageBonusBase = 30;
	public static float damageBonusPerLevel = 5;
	
	public static int rangeBase = 220;
	public static int rangePerLevel = 15;
	
	private static double angle = 0.4f;
	
	public SpellMultiShot()
	{
		instantCast = false;
	}
	
	//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{
		return "You fire three arrows at once in a wide angle.\n\nDamage / shot: " + getDamage(getLevel()) + " - " + (getDamage(getLevel()) + getBonusDamage(getLevel())) + "\nRange: " + getRange(getLevel());
	}
	
	@Override
	public float getManaCost()
	{
		return getManaCostStatic(getLevel());
	}
	
	@Override
	public float getCooldown()
	{
		return cooldownBase + cooldownPerLevel * (getLevel() - 1);
	}
	
	@Override
	public DataPacket generateRequest(float x, float y, float rotation, float dirX, float dirY)
	{
		DataPacket dp = new DataPacket();
		dp.setInt(MessageType.MSG_SPELL_REQUEST);
		dp.setString(name);
		dp.setInt(getLevel());
		dp.setFloat(x);
		dp.setFloat(y);
		dp.setFloat(rotation);
		dp.setFloat(dirX);
		dp.setFloat(dirY);
		
		return dp;
	}
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		float rotation = dp.getFloat();
		float dirX = dp.getFloat();
		float dirY = dp.getFloat();
		
		double alpha;
		if(dirX < 0)
			alpha = Math.atan(dirY / dirX) + Math.PI;
		else if(dirX == 0 && dirY > 0)
			alpha = Math.PI / 2;
		else if(dirX == 0 && dirY <= 0)
			alpha = Math.PI / 2 * 3;
		else
			alpha = Math.atan(dirY / dirX);
		
		BulletOnHitEffect effect = new BulletOnHitEffect(getDamage(level) + GameHelper.getInstance().getRandom().nextInt((int) getBonusDamage(level) + 1), 0, "", 0);
		
		realm.shoot(shooterId, true, DamageType.Physical, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, effect);
		
		double beta = alpha - angle;
		float bDirX = (float) Math.cos(beta);
		float bDirY = (float) Math.sin(beta);
		float bRot = MathUtils.radiansToDegrees * ((float) Math.atan2(bDirX, -bDirY));
		realm.shoot(shooterId, true, DamageType.Physical, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + bDirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + bDirY * 16, bRot, bDirX, bDirY, bulletAsset, effect);
		
		double gamma = alpha + angle;
		float gDirX = (float) Math.cos(gamma);
		float gDirY = (float) Math.sin(gamma);
		float gRot = MathUtils.radiansToDegrees * ((float) Math.atan2(gDirX, -gDirY));
		realm.shoot(shooterId, true, DamageType.Physical, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + gDirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + gDirY * 16, gRot, gDirX, gDirY, bulletAsset, effect);
	}
	
	@Override
	public String getCastSound()
	{
		return SoundFile.bow3;
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static float getDamage(int level)
	{
		return damageBase + damagePerLevel * (level - 1);
	}
	
	public static float getBonusDamage(int level)
	{
		return damageBonusBase + damageBonusPerLevel * (level - 1);
	}
	
	public static int getRange(int level)
	{
		return rangeBase + rangePerLevel * (level - 1);
	}
}
