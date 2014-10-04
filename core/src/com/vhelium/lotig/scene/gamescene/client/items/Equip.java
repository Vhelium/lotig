package com.vhelium.lotig.scene.gamescene.client.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.offhand.EquipOffHand;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.EquipWeapon;
import com.vhelium.lotig.scene.gamescene.client.items.weapon.mod.WeaponModification;

public abstract class Equip extends Item
{
	protected String featuredAttribute = "na";
	protected HashMap<String, Integer> bonusAttributes;
	protected HashMap<String, Integer> possibleAttributesWithMax;
	public EquipModification modification = null;
	private final Random r;
	protected final static int basePrice = 150;
	
	private static String getRarity(int attributeCount)
	{
		if(attributeCount <= 2)
			return "common";
		else if(attributeCount <= 3)
			return "uncommon";
		else if(attributeCount <= 4)
			return "rare";
		else
			return "legendary";
	}
	
	protected Equip()
	{
		price = basePrice;
		r = GameHelper.getInstance().getRandom();
		bonusAttributes = new HashMap<String, Integer>();
		possibleAttributesWithMax = new HashMap<String, Integer>();
	}
	
	protected void randomStats(int level, int modifier)
	{
		int baseLevel = Math.min(modifier, 10);
		int attributeCount = Math.max(baseLevel / 2, 1);
		boolean bonusAttribute = (baseLevel > 2 && baseLevel % 2 > 0) ? (GameHelper.getInstance().getRandom().nextInt(2) == 1 ? true : false) : false;
		if(bonusAttribute)
			attributeCount++;
		
		attributeCount = Math.min(attributeCount, Constants.maxAttributeCount);
		double attributeBonusFactor = 1 + 1 / (Math.pow(Constants.attributeBonusFactModifier, (attributeCount - 1)));
		
		int i = 0;
		while(i < attributeCount && possibleAttributesWithMax.size() > 0)
		{
			String attr = getRandomAttribute();
			bonusAttributes.put(attr, (int) (getRandomAttributeValue(attr, level) * attributeBonusFactor));
			possibleAttributesWithMax.remove(attr);
			price += bonusAttributes.get(attr) * GameHelper.getInstance().getPricePerAttributePoint(attr);
			
			i++;
		}
		
		score = (price - basePrice) / Constants.ScoreToPriceFactor;
		
	}
	
	protected int getRandomAttributeValue(String attribute, int level)
	{
		int max = Math.round(possibleAttributesWithMax.get(attribute) / (float) Constants.ItemMaxLevel * level);
		int attr = r.nextInt(Math.max(max / 2, 1)) + 1;
		return max / 2 + attr;
	}
	
	protected int getRandomAttributeValue(int max, int level)
	{
		max = Math.round(max / (float) Constants.ItemMaxLevel * level);
		int attr = r.nextInt((int) Math.max(max / 2f / Constants.ItemMaxLevel * level, 1)) + 1;
		return max / 2 + attr;
	}
	
