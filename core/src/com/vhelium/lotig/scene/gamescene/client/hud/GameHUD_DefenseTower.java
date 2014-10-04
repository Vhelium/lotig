package com.vhelium.lotig.scene.gamescene.client.hud;

import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.vhelium.lotig.components.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.components.Sprite;
import com.vhelium.lotig.components.TextButton;
import com.vhelium.lotig.components.TextButton.OnMyClickListener;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.EntityServerDefenseTower;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_DefenseTower;
import com.vhelium.lotig.scene.gamescene.server.levelobject.LevelObject_DefenseTower.DefenseTowerState;

public class GameHUD_DefenseTower extends GameHUDMenu
{
	Main activity;
	private final ConcurrentHashMap<Integer, TextButton> cmds = new ConcurrentHashMap<Integer, TextButton>();
	private Label text;
	private LevelObject_DefenseTower selectedTower;
	
	public GameHUD_DefenseTower(IGameHUDCallback hudCallback)
	{
		super(hudCallback);
	}
	
	@Override
	public void setParam(Integer param)
	{
		int id = param;
		selectedTower = (LevelObject_DefenseTower) player.getLevel().getMap().getLevelObjects().get(id);
		showUp();
	}
	
	@Override
	public void showUp()
	{
		if(selectedTower != null)
		{
			if(selectedTower.getState() == DefenseTowerState.UNBUILT)
			{
				cmds.get(1).setVisible(true);
				cmds.get(1).setText("Build: " + LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.BASE) + "$");
				if(((GameHUD_TDInfo) hudCallback.getMenu("tdInfo")).getCash() >= LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.BASE))
					cmds.get(1).setEnabled(true);
				else
					cmds.get(1).setEnabled(false);
				
				cmds.get(2).setVisible(false);
				
				cmds.get(3).setVisible(false);
				
				text.setText(selectedTower.getTowerName());
			}
			else if(selectedTower.getState() == DefenseTowerState.BASE)
			{
				cmds.get(1).setVisible(true);
				cmds.get(1).setText("Physical tower:" + LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.PHYSICAL1) + "$");
				if(((GameHUD_TDInfo) hudCallback.getMenu("tdInfo")).getCash() >= LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.PHYSICAL1))
					cmds.get(1).setEnabled(true);
				else
					cmds.get(1).setEnabled(false);
				
				cmds.get(2).setVisible(true);
				cmds.get(2).setText("Ice tower:" + LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.ICE1) + "$");
				if(((GameHUD_TDInfo) hudCallback.getMenu("tdInfo")).getCash() >= LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.ICE1))
					cmds.get(2).setEnabled(true);
				else
					cmds.get(2).setEnabled(false);
				
				cmds.get(3).setVisible(true);
				cmds.get(3).setText("Fire tower:" + LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.FIRE1) + "$");
				if(((GameHUD_TDInfo) hudCallback.getMenu("tdInfo")).getCash() >= LevelObject_DefenseTower.getUpgradeCostToState(DefenseTowerState.FIRE1))
					cmds.get(3).setEnabled(true);
				else
					cmds.get(3).setEnabled(false);
				
				text.setText(selectedTower.getTowerName() + "\nDamage: " + EntityServerDefenseTower.getDamage(selectedTower.getState()));
			}
			else if(selectedTower.getState() == DefenseTowerState.PHYSICAL3 || selectedTower.getState() % 100 < 3)
			{
				cmds.get(1).setVisible(true);
				cmds.get(1).setText("Upgrade: " + LevelObject_DefenseTower.getUpgradeCostToState(selectedTower.getState() + 1) + "$");
				if(((GameHUD_TDInfo) hudCallback.getMenu("tdInfo")).getCash() >= LevelObject_DefenseTower.getUpgradeCostToState(selectedTower.getState() + 1))
					cmds.get(1).setEnabled(true);
				else
					cmds.get(1).setEnabled(false);
				
				cmds.get(2).setVisible(false);
				
				cmds.get(3).setVisible(false);
				
				text.setText(selectedTower.getTowerName() + "\nDamage: " + EntityServerDefenseTower.getDamage(selectedTower.getState()));
			}
			else
			{
				cmds.get(1).setVisible(false);
				cmds.get(2).setVisible(false);
				cmds.get(3).setVisible(false);
				
				if(selectedTower.getState() == DefenseTowerState.PHYSICAL4)
					text.setText(selectedTower.getTowerName() + "\nSplash Damage: " + EntityServerDefenseTower.getDamage(selectedTower.getState()));
				else
					text.setText(selectedTower.getTowerName() + "\nDamage: " + EntityServerDefenseTower.getDamage(selectedTower.getState()));
			}
		}
	}
	
	@Override
	public void loadResources(Main activity)
	{
		this.activity = activity;
		final TextureRegion textureRegionRight = GameHelper.$.getGuiAsset("right");
		final TextureRegion textureRegionCmdPort = GameHelper.$.getGuiAsset("cmdPort");
		
		Sprite spriteRight = new Sprite(587, 48, textureRegionRight);
		
		for(int i = 1; i <= 3; i++)
		{
			final int nr = i;
			TextButton cmd = new TextButton(16, 220 - i * 50, textureRegionCmdPort.getRegionWidth() + 80, textureRegionCmdPort.getRegionHeight(), textureRegionCmdPort, GameHelper.getInstance().getMainFont(FontCategory.InGame, 20), "", new Color(210 / 255f, 210 / 255f, 210 / 255f, 1), new Color(1, 1, 1, 1), SoundFile.menu_ingame_selected, new OnMyClickListener()
			{
				@Override
				public void onClick(ChangeEvent event, Actor actor)
				{
					if(selectedTower != null)
					{
						DataPacket dp = new DataPacket(MessageType.MSG_UPGRADE_DEFENSE_TOWER);
						dp.setInt(selectedTower.id);
						if(selectedTower.getState() == DefenseTowerState.UNBUILT)
							dp.setInt(DefenseTowerState.BASE);
						else if(selectedTower.getState() == DefenseTowerState.BASE)
							dp.setInt(nr * 100 + 1);
						else
							dp.setInt(selectedTower.getState() + 1);
						hudCallback.sendDataPacket(dp);
					}
				}
			});
			spriteRight.addActor(cmd);
			cmds.put(i, cmd);
		}
		
		text = new Label("", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.InGame, 18), Color.WHITE));
		text.setAlignment(Align.top | Align.left);
		text.setPosition(17, 14);
		spriteRight.addActor(text);
		
		this.addActor(spriteRight);
	}
	
	@Override
	public void update(float delta)
	{
		
	}
}
