package com.vhelium.lotig.constants;

import com.badlogic.gdx.Gdx;

public class Log
{
	public static void l(String s1, String s2)
	{
		Gdx.app.log(s1, s2);
	}
	
	public static void e(String s1, String s2)
	{
		Gdx.app.error(s1, s2);
	}
	
	public static void d(String s1, String s2)
	{
		Gdx.app.debug(s1, s2);
	}
	
	public static void w(String s1, String s2)
	{
		Gdx.app.debug(s1, s2);
	}
}