	@Override
	protected void applyIcon()
	{
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon("equip/" + NAME + "_" + getRarity(bonusAttributes.size())));
	}
	
	protected String statsToString()
	{
		String res = bonusAttributes.containsKey(featuredAttribute) ? GameHelper.getInstance().getAttributeNames().get(featuredAttribute) + ": " + bonusAttributes.get(featuredAttribute) + "\n" : "";
		for(Entry<String, Integer> stat : bonusAttributes.entrySet())
			if(!stat.getKey().equals(featuredAttribute))
				res += GameHelper.getInstance().getAttributeNames().get(stat.getKey()) + ": " + stat.getValue() + "\n";
		return res;
	}
	
	private String getRandomAttribute()
	{
		return (String) possibleAttributesWithMax.keySet().toArray()[r.nextInt(possibleAttributesWithMax.size())];
	}
	
	public List<String> getAttributesNames()
	{
		List<String> aAttributeNames = new ArrayList<String>();
		
		for(Entry<String, Integer> stat : bonusAttributes.entrySet())
		{
			aAttributeNames.add(stat.getKey());
		}
		
		return aAttributeNames;
	}
	
	public HashMap<String, Integer> getAttributes()
	{
		return bonusAttributes;
	}
	
	public int getAttribute(String ATTR)
	{
		ATTR = ATTR.toUpperCase();
		if(bonusAttributes.containsKey(ATTR))
			return bonusAttributes.get(ATTR);
		else
			return 0;
	}
	
	protected String statsToStringFormat()
	{
		String res = String.valueOf(bonusAttributes.size() + (modification != null ? 1 : 0)) + ":";
		for(Entry<String, Integer> stat : bonusAttributes.entrySet())
			res += stat.getKey() + ":" + stat.getValue() + ":";
		if(modification != null)
			res += "MOD:" + modification.getName() + ":";
		return res;
	}
	
	protected static void applyStatsFromStringFormat(StringTokenizer st, Equip res)
	{
		int attrCount = Integer.parseInt(st.nextToken());
		for(int i = 0; i < attrCount; i++)
		{
			String attr = st.nextToken();
			if(attr.equals("MOD"))
				res.modification = EquipModification.getModFromName(st.nextToken());
			else
				res.bonusAttributes.put(attr, Integer.parseInt(st.nextToken()));
		}
		res.score = Integer.parseInt(st.nextToken());
		res.price = Integer.parseInt(st.nextToken());
		if(res.score == -1 || res.price == -1)
			recalculateScoreAndPrice(res);
	}
	
	public static void recalculateScoreAndPrice(Equip equip)
	{
		equip.price = basePrice;
		for(Entry<String, Integer> e : equip.bonusAttributes.entrySet())
		{
			if(e.getKey().equals("MOD"))
				equip.price += Price.EquipModification;
			else if(equip instanceof EquipWeapon && e.getKey().equals("DMG"))
				equip.price += (e.getValue() / ((WeaponModification) equip.modification).getDmgFactor() - ((EquipWeapon) equip).minDamage) * GameHelper.getInstance().getPricePerAttributePoint(e.getKey());
			else
				equip.price += e.getValue() * GameHelper.getInstance().getPricePerAttributePoint(e.getKey());
		}
		equip.score = Math.max((equip.price - basePrice) / Constants.ScoreToPriceFactor, 1);
	}
	
	@Override
	public String toString()
	{
		return NAME + " - " + score;
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ":" + statsToStringFormat() + score + ":" + price;
	}
	
	@Override
	public String getDescription()
	{
		return NAME + " (" + score + "):\n\n" + statsToString();
	}
	
	public String[] getCompareDescription(Equip equipped)
	{
		String[] description = new String[2];
		
		description[0] = NAME + " (" + score + "):" + (this instanceof EquipOffHand ? "\nClass: " + ((EquipOffHand) this).playerClass : (this instanceof EquipWeapon ? "\nClass: " + ((EquipWeapon) this).playerClass : "")) + "\n\n";
		if(bonusAttributes.containsKey(featuredAttribute))
		{
			description[0] += GameHelper.getInstance().getAttributeNames().get(featuredAttribute) + ": " + bonusAttributes.get(featuredAttribute);
			if(equipped.getAttributes().containsKey(featuredAttribute))
			{
				int dif = getAttribute(featuredAttribute) - equipped.getAttribute(featuredAttribute);
				description[0] += " (" + (dif < 0 ? "" : "+") + dif + ")";
			}
			else
			{
				description[0] += " (+" + getAttribute(featuredAttribute) + ")";
			}
			description[0] += "\n";
			description[1] = "";
		}
		else
		{
			if(equipped.getAttributes().containsKey(featuredAttribute))
				description[1] = "-" + GameHelper.getInstance().getAttributeNames().get(featuredAttribute) + ": " + equipped.getAttribute(featuredAttribute) + "\n";
			else
				description[1] = "";
		}
		
		for(Entry<String, Integer> stat : bonusAttributes.entrySet())
		{
			if(!stat.getKey().equals(featuredAttribute))
			{
				description[0] += GameHelper.getInstance().getAttributeNames().get(stat.getKey()) + ": " + stat.getValue();
				if(equipped.getAttributes().containsKey(stat.getKey()))
				{
					int dif = getAttribute(stat.getKey()) - equipped.getAttribute(stat.getKey());
					description[0] += " (" + (dif < 0 ? "" : "+") + dif + ")";
				}
				else
				{
					description[0] += " (+" + getAttribute(stat.getKey()) + ")";
				}
				description[0] += "\n";
			}
		}
		if(description[0].endsWith("\n"))
			description[0] = description[0].substring(0, description[0].length() - 1);
		
		for(Entry<String, Integer> stat : equipped.getAttributes().entrySet())
		{
			if(!stat.getKey().equals(featuredAttribute))
			{
				if(!getAttributes().containsKey(stat.getKey()))
					description[1] += "-" + GameHelper.getInstance().getAttributeNames().get(stat.getKey()) + ": " + stat.getValue() + "\n";
			}
		}
		if(equipped.modification != null && (this.modification == null || !this.modification.getName().equals(equipped.modification.getName())))
			description[1] += "-" + equipped.modification.getName();
		
		return description;
	}
	
	public void increaseAttribute(String attr, int i)
	{
		if(bonusAttributes.containsKey(attr))
			bonusAttributes.put(attr, bonusAttributes.get(attr) + i);
		price += i * GameHelper.getInstance().getPricePerAttributePoint(attr);
		score = (price - basePrice) / Constants.ScoreToPriceFactor;
	}
}
