package com.vhelium.lotig.scene.gamescene;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.vhelium.lotig.Main;
import com.vhelium.lotig.constants.Log;

public class SoundManager
{
	private static SoundManager instance;
	private boolean isLoaded = false;
	private Main activity;
	private ConcurrentHashMap<String, Music> musics;
	private ConcurrentHashMap<String, Sound> sounds;
	private String lastMusicPlayed = null;
	private String nextMusic = null;
	private float musicSwitchLeft = 0f;
	private final float musicSwitchTimeOut = 500f;
	private final float musicSwitchTimeIn = 750f;
	private boolean hasMusic, hasSound;
	
	private final static float maxMusic = 0.5f;
	
	public static void load(Main activity)
	{
		instance = new SoundManager();
		instance.activity = activity;
		instance.musics = new ConcurrentHashMap<String, Music>();
		instance.sounds = new ConcurrentHashMap<String, Sound>();
		
		instance.hasMusic = Boolean.parseBoolean(GlobalSettings.getInstance().getDataValue("Music"));
		instance.hasSound = Boolean.parseBoolean(GlobalSettings.getInstance().getDataValue("Sound"));
		
		try
		{
			instance.loadSound();
			instance.loadMusic();
		}
		catch (final IOException e)
		{
			Log.e("SoundManager", "ERROR LOADING SOUND/MUSIC: " + e.getMessage());
		}
		instance.isLoaded = true;
	}
	
	public static void loadSpellSounds(String playerClass)
	{
		if(playerClass.equalsIgnoreCase("Barbarian"))
		{
			instance.loadSound(SoundFile.shout_bloodthirst);
			instance.loadSound(SoundFile.shout_speedup);
			instance.loadSound(SoundFile.swing1);
			instance.loadSound(SoundFile.swing2);
			instance.loadSound(SoundFile.shout_rage);
			instance.loadSound(SoundFile.shout_speedup);
			instance.loadSound(SoundFile.spell_attack_up);
			instance.loadSound(SoundFile.shout_damage);
			instance.loadSound(SoundFile.shout_thunder);
		}
		else if(playerClass.equalsIgnoreCase("Dark Priest"))
		{
			instance.loadSound(SoundFile.spell_attack_up);
			instance.loadSound(SoundFile.electric);
			instance.loadSound(SoundFile.spell_shieldup);
			instance.loadSound(SoundFile.spell_ghost1);
			instance.loadSound(SoundFile.fire_burst);
			instance.loadSound(SoundFile.spell_summon);
			instance.loadSound(SoundFile.spell_dispel);
			instance.loadSound(SoundFile.spell_bling);
			instance.loadSound(SoundFile.spell_cursed);
			instance.loadSound(SoundFile.spell_failed);
			instance.loadSound(SoundFile.spell_missile);
		}
		else if(playerClass.equalsIgnoreCase("Death Knight"))
		{
			instance.loadSound(SoundFile.swing1);
			instance.loadSound(SoundFile.swing2);
			instance.loadSound(SoundFile.burning_up);
			instance.loadSound(SoundFile.spell_fire_cast);
			instance.loadSound(SoundFile.spell_dispel);
			instance.loadSound(SoundFile.spell_attack_up);
			instance.loadSound(SoundFile.shout_damage);
			instance.loadSound(SoundFile.spell_shieldup_mithril);
			instance.loadSound(SoundFile.spell_shieldup);
			instance.loadSound(SoundFile.spell_penetrate);
			instance.loadSound(SoundFile.shout_taunt);
		}
		else if(playerClass.equalsIgnoreCase("Druid"))
		{
			instance.loadSound(SoundFile.frost_blast);
			instance.loadSound(SoundFile.spell_failed);
			instance.loadSound(SoundFile.spell_ghost2);
			instance.loadSound(SoundFile.spell_tremble);
			instance.loadSound(SoundFile.spell_attack_up);
			instance.loadSound(SoundFile.spell_root);
			instance.loadSound(SoundFile.spell_fire_cast);
			instance.loadSound(SoundFile.spell_bling);
			instance.loadSound(SoundFile.spell_missile);
			instance.loadSound(SoundFile.electric);
			instance.loadSound(SoundFile.spell_summon);
			instance.loadSound(SoundFile.spell_root);
		}
		else if(playerClass.equalsIgnoreCase("Ranger"))
		{
			instance.loadSound(SoundFile.bow3);
			instance.loadSound(SoundFile.frost_blast);
			instance.loadSound(SoundFile.bow2);
			instance.loadSound(SoundFile.fire_burst);
			instance.loadSound(SoundFile.spell_fire_cast);
			instance.loadSound(SoundFile.electric);
			instance.loadSound(SoundFile.spell_bling);
			instance.loadSound(SoundFile.spell_failed);
			instance.loadSound(SoundFile.bow1);
			instance.loadSound(SoundFile.spell_spikes);
			instance.loadSound(SoundFile.spell_beasts);
		}
		else if(playerClass.equalsIgnoreCase("Sorcerer"))
		{
			instance.loadSound(SoundFile.spell_port);
			instance.loadSound(SoundFile.fire_burst);
			instance.loadSound(SoundFile.firestorm);
			instance.loadSound(SoundFile.frost_blast);
			instance.loadSound(SoundFile.spell_ice_splatter);
			instance.loadSound(SoundFile.spell_bling);
			instance.loadSound(SoundFile.spell_missile);
			instance.loadSound(SoundFile.spell_explosion_short);
			instance.loadSound(SoundFile.spell_failed);
			instance.loadSound(SoundFile.burning_up);
			instance.loadSound(SoundFile.spell_lightning_cast);
			instance.loadSound(SoundFile.spell_slow_down);
		}
	}
	
