package com.vhelium.lotig.scene.gamescene.spells.sorcerer;

import java.util.HashMap;
import java.util.Map.Entry;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.EntityServerMixin;
import com.vhelium.lotig.scene.gamescene.server.Realm;
import com.vhelium.lotig.scene.gamescene.spells.Spell;

public class SpellMagicTrick extends Spell
{
	public static String name = "Magic Trick";
	public static String effectAsset = "Magic Trick";
	
	public static float cooldownBase = 90000;
	public static float cooldownPerLevel = 1500;
	
	public static float manaCostBase = 280;
	public static float manaCostPerLevel = 20;
	
	public static int attrBase = 50;
	public static int attrLevel = 10;
	
	public static int durationBase = 8000;
	public static int durationPerLevel = 200;
	
	public static int aoeRadiusBase = 220;
	public static int aoeRadiusPerLevel = 5;
	
	public static float targetCount = 4;
	public static float targetCountPerLevel = 0.2f;
	
	public SpellMagicTrick()
	{
		instantCast = true;
	}
	
//>>>>>>>>>>>>>>>>  CLIENT vvv  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	@Override
	public String getDescription()
	{
		return "Random effect on you and nearby allies.\n\nRadius: " + getAoeRadius(getLevel()) + "\nMaximum targets: " + getTargetCount(getLevel());
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
		EntityServerMixin shooter = realm.getEntity(shooterId);
		if(shooter == null)
			return;
		
		realm.playEffect(shooter.getOriginX(), shooter.getOriginY(), getAoeRadius(spellLevel) * 2, getAoeRadius(spellLevel) * 2, effectAsset, 0);
		realm.playSound(SoundFile.spell_bling, shooter.getOriginX(), shooter.getOriginY());
		
		String attr = getRandomAttr();
		
		realm.requestCondition(shooterId, name, attr, getLPH(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
		
		HashMap<Integer, EntityServerMixin> entitiesInRange = realm.getEntitiesInRange(shooter.getOriginX(), shooter.getOriginY(), getAoeRadius(spellLevel));
		int buffed = 1;
		for(Entry<Integer, EntityServerMixin> entity : entitiesInRange.entrySet())
		{
			if(buffed >= getTargetCount(spellLevel))
				break;
			if(entity.getKey() != shooterId && entity.getValue().getTeamNr() == shooter.getTeamNr())
			{
				buffed++;
				realm.requestCondition(entity.getKey(), name, attr, getLPH(spellLevel), getDuration(spellLevel), true, effectAsset, System.currentTimeMillis());
			}
		}
	}
	
	private static String getRandomAttr()
	{
		int r = GameHelper.getInstance().getRandom().nextInt(13);
		switch(r)
		{
			case 0:
				return "STR";
			case 1:
				return "DEX";
			case 2:
				return "SPD";
			case 3:
				return "VIT";
			case 4:
				return "WIS";
			case 5:
				return "INT";
			case 6:
				return "ABSORB";
			case 7:
				return "DMG";
			case 8:
				return "FRES";
			case 9:
				return "CRES";
			case 10:
				return "LRES";
			case 11:
				return "PRES";
			case 12:
				return "ARMOR";
			default:
				return "null";
		}
	}
	
	public static float getManaCostStatic(int level)
	{
		return manaCostBase + manaCostPerLevel * (level - 1);
	}
	
	public static int getLPH(int level)
	{
		return attrBase + attrLevel * (level - 1);
	}
	
	public static int getDuration(int level)
	{
		return durationBase + durationPerLevel * (level - 1);
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
