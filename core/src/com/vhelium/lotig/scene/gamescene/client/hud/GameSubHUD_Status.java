package com.vhelium.lotig.scene.gamescene.client.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.client.items.IPlayerAttributeListener;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;

public class GameSubHUD_Status extends GameHUDMenu implements IPlayerAttributeListener
{
	float xLeft = 235;
	float xRight = 410;
	
	Label txtHp;
	Label txtMana;
	Label txtLeft;
	Label txtRight;
	
	public GameSubHUD_Status(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void showUp()
	{
		updateText();
	}
	
	@Override
	public void loadResources(Main activity)
	{
		txtHp = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), Color.WHITE));
		txtHp.setAlignment(Align.top | Align.left);
		txtHp.setPosition(xLeft, 45);
		txtHp.setColor(12 / 255f, 200 / 255f, 82 / 255f, 1);
		
		txtMana = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), Color.WHITE));
		txtMana.setAlignment(Align.top | Align.left);
		txtMana.setPosition(xRight, 45);
		txtMana.setColor(91 / 255f, 144 / 255f, 231 / 255f, 1);
		
		txtLeft = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		txtLeft.setAlignment(Align.top | Align.left);
		txtLeft.setPosition(xLeft, 45);
		
		txtRight = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		txtRight.setAlignment(Align.left | Align.top);
		txtRight.setPosition(xRight, 45);
		
		this.addActor(txtHp);
		this.addActor(txtMana);
		this.addActor(txtLeft);
		this.addActor(txtRight);
	}
	
	@Override
	public void update(float delta)
	{
		txtHp.setText("Hp: " + (int) player.getHp() + " / " + (int) player.getAttribute("MAXHP"));
		txtMana.setText("Mana: " + (int) player.getMana() + " / " + (int) player.getAttribute("MAXMANA"));
	}
	
	@Override
	public void playerAttributeChanged()
	{
		updateText();
	}
	
	private void updateText()
	{
		update(0f);
		
		StringBuilder left = new StringBuilder("\n\n");
		/*1*/left.append("Strength: " + (int) player.getAttribute("STR") + "\n");
		/*2*/left.append("Dexterity: " + (int) player.getAttribute("DEX") + "\n");
		/*3*/left.append("Speed: " + (int) player.getAttribute("SPD") + "\n\n");
		
		/*4*/left.append("Damage: " + Math.round(player.getDamage()) + " - " + Math.round(player.getDamage() + player.getAttribute("BONUSDMG")) + "\n");
		/*5*/left.append("Life Per Hit: " + (int) player.getAttribute("LPH") + "\n");
		/*6*/left.append("Mana Per Hit: " + (int) player.getAttribute("MPH") + "\n");
		/*7*/left.append("CD reduction: " + (int) player.getAttribute("CDR") + "%\n");
		
		txtLeft.setText(left.toString());
		
		StringBuilder right = new StringBuilder("\n\n");
		/*1*/right.append("Vitality: " + (int) player.getAttribute("VIT") + "\n");
		/*2*/right.append("Intelligence: " + (int) player.getAttribute("INT") + "\n");
		/*3*/right.append("Wisdom: " + (int) player.getAttribute("WIS") + "\n\n");
		
		/*4*/right.append("Armor: " + (int) player.getAttribute("ARMOR") + "\n");
		/*5*/right.append("Fire Res.: " + (int) player.getAttribute("FRES") + "\n");
		/*6*/right.append("Cold Res.: " + (int) player.getAttribute("CRES") + "\n");
		/*7*/right.append("Lightning Res.: " + (int) player.getAttribute("LRES") + "\n");
		/*8*/right.append("Thorns: " + (int) player.getAttribute("THORNS") + "\n");
		
		txtRight.setText(right.toString());
		
//		String statsDisplayText = "";
//		
//		leftText.setText("Hp: " + (int) player.getHp() + " / " + (int) player.getAttribute("MAXHP") + "\nMana: " + (int) player.getMana() + " / " + (int) player.getAttribute("MAXMANA") + "\nDamage: " + player.getDamage(););
//		
//		HashMap<String, String> tmp = GameHelper.getInstance().getAttributeNames();
//		float tempAttributeCount = 0;
//		for(Entry<String, String> e : tmp.entrySet())
//		{
//			tempAttributeCount = player.getAttribute(e.getKey());
//			statsDisplayText += "" + e.getValue() + ": " + Float.toString(tempAttributeCount) + "\n";
//		}
//		statsText.setText(statsDisplayText);
//		statsText.setY(leftText.getY() + leftText.getHeight());
	}
	
	@Override
	public void initHUD(PlayerClient player)
	{
		player.addAttributeListener(this);
		super.initHUD(player);
	}
}