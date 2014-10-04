package com.vhelium.lotig.scene.connection.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import ch.vhelium.lotig.beta.R;

public class ServerListWlanActivity extends Activity
{
	public static String EXTRA_SELECTED_IP = "wlanaddress";
	public static String EXTRA_SELECTED_PORT = "portnr";
	private EditText txtIp;
	private EditText txtPort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_ip);
		
		// get edittext component
		txtIp = (EditText) findViewById(R.id.enter_ip_box);
		txtPort = (EditText) findViewById(R.id.enter_port);
		
	}
	
	public void sendConnect(View view)
	{
		// display a floating message
		Intent i = new Intent();
		i.putExtra(EXTRA_SELECTED_IP, txtIp.getText().toString());
		i.putExtra(EXTRA_SELECTED_PORT, txtPort.getText().toString());
		Log.e("IP", "" + txtIp.getText());
		Log.e("PORT", "" + txtPort.getText());
		ServerListWlanActivity.this.setResult(Activity.RESULT_OK, i);
		ServerListWlanActivity.this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			setResult(Activity.RESULT_CANCELED);
			finish();
			return true;
		}
		return false;
	}
}
