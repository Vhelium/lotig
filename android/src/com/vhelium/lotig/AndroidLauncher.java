package com.vhelium.lotig;

import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class AndroidLauncher extends AndroidApplication
{
	AndroidResolver resolver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		resolver = new AndroidResolver(this);
		GameHelper.setPlatformResolver(resolver);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Main(), config);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		resolver.activityResultReceived(requestCode, resultCode, data);
	}
}
