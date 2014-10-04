package com.vhelium.lotig.scene.gamescene.client.items.consumable;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class AttributePotion extends ConsumableItem
{
	String attribute;
	int value;
	
	protected AttributePotion(String attribute, int value)
	{
		this.attribute = attribute.toUpperCase();
		this.value = value;
		NAME = "Potion of " + this.attribute;
		CATEGORY = ItemCategory.Consumable;
	}
	
	@Override
	public boolean use(PlayerClient player)
	{
		if(player.requestPermanentAttributeIncrease(attribute, value))
		{
			player.hud.getCallback().postMessage(attribute + " - Potion drunk: " + player.getAttributePotionsDrunk(attribute) + " / " + Constants.attributePotionMaxDrunk, true);
			return true;
		}
		else
		{
			player.hud.getCallback().postMessage("Already maximum number of " + attribute + " - Potions drunk!", false);
			return false;
		}
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return true;
	}
	
	@Override
	protected void applyIcon()
	{
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon("Attribute Potion"));
	}
	
	@Override
	public String getDescription()
	{
		return NAME + ":\n\nPermantently increases your\n" + attribute + " by " + value + ".\nYou can only drink a\nmaximum of " + Constants.attributePotionMaxDrunk + " potions\nper attribute!";
	}
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	@Override
	public String toStringFormat()
	{
		return "AtPt:" + attribute + ":" + value + ":" + count;
	}
	
	public static AttributePotion createItemFromStringFormat(StringTokenizer st)
	{
		return getAttributePotion(st.nextToken(), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
	}
	
	public static AttributePotion getAttributePotion(String attribute, int value, int count)
	{
		AttributePotion potion = new AttributePotion(attribute, value);
		potion.applyIcon();
		potion.count = count;
		potion.price = GameHelper.getInstance().getPricePerAttributePoint(attribute.toUpperCase()) * Price.AttributePotionPriceFactor;
		return potion;
	}
	
	public static AttributePotion getRandomAttributePotion(int count)
	{
		String attr = "";
		int value = 0;
		int r = GameHelper.getInstance().getRandom().nextInt(13);
		switch(r)
		{
			case 0:
				attr = "STR";
				value = 10;
				break;
			case 1:
				attr = "DEX";
				value = 10;
				break;
			case 2:
				attr = "SPD";
				value = 10;
				break;
			case 3:
				attr = "VIT";
				value = 10;
				break;
			case 4:
				attr = "WIS";
				value = 10;
				break;
			case 5:
				attr = "INT";
				value = 10;
				break;
			case 6:
				attr = "MAXHP";
				value = 100;
				break;
			case 7:
				attr = "MAXMANA";
				value = 30;
				break;
			case 8:
				attr = "FRES";
				value = 10;
				break;
			case 9:
				attr = "CRES";
				value = 10;
				break;
			case 10:
				attr = "LRES";
				value = 10;
				break;
			case 11:
				attr = "PRES";
				value = 10;
				break;
			case 12:
				attr = "ARMOR";
				value = 40;
				break;
		}
		
		return getAttributePotion(attr, value, count);
	}
	
	@Override
	public StackableItem oneCopy()
	{
		return getAttributePotion(attribute, value, 1);
	}
}
