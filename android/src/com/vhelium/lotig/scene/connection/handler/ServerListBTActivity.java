package com.vhelium.lotig.scene.connection.handler;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/** A simple list activity that displays Bluetooth devices that are in discoverable mode. This can be used as a gamelobby where players can see available servers and pick the one they wish to connect to. */

public class ServerListBTActivity extends ListActivity
{
	public static String EXTRA_SELECTED_DEVICE = "btaddress";
	
	private BluetoothAdapter myBt;
	
	private ServerListBTActivity self;
	
	private ArrayAdapter<String> arrayAdapter;
	
	private final BroadcastReceiver myReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Parcelable btParcel = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			BluetoothDevice btDevice = (BluetoothDevice) btParcel;
			arrayAdapter.add(btDevice.getName() + " - " + btDevice.getAddress());
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		self = this;
		arrayAdapter = new ArrayAdapter<String>(self, android.R.layout.simple_list_item_1);
		ListView lv = self.getListView();
		lv.setAdapter(arrayAdapter);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				myBt.cancelDiscovery(); // Cancel BT discovery explicitly so
				// that connections can go through
				String btDeviceInfo = ((TextView) arg1).getText().toString();
				String btHardwareAddress = btDeviceInfo.substring(btDeviceInfo.length() - 17);
				Intent i = new Intent();
				i.putExtra(EXTRA_SELECTED_DEVICE, btHardwareAddress);
				self.setResult(Activity.RESULT_OK, i);
				finish();
			}
		});
		myBt = BluetoothAdapter.getDefaultAdapter();
		myBt.startDiscovery();
		self.setResult(Activity.RESULT_CANCELED);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			onDestroy();
			self.setResult(Activity.RESULT_CANCELED);
			finish();
			return true;
		}
		return false;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(myReceiver, filter);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		this.unregisterReceiver(myReceiver);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(myBt != null)
		{
			myBt.cancelDiscovery(); // Ensure that we don't leave discovery running by accident
		}
	}
	
}
