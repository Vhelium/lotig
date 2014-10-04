package com.vhelium.lotig.scene.connection;

public class MessageType
{
	//COMMENTS MAY BE OUTDATED!
	
	public static final int MSG_SEND_CLIENT_INFO = 1;
	/*Send character to server
	 * From Client
	 * String playerName
	 */
	
	public static final int MSG_LOAD_MAP_INFO = 2;
	/*Send info which map to load
	 * From Server
	 * String mapName
	 */
	
	public static final int MSG_REMOVE_ENTITY = 3;
	/*
	 * From Server
	 */
	
	public static final int MSG_ADD_ENTITY = 4;
	/*Send notification to player that one joined and give them the infos
	 * From Server
	 */
	
	public static final int MSG_SEND_PLAYER_POS = 5;
	/*
	 * From Client
	 * float posX
	 * float posY
	 */
	
	public static final int MSG_ENTITY_POS_UPDATE = 6;
	/*
	 * From Server
	 * int playerCount
	 * <-int playerNr
	 * float X
	 * float Y->
	 * <-int playerNr
	 * float X
	 * float Y->
	 */
	
	public static final int MSG_REQUEST_SHOOTBULLET = 7;
	/*
	 * From Client
	 * float x
	 * float y
	 * float rotation
	 * String bulletType
	 */
	
	public static final int MSG_BULLET_SHOT = 8;
	/*
	 * From Server
	 * int bulletID
	 * float x
	 * float y
	 * float rotation
	 * float bulletType
	 */
	
	public static final int MSG_MAP_LOAD_COMPLETED = 9;
	
	public static final int MSG_PLAYER_NR = 10;
	
	public static final int MSG_REMOVE_BULLETS = 11;
	
	public static final int MSG_ENEMY_DEATH = 12;
	
	public static final int MSG_PLAYER_HEALTH_UPDATE = 13;//From Server
	
	public static final int MSG_REQUEST_TOWN_PORT = 14;
	
	public static final int MSG_PLAYER_STATUS_UPDATE = 15;//From Client: Damage, ShootSpeed
	
	public static final int MSG_OTHER_PLAYER_SP_CHANGED = 16;
	
	public static final int MSG_ITEMS_DROPPED = 17;
	
	public static final int MSG_REQUEST_LOOT_ITEM = 18;
	
	public static final int MSG_LOOT_ITEM_PICKED = 19;
	
	public static final int MSG_NEXT_LAIR = 20;
	
	public static final int MGS_DAMAGE_NUMBER = 21;
	
	public static final int MSG_SPELL_REQUEST = 22;
	
	public static final int MSG_REQUEST_REALM = 23;
	
	public static final int MSG_JOIN_TEAM = 24;
	
	public static final int MSG_REQUEST_CONDITION = 25;
	
	public static final int MSG_ADD_CONDITION = 26;
	
	public static final int MSG_PLAY_EFFECT = 27;
	
	public static final int MSG_ADD_CONDITION_EFFECT_OTHERS = 28;
	
	public static final int MSG_DROP_PLAYER_ITEM = 29;
	
	public static final int MSG_TIME_PING = 30;
	
	public static final int MSG_ENTITY_SET_NEW_POS = 31;
	
	public static final int MSG_REMOVE_LEVEL_OBJECTS = 32;
	
	public static final int MSG_LEVEL_OBJECT_STATE_CHANGED = 33;
	
	public static final int MSG_PLAYER_DIED = 34;
	
	public static final int MSG_REQUEST_RESPAWN = 35;
	
	public static final int MSG_PLAYER_TEMP_ATTR_CHANGE = 36;
	
	public static final int MSG_UPDATE_PVP_SCORES = 37;
	
	public static final int MSG_REQUEST_PVP_SCORE_RESET = 38;
	
	public static final int MSG_PLAYER_TEAM_RESPAWN = 39;
	
	public static final int MSG_PLAYER_STUNNED = 40;
	
	public static final int MSG_REMOVE_CONDITION = 41;
	
	public static final int MSG_BOSS_HP_UPDATE = 42;
	
	public static final int MSG_ADD_LEVEL_OBJECT = 43;
	
	public static final int MSG_OTHER_MAP_HAS_BEEN_LOADED = 44;
	
	public static final int MSG_UPDATE_ARENA_ROUND = 45;
	
	public static final int MSG_ARENA_NEXT_ROUND = 46;
	
	public static final int MSG_ADD_ENTITIES = 47;
	
	public static final int MSG_NEW_LAIR = 48;
	
	public static final int MSG_REQUEST_REALM_WITH_LAIR_NR = 49;
	
	public static final int MSG_BOSS_DEATH = 50;
	
	public static final int MSG_ARENA_FAME_REWARD = 51;
	
	public static final int MSG_POST_MESSAGE = 52;
	
	public static final int MSG_REQUEST_CHEST_ITEM = 53;
	
	public static final int MSG_CHEST_ITEM_PICKED = 54;
	
	public static final int MSG_PLAY_SOUND = 55;
	
	public static final int MSG_PLAYER_ROOTED = 56;
	
	public static final int MSG_PLAYER_SILENCED = 57;
	
	public static final int MSG_REQUEST_TEMP_REALM = 58;
	
	public static final int MSG_CONFIRM_REALM_REQUEST = 59;
	
	public static final int MSG_PLAYER_TEAM_JOINED = 60;
	
	public static final int MSG_VERSION_CHECK = 61;
	
	public static final int MSG_PLAYER_COOLDOWN_LOWERING = 62;
	
	public static final int MSG_QUEST_UPDATE_HOST = 63;
	
	public static final int MSG_QUEST_UPDATE_CLIENT = 64;
	
	public static final int MSG_CLIENT_PLAYER_JOINED_OTHER_MAP = 65;
	
	public static final int MSG_UNIQUE_CHEST_OPENED = 66;
	
	public static final int MSG_NPC_SPOKEN = 67;
	
	public static final int MSG_CHANGE_SKIN = 68;
	
	public static final int MSG_ENTITY_SKIN_CHANGED = 69;
	
	public static final int MSG_LEVER_PULLED = 70;
	
	public static final int MSG_LEVEL_OBJECT_REMOVE_COLLISION = 71;
	
	public static final int MSG_AREA_ENTERED = 72;
	
	public static final int MSG_UPGRADE_DEFENSE_TOWER = 73;
	
	public static final int MSG_TD_CASH_UPDATE = 74;
	
	public static final int MSG_TD_NEXT_ROUND = 75;
	
	public static final int MSG_TD_HP_UPDATE = 76;
	
	public static final int MSG_REMOVE_ENTITIES = 77;
	
	public static final int MSG_ADD_MOVEMENT_MODIFIER = 78;
	
	public static final int MSG_I_WANNA_DIE = 79;
	
	public static final int MSG_REQUEST_RECOVER_HEAL = 80;
	
	public static final int MSG_PLAYER_YOU_GOT_HIT = 81;
	
	public static final int MSG_REQUEST_ENEMY_ON_FIRE = 82;
	
	public static final int MSG_REQUEST_RANDOM_BLINK = 83;
}