	private void loadSound(String sound)
	{
		if(!sounds.containsKey(sound))
		{
			Sound s = GameHelper.$.loadSound(sound + ".ogg");
			if(s != null)
				sounds.put(sound, s);
		}
	}
	
	private void loadSound() throws IOException
	{
		try
		{
			for(Field field : SoundFile.class.getDeclaredFields())
			{
				loadSound((String) field.get(null));
			}
		}
		catch (IllegalArgumentException e)
		{
			Log.e("SoundManager", "Error loading sound: " + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			Log.e("SoundManager", "Error loading sound: " + e.getMessage());
		}
	}
	
	private void loadMusic() throws IOException
	{
		musics.put("town", GameHelper.$.loadMusic("town.ogg"));
		musics.put("overworld", GameHelper.$.loadMusic("overworld.ogg"));
		musics.put("dungeon", GameHelper.$.loadMusic("dungeon.ogg"));
		musics.put("boss", GameHelper.$.loadMusic("boss.ogg"));
		musics.put("arena", GameHelper.$.loadMusic("arena.ogg"));
		musics.put("menu", GameHelper.$.loadMusic("menu.ogg"));
	}
	
	public static void playSound(String sound)
	{
		if(instance.hasSound)
		{
			if(!instance.sounds.containsKey(sound))
			{
				Sound s = GameHelper.$.loadSound(sound + ".ogg");
				if(s != null)
					instance.sounds.put(sound, s);
//				s.setPlayOnLoad(true);
			}
			else
			{
				instance.sounds.get(sound).play();
			}
		}
	}
	
	public static void playMusic(String music)
	{
		if(instance == null || music == null || !instance.musics.containsKey(music) || (instance.lastMusicPlayed != null && instance.lastMusicPlayed.equals(music) && instance.musics.get(instance.lastMusicPlayed).isPlaying()))
			return;
		
		stopMusic(instance.lastMusicPlayed);
		
		instance.musics.get(music).setVolume(1f * maxMusic);
		instance.musics.get(music).setLooping(true);
		if(instance.hasMusic)
			instance.musics.get(music).play();
		instance.musicSwitchLeft = 0f;
		instance.lastMusicPlayed = music;
	}
	
	public static void switchMusic(String music)
	{
		if(music.equals(instance.lastMusicPlayed) && instance.musics.get(instance.lastMusicPlayed).isPlaying())
			return;
		if(!instance.musics.containsKey(music))
			Log.e("SoundManager", "MISSING MUSIC: " + music);
		instance.nextMusic = music;
		if(instance.lastMusicPlayed != null)
			instance.musicSwitchLeft = instance.musicSwitchTimeOut;
		else
			playMusic(music);
	}
	
	public static void resumeMusic()
	{
		if(instance != null)
			SoundManager.playMusic(instance.lastMusicPlayed);
	}
	
	public static void update(float delta)
	{
		if(instance != null && instance.isLoaded)
			instance.selfUpdate(delta);
	}
	
	private void selfUpdate(float delta)
	{
		if(musicSwitchLeft > 0)
		{
			musicSwitchLeft -= delta;
			if(nextMusic != null)
			{
				//make old music quieter:
				if(musics.get(lastMusicPlayed) != null)
					musics.get(lastMusicPlayed).setVolume(Math.max(musicSwitchLeft / musicSwitchTimeOut, 0));
				if(musicSwitchLeft <= 0)//time to switch to new track
				{
					if(musics.containsKey(nextMusic))
					{
						stopMusic(lastMusicPlayed);
						
						musics.get(nextMusic).setVolume(0);
						musics.get(nextMusic).setLooping(true);
						if(hasMusic)
							musics.get(nextMusic).play();
						lastMusicPlayed = nextMusic;
					}
					nextMusic = null;
					musicSwitchLeft = musicSwitchTimeIn;
				}
			}
			else
			{
				//make new louder:
				if(musics.get(lastMusicPlayed) != null)
				{
					musics.get(lastMusicPlayed).setVolume(Math.max(1f - musicSwitchLeft / musicSwitchTimeIn, 0) * maxMusic);
					if(musicSwitchLeft <= 0)
						musics.get(lastMusicPlayed).setVolume(1);
				}
			}
		}
	}
	
	private static void stopMusic(String music)
	{
		if(music != null && instance.musics.get(music) != null && instance.musics.get(music).isPlaying())
		{
			instance.musics.get(music).stop();
		}
	}
	
	public static void stopMusic()
	{
		if(instance != null)
			stopMusic(instance.lastMusicPlayed);
	}
	
	public static void setHasMusic(boolean value)
	{
		if(instance.hasMusic && !value)
		{
			instance.hasMusic = value;
			stopMusic(instance.lastMusicPlayed);
		}
		else if(!instance.hasMusic && value)
		{
			instance.hasMusic = value;
			playMusic(instance.lastMusicPlayed);
		}
	}
	
	public static void setHasSound(boolean value)
	{
		instance.hasSound = value;
	}
	
	public static void unload()
	{
		for(Sound sound : instance.sounds.values())
			sound.dispose();
		instance.sounds.clear();
		
		for(Music music : instance.musics.values())
			music.dispose();
		instance.musics.clear();
	}
}
