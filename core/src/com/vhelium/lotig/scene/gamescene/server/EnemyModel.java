package com.vhelium.lotig.scene.gamescene.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.vhelium.lotig.components.Rectangle;
import com.vhelium.lotig.constants.Constants;
import com.vhelium.lotig.scene.gamescene.server.enemy.EnemyMeele;
import com.vhelium.lotig.scene.gamescene.server.enemy.EnemyNuker;
import com.vhelium.lotig.scene.gamescene.server.enemy.EnemyRanger;
import com.vhelium.lotig.scene.gamescene.server.enemy.EnemyTD;
import com.vhelium.lotig.scene.gamescene.server.enemy.boss.BossArtos;
import com.vhelium.lotig.scene.gamescene.server.enemy.boss.BossBalis;
import com.vhelium.lotig.scene.gamescene.server.enemy.boss.BossEridus;
import com.vhelium.lotig.scene.gamescene.server.enemy.boss.BossFugis;
import com.vhelium.lotig.scene.gamescene.server.enemy.boss.BossHotrax;
import com.vhelium.lotig.scene.gamescene.server.enemy.boss.BossLaxos;
import com.vhelium.lotig.scene.gamescene.server.enemy.boss.BossWistos;
import com.vhelium.lotig.scene.gamescene.spells.SpellEnemy;
import com.vhelium.lotig.scene.gamescene.spells.SpellPriorityComparor;

public class EnemyModel
{
	public String name = "null";
	public String type = "null";
	public String asset = "null";
	public String bulletAsset = "null";
	public int width;
	public int height;
	
	public float hp;
	public float hpPL;
	
	public float mana;
	public float manaPL;
	
	public float damage;
	public float damagePL;
	
	public float damageBonus;
	public float damageBonusPL;
	
	public float armor;
	public float armorPL;
	
	public float fireRes;
	public float fireResPL;
	
	public float coldRes;
	public float coldResPL;
	
	public float lightningRes;
	public float lightningResPL;
	
	public float poisonRes;
	public float poisonResPL;
	
	public float speed;
	public float speedPL;
	
	public float shootSpeed;
	public float shootSpeedPL;
	
	public float detectRange;
	public float detectRangePL;
	
	public float shootRange;
	public float shootRangePL;
	
	public boolean piercing;
	
	public boolean oocRegen;
	
	public boolean roaming;
	
	public float globalSpellCooldown;
	
	public List<SpellEnemy> spells = new ArrayList<SpellEnemy>();
	
	public String deathAnimation;
	
	public EntityServer createEntity(int difficulty, int level, int playerCount, Realm realm)
	{
		level = level + Constants.LevelsPerDifficulty * difficulty;
		EntityServer res = null;
		
		if(type.equalsIgnoreCase("meele"))
			res = new EnemyMeele(realm);
		else if(type.equalsIgnoreCase("ranger"))
			res = new EnemyRanger(realm);
		else if(type.equalsIgnoreCase("nuker"))
			res = new EnemyNuker(realm);
		else if(type.equalsIgnoreCase("towerdefense"))
			res = new EnemyTD(realm);
		else if(type.equalsIgnoreCase("boss"))
		{
			if(name.equals("Eridus"))
				res = new BossEridus(realm, difficulty, level, playerCount);
			else if(name.equals("Artos"))
				res = new BossArtos(realm, playerCount);
			else if(name.equals("Fugis"))
				res = new BossFugis(realm, playerCount);
			else if(name.equals("Wistos"))
				res = new BossWistos(realm, playerCount);
			else if(name.equals("Balis"))
				res = new BossBalis(realm, playerCount);
			else if(name.equals("Laxos"))
				res = new BossLaxos(realm, playerCount);
			else if(name.equals("Hotrax"))
				res = new BossHotrax(realm, playerCount);
		}
		
		res.level = level;
		res.name = name;
		res.type = type;
		res.asset = asset;
		res.bulletAsset = bulletAsset;
		res.width = width;
		res.height = height;
		res.rectangle = new Rectangle(0, 0, width, height);
		
		res.setMaxHp(hp + hpPL * level);
		res.setHp(res.getMaxHp());
		res.setMaxHp(res.getHp() + (playerCount - 1) * (res.getHp() / 2));//TODO: adjust!
		res.setHp(res.getMaxHp());
		res.setMaxMana(mana + manaPL * level);
		res.setMana(res.getMaxMana());
		res.setDamage(damage + damagePL * level);
		res.setDamage(res.getDamage() + (playerCount - 1) * (res.getDamage() / 3));//TODO: adjust!
		res.setDamageBonus(damageBonus + damageBonusPL * level);
		res.setArmor(armor + armorPL * level);
		res.setFireRes(fireRes + fireResPL * level);
		res.setColdRes(coldRes + coldResPL * level);
		res.setLightningRes(lightningRes + lightningResPL * level);
		res.setPoisonRes(poisonRes + poisonResPL * level);
		res.baseSpeed = speed + speedPL * level;
		res.shootSpeed = shootSpeed + shootSpeedPL * level;
		res.detectRange = detectRange + detectRangePL * level;
		res.shootRange = shootRange + shootRangePL * level;
		res.piercing = piercing;
		res.roaming = roaming;
		res.deathAnimation = deathAnimation;
		res.oocRegen = oocRegen;
		
		res.globalSpellCooldown = globalSpellCooldown;
		for(SpellEnemy spell : spells)
		{
			res.spells.add(SpellEnemy.getSpell(spell.getName(), spell.getLevel() + Constants.LevelsPerDifficulty * difficulty, spell.priority, spell.conditions));
		}
		Collections.sort(res.spells, new SpellPriorityComparor());
		
		return res;
	}
}
