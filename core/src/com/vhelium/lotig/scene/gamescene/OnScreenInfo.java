package com.vhelium.lotig.scene.gamescene;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.components.Sprite;

public class OnScreenInfo extends Group
{
	private static final int fontSize = 15;
	private List<Integer> linked = new ArrayList<Integer>();
	private String text;
	private int textX, textY;
	
	private String asset;
	private int assetX, assetY;
	private Sprite sprite;
	private Label txt;
	public int id;
	
	public OnScreenInfo(int id)
	{
		this.id = id;
	}
	
	public void load(Main activity)
	{
		txt = new Label(text, new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, fontSize), Color.GREEN));
		txt.setAlignment(Align.top | Align.left);
		txt.setPosition(4, 4);
		Sprite bgTxt = new Sprite(textX - 4, textY - 4, txt.getWidth() + 2 * 4, txt.getHeight() + 2 * 4, GameHelper.getInstance().getGuiAsset("backgroundOSI"));
		bgTxt.addActor(txt);
		this.addActor(bgTxt);
		sprite = new Sprite(assetX, assetY, GameHelper.getInstance().getGameAsset(asset));
		sprite.addAction(Actions.forever(Actions.sequence(Actions.scaleTo(0.86f, 0.86f, 0.8f), Actions.scaleTo(1f, 1f, 0.8f))));
		this.addActor(sprite);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setTextX(int textX)
	{
		this.textX = textX;
	}
	
	public void setTextY(int textY)
	{
		this.textY = textY;
	}
	
	public void setAsset(String asset)
	{
		this.asset = asset;
	}
	
	public void setAssetX(int assetX)
	{
		this.assetX = assetX;
	}
	
	public void setAssetY(int assetY)
	{
		this.assetY = assetY;
	}
	
	public Sprite getSprite()
	{
		return sprite;
	}
	
	public Label getTxt()
	{
		return txt;
	}
	
	public void addLink(Integer l)
	{
		linked.add(l);
	}
	
	public void setLinked(List<Integer> linked)
	{
		this.linked = linked;
	}
	
	public List<Integer> getLinked()
	{
		return linked;
	}
}
