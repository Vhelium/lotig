package com.vhelium.lotig.scene.gamescene.client;

import java.util.StringTokenizer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_NPC;

public class Dialogue extends Sprite
{
	private static final int textGapX = 10;
	private static final int textGapY = 6;
	private static final float fadeInTime = 0.3f;
	private static final float fadeOutTime = 0.35f;
	private static final float displayTime = 4f;
	
	public Dialogue(final ClientLevel level, final float pX, final float pY, String text, final LevelObject_NPC npc)// pX & pY position on top of entity
	{
		super(0, 0, GameHelper.getInstance().getGuiAsset("dialogue_frame"));
		final Label txt = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 17), Color.WHITE));
		txt.setAlignment(Align.top | Align.left);
		txt.setPosition(textGapX, textGapY);
		final TextureRegion txtTriangle = GameHelper.getInstance().getGuiAsset("dialogue_triangle");
		
		final StringTokenizer st = new StringTokenizer(text, "$");
		final int paragraphs = st.countTokens();
		
		final Sprite triangle = new Sprite(0, 0, txtTriangle);
		triangle.addAction(Actions.sequence(Actions.repeat(paragraphs, Actions.sequence(Actions.alpha(0.95f, fadeInTime), Actions.alpha(1f, displayTime), Actions.alpha(0f, fadeOutTime)))));
		this.addActor(triangle);
		
		txt.addAction(Actions.sequence(Actions.repeat(paragraphs, Actions.sequence(Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				String display = st.nextToken().replaceAll("%", "\n");
				txt.setText(display);
				txt.pack();
				
				Dialogue.this.setWidth(txt.getWidth() + textGapX * 2);
				Dialogue.this.setHeight(txt.getHeight() + textGapY * 2);
				
				Dialogue.this.setX(pX - getWidth() / 2);
				Dialogue.this.setY(pY - txtTriangle.getRegionHeight() - Dialogue.this.getHeight());
				
				triangle.setX(Dialogue.this.getWidth() / 2 - txtTriangle.getRegionWidth() / 2);
				triangle.setY(Dialogue.this.getHeight() - 1);
			}
		}), Actions.alpha(0.95f, fadeInTime), Actions.alpha(1f, displayTime), Actions.alpha(0f, fadeOutTime))), Actions.run(new Runnable()
		{
			@Override
			public void run()
			{
				Dialogue.this.remove();
				if(npc != null)
					level.onDialogueFinished(npc);
			}
		})));
		this.addActor(txt);
		
		this.addAction(Actions.sequence(Actions.repeat(paragraphs, Actions.sequence(Actions.alpha(0.95f, fadeInTime), Actions.alpha(1f, displayTime), Actions.alpha(0f, fadeOutTime)))));
	}
}
