package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.BulletOnHitEffect;
import com.vhelium.lotig.scene.gamescene.Condition;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.IConditionListener;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellManaBurn extends Spell
{
	public static String name = "Mana Burn";
	public static String effectAsset = "Mana Burn";
	public static String bulletAsset = "Mana Burn";
	
	public static float cooldownBase = 20000f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 200;
	public static float manaCostPerLevel = 30;
	
	public static float manaburnBase = 400;
	public static float manaburnPerLevel = 50;
	
	public static int oneDamagePerXmana = 2;
	
	public static int rangeBase = 400;
	public static int rangePerLevel = 10;
	
	public SpellManaBurn()
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
		return "You summon a projectile that burns mana from the first enemy it hits and deals damage.\n\nDamage: 1 damage per " + oneDamagePerXmana + " mana burned\nMana burn: " + getManaBurn(getLevel()) + "\nRange: " + getRange(getLevel());
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
	
	public static void activate(final int shooterId, int spellLevel, DataPacket dp, final Realm realm)
	{
		final int level = spellLevel;
		final float x = dp.getFloat();
		final float y = dp.getFloat();
		float rotation = dp.getFloat();
		float dirX = dp.getFloat();
		float dirY = dp.getFloat();
		
		BulletOnHitEffect ohe = new BulletOnHitEffect(1, 0, new Condition(name, 500, effectAsset, 0, new IConditionListener()
		{
			@Override
			public void onDied(EntityServerMixin target)
			{
				
			}
			
			@Override
			public void onApplied(EntityServerMixin target)
			{
				float manaToBurn = Math.max(Math.min(getManaBurn(level), target.getMana()), 0);
				target.setMana(target.getMana() - manaToBurn);
				float damage = manaToBurn / oneDamagePerXmana;
				target.DoDamage(damage, DamageType.Physical, realm.isPvP());
				
				realm.sendDamageNumberToAllPlayers(target.Nr, -manaToBurn, DamageType.Mana);
				realm.sendDamageNumberToAllPlayers(target.Nr, damage, DamageType.Physical);
				
				realm.playSound(SoundFile.firestorm, target.getOriginX(), target.getOriginY());
			}
		}));
		
		realm.shoot(shooterId, false, DamageType.Physical, getRange(level), x - GameHelper.getInstance().getBulletAsset(bulletAsset).getWidth() / 2 + dirX * 16, y - GameHelper.getInstance().getBulletAsset(bulletAsset).getHeight() / 2 + dirY * 16, rotation, dirX, dirY, bulletAsset, ohe);
	}
	
	@Override
	public String getCastSound()
	{
		return SoundFile.spell_missile;
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static float getManaBurn(int level)
	{
		return manaburnBase + manaburnPerLevel * (level - 1);
	}
	
	public static int getRange(int level)
	{
		return rangeBase + rangePerLevel * (level - 1);
	}
}
