package com.vhelium.lotig.scene.gamescene.spells.deathknight;

import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellTaunt extends Spell
{
	public static String name = "Taunt";
	public static String effectAsset = "Taunt";
	
	public static float cooldownBase = 8000f;
	public static float cooldownPerLevel = 100f;
	
	public static float manaCostBase = 150;
	public static float manaCostPerLevel = 15;
	
	public static int aoeRadiusBase = 150;
	public static int aoeRadiusPerLevel = 7;
	
	public static float targetCount = 3;
	public static float targetCountPerLevel = 0.5f;
	
	public SpellTaunt()
	{
		instantCast = true;
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
		return "You taunt nearby enemies pulling their attention on you.\n\nMaximum targets: " + getTargetCount(getLevel()) + "\nRadius: " + getAoeRadius(getLevel());
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
		
		return dp;
	}
	
	//>>>>>>>>>>>>>>>>  SERVER vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	public static void activate(int shooterId, int spellLevel, DataPacket dp, Realm realm)
	{
		if(realm.getPlayer(shooterId) == null)
			return;
		int level = spellLevel;
		float x = dp.getFloat();
		float y = dp.getFloat();
		
		realm.playEffect(x, y, getAoeRadius(level) * 2, getAoeRadius(level) * 2, effectAsset, 0);
		realm.playSound(SoundFile.shout_taunt, x, y);
		int tauntedTargets = 0;
		for(Entry<Integer, EntityServer> e : realm.getEnemies().entrySet())
		{
			if(Math.pow(e.getValue().getX() - realm.getPlayer(shooterId).getX(), 2) + Math.pow(e.getValue().getY() - realm.getPlayer(shooterId).getY(), 2) <= getAoeRadius(level) * getAoeRadius(level))
			{
				e.getValue().setTarget(realm.getPlayer(shooterId));
				tauntedTargets++;
			}
			if(tauntedTargets > getTargetCount(level))
				break;
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getAoeRadius(int level)
	{
		return aoeRadiusBase + aoeRadiusPerLevel * (level - 1);
	}
	
	public static int getTargetCount(int level)
	{
		return (int) (targetCount + targetCountPerLevel * (level - 1));
	}
}
