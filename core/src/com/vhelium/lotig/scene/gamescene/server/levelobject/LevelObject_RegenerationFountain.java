package com.vhelium.lotig.scene.gamescene.server.levelobject;

import java.util.List;
import com.vhelium.lotig.components.AnimatedSprite;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.scene.gamescene.DamageType;
import com.vhelium.lotig.scene.gamescene.GameHelper;
import com.vhelium.lotig.scene.gamescene.SoundFile;
import com.vhelium.lotig.scene.gamescene.server.PlayerServer;
import com.vhelium.lotig.scene.gamescene.server.Realm;

public class LevelObject_RegenerationFountain extends LevelObject
{
	public static final String NAME = "RegFo";
	private final int animationTime = 100;
	private final int regCooldown = 1200;
	private float regCooldownElapsed = 0f;
	
	public LevelObject_RegenerationFountain(int id, Realm realm, Rectangle rectangle)
	{
		this.id = id;
		this.asset = "RegFo";
		this.bounds = rectangle;
		this.animated = true;
	}
	
	public LevelObject_RegenerationFountain(int id, float x, float y, int w, int h)
	{
		this.id = id;
		this.animated = true;
		this.asset = "RegFo";
		
		sprite = new AnimatedSprite(x, y, w, h, GameHelper.getInstance().getGameAssetTiledTextureRegion(asset, 3, 1));
		((AnimatedSprite) sprite).animate(animationTime);
		bounds = new Rectangle(x, y, w, h);
	}
	
	@Override
	public void update(float delta, Realm realm)
	{
		regCooldownElapsed += delta;
		if(regCooldownElapsed > regCooldown)
		{
			regCooldownElapsed -= regCooldown;
			boolean regged = false;
			List<PlayerServer> playersInRange = realm.getPlayersInRange(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, 100);
			for(PlayerServer player : playersInRange)
			{
				
				float hp = Math.min(player.getMaxHp() - player.getHp(), player.getMaxHp() / 4);
				player.setHp(player.getHp() + hp);
				if(hp > 0)
					realm.sendDamageNumberToAllPlayers(player.Nr, hp, DamageType.Heal);
				
				float mana = Math.min(player.getMaxMana() - player.getMana(), player.getMaxMana() / 4);
				player.setMana(player.getMana() + mana);
				if(mana > 0)
					realm.sendDamageNumberToAllPlayers(player.Nr, mana, DamageType.Mana);
				
				regged = hp != 0 || mana != 0;
				if(regged)
					realm.playerHealthUpdate(player.Nr);
			}
			if(regged)
			{
				//PLAY EFFECT
				realm.playSound(SoundFile.RegFo_healed, bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
			}
		}
	}
	
	@Override
	public String toStringFormat()
	{
		return NAME;
	}
	
	@Override
	public void stateChangedClient(int state)
	{
		
	}
	
	@Override
	public void trigger(Realm realm, int entityId)
	{
		
	}
}
