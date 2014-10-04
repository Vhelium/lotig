package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellBlink extends Spell
{
	public static String name = "Blink";
	public static String effectAsset = "Blink";
	
	public static float cooldownBase = 200f;
	public static float cooldownPerLevel = 500f;
	
	public static float manaCostBase = 1;
	public static float manaCostPerLevel = 1;
	
	public static int rangeAmountBase = 500;
	public static int rangeAmountPerLevel = 1;
	
	public SpellBlink()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "You port forwards over a short distance.\n\nRange: " + getRangeAmount(getLevel());
	}
	
	@Override
	public String getName()
	{
		return name;
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
		dp.setFloat(dirX);
		dp.setFloat(dirY);
		
		return dp;
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
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		final float x = dp.getFloat();
		final float y = dp.getFloat();
		float newX = x, newY = y;
		final float dirX = dp.getFloat();
		final float dirY = dp.getFloat();
		
		final int width = (int) realm.getEntity(shooterId).getWidth();
		final int height = (int) realm.getEntity(shooterId).getHeight();
		
		float distanceTraveled = 0;
		final float maxDistance = getRangeAmount(spellLevel);
		final float tileSize = realm.getMap().getTileSize();
		
		realm.playSound(SoundFile.spell_port, x, y);
		
		while(distanceTraveled < maxDistance)
		{
			newX += tileSize / 2 * dirX;
			newY += tileSize / 2 * dirY;
			distanceTraveled += tileSize / 2;
			if(realm.getMap().isCollisionAt(newX, newY, width, height, false))
			{
				int stopAfter = 15;
				do
				{
					newX -= 5 * dirX;
					newY -= 5 * dirY;
					stopAfter--;
				} while(stopAfter > 0 && realm.getMap().isCollisionAt(newX, newY, width, height, false));
				if(stopAfter <= 0)//still in collision
				{
					newX = x;
					newY = y;
				}
				break;
			}
		}
		
		realm.setNewEntityPosition(shooterId, newX, newY);
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getRangeAmount(int level)
	{
		return rangeAmountBase + rangeAmountPerLevel * (level - 1);
	}
}
