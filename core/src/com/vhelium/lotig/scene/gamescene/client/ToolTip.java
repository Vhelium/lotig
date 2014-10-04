package com.vhelium.lotig.scene.gamescene.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class ToolTip extends Sprite
{
	private static final int textGapX = 10;
	private static final int textGapY = 6;
	private static final float fadeInTime = 0.3f;
	private static final float fadeOutTime = 0.35f;
	
	private final Label txt;
	private final Sprite triangle;
	private final TextureRegion txtTriangle;
	
	public ToolTip(final float originX, final float originY, final String text)
	{
		super(0, 0, GameHelper.getInstance().getGuiAsset("tooltip_frame"));
		txt = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 17), Color.WHITE));
		txt.setAlignment(Align.top | Align.left);
		txt.setPosition(textGapX, textGapY);
		this.addActor(txt);
		
		txtTriangle = GameHelper.getInstance().getGuiAsset("tooltip_triangle");
		triangle = new Sprite(0, 0, txtTriangle);
		this.addActor(triangle);
		
		String display = text.replaceAll("%", "\n");
		txt.setText(display);
		txt.pack();
		
		ToolTip.this.setWidth(txt.getWidth() + textGapX * 2);
		ToolTip.this.setHeight(txt.getHeight() + textGapY * 2);
		
		ToolTip.this.setX(originX - getWidth() / 2);
		ToolTip.this.setY(originY - txtTriangle.getRegionHeight() - ToolTip.this.getHeight());
		
		triangle.setX(ToolTip.this.getWidth() / 2 - txtTriangle.getRegionWidth() / 2);
		triangle.setY(ToolTip.this.getHeight() - 1);
	}
	
	public void show(Group owner)
	{
		this.remove();
		
		triangle.clearActions();
		txt.clearActions();
		this.clearActions();
		
		triangle.getColor().a = 0;
		txt.getColor().a = 0;
		this.getColor().a = 0;
		
		triangle.addAction(Actions.alpha(1f, fadeInTime));
		txt.addAction(Actions.alpha(1f, fadeInTime));
		this.addAction(Actions.alpha(1f, fadeInTime));
		
		owner.addActor(this);
	}
	
	public void hide()
	{
		triangle.clearActions();
		txt.clearActions();
		this.clearActions();
		
		triangle.getColor().a = 0;
		txt.getColor().a = 0;
		this.getColor().a = 0;
		
		triangle.addAction(Actions.alpha(0f, fadeOutTime));
		txt.addAction(Actions.alpha(0f, fadeOutTime));
		this.addAction(Actions.sequence(Actions.alpha(0f, fadeOutTime), Actions.delay(0.2f), Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				ToolTip.this.remove();
			}
		})));
	}
}
