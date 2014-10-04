package com.vhelium.lotig.scene.gamescene.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class DamageNumber extends Label
{
	public DamageNumber(final float targetWidth, final float heightOffset, final int value, int damageType, final Main activity)
	{
		super(String.valueOf(value), new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		this.setAlignment(Align.top | Align.center);
		
		switch(damageType)
		{
			case DamageType.Heal:
				this.setText("+" + value);
				this.setColor(0 / 255f, 255 / 255f, 0 / 255f, 1);
				break;
			case DamageType.Physical:
				this.setColor(255 / 255f, 0 / 255f, 0 / 255f, 1);
				break;
			case DamageType.Fire:
				this.setColor(255 / 255f, 128 / 255f, 0 / 255f, 1);
				break;
			case DamageType.Cold:
				this.setColor(0 / 255f, 204 / 255f, 204 / 255f, 1);
				break;
			case DamageType.Lightning:
				this.setColor(153 / 255f, 51 / 255f, 255 / 255f, 1);
				break;
			case DamageType.Poison:
				this.setColor(102 / 255f, 102 / 255f, 0 / 255f, 1);
				break;
			case DamageType.Absolute:
				this.setColor(224 / 255f, 224 / 255f, 224 / 255f, 1);
				break;
			case DamageType.Mana:
				if(value >= 0)
					this.setText("+" + value);
				this.setColor(18 / 255f, 80 / 255f, 158 / 255f, 1);
				break;
		}
		
		if(value == 0)
			this.setText("Absorbed");
		
		this.setOrigin(getWidth() / 2, getHeight() / 2);
		this.setX(targetWidth / 2 - getWidth() / 2);
		this.setY(-8 + heightOffset);
		
		this.addAction(Actions.sequence(Actions.moveTo(getX(), -40 + heightOffset, 0.4f), Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				DamageNumber.this.remove();
			}
		})));
	}
	
	public DamageNumber(final float targetWidth, final int value, int damageType, final Main activity)
	{
		this(targetWidth, 0, value, damageType, activity);
	}
}
