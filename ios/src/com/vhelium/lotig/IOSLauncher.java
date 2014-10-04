package com.vhelium.lotig;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class IOSLauncher extends IOSApplication.Delegate
{
	@Override
	protected IOSApplication createApplication()
	{
		GameHelper.setPlatformResolver(new IOSResolver());
		IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		return new IOSApplication(new Main(), config);
	}
	
	public static void main(String[] argv)
	{
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, IOSLauncher.class);
		pool.close();
	}
}