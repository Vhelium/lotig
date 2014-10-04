package com.vhelium.lotig.scene.gamescene.client.items.consumable;

import java.util.StringTokenizer;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.constants.Price;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.ItemCategory;
import com.vhelium.lotig.scene.gamescene.client.items.StackableItem;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class UniversalGlyph extends ConsumableItem
{
	protected UniversalGlyph()
	{
		NAME = "Universal Glyph";
		CATEGORY = ItemCategory.Consumable;
	}
	
	@Override
	public boolean use(PlayerClient player)
	{
		player.spellLevelUp(Glyph.getRandomSpell(player.getPlayerClass()));
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
		return NAME + ":\n\nIncreases a random spell\nof your player class.";
	}
	
	@Override
	public String toString()
	{
		return NAME + " (" + count + ")";
	}
	
	@Override
	public String toStringFormat()
	{
		return "UniGlyph:" + count;
	}
	
	public static UniversalGlyph createItemFromStringFormat(StringTokenizer st)
	{
		return getClassGlyph(Integer.parseInt(st.nextToken()));
	}
	
	public static UniversalGlyph getClassGlyph(int count)
	{
		UniversalGlyph universalGlyph = new UniversalGlyph();
		universalGlyph.applyIcon();
		universalGlyph.count = count;
		universalGlyph.price = Price.UniversalGlyph;
		return universalGlyph;
	}
	
	@Override
	public StackableItem oneCopy()
	{
		return getClassGlyph(1);
	}
}
