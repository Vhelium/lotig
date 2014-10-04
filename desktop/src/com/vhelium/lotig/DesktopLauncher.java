package com.vhelium.lotig;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class DesktopLauncher
{
	public static void main(String[] arg)
	{
		GameHelper.setPlatformResolver(new DesktopResolver());
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		config.vSyncEnabled = false;
		new LwjglApplication(new Main(), config);
	}
}