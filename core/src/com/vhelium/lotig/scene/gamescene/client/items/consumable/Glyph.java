package com.vhelium.lotig.scene.gamescene.client.items.consumable;

import java.util.List;
import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.Item;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class Glyph extends ConsumableItem
{
	String spell;
	
	protected Glyph(String spell)
	{
		this.spell = spell;
		NAME = "Glyph: " + spell;
		CATEGORY = ItemCategory.Glyph;
	}
	
	@Override
	public boolean use(PlayerClient player)
	{
		player.spellLevelUp(spell);
		return true;
	}
	
	@Override
	protected void applyIcon()
	{
		icon = new Sprite(4, 4, GameHelper.getInstance().getItemIcon("Glyph"));
	}
	
	@Override
	public String getDescription()
	{
		return NAME + ":\n\nIncreases your spell\n" + spell + " by 1.";
	}
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	@Override
	public String toStringFormat()
	{
		return "Glyph:" + spell + ":" + count;
	}
	
	public static Glyph createItemFromStringFormat(StringTokenizer st)
	{
		return getGlyph(st.nextToken(), Integer.parseInt(st.nextToken()));
	}
	
	public static Glyph getGlyph(String spell, int count)
	{
		Glyph glyph = new Glyph(spell);
		glyph.applyIcon();
		glyph.count = count;
		glyph.price = Price.Glyph;
		return glyph;
	}
	
	public String getSpellName()
	{
		return spell;
	}
	
	public static Item getRandomGlyph(int count)
	{
		return getGlyph(getRandomSpell(""), count);
	}
	
	public static String getRandomSpell(String playerClass)
	{
		if(playerClass == null || playerClass.equals(""))
		{
			int r = GameHelper.getInstance().getRandom().nextInt(6);
			switch(r)
			{
				case 0:
					playerClass = "Barbarian";
					break;
				case 1:
					playerClass = "Druid";
					break;
				case 2:
					playerClass = "Dark Priest";
					break;
				case 3:
					playerClass = "Ranger";
					break;
				case 4:
					playerClass = "Death Knight";
					break;
				case 5:
					playerClass = "Sorcerer";
					break;
			}
		}
		List<String> spells = GameHelper.getInstance().getPlayerClassSpells().get(playerClass);
		int r = GameHelper.getInstance().getRandom().nextInt(spells.size());
		return spells.get(r);
	}
	
	@Override
	public StackableItem oneCopy()
	{
		return getGlyph(spell, 1);
	}
	
	@Override
	public boolean isUsable(PlayerClient player)
	{
		return player.containsSpell(spell);
	}
}
