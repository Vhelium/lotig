package com.vhelium.lotig.scene.gamescene.server.levelobject;

import com.badlogic.gdx.maps.MapProperties;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.connection.DataPacket;
import com.vhelium.lotig.scene.connection.MessageType;
import com.vhelium.lotig.scene.gamescene.client.player.PlayerClient;
import com.vhelium.lotig.scene.gamescene.server.EntityServerDefenseTower;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_DefenseTower extends LevelObject
{
	public static final String NAME = "tdT";
	private EntityServerDefenseTower towerEntity;
	
	public LevelObject_DefenseTower(int id, Realm realm, Rectangle rectangle)//server
	{
		this.id = id;
		this.bounds = rectangle;
		this.animated = false;
		
		setState(DefenseTowerState.UNBUILT);
		
		towerEntity = new EntityServerDefenseTower(realm);
		towerEntity.setLevel(0);
		towerEntity.setName("DefenseTower");
		towerEntity.setAsset(towerEntity.getAssetForState());
		towerEntity.setBulletAsset("DefenseTower");
		towerEntity.setWidth(PlayerClient.WIDTH);
		towerEntity.setHeight(PlayerClient.HEIGHT);
		towerEntity.setRectangle(new Rectangle(0, 0, PlayerClient.WIDTH, PlayerClient.HEIGHT));
		
		towerEntity.setMaxHp(1);
		towerEntity.setHp(towerEntity.getMaxHp());
		towerEntity.setMaxHp(towerEntity.getHp());
		towerEntity.setHp(towerEntity.getMaxHp());
		towerEntity.setMaxMana(0);
		towerEntity.setMana(towerEntity.getMaxMana());
		towerEntity.setArmor(0);
		towerEntity.setFireRes(0);
		towerEntity.setColdRes(0);
		towerEntity.setLightningRes(0);
		towerEntity.setBaseSpeed(0f);
		
		towerEntity.setShootRange(280);
		towerEntity.setDetectRange(240);
		towerEntity.setPiercing(false);
		towerEntity.setShootSpeed(1000);
		towerEntity.setDamage(EntityServerDefenseTower.getDamage(1));
		towerEntity.setDamageBonus(0);
		
		towerEntity.setX(bounds.getX());
		towerEntity.setY(bounds.getY());
	}
	
	public LevelObject_DefenseTower(int id, float x, float y, int w, int h, int state)//client
	{
		this.id = id;
		bounds = new Rectangle(x, y, w, h);
		
		MapProperties properties = new MapProperties();
		properties.put("defenseTower", String.valueOf(id));
		event = new OnEnterEvent(null, bounds, properties);
	}
	
	@Override
	public void onAdded(Realm realm)
	{
		
	}
	
	@Override
	public void onRemoved(Realm realm)
	{
		if(towerEntity != null)
			realm.removeFriendlyMinion(towerEntity);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME + ";" + state;
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		if(levelObjectCallback != null)
			levelObjectCallback.stateChanged(this, state);
	}
	
	public void upgradeTower(Realm realm, int path)
	{
		if(towerEntity != null)
		{
			if(state == DefenseTowerState.UNBUILT)
				realm.addFriendlyMinion(towerEntity);
			setState(path);
			towerEntity.setDamage(EntityServerDefenseTower.getDamage(state));
			
			this.setStateAndInform(state, realm);
			
			towerEntity.setBulletAsset(towerEntity.getAssetForState());
			towerEntity.setAsset(towerEntity.getAssetForState());
			
			DataPacket dpOthers = new DataPacket();
			dpOthers.setInt(MessageType.MSG_ENTITY_SKIN_CHANGED);
			dpOthers.setInt(towerEntity.Nr);
			dpOthers.setString(towerEntity.getAsset());
			realm.sendToAllActivePlayers(dpOthers);
		}
	}
	
	public static int getUpgradeCostToState(int state)
	{
		switch(state)
		{
			case DefenseTowerState.BASE:
				return 100;
			case DefenseTowerState.PHYSICAL1:
				return 80;
			case DefenseTowerState.PHYSICAL2:
				return 180;
			case DefenseTowerState.PHYSICAL3:
				return 350;
			case DefenseTowerState.PHYSICAL4:
				return 1500;
			case DefenseTowerState.ICE1:
				return 150;
			case DefenseTowerState.ICE2:
				return 300;
			case DefenseTowerState.ICE3:
				return 450;
			case DefenseTowerState.FIRE1:
				return 150;
			case DefenseTowerState.FIRE2:
				return 300;
			case DefenseTowerState.FIRE3:
				return 550;
			default:
				return 0;
		}
	}
	
	@Override
	public void setState(int state)
	{
		super.setState(state);
		if(towerEntity != null)
			towerEntity.setState(state);
	}
	
	public abstract class DefenseTowerState
	{
		public static final int UNBUILT = 0, BASE = 1;
		
		public static final int PHYSICAL1 = 101;
		public static final int PHYSICAL2 = 102;
		public static final int PHYSICAL3 = 103;
		public static final int PHYSICAL4 = 104;
		
		public static final int ICE1 = 201;
		public static final int ICE2 = 202;
		public static final int ICE3 = 203;
		
		public static final int FIRE1 = 301;
		public static final int FIRE2 = 302;
		public static final int FIRE3 = 303;
	}
	
	public String getTowerName()
	{
		switch(state)
		{
			case DefenseTowerState.BASE:
				return "Tower";
			case DefenseTowerState.PHYSICAL1:
				return "Physical Tower I";
			case DefenseTowerState.PHYSICAL2:
				return "Physical Tower II";
			case DefenseTowerState.PHYSICAL3:
				return "Physical Tower III";
			case DefenseTowerState.PHYSICAL4:
				return "Physical Tower IV";
			case DefenseTowerState.ICE1:
				return "Ice Tower I";
			case DefenseTowerState.ICE2:
				return "Ice Tower II";
			case DefenseTowerState.ICE3:
				return "Ice Tower III";
			case DefenseTowerState.FIRE1:
				return "Fire Tower I";
			case DefenseTowerState.FIRE2:
				return "Fire Tower II";
			case DefenseTowerState.FIRE3:
				return "Fire Tower III";
			default:
				return "";
		}
	}
}
